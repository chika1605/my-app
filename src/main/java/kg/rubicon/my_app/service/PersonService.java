package kg.rubicon.my_app.service;

import kg.rubicon.my_app.dto.PersonDto;
import kg.rubicon.my_app.mapper.PersonMapper;
import kg.rubicon.my_app.ml.MlService;
import kg.rubicon.my_app.ml.model.SaveDocRequest;
import kg.rubicon.my_app.model.Document;
import kg.rubicon.my_app.model.Person;
import kg.rubicon.my_app.model.PersonStatus;
import kg.rubicon.my_app.repository.DocumentRepository;
import kg.rubicon.my_app.repository.PersonRepository;
import kg.rubicon.my_app.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final DocumentRepository documentRepository;
    private final MlService mlService;

    @Transactional
    public PersonDto approve(Long id, boolean approved) {
        Person person = personRepository.findById(id).orElse(null);
        if (person == null) {
            throw new ResourceNotFoundException("Person with ID: %d not found".formatted(id));
        }

        if (approved) {
            personRepository.personUpdateStatusById(id, PersonStatus.VERIFIED.getId());
            List<Document> documents = person.getDocumentsManyToMany();

            Document document = documents.get(0);
            Path path = Paths.get(System.getProperty("user.dir"))
                    .resolve("uploads")
                    .resolve("files")
                    .resolve(document.getFileName());

            String content = null;
            try {
                content = Files.readString(path, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            SaveDocRequest req = SaveDocRequest.builder()
                    .personId(person.getId())
                    .documentId(document.getId())
                    .filename(document.getFileName())
                    .text(content)
                    .build();
            mlService.saveDoc(req);
            return personMapper.toDto(person, person.getPhotoUrl(), documents.stream().map(Document::getFileName).toList());
        } else {

            if (person.getDocumentsManyToMany() != null) {
                for (Document doc : person.getDocumentsManyToMany()) {
                    if (doc.getPersonsManyToMany() == null || doc.getPersonsManyToMany().size() <= 1) {
                        documentRepository.delete(doc);
                    }
                }
            }

            personRepository.delete(person);
            return null;
        }

    }
}
