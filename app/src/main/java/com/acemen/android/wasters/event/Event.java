package com.acemen.android.wasters.event;

/**
 * Created by Audrik ! on 22/08/2016.
 */
public class Event implements IEvent {
    public static final int DEFAULT_ID = 0;
    private String header;
    private String message;
    private int id;

    public Event(String header, String message) {
        this(header, message, DEFAULT_ID);
    }

    public Event(String header, String message, int id) {
        this.header = header;
        this.message = message;
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getHeader() {
        return header;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
