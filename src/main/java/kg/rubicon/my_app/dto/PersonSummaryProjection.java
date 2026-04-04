package kg.rubicon.my_app.dto;

import java.time.LocalDate;

public interface PersonSummaryProjection {
    Long getId();
    String getImageName();
    LocalDate getBirthDate();
    LocalDate getDeathDate();
    Integer getBirthYear();
    Integer getDeathYear();
    short getStatus();

    // переводы — все 4 языка плоско
    String getRuFullName();
    String getRuBirthPlace();
    String getRuOccupation();
    String getRuCharge();

    String getKyFullName();
    String getKyBirthPlace();
    String getKyOccupation();
    String getKyCharge();

    String getEnFullName();
    String getEnBirthPlace();
    String getEnOccupation();
    String getEnCharge();

    String getTrFullName();
    String getTrBirthPlace();
    String getTrOccupation();
    String getTrCharge();
}
