package com.app.todo.config.security.oauth2;

import com.app.todo.Repository.CustomerRepository;
import com.app.todo.Repository.RoleRepository;
import com.app.todo.Repository.UserRepository;
import com.app.todo.config.security.AuthProvider;
import com.app.todo.config.security.oauth2.user.OAuth2UserInfo;
import com.app.todo.config.security.oauth2.user.OAuth2UserInfoFactory;
import com.app.todo.handler.OAuth2AuthenticationProcessingException;
import com.app.todo.modules.Customer;
import com.app.todo.modules.Role;
import com.app.todo.modules.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

@Service
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        System.out.println("Load User Run ");
        System.out.println(oAuth2User != null);
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) throws OAuth2AuthenticationProcessingException {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.getUserByUsername(oAuth2UserInfo.getEmail());
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }
        return user;
    }


    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();
        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        System.out.println(user.getProvider() + " iiiiiiiiiiiiiidd");
        user.setProviderId(oAuth2UserRequest.getClientRegistration().getClientId());
        System.out.println(user.getProviderId() + " iiiiiiiiiiiiiidd");
        user.setUsername(oAuth2UserInfo.getEmail());
        Optional<Role> customerRole = roleRepository.getRoleByName("CUSTOMER");
        HashSet<Role> roles = new HashSet<>();
        roles.add(customerRole.get());
        user.setRoles(roles);
        user.setAttributes(oAuth2UserInfo.getAttributes());
        Customer customer = new Customer(oAuth2UserInfo.getName(), oAuth2UserInfo.getName(), new Date(), new HashSet<>(), user);
        return customerRepository.save(customer);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        return userRepository.save(existingUser);
    }
}
