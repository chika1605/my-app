package kg.rubicon.my_app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kg.rubicon.my_app.model.Document;
import kg.rubicon.my_app.model.Language;
import kg.rubicon.my_app.model.Person;
import kg.rubicon.my_app.model.PersonTranslation;
import kg.rubicon.my_app.model.dto.ConfirmResponse;
import kg.rubicon.my_app.model.dto.PersonDataDto;
import kg.rubicon.my_app.repository.DocumentRepository;
import kg.rubicon.my_app.repository.PersonRepository;
import kg.rubicon.my_app.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmService {

    private final DocumentRepository documentRepository;
    private final PersonRepository personRepository;

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    @Value("${server.url:http://localhost:8080}")
    private String serverUrl;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Transactional
    public ConfirmResponse confirm(Long documentId, String personDataJson, MultipartFile photo) throws IOException {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentId));

        PersonDataDto dto = objectMapper.readValue(personDataJson, PersonDataDto.class);

        Person person = Person.builder()
                .birthYear(dto.birthYear())
                .deathYear(dto.deathYear())
                .repressionYear(dto.repressionYear())
                .birthDate(dto.birthDate())
                .deathDate(dto.deathDate())
                .arrestDate(dto.arrestDate())
                .sentenceDate(dto.sentenceDate())
                .rehabilitationDate(dto.rehabilitationDate())
                .build();

        if (photo != null && !photo.isEmpty()) {
            String photoName = photo.getOriginalFilename();
            if (photoName == null || (!photoName.endsWith(".jpg") && !photoName.endsWith(".jpeg") && !photoName.endsWith(".png")))
                throw new IllegalArgumentException("Only .jpg, .jpeg and .png allowed for photo");
            String ext = getExtension(photoName);
            String fileName = UUID.randomUUID() + "." + ext;
            Path dir = Paths.get(uploadDir, "images");
            Files.createDirectories(dir);
            Files.copy(photo.getInputStream(), dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            person.setPhotoUrl(serverUrl + "/files/images/" + fileName);
        }

        personRepository.save(person);

        if (dto.translations() != null) {
            List<PersonTranslation> translations = new ArrayList<>();
            for (Map.Entry<String, PersonDataDto.TranslationDto> entry : dto.translations().entrySet()) {
                Language lang = resolveLanguage(entry.getKey());
                if (lang == null) continue;
                PersonDataDto.TranslationDto t = entry.getValue();
                translations.add(PersonTranslation.builder()
                        .language(lang.getId())
                        .fullName(t.fullName())
                        .normalizedName(t.normalizedName())
                        .birthPlace(t.birthPlace())
                        .deathPlace(t.deathPlace())
                        .region(t.region())
                        .district(t.district())
                        .occupation(t.occupation())
                        .charge(t.charge())
                        .sentence(t.sentence())
                        .biography(t.biography())
                        .person(person)
                        .build());
            }
            person.setTranslations(translations);
            personRepository.save(person);
        }

        document.setPerson(person);
        documentRepository.save(document);

        return new ConfirmResponse(
                person.getId(),
                document.getId(),
                person.getPhotoUrl(),
                person.getStatusAsEnum().name()
        );
    }

    private Language resolveLanguage(String slug) {
        for (Language lang : Language.values()) {
            if (lang.getSlug().equals(slug)) return lang;
        }
        return null;
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
