package com.oneisnstep.mcpserver.dto;

import lombok.Data;

@Data
public class User {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zip;
}
