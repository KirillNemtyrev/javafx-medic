package com.project.medic.dto;

public class SignUpDTO {
    private String email;
    private String username;
    private String name;
    private String password;

    public SignUpDTO(String email, String username, String name, String password) {
        this.email = email;
        this.username = username;
        this.name = name;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
