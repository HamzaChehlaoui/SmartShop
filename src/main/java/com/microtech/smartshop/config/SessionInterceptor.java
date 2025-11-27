package com.microtech.smartshop.config;

import com.microtech.smartshop.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private static final String USER_SESSION_KEY = "LOGGED_USER";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/api/auth/") ||
                requestURI.equals("/api/products") ||
                (requestURI.startsWith("/api/products/") && request.getMethod().equals("GET")) ||
                requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(USER_SESSION_KEY) == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Non authentifié. Veuillez vous connecter.");
        }

        return true;
    }

    public static User getLoggedUser(HttpSession session) {
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session invalide");
        }
        User user = (User) session.getAttribute(USER_SESSION_KEY);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Non authentifié");
        }
        return user;
    }

    public static void setLoggedUser(HttpSession session, User user) {
        session.setAttribute(USER_SESSION_KEY, user);
    }

    public static void clearSession(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
}
