package api.gossip.uz.util;

import api.gossip.uz.config.CustomUserDetails;
import api.gossip.uz.enums.ProfileRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SpringSecurityUtil {

    public static CustomUserDetails getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }

    public static Integer getCurrentProfileId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails profile = (CustomUserDetails) authentication.getPrincipal();
        return profile.getId();
    }

    public static boolean hasRole(ProfileRole requiredRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().anyMatch(sga -> sga.getAuthority().equals(requiredRole.name()));
    }

    //todo
    public static Integer getCurrentUserId() {
        return Optional.ofNullable(getCurrentAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof CustomUserDetails currentUser) {
                        return currentUser.getId();
                    } else return null;
                }).orElse(null);
    }

    public static Authentication getCurrentAuthentication() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return securityContext.getAuthentication();
    }
}
