package com.acemen.android.wasters.entity;

/**
 * Created by Audrik ! on 24/08/2016.
 */
public class ResponseEntity {
    private boolean success;
    private String errorMessage;

    public ResponseEntity(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
