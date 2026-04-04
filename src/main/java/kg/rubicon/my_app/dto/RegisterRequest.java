package kg.rubicon.my_app.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank String password
) {}