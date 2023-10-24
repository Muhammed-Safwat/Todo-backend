package com.app.todo.service;

import com.app.todo.dto.AuthenticationReq;
import com.app.todo.dto.CustomerDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface CustomerService {

    ResponseEntity<?> registerCustomer(CustomerDto customerDto) ;

    ResponseEntity<?> confirmAccount(String token) ;

    ResponseEntity<?> login(AuthenticationReq authenticationReq)  ;

    ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
