package com.example.unit_test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
