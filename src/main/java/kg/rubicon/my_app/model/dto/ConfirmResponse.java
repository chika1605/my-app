package kg.rubicon.my_app.model.dto;

public record ConfirmResponse(
        Long personId,
        Long documentId,
        String imageName,
        String status
) {}
