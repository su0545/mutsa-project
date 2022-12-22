package com.example.gitlabproject.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter

public class UserJoinRequest {
    private String username;
    private String password;
}
