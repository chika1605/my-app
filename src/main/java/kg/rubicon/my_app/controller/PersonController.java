package kg.rubicon.my_app.controller;

import kg.rubicon.my_app.chat.ChatService;
import kg.rubicon.my_app.dto.*;
import kg.rubicon.my_app.model.dto.ConfirmResponse;
import kg.rubicon.my_app.service.ConfirmService;
import kg.rubicon.my_app.service.PersonService;
import kg.rubicon.my_app.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final PersonService personService;

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

    @GetMapping
    public ResponseEntity<Page<PersonSummaryDto>> getAll(@ModelAttribute PersonFilterRequest filter) {
        return ResponseEntity.ok(personService.getAll(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDetailDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(personService.getById(id));
    }

    @GetMapping("/{id}/audio")
    public ResponseEntity<byte[]> getVoice(@PathVariable Long id, @RequestParam(defaultValue = "ru") String language) {
        return ResponseEntity.ok(personService.getVoice(id, language));
    }

}
