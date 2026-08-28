package com.tsinjo.controller;

import com.tsinjo.model.User;
import com.tsinjo.service.UserService;
import com.tsinjo.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping({"/profile", "/me"})
    public ResponseEntity<UserResponse> findUserByJwtToken(@RequestHeader("Authorization") String jwt) throws Exception {
        User user= userService.findUserByJwtToken(jwt);
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getFullName(), user.getEmail(),
                user.getRole(), user.getAddresses()));
    }
}
