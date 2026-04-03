package kg.rubicon.my_app.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Language {

    RU((short) 1, "ru"),
    KY((short) 2, "ky"),
    EN((short) 3, "en"),
    TR((short) 4, "tr");


    private final short id;
    private final String slug;

    public static Language getFromId(short id) {
        for (Language lang : values()) {
            if (lang.id == id) {
                return lang;
            }
        }
        throw new IllegalArgumentException("Unknown Language id: " + id);
    }

}
