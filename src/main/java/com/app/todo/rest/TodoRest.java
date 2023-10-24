package com.app.todo.rest;

import com.app.todo.dto.TodoDto;
import com.app.todo.service.TodoServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/todos")
public class TodoRest {

    private TodoServiceImp todoServiceImp;

    @Autowired
    public TodoRest(TodoServiceImp todoServiceImp) {
        this.todoServiceImp = todoServiceImp;
    }

    @GetMapping("/{id}")
    public Page<TodoDto> getTodos(@Valid @PathVariable("id") UUID id,
                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                  @RequestParam(name = "size", defaultValue = "10") int size) {
        return todoServiceImp.findTodos(id, page, size);
    }

    @PostMapping
    public ResponseEntity<?> addTodo(@Valid @RequestBody TodoDto todoDto) {
        System.out.println("addTodo( @RequestBody TodoDto todoDto , BindingResult result)");
        return todoServiceImp.addTodo(todoDto);
    }

    @PutMapping
    public ResponseEntity<?> updateTodo(@Valid @RequestBody TodoDto todoDto) {
        return todoServiceImp.updateTodo(todoDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> toggleStatus(@Valid @PathVariable("id") long id, @Valid @RequestParam("customerId") UUID customerId) {
        System.out.println(id);
        return todoServiceImp.toggleStatus(id, customerId);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTodo(@Valid @RequestParam("id") long id, @Valid @RequestParam("customerId") UUID customerId) {
        return todoServiceImp.deleteTodo(id, customerId);
    }

}
