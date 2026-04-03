package kg.rubicon.my_app.service;

import jakarta.persistence.EntityNotFoundException;
import kg.rubicon.my_app.service.MlServiceClient;
import kg.rubicon.my_app.model.Document;
import kg.rubicon.my_app.repository.DocumentRepository;
import kg.rubicon.my_app.repository.PersonRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final DocumentRepository documentRepository;
    private final PersonRepository personRepository;
    private final MlServiceClient mlServiceClient;

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    @Transactional
    public Document upload(MultipartFile file, Long personId) throws IOException {

        var person = personRepository.findById(personId)
                .orElseThrow(() -> new EntityNotFoundException("Person not found: " + personId));

        // оригинальное имя сохраняем
        String originalName = file.getOriginalFilename();

        // на диске храним как UUID + расширение
        String ext = getExtension(originalName);
        String fileName = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

        Path dir = Paths.get(uploadDir);
        Files.createDirectories(dir);
        Path filePath = dir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String fullText = new String(file.getBytes(), StandardCharsets.UTF_8);

        Document document = Document.builder()
                .originalName(originalName)   // delo_baytemirova.txt
                .fileName(fileName)           // uuid.txt
                .filePath(filePath.toString())
                .fullText(fullText)
                .uploadedAt(LocalDateTime.now())
                .person(person)
                .build();
        documentRepository.save(document);

        mlServiceClient.saveDoc(new MlServiceClient.SaveDocRequest(
                personId,
                document.getId(),
                originalName,   // ML-сервису отдаём оригинальное имя
                fullText,
                "ru"
        ));

        return document;
    }

    // хелпер для расширения
    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}