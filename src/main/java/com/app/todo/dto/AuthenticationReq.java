package com.app.todo.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class AuthenticationReq {

    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    public AuthenticationReq() {
    }

    public AuthenticationReq(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
