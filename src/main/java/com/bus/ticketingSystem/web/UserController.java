package com.bus.ticketingSystem.web;

import com.bus.ticketingSystem.DTO.UserDTO;
import com.bus.ticketingSystem.service.interfaces.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO user) {
        return userService.createUser(user);
    }

    @GetMapping("/confirm-account")
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token")String confirmationToken) {
        return userService.confirmEmail(confirmationToken);
    }
}
