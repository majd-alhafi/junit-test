package com.example.unit_test.services;

import com.example.unit_test.dto.UserRequest;
import com.example.unit_test.dto.UserResponse;
import com.example.unit_test.models.User;
import com.example.unit_test.repositories.UserRepository;
import static com.example.unit_test.utils.Constants.*;
import com.example.unit_test.utils.MissingCredentialsException;
import com.example.unit_test.utils.DuplicateResourceException;
import com.example.unit_test.utils.UserNotFoundException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public UserResponse getUserByUserName(String userName) throws UserNotFoundException {
        User user = userRepository.findUserByUserName(userName);
        if (user == null) {
            throw new UserNotFoundException("User with username " + userName + " not found");
        }
        return mapUserToUserResponse(user);
    }

    private UserResponse mapUserToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
    private User mapUserRequestToUser(UserRequest userRequest) {
        return User.builder()
                .userName(userRequest.getUserName())
                .email(userRequest.getEmail())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .phoneNumber(userRequest.getPhoneNumber())
                .build();
    }

    public void createUser(UserRequest userRequest) throws DuplicateResourceException, MissingCredentialsException {
        String userName = userRequest.getUserName();
        String email = userRequest.getEmail();
        if (StringUtils.isBlank(userName) && StringUtils.isBlank(email)) {
            throw new MissingCredentialsException(MISSING_CREDENTIALS_EXCEPTION_MESSAGE);
        }

        if (StringUtils.isNotBlank(userName) && (userRepository.findUserByUserName(userName)!= null)) {
            throw new DuplicateResourceException(USERNAME_ALREADY_EXISTS_EXCEPTION_MESSAGE);
        }

        else if (StringUtils.isNotBlank(email) && (userRepository.findUserByEmail(email)!= null)) {
            throw new DuplicateResourceException(EMAIL_ALREADY_EXISTS_EXCEPTION_MESSAGE);
        }
        User user = mapUserRequestToUser(userRequest);
        userRepository.save(user);
    }
}
