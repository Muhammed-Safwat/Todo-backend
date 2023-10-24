package com.app.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Getter
@Setter
public class TodoDto {

    private long id;

    @NotNull(message = "Please provide a title.")
    @NotBlank(message = "Title Cannot be blank.")
    private String title;

    @NotNull(message = "Please provide a status")
    private String status;

    private String description;

    @NotNull(message = "Customer ID cannot be blank.")
    private UUID customerId;

    public TodoDto() {
    }

    public TodoDto(String title, String status, String description) {
        this.title = title;
        this.status = status;
        this.description = description;
    }

    public TodoDto(String title, String status, String description, UUID customerId) {
        this.title = title;
        this.status = status;
        this.description = description;
        this.customerId = customerId;
    }

    public TodoDto(long id, String title, String status, String description, UUID id1) {
        this.title = title;
        this.status = status;
        this.description = description;
        this.customerId = id1;
        this.id = id;
    }

}
