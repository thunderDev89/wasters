package com.acemen.android.wasters.event;

/**
 * Created by Audrik ! on 02/07/2016.
 */
public interface IEvent {
    int getId();
    void setHeader(String header);
    void setMessage(String message);
    String getHeader();
    String getMessage();
}
