package kg.rubicon.my_app.service;

import kg.rubicon.my_app.dto.PersonCreationRequest;
import kg.rubicon.my_app.dto.PersonDto;
import kg.rubicon.my_app.mapper.PersonMapper;
import kg.rubicon.my_app.model.Person;
import kg.rubicon.my_app.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    @Transactional
    public PersonDto createPerson(PersonCreationRequest request, MultipartFile file) {

        Person person = personMapper.toEntity(request);
        person = personRepository.save(person);

        //TODO: save file

        return personMapper.toDto(person, image);

    }

}
