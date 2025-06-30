package com.bus.ticketingSystem.service;

import com.bus.ticketingSystem.entity.Ticket;
import com.bus.ticketingSystem.service.interfaces.QRCodeGeneratorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class QRCodeGeneratorServiceImpl implements QRCodeGeneratorService {
    @Override
    public String generateQRCodeBase64(Ticket ticket) {
        byte[] qrBytes = new byte[0];
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            String ticketJson = convertTicketToJson(ticket);
            BitMatrix bitMatrix = qrCodeWriter.encode(ticketJson, BarcodeFormat.QR_CODE, 200, 200);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            qrBytes = outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }

        return Base64.encodeBase64String(qrBytes);
    }

    private String convertTicketToJson(Ticket ticket) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.writeValueAsString(ticket);
        } catch (IOException e) {
            System.err.println("Error converting ticket to JSON: " + e.getMessage());
            return null;
        }
    }
}
