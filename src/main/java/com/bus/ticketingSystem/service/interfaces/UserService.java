package com.bus.ticketingSystem.service.interfaces;

import com.bus.ticketingSystem.DTO.UserDTO;
import com.bus.ticketingSystem.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    User getUser(Long id);

    User getUser(String username);

    ResponseEntity<?> createUser(UserDTO user);

    long getUserId(String username);

    ResponseEntity<?> confirmEmail(String confirmationToken);

    List<User> getUsers();

    User updateUser(UserDTO userDTO);

    void deleteUser(long id);

    void updateProfile(UserDTO userDTO);
}
