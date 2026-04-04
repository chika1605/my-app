package kg.rubicon.my_app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.rubicon.my_app.dto.PersonCreationRequest;
import kg.rubicon.my_app.model.Document;
import kg.rubicon.my_app.model.Language;
import kg.rubicon.my_app.model.Person;
import kg.rubicon.my_app.model.PersonTranslation;
import kg.rubicon.my_app.model.dto.ConfirmResponse;
import kg.rubicon.my_app.repository.DocumentRepository;
import kg.rubicon.my_app.repository.PersonRepository;
import kg.rubicon.my_app.util.UploadProperties;
import kg.rubicon.my_app.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmService {

    private final DocumentRepository documentRepository;
    private final PersonRepository personRepository;
    private final UploadProperties properties;
    private final ObjectMapper objectMapper;

    private String savePhoto(MultipartFile photo) throws IOException {
        String photoName = photo.getOriginalFilename();
        String ext = getExtension(photoName);

        if (!List.of("jpg", "jpeg", "png").contains(ext.toLowerCase())) {
            throw new IllegalArgumentException("Only .jpg, .jpeg and .png allowed for photo");
        }

        String fileName = UUID.randomUUID() + "." + ext;
        Path dir = Paths.get(properties.getDir(), properties.getFolders().get("images"));
        Files.createDirectories(dir);
        Files.copy(photo.getInputStream(), dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    @Transactional
    public ConfirmResponse confirm(PersonCreationRequest dto, MultipartFile photo) throws IOException {

        Document document = documentRepository.findById(dto.getDocumentId())
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + dto.getDocumentId()));

        String fullName = "";
        if (dto.getTranslations() != null && !dto.getTranslations().isEmpty()) {
            PersonCreationRequest.TranslationRequest ruTranslation = dto.getTranslations().stream()
                    .filter(t -> t.getLanguage() == Language.RU.getId()) // или нужный id
                    .findFirst()
                    .orElse(dto.getTranslations().get(0));
            fullName = ruTranslation.getFullName() != null ? ruTranslation.getFullName() : "";
        }

        Person person = Person.builder()
                .birthYear(dto.getBirthYear())
                .deathYear(dto.getDeathYear())
                .normalizedName(fullName.toLowerCase(Locale.ROOT))
                .birthDate(dto.getBirthDate())
                .deathDate(dto.getDeathDate())
                .arrestDate(dto.getArrestDate())
                .sentenceDate(dto.getSentenceDate())
                .rehabilitationDate(dto.getRehabilitationDate())
                .build();

        if (photo != null && !photo.isEmpty()) {
            person.setImageName(savePhoto(photo));
        }

        if (dto.getTranslations() != null) {
            List<PersonTranslation> translations = new ArrayList<>();
            for (PersonCreationRequest.TranslationRequest t : dto.getTranslations()) {
                translations.add(PersonTranslation.builder()
                        .language(t.getLanguage())
                        .fullName(t.getFullName())
                        .birthPlace(t.getBirthPlace())
                        .deathPlace(t.getDeathPlace())
                        .region(t.getRegion())
                        .district(t.getDistrict())
                        .occupation(t.getOccupation())
                        .charge(t.getCharge())
                        .sentence(t.getSentence())
                        .biography(t.getBiography())
                        .person(person)
                        .build());
            }
            person.setTranslations(translations);
        }

        personRepository.save(person);

        document.getPersons().add(person);
        documentRepository.save(document);

        return new ConfirmResponse(
                person.getId(),
                document.getId(),
                person.getImageName(),
                person.getStatusAsEnum().name()
        );
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
