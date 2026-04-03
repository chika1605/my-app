package kg.rubicon.my_app.mapper;

import kg.rubicon.my_app.dto.PersonCreationRequest;
import kg.rubicon.my_app.model.Person;
import kg.rubicon.my_app.model.PersonStatus;
import kg.rubicon.my_app.model.PersonTranslation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersonMapper {

    public Person toEntity(PersonCreationRequest request) {
        if (request == null) {
            return null;
        }

        Person person = Person.builder()
                .birthYear(request.getBirthYear())
                .deathYear(request.getDeathYear())
                .birthDate(request.getBirthDate())
                .deathDate(request.getDeathDate())
                .arrestDate(request.getArrestDate())
                .sentenceDate(request.getSentenceDate())
                .rehabilitationDate(request.getRehabilitationDate())
                .status(PersonStatus.PENDING.getId())
                .build();

        List<PersonTranslation> translations = null;
        if (request.getTranslations() != null && !request.getTranslations().isEmpty()) {
            translations = request.getTranslations().stream()
                    .map(t ->
                            PersonTranslation.builder()
                            .language(t.getLanguage())
                            .fullName(t.getFullName())
                            .normalizedName(t.getNormalizedName())
                            .birthPlace(t.getBirthPlace())
                            .deathPlace(t.getDeathPlace())
                            .region(t.getRegion())
                            .district(t.getDistrict())
                            .occupation(t.getOccupation())
                            .charge(t.getCharge())
                            .sentence(t.getSentence())
                            .biography(t.getBiography())
                            .person(person)
                            .build())
                    .collect(Collectors.toList());
            person.setTranslations(translations);
        }

        return person;
    }

}
