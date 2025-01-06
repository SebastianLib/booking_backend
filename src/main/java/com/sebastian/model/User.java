package com.sebastian.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sebastian.domain.USER_ROLE;

import com.sebastian.domain.USER_STATUS;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = true)
    private String email;

    private String username;

    private String surname;

    private String phone;

    private String prefix;

    @Column(nullable = true)
    private String image;

    @Size(min = 6, max = 6, message = "Verification code must be exactly 6 characters long")
    private String verificationCode;

    @Enumerated(EnumType.STRING)
    private USER_ROLE role = USER_ROLE.ROLE_CUSTOMER;

    @Enumerated(EnumType.STRING)
    private USER_STATUS status = USER_STATUS.WAITING_FOR_VERIFICATION;

}
