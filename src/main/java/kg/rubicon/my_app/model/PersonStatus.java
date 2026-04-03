package kg.rubicon.my_app.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PersonStatus {

    PENDING((short) 1),
    VERIFIED((short) 2);

    private final short id;

    public static PersonStatus getFromId(short id) {
        for (PersonStatus status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown PersonStatus id: " + id);
    }
}