package kg.rubicon.my_app.controller;

import kg.rubicon.my_app.dto.PersonDto;
import kg.rubicon.my_app.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/moderator")
@RequiredArgsConstructor
public class ModeratorController {

    private final PersonService personService;

    @PutMapping("/persons/{id}")
    public ResponseEntity<PersonDto> createPerson(@RequestParam(value = "approved", defaultValue = "false") boolean approved,
                                                  @PathVariable Long id) {
        PersonDto confirmedPerson = personService.approve(id, approved);

        return ResponseEntity.ok(confirmedPerson);
    }

}
