package com.sebastian.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sebastian.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}
