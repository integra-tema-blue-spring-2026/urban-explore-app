package cloudflight.integra.backend.controller.utils;

import org.springframework.security.core.Authentication;

public class AdminChecker {
    public static boolean isAdmin(Authentication authentication) {
        return authentication != null
            && authentication.getAuthorities() != null
            && authentication.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
