package kg.rubicon.my_app.controller;

import jakarta.persistence.EntityNotFoundException;
import kg.rubicon.my_app.model.Document;
import kg.rubicon.my_app.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("personId") Long personId) {

        List<Map<String, Object>> results = new ArrayList<>();
        List<Map<String, Object>> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            String name = file.getOriginalFilename();

            if (file.isEmpty()) {
                errors.add(Map.of("file", name != null ? name : "unknown", "error", "File is empty"));
                continue;
            }
            if (name == null || (!name.endsWith(".txt") && !name.endsWith(".md"))) {
                errors.add(Map.of("file", name != null ? name : "unknown", "error", "Only .txt and .md allowed"));
                continue;
            }

            try {
                Document doc = uploadService.upload(file, personId);
                results.add(Map.of(
                        "documentId", doc.getId(),
                        "fileName",   doc.getFileName(),
                        "uploadedAt", doc.getUploadedAt().toString()
                ));
            } catch (EntityNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            } catch (Exception e) {
                errors.add(Map.of("file", name != null ? name : "unknown", "error", e.getMessage()));
            }
        }

        return ResponseEntity.ok(Map.of("uploaded", results, "errors", errors));
    }
}