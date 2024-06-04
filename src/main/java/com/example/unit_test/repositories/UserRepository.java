package com.example.unit_test.repositories;

import com.example.unit_test.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,String> {
    User findUserByUserName(String userName);

    User findUserByEmail(String email);
}
