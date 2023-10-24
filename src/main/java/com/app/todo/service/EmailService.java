package com.app.todo.service;

import com.app.todo.modules.Customer;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import static com.app.todo.utils.AppConstants.EMAIL_CONFORMATION_PREFIX;

public interface EmailService  {

    String sendMail(Customer customer, String token) ;

}
