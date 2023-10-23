package com.picpayproject.dtos;

import java.math.BigDecimal;

import com.picpayproject.domain.user.UserType;

public record UserDTO(String firstName, String lastName, String document, BigDecimal balance, String email,
                String password, UserType userType) {

}
