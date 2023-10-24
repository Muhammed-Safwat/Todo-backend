package com.app.todo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
           /* CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if(null != csrfToken.getHeaderName()) {
                response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
                System.out.println("********************************");
                System.out.println(csrfToken.getToken());
                System.out.println("********************************");
            }*/
        filterChain.doFilter(request, response);
    }

}