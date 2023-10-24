package com.app.todo.config.security;

import com.app.todo.Repository.UserRepository;
import com.app.todo.config.security.oauth2.CustomOAuth2UserService;
import com.app.todo.config.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.app.todo.config.security.oauth2.OAuth2LoginSuccessHandler;
import com.app.todo.filter.CsrfCookieFilter;
import com.app.todo.filter.JwtAuthenticationFilter;
import com.app.todo.service.CustomerServiceImp;
import com.app.todo.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomerServiceImp customerServiceImp;

    @Autowired
    private SecurityUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("*"));
        config.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // cors configuration
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()));
       /*
            CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
            requestHandler.setCsrfRequestAttributeName(null);
            http.csrf((csrf) -> csrf
                            .csrfTokenRequestHandler(requestHandler)
                            .ignoringRequestMatchers("/auth/**" , "/oauth2/**")
                            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
        */
        http.csrf((csrf) -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests((requests) ->
                requests.requestMatchers("/auth/**",
                                "/oauth2/**",
                                "/auth/confirm",
                                "/auth/register",
                                "/swagger-ui.html",
                                "/index.html")
                        .permitAll()
                        .anyRequest().permitAll());
        http.oauth2Login(oAuth -> oAuth
                .authorizationEndpoint(entry -> entry.baseUri("/oauth2/authorize"))
                .redirectionEndpoint(entry -> entry.baseUri("/oauth2/callback/*"))
                .userInfoEndpoint(info -> info.userService(customOAuth2UserService))
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler));
        http.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new JwtAuthenticationFilter(jwtTokenUtils, userDetailsService), BasicAuthenticationFilter.class);
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new SecurityUserDetailsService(userRepository);
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        return new AuthenticationProviderService();
    }

}
