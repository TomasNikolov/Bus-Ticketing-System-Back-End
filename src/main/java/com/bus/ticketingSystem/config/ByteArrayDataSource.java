package com.bus.ticketingSystem.config;

import jakarta.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ByteArrayDataSource implements DataSource {
    private final byte[] data;
    private final String contentType;
    private final String name;

    public ByteArrayDataSource(byte[] data, String name, String contentType) {
        this.data = data;
        this.name = name;
        this.contentType = contentType;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(data);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("This DataSource is read-only");
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getName() {
        return name;
    }
}
