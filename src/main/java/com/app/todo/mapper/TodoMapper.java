package com.app.todo.mapper;

import com.app.todo.dto.TodoDto;
import com.app.todo.modules.Todo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TodoMapper {

    Todo fromDto(TodoDto todoDto);

    @Mapping(source = "customer.id", target = "customerId")
    TodoDto fromTodo(Todo todo);

}

