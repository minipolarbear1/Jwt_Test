package com.example.jwttest.domain.dto;

import lombok.Data;

@Data
public class ReqUser {
    private String email;
    private String password;
}
