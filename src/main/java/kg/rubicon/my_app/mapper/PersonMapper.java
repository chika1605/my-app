package kg.rubicon.my_app.mapper;

import kg.rubicon.my_app.dto.PersonDto;
import kg.rubicon.my_app.model.Person;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonMapper {

    public PersonDto toDto(Person person, String imageFileName, List<String> resources) {
        if (person == null) {
            return null;
        }

        PersonDto dto = new PersonDto();
        dto.setBirthYear(person.getBirthYear());
        dto.setDeathYear(person.getDeathYear());
        dto.setBirthDate(person.getBirthDate());
        dto.setDeathDate(person.getDeathDate());
        dto.setArrestDate(person.getArrestDate());
        dto.setSentenceDate(person.getSentenceDate());
        dto.setRehabilitationDate(person.getRehabilitationDate());

        if (person.getTranslations() != null && !person.getTranslations().isEmpty()) {
            List<PersonDto.TranslationDto> translationDtos = person.getTranslations().stream()
                    .map(t -> {
                        PersonDto.TranslationDto translationDto = new PersonDto.TranslationDto();
                        translationDto.setLanguage(t.getLanguage());
                        translationDto.setFullName(t.getFullName());
                        translationDto.setBirthPlace(t.getBirthPlace());
                        translationDto.setDeathPlace(t.getDeathPlace());
                        translationDto.setRegion(t.getRegion());
                        translationDto.setDistrict(t.getDistrict());
                        translationDto.setOccupation(t.getOccupation());
                        translationDto.setCharge(t.getCharge());
                        translationDto.setSentence(t.getSentence());
                        translationDto.setBiography(t.getBiography());
                        return translationDto;
                    })
                    .toList();
            dto.setTranslations(translationDtos);
        }

        dto.setImageFileName(imageFileName);

        if (resources != null && !resources.isEmpty()) {
            List<PersonDto.ResourceInfo> resourceInfos = resources.stream()
                    .map(r -> {
                        PersonDto.ResourceInfo resourceInfo = new PersonDto.ResourceInfo();
                        resourceInfo.setFileName(r);
                        return resourceInfo;
                    })
                    .toList();
            dto.setResources(resourceInfos);
        }

        return dto;
    }
}
