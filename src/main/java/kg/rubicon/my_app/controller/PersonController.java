package kg.rubicon.my_app.controller;

import jakarta.validation.Valid;
import kg.rubicon.my_app.dto.PersonCreationRequest;
import kg.rubicon.my_app.dto.PersonDto;
import kg.rubicon.my_app.service.PersonService;
import kg.rubicon.my_app.util.annatation.ImageFile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping("/persons")
    public ResponseEntity<PersonDto> createPerson(
            @RequestPart("person") @Valid PersonCreationRequest request,
            @RequestPart(value = "file", required = false) @ImageFile MultipartFile file
    ) {
        PersonDto createdPerson = personService.createPerson(request, file);

        return ResponseEntity.ok(createdPerson);
    }

}
