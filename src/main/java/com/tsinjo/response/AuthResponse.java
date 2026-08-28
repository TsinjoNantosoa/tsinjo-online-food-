package com.tsinjo.response;

import com.tsinjo.model.USER_ROLE;

public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private UserResponse user;
    private String message;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    /** Compatibility aliases for existing clients. */
    public String getJwt() { return token; }
    public void setJwt(String jwt) { this.token = jwt; }
    public USER_ROLE getRole() { return user == null ? null : user.role(); }
    public void setRole(USER_ROLE ignored) { }
}
