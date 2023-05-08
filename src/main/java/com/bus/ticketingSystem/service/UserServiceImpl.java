package com.bus.ticketingSystem.service;

import com.bus.ticketingSystem.DTO.UserDTO;
import com.bus.ticketingSystem.entity.ConfirmationToken;
import com.bus.ticketingSystem.entity.User;
import com.bus.ticketingSystem.exception.EntityNotFoundException;
import com.bus.ticketingSystem.exception.ErrorResponse;
import com.bus.ticketingSystem.repository.ConfirmationTokenRepository;
import com.bus.ticketingSystem.repository.UserRepository;
import com.bus.ticketingSystem.service.interfaces.BookingService;
import com.bus.ticketingSystem.service.interfaces.TicketService;
import com.bus.ticketingSystem.service.interfaces.UserService;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Override
    public User getUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        return unwrapUser(user, id);
    }

    @Override
    public User getUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return unwrapUser(user, 404L);
    }

    @Override
    @Transactional
    public ResponseEntity<?> createUser(UserDTO userDTO) {
        if (userRepository.existsByEmailOrUsername(userDTO.getEmail(), userDTO.getUsername())) {
            return new ResponseEntity<>(new ErrorResponse(Arrays.asList("User already exist")), HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.save(buildUser(userDTO));

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);

        sendEmail(userDTO, confirmationToken);

        System.out.println("Confirmation Token: " + confirmationToken.getConfirmationToken());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public long getUserId(String username) {
        return unwrapUser(userRepository.findByUsername(username), 404L).getId();
    }

    @Override
    @Transactional
    public ResponseEntity<?> confirmEmail(String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userRepository.findByEmailIgnoreCase(token.getUser().getEmail());
            user.setEnabled(true);
            userRepository.save(user);
            return ResponseEntity.ok("Email verified successfully!");
        }

        return ResponseEntity.badRequest().body("Error: Couldn't verify email");
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User updateUser(UserDTO userDTO) {
        User user = unwrapUser(userRepository.findById(userDTO.getId()), userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEnabled(Boolean.parseBoolean(userDTO.getEnabled()));

        if (userDTO.getRole().equals("USER")) {
            user.setRole(User.Role.USER);
        } else {
            user.setRole(User.Role.ADMIN);
        }

        return user;
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        confirmationTokenRepository.deleteAllInBatch(confirmationTokenRepository.findByUserId(id));
        userRepository.deleteById(id);
    }

    private void sendEmail(UserDTO user, ConfirmationToken token) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new jakarta.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("tomasnikolov12@gmail.com", "znlhjkslesrqgpdo");
                        }
                    });

            Transport.send(createConfirmationEmail(user, token, session));

            System.out.println("Sent message successfully....");
        } catch (MessagingException e) {
            System.out.println("Error in send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Message createConfirmationEmail(UserDTO userDTO, ConfirmationToken confirmationToken, Session session) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("tomasnikolov12@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userDTO.getEmail()));
        message.setSubject("Confirm Your Account Registration");
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("Dear " + userDTO.getFirstName() + " " + userDTO.getLastName() + ",\n" +
                "\n" +
                "Thank you for registering with our service. We are excited to have you on board and look forward to serving you.\n" +
                "\n" +
                "Before we can activate your account, we need to confirm your email address. Please click on the link below to confirm your registration:\n" +
                "\n" +
                userDTO.getConfirmationUrl() + confirmationToken.getConfirmationToken() + "\n" +
                "\n" +
                "If you did not register for our service, please ignore this email.\n" +
                "\n" +
                "Thank you for choosing our service.\n" +
                "\n" +
                "Best regards,\n" +
                "Bus Ticketing Company");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        message.setContent(multipart);

        return message;
    }

    private User buildUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEnabled(false);
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        user.setRole(User.Role.USER);

        return user;
    }

    static User unwrapUser(Optional<User> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(id, User.class);
    }
}
