package com.app.todo.config.security.oauth2;

import com.app.todo.Repository.CustomerRepository;
import com.app.todo.Repository.UserRepository;
import com.app.todo.modules.Customer;
import com.app.todo.utils.JwtTokenUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.app.todo.utils.AppConstants.CLIENT_URL;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private JwtTokenUtils jwtTokenUtils;

    private UserRepository userRepository;

    private CustomerRepository customerRepository;

    @Autowired
    public OAuth2LoginSuccessHandler(JwtTokenUtils jwtTokenUtils, UserRepository userRepository, CustomerRepository customerRepository) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    public OAuth2LoginSuccessHandler(String defaultTargetUrl, JwtTokenUtils jwtTokenUtils, UserRepository userRepository, CustomerRepository customerRepository) {
        super(defaultTargetUrl);
        this.jwtTokenUtils = jwtTokenUtils;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String baseUrl = CLIENT_URL + "/auth/social/";
        Optional<Customer> user = customerRepository.getByUsername(authentication.getName());
        String userId = user.get().getId().toString();
        String username = user.get().getUsername();
        String firstName = user.get().getFirstName();
        String exp = LocalDateTime.now().toString();
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.get().getId());
        String accessToken = jwtTokenUtils.generateAccessToken(user.get(), claims);
        String refreshToken = jwtTokenUtils.generateRefreshToken(user.get(), claims);
        String targetUrl = baseUrl + String.format("%s/%s/%s/%s/%s/%s", accessToken, refreshToken, userId, username, firstName, exp);
        System.out.println("--------------- Target Url " + targetUrl);
        return UriComponentsBuilder.fromUriString(targetUrl)
                .build().toUriString();
    }
}