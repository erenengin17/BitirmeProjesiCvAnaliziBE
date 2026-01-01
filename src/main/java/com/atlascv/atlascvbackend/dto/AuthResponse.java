package com.atlascv.atlascvbackend.dto;

public class AuthResponse {

    private String token;
    private Long id;
    private String fullName;
    private String email;

    public AuthResponse(String token, Long id, String fullName, String email) {
        this.token = token;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }

    public String getToken() { return token; }
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
}
