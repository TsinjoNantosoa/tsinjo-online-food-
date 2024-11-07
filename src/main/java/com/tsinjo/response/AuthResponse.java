package com.tsinjo.response;

import com.tsinjo.model.USER_ROLE;
import lombok.Data;

@Data
public class AuthResponse {

    private String  jwt;

    private String message;

    private USER_ROLE role;

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setRole(USER_ROLE role) {
        this.role = role;
    }

    public USER_ROLE getRole() {
        return role;
    }
}


