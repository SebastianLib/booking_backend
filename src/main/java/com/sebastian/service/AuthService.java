package com.sebastian.service;

import com.sebastian.model.User;
import com.sebastian.request.SignupRequest;

public interface AuthService {

    String createUser(SignupRequest req);

    public User findUserByJwtToken(String jwt) throws Exception;

    void checkIfAdmin(String jwt) throws Exception;
}
