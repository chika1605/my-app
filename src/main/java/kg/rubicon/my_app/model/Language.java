package kg.rubicon.my_app.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Language {

    RU((short) 1),
    KG((short) 2),
    EN((short) 3);

    private final short id;

    public static Language getFromId(short id) {
        for (Language lang : values()) {
            if (lang.id == id) {
                return lang;
            }
        }
        throw new IllegalArgumentException("Unknown Language id: " + id);
    }

}
