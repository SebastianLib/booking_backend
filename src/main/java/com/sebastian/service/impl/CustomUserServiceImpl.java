package com.sebastian.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sebastian.domain.USER_ROLE;
import com.sebastian.model.User;
import com.sebastian.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
  
            User user = userRepository.findByEmail(username);
            if(user != null){
                return buildUserDetails(user.getEmail(), user.getPassword(), user.getRole());
            }
    throw new UsernameNotFoundException("user not found");
}

private UserDetails buildUserDetails(String email, String password, USER_ROLE role) {
    if (role == null) role = USER_ROLE.ROLE_CUSTOMER;

    List<GrantedAuthority> authorityList = new ArrayList<>();
    authorityList.add(new SimpleGrantedAuthority("ROLE_" + role));

    return new org.springframework.security.core.userdetails.User(email, password, authorityList);
}
}