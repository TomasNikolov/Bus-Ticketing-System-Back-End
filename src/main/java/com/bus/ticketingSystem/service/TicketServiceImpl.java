package com.bus.ticketingSystem.service;

import com.bus.ticketingSystem.DTO.TicketDTO;
import com.bus.ticketingSystem.entity.Ticket;
import com.bus.ticketingSystem.entity.User;
import com.bus.ticketingSystem.exception.EntityNotFoundException;
import com.bus.ticketingSystem.repository.TicketRepository;
import com.bus.ticketingSystem.service.interfaces.BusService;
import com.bus.ticketingSystem.service.interfaces.QRCodeGeneratorService;
import com.bus.ticketingSystem.service.interfaces.TicketService;
import com.bus.ticketingSystem.service.interfaces.UserService;
import com.lowagie.text.DocumentException;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.annotation.Resource;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.persistence.criteria.Path;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;

import org.xhtmlrenderer.pdf.ITextRenderer;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TicketServiceImpl implements TicketService {
    private final TemplateEngine templateEngine;
    private TicketRepository ticketRepository;
    private BusService busService;
    private UserService userService;
    private QRCodeGeneratorService qrCodeGeneratorService;

    public TicketServiceImpl(TicketRepository ticketRepository, BusService busService, UserService userService,
                             TemplateEngine templateEngine, QRCodeGeneratorService qrCodeGeneratorService) {
        this.ticketRepository = ticketRepository;
        this.busService = busService;
        this.userService = userService;
        this.templateEngine = templateEngine;
        this.qrCodeGeneratorService = qrCodeGeneratorService;
    }

    @Override
    @Transactional
    public List<TicketDTO> reserveTickets(List<TicketDTO> tickets) {
        List<Ticket> ticketsForSave = new ArrayList<>();
        Map<Integer, Boolean> busSeats = generateSeatsMap(tickets.get(0));

        for (TicketDTO item : tickets) {
            ticketsForSave.add(createTicket(item));
        }

        List<Ticket> alreadySavedTickets = ticketRepository.saveAllAndFlush(generateSeatNumbers(ticketsForSave, busSeats));

        if (alreadySavedTickets.size() == tickets.size()) {
            busService.updateBusSeats(tickets.get(0).getBusId(), tickets.size());
        }

        return this.parseTickets(alreadySavedTickets);
    }

    @Override
    @Transactional
    public List<Ticket> payTickets(long userId) {
        List<Ticket> tickets = ticketRepository.findUnpaidTicketsByUserId(userId);
        for (Ticket ticket : tickets) {
            ticket.setPayed(true);
        }
        return ticketRepository.saveAllAndFlush(tickets);
    }

    @Override
    public List<Ticket> getUnpaidTicketsByUserId(long userId) {
        return ticketRepository.findUnpaidTicketsByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteTicket(long id) {
        ticketRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteTickets(Set<Long> ids) {
        List<Ticket> tickets = ticketRepository.findAllById(ids);
        Map<Long, Integer> ticketsByBus = initBusMap(tickets);
        for (Ticket ticket : tickets) {
            ticketsByBus.put(ticket.getBus().getId(), ticketsByBus.get(ticket.getBus().getId()) + 1);
        }

        busService.updateBusSeatsAfterTicketDeletion(ticketsByBus);
        ticketRepository.deleteAllById(ids);
    }

    @Override
    public void sendTicket(long id) {
        Ticket ticket = unwrapTicket(ticketRepository.findById(id));
        generatePDFAndSendMail(ticket, ticket.getUser());
    }

    @Override
    @Transactional
    public void deleteTicketsByUserId(long userId) {
        List<Ticket> tickets = ticketRepository.findTicketsByUserId(userId);
        Map<Long, Integer> ticketsByBus = initBusMap(tickets);
        for (Ticket ticket : tickets) {
            ticketsByBus.put(ticket.getBus().getId(), ticketsByBus.get(ticket.getBus().getId()) + 1);
        }

        busService.updateBusSeatsAfterTicketDeletion(ticketsByBus);
        ticketRepository.deleteAllInBatch(tickets);
    }

    @Override
    public void deleteTicketsByBusId(long busId) {
        ticketRepository.deleteAllInBatch(ticketRepository.findTicketsByBusId(busId));
    }

    private List<TicketDTO> parseTickets(List<Ticket> alreadySavedTickets) {
        List<TicketDTO> ticketDTOs = new ArrayList<>();
        for (Ticket ticket : alreadySavedTickets) {
            TicketDTO ticketDTO = new TicketDTO();
            ticketDTO.setPassengerName(ticket.getPassengerName());
            ticketDTO.setStartDestination(ticket.getStartDestination());
            ticketDTO.setEndDestination(ticket.getEndDestination());
            ticketDTO.setPrice(ticket.getPrice());
            ticketDTO.setSeatNumber(ticket.getSeatNumber());
            ticketDTO.setId(ticket.getId());
            ticketDTOs.add(ticketDTO);
        }

        return ticketDTOs;
    }

    private static Map<Long, Integer> initBusMap(List<Ticket> tickets) {
        Map<Long, Integer> result = new HashMap<>();
        for (Ticket ticket : tickets) {
            result.put(ticket.getBus().getId(), 0);
        }

        return result;
    }

    private void generatePDFAndSendMail(Ticket ticket, User user) {
        int random = (int) (Math.random() * 90) + 10;
        String nameGenerator = user.getFirstName() + "_" + user.getLastName() + "_ticket_" + random + ".pdf";
        try {
//            String qrCodePath = qrCodeGeneratorService.generateQRCode(ticket);
            String qrCode = qrCodeGeneratorService.generateQRCodeBase64(ticket);
            createPdf(ticket, user, nameGenerator, qrCode);
            sendEmail(user, nameGenerator);
        } catch (DocumentException | IOException e) {
            System.out.println("Error in generatePDFAndSendMail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendEmail(User user, String fileName) {
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

            Transport.send(createTicketEmail(user, fileName, session));

            System.out.println("Sent message successfully....");
        } catch (MessagingException e) {
            System.out.println("Error in send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Message createTicketEmail(User user, String fileName, Session session) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("tomasnikolov12@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
        message.setSubject("BUS TICKET");
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("Dear " + user.getFirstName() + " " + user.getLastName() + ",\n" +
                "\n" +
                "We are pleased to confirm your booking for the upcoming bus journey with our company. Your bus ticket is attached to this email.\n" +
                "\n" +
                "Please arrive at the pickup point at least 15 minutes before the scheduled departure time. Our team will be there to assist you with any queries or concerns you may have.\n" +
                "\n" +
                "We hope you have a pleasant journey with us.\n" +
                "\n" +
                "Best regards,\n" +
                "\n" +
                "Bus Ticketing Company");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        messageBodyPart = new MimeBodyPart();

        DataSource source = new FileDataSource("tickets/" + fileName);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(fileName);
        multipart.addBodyPart(messageBodyPart);
        message.setContent(multipart);

        return message;
    }

    public void createPdf(Ticket ticket, User user, String fileName, String qrCodePath) throws DocumentException, IOException {
        Context context = new Context();
        context.setVariable("name", user.getFirstName() + " " + user.getLastName());
        context.setVariable("date", ticket.getIssueDate());
        context.setVariable("From", ticket.getStartDestination());
        context.setVariable("to", ticket.getEndDestination());
        context.setVariable("ticketNumber", ticket.getId());
        context.setVariable("seatNumber", ticket.getSeatNumber());
        context.setVariable("price", ticket.getPrice());
        context.setVariable("qrCodePath", qrCodePath);

        String processHTML = templateEngine.process("ticket_template", context);

        try {
            OutputStream out = new FileOutputStream("tickets/" + fileName);
            ITextRenderer ir = new ITextRenderer();
            ir.setDocumentFromString(processHTML);
            ir.layout();
            ir.createPDF(out, false);
            ir.finishPDF();
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("Cannot create PDF");
            e.printStackTrace();
        }
    }

    private Ticket createTicket(TicketDTO item) {
        Ticket ticket = new Ticket();
        ticket.setBus(busService.getBusById(item.getBusId()));
        ticket.setUser(userService.getUser(item.getUserId()));
        ticket.setPassengerName(item.getPassengerName());
        ticket.setStartDestination(item.getStartDestination());
        ticket.setEndDestination(item.getEndDestination());
        ticket.setIssueDate(LocalDateTime.now());
        ticket.setPrice(item.getPrice());
        ticket.setPayed(false);

        return ticket;
    }

    private Map<Integer, Boolean> generateSeatsMap(TicketDTO ticket) {
        Map<Integer, Boolean> result = new HashMap<>();
        for (int i = 1; i <= ticket.getBusCapacity(); i++) {
            result.put(i, false);
        }

        return pushReservedSeats(result, ticket.getBusId());
    }

    private Map<Integer, Boolean> pushReservedSeats(Map<Integer, Boolean> result, long busId) {
        List<Ticket> reservedTickets = ticketRepository.findTicketsByBusId(busId);
        for (Ticket ticket : reservedTickets) {
            result.put(ticket.getSeatNumber(), true);
        }

        return result;
    }

    private List<Ticket> generateSeatNumbers(List<Ticket> tickets, Map<Integer, Boolean> reservedSeats) {
        for (Ticket ticket : tickets) {
            for (Map.Entry<Integer, Boolean> entry : reservedSeats.entrySet()) {
                if (!entry.getValue()) {
                    entry.setValue(true);
                    ticket.setSeatNumber(entry.getKey());
                    break;
                }
            }
        }

        return tickets;
    }

    private static Ticket unwrapTicket(Optional<Ticket> entity) {
        if (entity.isPresent()) return entity.get();
        else
            throw new EntityNotFoundException("We apologize, but we were unable to find any tickets with this ID in our system.");
    }
}
