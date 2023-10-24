package com.app.todo.rest;

import com.app.todo.dto.AuthenticationReq;
import com.app.todo.dto.CustomerDto;
import com.app.todo.service.CustomerServiceImp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Log
public class AuthRestController {

    private CustomerServiceImp customerServiceImp;

    @Autowired
    public AuthRestController(CustomerServiceImp customerServiceImp) {
        this.customerServiceImp = customerServiceImp;
    }

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody CustomerDto customerDto) {
        return customerServiceImp.registerCustomer(customerDto);
    }

    @GetMapping("/confirm")
    public ResponseEntity confirmAccount(@Valid @RequestParam("token") String token) {
        return customerServiceImp.confirmAccount(token);
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody AuthenticationReq authenticationReq) {
        log.info("===> Authentication Req");
        return customerServiceImp.login(authenticationReq);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return customerServiceImp.refreshToken(request, response);
    }


}
