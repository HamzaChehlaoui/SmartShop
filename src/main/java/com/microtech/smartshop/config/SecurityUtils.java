package com.microtech.smartshop.config;

import com.microtech.smartshop.entity.User;
import com.microtech.smartshop.enums.UserRole;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class SecurityUtils {

    private static final String USER_SESSION_KEY = "LOGGED_USER";

    /**
     * Get the currently logged-in user from session
     */
    public static User getCurrentUser(HttpSession session) {
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session invalide");
        }
        User user = (User) session.getAttribute(USER_SESSION_KEY);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Non authentifié. Veuillez vous connecter.");
        }
        return user;
    }

    /**
     * Check if current user is ADMIN
     */
    public static void requireAdmin(HttpSession session) {
        User user = getCurrentUser(session);
        if (user.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé. Privilèges ADMIN requis.");
        }
    }

    /**
     * Check if current user is CLIENT
     */
    public static void requireClient(HttpSession session) {
        User user = getCurrentUser(session);
        if (user.getRole() != UserRole.CLIENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé. Privilèges CLIENT requis.");
        }
    }

    /**
     * Check if current user can access client data
     * ADMIN: can access any client
     * CLIENT: can only access their own data
     */
    public static void checkClientAccess(HttpSession session, Long clientId) {
        User user = getCurrentUser(session);

        if (user.getRole() == UserRole.ADMIN) {
            return;
        }

        if (user.getRole() == UserRole.CLIENT) {
            if (user.getClient() == null || !user.getClient().getId().equals(clientId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Accès refusé. Vous ne pouvez accéder qu'à vos propres données.");
            }
        }
    }

    /**
     * Get client ID for current CLIENT user
     */
    public static Long getCurrentClientId(HttpSession session) {
        User user = getCurrentUser(session);
        if (user.getRole() != UserRole.CLIENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Utilisateur n'est pas un CLIENT");
        }
        if (user.getClient() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Client non associé à l'utilisateur");
        }
        return user.getClient().getId();
    }

    /**
     * Check if user is ADMIN or the client owner
     */
    public static boolean isAdminOrOwner(HttpSession session, Long clientId) {
        User user = getCurrentUser(session);

        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }

        if (user.getRole() == UserRole.CLIENT && user.getClient() != null) {
            return user.getClient().getId().equals(clientId);
        }

        return false;
    }
}
