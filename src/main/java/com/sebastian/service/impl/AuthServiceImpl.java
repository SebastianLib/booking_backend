package com.sebastian.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.sebastian.domain.USER_STATUS;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sebastian.config.JwtProvider;
import com.sebastian.domain.USER_ROLE;
import com.sebastian.model.User;
import com.sebastian.repository.UserRepository;
import com.sebastian.request.SignupRequest;
import com.sebastian.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public String createUser(@Valid SignupRequest req) {
        System.out.println("Email: " + req);
        User user = userRepository.findByEmail(req.getEmail());

        if(user==null){
           User createdUser = new User();
            createdUser.setEmail(req.getEmail());
            createdUser.setUsername(req.getUsername());
            createdUser.setSurname(req.getSurname());
            createdUser.setPassword(req.getPassword());
            createdUser.setPhone(req.getPhone());
            createdUser.setPrefix(req.getPrefix());
            createdUser.setImage(req.getImage());
            createdUser.setRole(USER_ROLE.valueOf(String.valueOf(req.getRole()))); // Konwersja String na USER_ROLE
            createdUser.setStatus(USER_STATUS.WAITING_FOR_VERIFICATION); // Domyślny status

            user = userRepository.save(createdUser);

        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(req.getEmail(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtProvider.generateToken(authentication);
    }

    @Override
    public User findUserByJwtToken(String jwt) throws Exception {
      String email = jwtProvider.getEmailFromJwtToken(jwt);

      User user = userRepository.findByEmail(email);
      if(user == null){
          throw new Exception("User not found");
      }
        return user;
    }

    public void checkIfAdmin(String jwt) throws Exception {
        User user = findUserByJwtToken(jwt);

        if (user.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new RuntimeException("Access denied: User is not an admin");
        }
    }
}
