package com.bus.ticketingSystem.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String confirmationUrl;
    private String enabled;
    private String role;
}
