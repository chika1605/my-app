package kg.rubicon.my_app.service;

import kg.rubicon.my_app.ml.MlService;
import kg.rubicon.my_app.ml.dto.GetInfoResponse;
import kg.rubicon.my_app.model.Document;
import kg.rubicon.my_app.repository.DocumentRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final DocumentRepository documentRepository;
    private final MlService mlServiceClient;

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    @Transactional
    public UploadResult upload(MultipartFile file) throws IOException {

        String originalName = file.getOriginalFilename();
        String ext = getExtension(originalName);
        String fileName = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

        Path dir = Paths.get(uploadDir, "files");
        Files.createDirectories(dir);
        Path filePath = dir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String fullText = extractText(file);

        Document document = Document.builder()
                .originalName(originalName)
                .fileName(fileName)
                .filePath(filePath.toString())
                .fullText(fullText)
                .uploadedAt(LocalDateTime.now())
                .person(null)
                .build();
        documentRepository.save(document);

        GetInfoResponse mlResponse = mlServiceClient.getInfo(fullText);

        return new UploadResult(document, mlResponse);
    }

    private String extractText(MultipartFile file) throws IOException {
        String name = file.getOriginalFilename();
        if (name != null && name.toLowerCase().endsWith(".pdf")) {
            byte[] bytes = file.getBytes();
            try (PDDocument doc = Loader.loadPDF(bytes)) {
                String text = new PDFTextStripper().getText(doc).trim();
                if (!text.isBlank()) {
                    return text;
                }
            }
            // PDFBox не смог извлечь текст — отправляем в ML (OCR через OpenAI)
            return mlServiceClient.extractPdfText(bytes, name).text();
        }
        return new String(file.getBytes(), StandardCharsets.UTF_8);
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public record UploadResult(Document document, GetInfoResponse mlResponse) {}
}
