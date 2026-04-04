package kg.rubicon.my_app.controller;

import kg.rubicon.my_app.dto.PersonCreationRequest;
import kg.rubicon.my_app.dto.UploadResult;
import kg.rubicon.my_app.model.dto.ConfirmResponse;
import kg.rubicon.my_app.service.ConfirmService;
import kg.rubicon.my_app.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
public class PersonController {

    private final ConfirmService confirmService;
    private final UploadService uploadService;

    @PostMapping
    public ResponseEntity<ConfirmResponse> confirm(
            @RequestBody PersonCreationRequest request,
            @RequestParam(value = "photo", required = false) MultipartFile photo) throws Exception {

        return ResponseEntity.ok(confirmService.confirm(request, photo));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty())
            throw new IllegalArgumentException("File is empty");

        String name = file.getOriginalFilename();
        if (name == null || (!name.endsWith(".txt") && !name.endsWith(".md") && !name.endsWith(".pdf")))
            throw new IllegalArgumentException("Only .txt, .md and .pdf allowed");

        try {
            return ResponseEntity.ok(uploadService.upload(file));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
