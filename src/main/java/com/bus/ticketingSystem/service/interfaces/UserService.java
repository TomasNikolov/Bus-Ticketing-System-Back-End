package com.bus.ticketingSystem.service.interfaces;

import com.bus.ticketingSystem.entity.User;

public interface UserService {
    User getUser(Long id);
    User getUser(String username);
    User createUser(User user);
}
