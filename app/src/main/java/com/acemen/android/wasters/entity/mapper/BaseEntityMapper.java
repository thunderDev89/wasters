package com.acemen.android.wasters.entity.mapper;

/**
 * Created by Audrik ! on 24/08/2016.
 */
public class BaseEntityMapper {
    private boolean success;
    private String errorMessage;

    public BaseEntityMapper(boolean success, String errorMessage) {
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
