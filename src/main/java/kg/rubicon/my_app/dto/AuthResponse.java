package kg.rubicon.my_app.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String username,
        String role
) {}