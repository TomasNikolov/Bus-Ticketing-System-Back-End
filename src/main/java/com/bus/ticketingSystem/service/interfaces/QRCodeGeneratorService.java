package com.bus.ticketingSystem.service.interfaces;

import com.bus.ticketingSystem.entity.Ticket;

public interface QRCodeGeneratorService {
    String generateQRCode(Ticket ticket);
    String generateQRCodeBase64(Ticket ticket);
}
