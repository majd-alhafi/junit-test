package com.example.unit_test.services;

import com.example.unit_test.dto.UserRequest;
import com.example.unit_test.dto.UserResponse;
import com.example.unit_test.models.User;
import com.example.unit_test.repositories.UserRepository;
import com.example.unit_test.utils.DuplicateResourceException;
import com.example.unit_test.utils.MissingCredentialsException;
import com.example.unit_test.utils.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import org.mockito.internal.matchers.Any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("Get User By userName")
    void getUserByUserName() {
        User user = createDummyUser();
        when(userRepository.findUserByUserName(anyString())).thenReturn(user);
        UserResponse userResponse = userService.getUserByUserName("dummy");
        User findedUser = mapToUser(userResponse);
        assertEquals(findedUser,user);
    }
    @Test
    @DisplayName("Get unexisting User")
    void getUserByUserNameDoesNotExists() {
        when(userRepository.findUserByUserName(anyString())).thenReturn(null);
        assertThrows(UserNotFoundException.class,() -> userService.getUserByUserName("dummy"));
    }

    @Test
    @DisplayName("Create User with valid credentials")
    void createUser() {
        /*
        User createdUser = createDummyUser();
        when(userRepository.save(any(User.class))).thenReturn(createdUser);
        UserRequest userRequest = mapToUserRequest(createdUser);
        */
        // this lines actually make no sense because userService.createUser is return void.
        // so that we can make a userRequest directly and pass it to the service
        // then verify if the method does not throw any exceptions (because if there is no exceptions that means the test is passed
        // and verify if the method userRepository.save() will be invoked
        UserRequest userRequest = createDummyUserRequestWithFullInformation();
        assertDoesNotThrow(() -> userService.createUser(userRequest));
        verify(userRepository).save(any(User.class));
    }
    @Test
    @DisplayName("Create User without email/user name")
    void createUserWithoutEmailAndUserName(){
        UserRequest userRequest = createDummyUserRequestWithoutEmailAndUserName();
        assertThrows(MissingCredentialsException.class,() -> userService.createUser(userRequest));
    }

    @Test
    @DisplayName("Create User with already existing email")
    void createUserWithAlreadyExistingEmail(){
        UserRequest userRequest = createDummyUserRequestWithAlreadyExistingEmail();
        when(userRepository.findUserByEmail(userRequest.getEmail())).thenReturn(new User());
        assertThrows(DuplicateResourceException.class,() -> userService.createUser(userRequest));
    }

    @Test
    @DisplayName("Create User with already existing username")
    void createUserWithAlreadyExistingUserName(){
        UserRequest userRequest = createDummyUserRequestWithAlreadyExistingUserName();
        when(userRepository.findUserByUserName(userRequest.getUserName())).thenReturn(new User());
        assertThrows(DuplicateResourceException.class,() -> userService.createUser(userRequest));
    }

    private UserRequest createDummyUserRequestWithoutEmailAndUserName() {
        return UserRequest.builder()
                .firstName("Dummy")
                .lastName("Dummy")
                .phoneNumber("054328712")
                .build();
    }
    private UserRequest createDummyUserRequestWithAlreadyExistingEmail() {
        return UserRequest.builder()
                .email("already.exists@gmail.com")
                .firstName("Dummy")
                .lastName("Dummy")
                .phoneNumber("054328712")
                .build();
    }
    private UserRequest createDummyUserRequestWithAlreadyExistingUserName() {
        return UserRequest.builder()
                .userName("already.exists")
                .firstName("Dummy")
                .lastName("Dummy")
                .phoneNumber("054328712")
                .build();
    }

    private UserRequest createDummyUserRequestWithFullInformation() {
        return UserRequest.builder()
                .userName("dummyUser")
                .email("dummy@dummy.com")
                .firstName("Dummy")
                .lastName("Dummy")
                .phoneNumber("054328712")
                .build();
    }

    private UserRequest mapToUserRequest(User createdUser) {
        return UserRequest.builder()
                .userName(createdUser.getUserName())
                .email(createdUser.getEmail())
                .firstName(createdUser.getFirstName())
                .lastName(createdUser.getLastName())
                .phoneNumber(createdUser.getPhoneNumber())
                .build();
    }


    private User mapToUser(UserResponse userResponse) {
        return User.builder()
                .id(userResponse.getId())
                .userName(userResponse.getUserName())
                .email(userResponse.getEmail())
                .firstName(userResponse.getFirstName())
                .lastName(userResponse.getLastName())
                .phoneNumber(userResponse.getPhoneNumber())
                .build();
    }

    private User createDummyUser() {
        return User.builder()
                .id("Dummy")
                .userName("dummyUser")
                .email("dummy@dummy.com")
                .firstName("Dummy")
                .lastName("Dummy")
                .phoneNumber("054328712")
                .build();
    }
}