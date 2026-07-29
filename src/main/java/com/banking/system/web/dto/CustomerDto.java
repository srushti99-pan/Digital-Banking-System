package com.banking.system.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private UserDto user;
}
