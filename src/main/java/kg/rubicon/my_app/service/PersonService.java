package kg.rubicon.my_app.service;

import kg.rubicon.my_app.dto.*;
import kg.rubicon.my_app.mapper.PersonMapper;
import kg.rubicon.my_app.ml.MlService;
import kg.rubicon.my_app.ml.model.SaveDocRequest;
import kg.rubicon.my_app.model.Document;
import kg.rubicon.my_app.model.Language;
import kg.rubicon.my_app.model.Person;
import kg.rubicon.my_app.model.PersonStatus;
import kg.rubicon.my_app.repository.DocumentRepository;
import kg.rubicon.my_app.repository.PersonRepository;
import kg.rubicon.my_app.util.UploadProperties;
import kg.rubicon.my_app.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final DocumentRepository documentRepository;
    private final MlService mlService;
    private final UploadProperties properties;

    @Transactional
    public PersonDto approve(Long id, boolean approved) {
        Person person = personRepository.findByIdWithDetails(id,
                        List.of(PersonStatus.PENDING.getId(), PersonStatus.VERIFIED.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Person with ID: %d not found".formatted(id)));

        if (approved) {
            // меняем через объект — Hibernate сам сделает UPDATE
            person.setStatus(PersonStatus.VERIFIED.getId());

            List<Document> documents = person.getDocuments();
            if (documents == null || documents.isEmpty()) {
                throw new ResourceNotFoundException("No documents found for person: " + id);
            }

            Document document = documents.get(0);
            String content = document.getExtractedText();
            if (content == null || content.isBlank()) {
                try {
                    Path path = Paths.get(System.getProperty("user.dir"))
                            .resolve(properties.getDir())
                            .resolve(properties.getFolders().get("files"))
                            .resolve(document.getFileName());
                    content = Files.readString(path, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read document file", e);
                }
            }

            SaveDocRequest req = SaveDocRequest.builder()
                    .personId(person.getId())
                    .documentId(document.getId())
                    .filename(document.getFileName())
                    .text(content)
                    .build();
            mlService.saveDoc(req);

            return personMapper.toDto(person, person.getImageName(),
                    documents.stream().map(Document::getFileName).toList());

        } else {
            if (person.getDocuments() != null) {
                for (Document doc : person.getDocuments()) {
                    if (doc.getPersons() == null || doc.getPersons().size() <= 1) {
                        documentRepository.delete(doc);
                    }
                }
            }
            personRepository.delete(person);
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Page<PersonSummaryDto> getAll(PersonFilterRequest filter) {
        List<Short> allowedStatuses = resolveAllowedStatuses();

        String name = filter.getName();
        String namePart1 = null;
        String namePart2 = null;
        if (name != null && name.contains(" ")) {
            String[] parts = name.trim().split("\\s+");
            namePart1 = parts[0];
            namePart2 = parts.length > 1 ? parts[1] : null;
        }

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());

        return personRepository.findSummaryWithFilters(
                Language.RU.getId(),
                Language.KY.getId(),
                Language.EN.getId(),
                Language.TR.getId(),
                name, namePart1, namePart2,
                filter.getRegion(),
                filter.getDistrict(),
                filter.getOccupation(),
                filter.getBirthYear(),
                filter.getDeathYear(),
                filter.getBirthDate(),
                filter.getDeathDate(),
                filter.getArrestDate(),
                filter.getSentenceDate(),
                filter.getRehabilitationDate(),
                allowedStatuses,
                pageable
        ).map(this::toSummaryDto);
    }

    private List<Short> resolveAllowedStatuses() {
        // получаем роль текущего пользователя через SecurityContext
        boolean isModerator = org.springframework.security.core.context
                .SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));

        if (isModerator) {
            return List.of(
                    PersonStatus.PENDING.getId(),
                    PersonStatus.VERIFIED.getId()
            );
        }
        return List.of(PersonStatus.VERIFIED.getId());
    }

    @Transactional(readOnly = true)
    public PersonDetailDto getById(Long id) {
        List<Short> allowedStatuses = resolveAllowedStatuses();

        Person person = personRepository.findByIdWithDetails(id, allowedStatuses)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + id));

        return toDetailDto(person);
    }

    private PersonDetailDto toDetailDto(Person person) {
        PersonDetailDto dto = new PersonDetailDto();
        dto.setId(person.getId());
        dto.setImageName(person.getImageName());
        dto.setBirthYear(person.getBirthYear());
        dto.setDeathYear(person.getDeathYear());
        dto.setBirthDate(person.getBirthDate());
        dto.setDeathDate(person.getDeathDate());
        dto.setArrestDate(person.getArrestDate());
        dto.setSentenceDate(person.getSentenceDate());
        dto.setRehabilitationDate(person.getRehabilitationDate());
        dto.setStatus(person.getStatus());

        Map<String, PersonDetailDto.TranslationDetail> translations = person.getTranslations()
                .stream()
                .collect(Collectors.toMap(
                        t -> t.getLanguageAsEnum().getSlug(),
                        t -> {
                            PersonDetailDto.TranslationDetail td = new PersonDetailDto.TranslationDetail();
                            td.setFullName(t.getFullName());
                            td.setBirthPlace(t.getBirthPlace());
                            td.setDeathPlace(t.getDeathPlace());
                            td.setRegion(t.getRegion());
                            td.setDistrict(t.getDistrict());
                            td.setOccupation(t.getOccupation());
                            td.setCharge(t.getCharge());
                            td.setSentence(t.getSentence());
                            td.setBiography(t.getBiography());
                            return td;
                        }
                ));
        dto.setTranslations(translations);

        List<PersonDetailDto.DocumentRef> docs = person.getDocuments()
                .stream()
                .map(d -> {
                    PersonDetailDto.DocumentRef ref = new PersonDetailDto.DocumentRef();
                    ref.setId(d.getId());
                    ref.setOriginalName(d.getOriginalName());

                    return ref;
                })
                .toList();
        dto.setDocuments(docs);

        return dto;
    }

    private PersonSummaryDto toSummaryDto(PersonSummaryProjection p) {
        PersonSummaryDto dto = new PersonSummaryDto();
        dto.setId(p.getId());
        dto.setImageName(p.getImageName());
        dto.setBirthDate(p.getBirthDate());
        dto.setDeathDate(p.getDeathDate());
        dto.setBirthYear(p.getBirthYear());
        dto.setDeathYear(p.getDeathYear());
        dto.setStatus(p.getStatus());

        Map<String, PersonSummaryDto.TranslationSummary> translations = new LinkedHashMap<>();
        translations.put("ru", buildTranslationSummary(p.getRuFullName(), p.getRuBirthPlace(), p.getRuOccupation(), p.getRuCharge()));
        translations.put("ky", buildTranslationSummary(p.getKyFullName(), p.getKyBirthPlace(), p.getKyOccupation(), p.getKyCharge()));
        translations.put("en", buildTranslationSummary(p.getEnFullName(), p.getEnBirthPlace(), p.getEnOccupation(), p.getEnCharge()));
        translations.put("tr", buildTranslationSummary(p.getTrFullName(), p.getTrBirthPlace(), p.getTrOccupation(), p.getTrCharge()));
        dto.setTranslations(translations);

        return dto;
    }

    private PersonSummaryDto.TranslationSummary buildTranslationSummary(
            String fullName, String birthPlace, String occupation, String charge) {
        PersonSummaryDto.TranslationSummary ts = new PersonSummaryDto.TranslationSummary();
        ts.setFullName(fullName);
        ts.setBirthPlace(birthPlace);
        ts.setOccupation(occupation);
        ts.setCharge(charge);
        return ts;
    }

}
