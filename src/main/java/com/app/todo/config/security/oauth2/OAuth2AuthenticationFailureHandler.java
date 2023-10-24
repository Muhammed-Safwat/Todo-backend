package com.app.todo.config.security.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        var targetUrl = UriComponentsBuilder.fromUriString("http://localhost:4200/auth/social/")
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString();
        System.out.println(exception.getLocalizedMessage());
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
