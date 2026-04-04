package kg.rubicon.my_app.controller;

import kg.rubicon.my_app.dto.UploadResult;
import kg.rubicon.my_app.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));

        String name = file.getOriginalFilename();
        if (name == null || (!name.endsWith(".txt") && !name.endsWith(".md") && !name.endsWith(".pdf")))
            return ResponseEntity.badRequest().body(Map.of("error", "Only .txt, .md and .pdf allowed"));

        try {
            UploadResult result = uploadService.upload(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}