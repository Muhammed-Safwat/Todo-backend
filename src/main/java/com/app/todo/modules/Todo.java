package com.app.todo.modules;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    private String status;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer")
    private Customer customer;

    public Todo() {
    }

    public Todo(String title, String status, String description, Customer customer) {
        this.title = title;
        this.status = status;
        this.description = description;
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return id == todo.id && Objects.equals(title, todo.title) && Objects.equals(customer, todo.customer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, customer);
    }
}
