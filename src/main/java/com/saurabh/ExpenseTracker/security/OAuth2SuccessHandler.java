package com.saurabh.ExpenseTracker.security;

import com.saurabh.ExpenseTracker.entity.User;
import com.saurabh.ExpenseTracker.repository.UserRepository;
import com.saurabh.ExpenseTracker.service.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public OAuth2SuccessHandler(
            UserRepository userRepository,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String providerId = oauth2User.getAttribute("sub");

        // Check if Google account already exists
        User user = userRepository
                .findByProviderAndProviderId("GOOGLE", providerId)
                .orElseGet(() -> createGoogleUser(email, name, providerId));

        // Generate our application's JWT
        String token = jwtService.generateToken(user.getUsername());

        // Redirect to React with JWT
        String redirectUrl =
                "http://localhost:5173/oauth2/success?token=" + token;

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private User createGoogleUser(
            String email,
            String name,
            String providerId
    ) {

        // Generate a unique username
        String username = name != null
                ? name.replaceAll("\\s+", "").toLowerCase()
                : "googleuser";

        username = username + "_" + UUID.randomUUID()
                .toString()
                .substring(0, 8);

        User user = new User(
                username,
                email,
                "GOOGLE",
                providerId,
                "USER"
        );

        return userRepository.save(user);
    }
}