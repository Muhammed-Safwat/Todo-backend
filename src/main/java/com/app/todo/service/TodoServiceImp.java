package com.app.todo.service;

import com.app.todo.Repository.CustomerRepository;
import com.app.todo.Repository.TodoRepository;
import com.app.todo.dto.FailureResponseHandler;
import com.app.todo.dto.SuccessResponseHandler;
import com.app.todo.dto.TodoDto;
import com.app.todo.mapper.TodoMapper;
import com.app.todo.modules.Customer;
import com.app.todo.modules.Todo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoServiceImp implements TodoService{

    private TodoRepository todoRepository;

    private CustomerRepository customerRepository;

    private TodoMapper todoMapper;

    @Autowired
    public TodoServiceImp(TodoRepository todoRepository, CustomerRepository customerRepository, TodoMapper todoMapper) {
        this.todoRepository = todoRepository;
        this.customerRepository = customerRepository;
        this.todoMapper = todoMapper;
    }

    @Override
    public Page<TodoDto> findTodos(UUID customerId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Todo> todoList = todoRepository.findTodosByCustomerId(customerId, pageRequest);
        List<TodoDto> DtoList = todoList.getContent().stream()
                .map(todo -> todoMapper.fromTodo(todo))
                .collect(Collectors.toList());
        return new PageImpl<>(DtoList, pageRequest, todoList.getTotalElements());
    }

    @Override
    public ResponseEntity<?> addTodo(TodoDto todoDto) {
        Optional<Customer> customer = customerRepository.getById(todoDto.getCustomerId());
        if (!customer.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(FailureResponseHandler.builder()
                            .ok(false)
                            .status(HttpStatus.BAD_REQUEST.value())
                            .error("Customer Id Not Valid")
                            .build());
        }
        Todo todo = todoMapper.fromDto(todoDto);
        todo.setCustomer(customer.get());
        Todo savedTodo = this.todoRepository.save(todo);
        if (savedTodo != null) {
            todoDto.setId(savedTodo.getId());
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(SuccessResponseHandler.builder()
                            .status(HttpStatus.ACCEPTED.value())
                            .ok(true)
                            .body(todoDto)
                            .message("Todo Added")
                            .build());
        }
        return ResponseEntity
                .badRequest()
                .body(FailureResponseHandler.builder()
                        .ok(false)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .build());
    }

    @Override
    public ResponseEntity<?> updateTodo(TodoDto todoDto) {
        Optional<Todo> todo = todoRepository.findById(todoDto.getId());
        if (!todo.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(FailureResponseHandler.builder()
                            .error(HttpStatus.BAD_REQUEST.toString())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .ok(false)
                            .error("Id not Valid")
                            .build());
        }
        todo.get().setTitle(todoDto.getTitle());
        todo.get().setStatus(todoDto.getStatus());
        todo.get().setDescription(todoDto.getDescription());
        Todo savedTodo = todoRepository.save(todo.get());
        return ResponseEntity
                .ok(SuccessResponseHandler
                        .builder()
                        .ok(true)
                        .message("Todo Deleted")
                        .body(todoMapper.fromTodo(savedTodo))
                        .build());
    }

    @Override
    public ResponseEntity<?> deleteTodo(long id, UUID customerId) {
        Optional<Todo> todo = todoRepository.findTodoByIdAndCustomerId(id, customerId);
        if (!todo.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(FailureResponseHandler
                            .builder()
                            .error(HttpStatus.BAD_REQUEST.toString())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .ok(false)
                            .error("Id not Valid")
                            .build());
        }
        todoRepository.delete(todo.get());
        return ResponseEntity
                .ok(SuccessResponseHandler
                        .builder()
                        .ok(true)
                        .message("Todo Deleted")
                        .build());
    }

    @Override
    public ResponseEntity<?> toggleStatus(long id, UUID customerId) {
        Optional<Todo> optionalTodo = todoRepository.findTodoByIdAndCustomerId(id, customerId);
        if (optionalTodo.isEmpty()) {
            ResponseEntity.badRequest().body(FailureResponseHandler
                    .builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .ok(false)
                    .error("Id Not Valid")
                    .build());
        }
        Todo todo = optionalTodo.get();
        if (todo.getStatus().equals("false")) {
            todo.setStatus("true");
        } else {
            todo.setStatus("false");
        }
        Todo updatedtodo = todoRepository.save(todo);
        return ResponseEntity.ok(SuccessResponseHandler
                .builder()
                .ok(true)
                .body(todoMapper.fromTodo(updatedtodo))
                .message("Todo updated Successfully")
                .build());
    }

}
