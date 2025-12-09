package com.microtech.smartshop.config;

import com.microtech.smartshop.entity.User;
import com.microtech.smartshop.enums.UserRole;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class SecurityUtils {

    private static final String USER_SESSION_KEY = "LOGGED_USER";


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


    public static void requireAdmin(HttpSession session) {
        User user = getCurrentUser(session);
        if (user.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé. Privilèges ADMIN requis.");
        }
    }




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



}
