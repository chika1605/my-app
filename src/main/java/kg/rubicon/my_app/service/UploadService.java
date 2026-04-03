package kg.rubicon.my_app.service;

import kg.rubicon.my_app.ml.MlService;
import kg.rubicon.my_app.ml.dto.GetInfoResponse;
import kg.rubicon.my_app.model.Document;
import kg.rubicon.my_app.model.DocumentStatus;
import kg.rubicon.my_app.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final DocumentRepository documentRepository;
    private final MlService mlService;

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    @Transactional
    public UploadResult upload(MultipartFile file) throws IOException {

        String originalName = file.getOriginalFilename();
        String ext = getExtension(originalName);
        String fileName = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

        Path dir = Paths.get(uploadDir);
        Files.createDirectories(dir);
        Path filePath = dir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String fullText = new String(file.getBytes(), StandardCharsets.UTF_8);

        Document document = Document.builder()
                .originalName(originalName)
                .fileName(fileName)
                .filePath(filePath.toString())
                .fullText(fullText)
                .uploadedAt(LocalDateTime.now())
                .status(DocumentStatus.NULL)
                .person(null)
                .build();
        documentRepository.save(document);

        GetInfoResponse mlResponse = mlService.getInfo(fullText);

        return new UploadResult(document, mlResponse);
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public record UploadResult(Document document, GetInfoResponse mlResponse) {}
}
