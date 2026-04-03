package kg.rubicon.my_app.controller;

import kg.rubicon.my_app.model.dto.ConfirmResponse;
import kg.rubicon.my_app.service.ConfirmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConfirmController {

    private final ConfirmService confirmService;

    @PostMapping("/confirm")
    public ResponseEntity<ConfirmResponse> confirm(
            @RequestParam("documentId") Long documentId,
            @RequestParam("personData") String personData,
            @RequestParam(value = "photo", required = false) MultipartFile photo) throws Exception {

        return ResponseEntity.ok(confirmService.confirm(documentId, personData, photo));
    }
}
