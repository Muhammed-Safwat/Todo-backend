package com.app.todo.service;

import com.app.todo.modules.ConfirmationToken;
import com.app.todo.modules.User;

public interface ConfirmationTokenService {

    ConfirmationToken saveConfirmationToken(User user);

    ConfirmationToken getConfirmationToken(String token) ;

    int confirmToken(String token);

}
