package com.bus.ticketingSystem.web;

import com.bus.ticketingSystem.entity.User;
import com.bus.ticketingSystem.exception.ErrorResponse;
import com.bus.ticketingSystem.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    UserService userService;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {

        User result = userService.createUser(user);
        if (result == null) {
            return new ResponseEntity<>(new ErrorResponse(Arrays.asList("User already exist")),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/read")
    public ResponseEntity<String> getUser() {
        return new ResponseEntity<>(userService.getUser("test").getUsername(), HttpStatus.OK);
    }
}
