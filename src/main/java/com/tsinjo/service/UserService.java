package com.tsinjo.service;


import com.tsinjo.model.User;

//This is the methode to find the user by jwt token
public interface UserService {
    public User findUserByJwtToken (String jwt) throws Exception;

    public User findUserByEmail(String email) throws Exception;

}
