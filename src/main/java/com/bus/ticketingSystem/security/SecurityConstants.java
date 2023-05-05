package com.bus.ticketingSystem.security;

public class SecurityConstants {
    public static final String SECRET_KEY = "bQeThWmZq4t7w!z$C&F)J@NcRfUjXn2r5u8x/A?D*G-KaPdSgVkYp3s6v9y$B&E)";
    public static final int TOKEN_EXPIRATION = 7200000; // 7200000 milliseconds = 7200 seconds = 2 hours.
    public static final String BEARER = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";
    public static final String REGISTER_PATH = "/user/register";
    public static final String BUS_PATH = "/buses";
    public static final String BOOKING_PATH = "/booking/**";
    public static final String TICKET_PATH = "/ticket/**";
    public static final String PAYMENT_PATH = "/payment";
}
