package com.revature.dto;

import javax.annotation.*;
import javax.validation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto
{

    private Long id;
    
    private String username;
    @NonNull(message = "Password should not be empty")
    private String password;
}