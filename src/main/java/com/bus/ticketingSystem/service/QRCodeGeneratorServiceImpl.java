package com.bus.ticketingSystem.service;

import com.bus.ticketingSystem.entity.Ticket;
import com.bus.ticketingSystem.service.interfaces.QRCodeGeneratorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class QRCodeGeneratorServiceImpl implements QRCodeGeneratorService {
    @Value("${qrcode.output.directory}")
    private String outputLocation;

    private static final String charset = "UTF-8";
    private static final String strDateFormat = "yyyyMMddhhmmss";

    @Override
    public String generateQRCode(Ticket ticket) {
        String path = "";
        try {
            String ticketJson = convertTicketToJson(ticket);
            if (ticketJson != null) {
                path = prepareOutputFileName();
                processQRCode(ticketJson, path);
            }
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("PATH: " + path);
        return path;
    }

    @Override
    public String generateQRCodeBase64(Ticket ticket) {
        byte[] qrBytes = new byte[0];
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            String ticketJson = convertTicketToJson(ticket);
            if (ticketJson != null) {
                processQRCode(ticketJson, prepareOutputFileName());
            }
            BitMatrix bitMatrix = qrCodeWriter.encode(ticketJson, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            qrBytes = outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }

        return Base64.encodeBase64String(qrBytes); // Encode to Base64
    }

    private void processQRCode(String data, String path) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(QRCodeGeneratorServiceImpl.charset),
                QRCodeGeneratorServiceImpl.charset), BarcodeFormat.QR_CODE, 400, 400);
        MatrixToImageWriter.writeToFile(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path));
    }

    private String prepareOutputFileName() {
        Date date = new Date();

        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate = dateFormat.format(date);

        StringBuilder sb = new StringBuilder();
        sb.append(outputLocation).append("\\").append("QRCode-").append(formattedDate).append(".png");

        return sb.toString();
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
