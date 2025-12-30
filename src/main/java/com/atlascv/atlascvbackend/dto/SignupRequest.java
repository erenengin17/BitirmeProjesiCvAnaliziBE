package com.atlascv.atlascvbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SignupRequest {

    @NotBlank
    public String fullName;

    @Email
    @NotBlank
    public String email;

    @NotBlank
    public String password;
}