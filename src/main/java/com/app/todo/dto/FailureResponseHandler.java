package com.app.todo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FailureResponseHandler {

    private Object error;
    private boolean ok;
    private int status;

}
