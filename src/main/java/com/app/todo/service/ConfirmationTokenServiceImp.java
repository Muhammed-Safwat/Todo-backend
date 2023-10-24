package com.app.todo.service;

import com.app.todo.Repository.ConfirmationTokenRepository;
import com.app.todo.modules.ConfirmationToken;
import com.app.todo.modules.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ConfirmationTokenServiceImp implements ConfirmationTokenService {

    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    public ConfirmationTokenServiceImp(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @Override
    public ConfirmationToken saveConfirmationToken(User user) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user);
        confirmationTokenRepository.save(confirmationToken);
        return confirmationToken;
    }

    @Override
    public ConfirmationToken getConfirmationToken(String token) {
        return confirmationTokenRepository.getByToken(token).orElse(null);
    }

    @Override
    public int confirmToken(String token) {
        return confirmationTokenRepository.confirmToken(token, LocalDateTime.now());
    }

}
