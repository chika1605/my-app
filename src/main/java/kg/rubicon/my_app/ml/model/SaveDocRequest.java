package kg.rubicon.my_app.ml.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaveDocRequest {

    private Long personId;
    private Long documentId;
    private String filename;
    private String text;

}
