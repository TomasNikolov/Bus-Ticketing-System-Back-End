package com.bus.ticketingSystem.service.interfaces;

import com.bus.ticketingSystem.DTO.UserDTO;
import com.bus.ticketingSystem.entity.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
    User getUser(Long id);
    User getUser(String username);
    ResponseEntity<?> createUser(UserDTO user);
    long getUserId(String username);
    ResponseEntity<?> confirmEmail(String confirmationToken);
}
