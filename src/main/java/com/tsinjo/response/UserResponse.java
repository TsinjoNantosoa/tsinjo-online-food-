package com.tsinjo.response;

import com.tsinjo.model.Address;
import com.tsinjo.model.USER_ROLE;

import java.util.List;

public record UserResponse(Long id, String fullName, String email, USER_ROLE role, List<Address> addresses) {
}
