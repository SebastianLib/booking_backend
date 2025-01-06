package com.sebastian.controller;

import com.sebastian.config.JwtProvider;
import com.sebastian.domain.USER_ROLE;
import com.sebastian.model.User;
import com.sebastian.repository.UserRepository;
import com.sebastian.request.LoginRequest;
import com.sebastian.request.SignupRequest;
import com.sebastian.response.AuthResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@Valid @RequestBody SignupRequest req) {

        System.out.println("SignupRequest received: " + req);

        if (userRepository.findByEmail(req.getEmail()) != null) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        // Create and save new user
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(USER_ROLE.ROLE_CUSTOMER);
        userRepository.save(user);

        // Generate JWT token
        String jwt = jwtProvider.generateToken(buildUserDetails(user.getEmail(), user.getPassword(), user.getRole()));

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("register success");
        res.setRole(USER_ROLE.ROLE_CUSTOMER);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest req) {
        // Authenticate user
        Authentication authentication = authenticate(req.getEmail(), req.getPassword());

        // Generate JWT token
        String jwt = jwtProvider.generateToken(authentication);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("login success");
        res.setRole(USER_ROLE.ROLE_CUSTOMER);

        return ResponseEntity.ok(res);
    }

    private Authentication authenticate(String email, String password) {
        // Load user from the database
        UserDetails userDetails = loadUserByUsername(email);

        // Validate password
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password...");
        }

        // Return authenticated token
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return buildUserDetails(user.getEmail(), user.getPassword(), user.getRole());
        }
        throw new UsernameNotFoundException("User not found");
    }

    private UserDetails buildUserDetails(String email, String password, USER_ROLE role) {
        if (role == null) role = USER_ROLE.ROLE_CUSTOMER;

        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_" + role));

        return new org.springframework.security.core.userdetails.User(email, password, authorityList);
    }
}
