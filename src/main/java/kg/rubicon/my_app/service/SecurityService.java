package kg.rubicon.my_app.service;

import kg.rubicon.my_app.util.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import kg.rubicon.my_app.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class SecurityService {

    public Long getCurrentAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return user.getId();
        }

        throw new UnauthorizedException("Unexpected principal type: " + principal.getClass());
    }
}
