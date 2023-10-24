package com.app.todo.service;

import com.app.todo.Repository.CustomerRepository;
import com.app.todo.Repository.RoleRepository;
import com.app.todo.config.security.AuthProvider;
import com.app.todo.config.security.AuthenticationProviderService;
import com.app.todo.dto.AuthenticationReq;
import com.app.todo.dto.CustomerDto;
import com.app.todo.dto.FailureResponseHandler;
import com.app.todo.modules.ConfirmationToken;
import com.app.todo.modules.Customer;
import com.app.todo.modules.Role;
import com.app.todo.modules.User;
import com.app.todo.utils.JwtTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.app.todo.dto.SuccessLoggedResponse.SuccessRegisterResponse;
import static com.app.todo.dto.SuccessLoggedResponse.getSuccessfulResponse;
import static com.app.todo.utils.AppConstants.EMAIL_CONFORMATION_PREFIX;
import static com.app.todo.utils.AppConstants.LOCAL_PROVIDER_ID;

@Service
@Log
public class CustomerServiceImp implements CustomerService{

    private CustomerDto customerDto;

    private CustomerRepository customerRepository;

    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    private ConfirmationTokenServiceImp confirmationTokenServiceImp;

    private AuthenticationProviderService authenticationProviderService;

    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    @Lazy
    public CustomerServiceImp(CustomerDto customerDto, CustomerRepository customerRepository,
                              RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                              ConfirmationTokenServiceImp confirmationTokenServiceImp,
                              AuthenticationProviderService authenticationProviderService,
                              JwtTokenUtils jwtTokenUtils) {
        this.customerDto = customerDto;
        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenServiceImp = confirmationTokenServiceImp;
        this.authenticationProviderService = authenticationProviderService;
        this.jwtTokenUtils = jwtTokenUtils;
    }
    @Override
    public ResponseEntity<?> registerCustomer(CustomerDto customerDto) {
        Optional<Customer> customer = customerRepository.getByUsername(customerDto.getUsername());
        if (customer.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(FailureResponseHandler.builder()
                            .error(customerDto.getUsername() + " is Exist")
                            .build());
        }
        Customer savedCustomer = createCustomer(customerDto);
        savedCustomer = customerRepository.save(savedCustomer);
        if (savedCustomer != null) {
            sendEmail(savedCustomer);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(SuccessRegisterResponse(savedCustomer, "Given user details are successfully registered"));
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(FailureResponseHandler.builder()
                        .error(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                        .build());
    }
    @Override
    public ResponseEntity<?> confirmAccount(String token) {
        ConfirmationToken confirmationToken = confirmationTokenServiceImp.getConfirmationToken(token);
        if (confirmationToken == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .body(FailureResponseHandler.builder()
                            .error("Token value is not Valid")
                            .build()
                    );
        }
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (confirmationToken.getConfirmedAt() != null || expiredAt.isBefore(LocalDateTime.now())) {
            return ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .body(FailureResponseHandler.builder()
                            .error("Token is Expired")
                            .build()
                    );
        }
        int isConfirmed = confirmationTokenServiceImp.confirmToken(token);
        if (isConfirmed > 0) {
            Customer customer = (Customer) confirmationToken.getUser();
            customer.setEnabled(true);
            customer.setLocked(false);
            Customer savedCustomer = customerRepository.save(customer);
            String accessToken = generateAccessToken(savedCustomer);
            String refreshToken = generateRefreshToken(savedCustomer);
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(getSuccessfulResponse(customer, "Account Confirmed Successfully",
                            accessToken,
                            refreshToken));
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(FailureResponseHandler.builder()
                        .error(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                        .build());
    }
    @Override
    public ResponseEntity<?> login(AuthenticationReq authenticationReq) {
        Optional<Customer> customer = customerRepository.getByUsername(authenticationReq.getUsername());
        if (customer.isPresent()) {
            try {
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        authenticationReq.getUsername(), authenticationReq.getPassword(), customer.get().getAuthorities());
                authentication = authenticationProviderService.authenticate(authentication);
                if (authentication.isAuthenticated()) {
                    String accessToken = generateAccessToken(customer.get());
                    String refreshToken = generateRefreshToken(customer.get());
                    return ResponseEntity
                            .status(HttpStatus.ACCEPTED)
                            .body(getSuccessfulResponse(customer.get(), "Logged in Successfully",
                                    accessToken, refreshToken));
                }
            } catch (Exception ex) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(FailureResponseHandler.builder()
                                .error(ex.getMessage())
                                .build());
            }
        }
        return ResponseEntity
                .badRequest()
                .body(FailureResponseHandler.builder()
                        .ok(false)
                        .error("This is " + authenticationReq.getUsername() + " email not found")
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build());
    }
    @Override
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String authHeader = request.getHeader("RefreshToken");
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(FailureResponseHandler.builder()
                            .error("Token is not valid")
                            .build()
                    );
        }
        refreshToken = authHeader.substring(7);
        if (!jwtTokenUtils.isTokenExpired(refreshToken)) {
            userEmail = jwtTokenUtils.extractUsername(refreshToken);
            if (userEmail != null) {
                var user = this.customerRepository.getByUsername(userEmail).orElseThrow();
                String newAccessToken = generateAccessToken(user);
                String newRefreshToken = generateRefreshToken(user);
                return ResponseEntity
                        .status(HttpStatus.ACCEPTED)
                        .body(getSuccessfulResponse(user, "Token Refreshed", newAccessToken, newRefreshToken));
            }
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(FailureResponseHandler
                        .builder()
                        .error(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                        .build());
    }

    private String generateRefreshToken(User user) {
        return jwtTokenUtils.generateRefreshToken(user, generateExtraClaims((user)));
    }

    private String generateAccessToken(User user) {
        return jwtTokenUtils.generateAccessToken(user, generateExtraClaims(user));
    }

    private Map<String, Object> generateExtraClaims(User user) {
        Map<String, Object> claims = new HashMap();
        claims.put("id", user.getId().toString());
        return claims;
    }

    private Customer createCustomer(CustomerDto customerDto) {
        Optional<Role> customerRole = roleRepository.getRoleByName("CUSTOMER");
        HashSet<Role> roles = new HashSet<>();
        roles.add(customerRole.get());
        String securePassword = passwordEncoder.encode(customerDto.getPassword());
        User user = User.builder().username(customerDto.getUsername())
                .password(securePassword)
                .roles(roles)
                .provider(AuthProvider.local)
                .providerId(LOCAL_PROVIDER_ID)
                .build();
        return new Customer(customerDto.getFirstName(), customerDto.getLastName(), new Date(), new HashSet<>(), user);
    }

    private void sendEmail(Customer customer) {
        ConfirmationToken token = confirmationTokenServiceImp.saveConfirmationToken(customer);
        String link = EMAIL_CONFORMATION_PREFIX + token.getToken();
        log.info(token.getToken());
        log.info(link);
        EmailServiceImp emailServiceImp = new EmailServiceImp();
        // send email for User
        /*
            synchronized (emailService){
                emailService.sendMail(savedCustomer, token);
             }
        */
    }
}
