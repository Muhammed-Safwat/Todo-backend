package com.app.todo.service;

import com.app.todo.dto.TodoDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface TodoService {
    Page<TodoDto> findTodos(UUID customerId, int page, int size);

    ResponseEntity<?> addTodo(TodoDto todoDto);

    ResponseEntity<?> updateTodo(TodoDto todoDto);

    ResponseEntity<?> deleteTodo(long id, UUID customerId);

    ResponseEntity<?> toggleStatus(long id, UUID customerId);
}
