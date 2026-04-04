package kg.rubicon.my_app.service;

import kg.rubicon.my_app.dto.*;
import kg.rubicon.my_app.model.RefreshToken;
import kg.rubicon.my_app.model.Role;
import kg.rubicon.my_app.model.User;
import kg.rubicon.my_app.repository.UserRepository;
import kg.rubicon.my_app.security.JwtService;
import kg.rubicon.my_app.util.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.email())) {
            throw new ConflictException("Username already taken");
        }
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        return new RegisterResponse(user.getUsername(), user.getRole().name());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        User user = userRepository.findByUsername(request.username()).orElseThrow();
        return buildAuthResponse(user);
    }

    @Transactional
    public RefreshResponse refresh(RefreshRequest request) {
        RefreshToken oldToken = refreshTokenService.validateRefreshToken(request.refreshToken());
        User user = oldToken.getUser();
        refreshTokenService.revokeToken(oldToken.getToken());
        String newAccessToken = jwtService.generateAccessToken(user);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
        return new RefreshResponse(newAccessToken, newRefreshToken.getToken());
    }

    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenService.revokeToken(request.refreshToken());
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken.getToken(), user.getUsername(), user.getRole().name());
    }
}