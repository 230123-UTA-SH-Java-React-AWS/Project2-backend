package com.revature.service;

import com.revature.dto.UserDto;
import com.revature.model.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();
}