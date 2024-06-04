package com.example.unit_test.controllers;

import com.example.unit_test.dto.UserRequest;
import com.example.unit_test.dto.UserResponse;
import com.example.unit_test.services.UserService;
import com.example.unit_test.utils.MissingCredentialsException;
import com.example.unit_test.utils.DuplicateResourceException;
import com.example.unit_test.utils.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    @GetMapping("/{userName}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserByUserName(@PathVariable("userName") String userName) throws UserNotFoundException {
        return userService.getUserByUserName(userName);
    };
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody UserRequest UserRequest) throws DuplicateResourceException, MissingCredentialsException {
        userService.createUser(UserRequest);
    }
}
