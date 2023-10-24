package com.app.todo.filter;

import com.app.todo.config.security.SecurityUserDetailsService;
import com.app.todo.utils.JwtTokenUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final SecurityUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenUtils jwtTokenUtils, SecurityUserDetailsService userDetailsService) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, JwtException {
        final String authHeader = request.getHeader("Authentication");
        final String jwt;
        final String userEmail;
        String id = request.getParameter("id");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            jwt = authHeader.substring(7);
            boolean isTokenNotValid = false;
            isTokenNotValid = jwtTokenUtils.isTokenExpired(jwt);
            userEmail = jwtTokenUtils.extractUsername(jwt);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = this.userDetailsService.loadUserByUsername(userEmail);
                if (!isTokenNotValid) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),
                            user.getAuthorities(), user.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            response.setStatus(401);
            response.setContentType("application/json");
            JSONObject resObject = new JSONObject();
            resObject.appendField("error", "Token is expired");
            resObject.appendField("ok", false);
            resObject.appendField("status", 401);
            response.getWriter().write(resObject.toJSONString());
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().endsWith("auth/login") ||
                request.getServletPath().endsWith("auth/register") ||
                request.getServletPath().endsWith("auth/confirm") ||
                request.getServletPath().endsWith("/refresh-token");
    }
}

