package com.app.todo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class CustomerDto {

    @NotNull(message = "Username is required.")
    @Email(message = "Please provide a valid email address.")
    private String username;

    @NotNull(message = "Password is required.")
    @NotBlank(message = "Password cannot be blank.")
    private String password;

    @NotNull(message = "First name is required.")
    @NotBlank(message = "First name cannot be blank.")
    private String firstName;

    @NotNull(message = "Last name is required.")
    @NotBlank(message = "Last name cannot be blank.")
    private String lastName;

    public CustomerDto() {
    }

    public CustomerDto(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
