package com.app.todo.modules;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
import java.util.Set;

@Entity
@Setter
@Getter
public class Customer extends User {

    private String firstName;

    private String lastName;

    @CreatedDate
    private Date createdDate;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private Set<Todo> todoList;

    public Customer() {
    }

    public Customer(String firstName, String lastName, Date date, Set<Todo> todoList, User user) {
        super(user.getUsername(), user.getPassword(), true, false, user.getProvider(), user.getProviderId(), user.getRoles(), user.getAttributes());
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdDate = date;
        this.todoList = todoList;
    }

}
