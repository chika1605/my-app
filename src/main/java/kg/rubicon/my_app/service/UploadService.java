package kg.rubicon.my_app.service;

import kg.rubicon.my_app.dto.UploadResult;
import kg.rubicon.my_app.ml.MlService;
import kg.rubicon.my_app.ml.dto.GetInfoResponse;
import kg.rubicon.my_app.ml.dto.PersonCard;
import kg.rubicon.my_app.ml.dto.SingleResult;
import kg.rubicon.my_app.model.Document;
import kg.rubicon.my_app.model.Person;
import kg.rubicon.my_app.model.PersonStatus;
import kg.rubicon.my_app.repository.DocumentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kg.rubicon.my_app.repository.PersonRepository;
import kg.rubicon.my_app.util.UploadProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ext.javatime.DateTimeParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadService {

    private final DocumentRepository documentRepository;
    private final MlService mlServiceClient;
    private final UploadProperties properties;
    private final PersonRepository personRepository;

    @Transactional
    public UploadResult upload(MultipartFile file) throws IOException {

        String originalName = file.getOriginalFilename();
        String ext = getExtension(originalName);
        String fileName = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

        Path dir = Paths.get(properties.getDir(), properties.getFolders().get("files"));
        Files.createDirectories(dir);
        Path filePath = dir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // TODO: извлечение текста
        String extractedText = new String(file.getBytes(), StandardCharsets.UTF_8);

        Document document = Document.builder()
                .originalName(originalName)
                .fileName(fileName)
                .extractedText(extractedText)
                .uploadedAt(LocalDateTime.now())
                .build();

        document = documentRepository.save(document);

        GetInfoResponse mlResponse = mlServiceClient.getInfo(extractedText);
        if (mlResponse.type().equalsIgnoreCase("single")) {
            SingleResult result = mlResponse.result();
            return new UploadResult(mlResponse.type(), document.getId(), result, fileName);
        }
        else if (mlResponse.type().equalsIgnoreCase("plural")) {
            List<String> normalizedNames = mlResponse.normalizedNames();
            handlePlural(document, normalizedNames);
        }

        return new UploadResult(mlResponse.type(), null, null, null);
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    private void handlePlural(Document document, List<String> normalizedNames) {
        if (normalizedNames == null || normalizedNames.isEmpty()) return;

        String joined = String.join(",", normalizedNames);
        List<Person> foundPersons = personRepository.findByNormalizedNamesBidirectional(joined);

        for (Person person : foundPersons) {
            person.getDocuments().add(document);
            document.getPersons().add(person);
        }

        personRepository.saveAll(foundPersons);
    }

}
