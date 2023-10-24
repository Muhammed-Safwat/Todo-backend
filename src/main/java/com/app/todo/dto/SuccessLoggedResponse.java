package com.app.todo.dto;

import com.app.todo.modules.Customer;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Date;

import static com.app.todo.utils.AppConstants.ACCESS_EXPATRIATION_TIME;

@Setter
@Getter
@Data
@Builder
@AllArgsConstructor
public class SuccessLoggedResponse {

    private String username;
    private String firstName;
    private String userId;
    private String accessToken;
    private String refreshToken;
    private Date expirationDate;


    public static SuccessResponseHandler getSuccessfulResponse(Customer user, String message, String accessToken, String refreshToken) {
        SuccessLoggedResponse sucResponse = SuccessLoggedResponse.builder()
                .username(user.getUsername())
                .userId(user.getId().toString())
                .firstName(user.getFirstName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expirationDate(new Date(System.currentTimeMillis() + ACCESS_EXPATRIATION_TIME))
                .build();
        return SuccessResponseHandler.builder()
                .body(sucResponse)
                .message(message)
                .ok(true)
                .status(HttpStatus.OK.value())
                .build();
    }

    public static SuccessResponseHandler SuccessRegisterResponse(Customer user, String message) {
        SuccessLoggedResponse sucResponse = SuccessLoggedResponse.builder()
                .username(user.getUsername())
                .userId(null)
                .firstName(null)
                .accessToken(null)
                .refreshToken(null)
                .expirationDate(null)
                .build();
        return SuccessResponseHandler.builder()
                .body(sucResponse)
                .message(message)
                .ok(true)
                .status(HttpStatus.OK.value())
                .build();
    }
}
