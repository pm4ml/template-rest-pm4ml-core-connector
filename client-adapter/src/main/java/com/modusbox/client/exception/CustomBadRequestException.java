package com.modusbox.client.exception;

/**
 * Exception representing a bad request from the client - for example, if the request contains bad data.
 *
 */
public class CustomBadRequestException extends Exception {

    public static final String DEFAULT_MESSAGE = "bad request";

    public CustomBadRequestException() {
        super(DEFAULT_MESSAGE);
    }

    public CustomBadRequestException(String message) {
        super(message);
    }

    public CustomBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomBadRequestException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    protected CustomBadRequestException(String message, Throwable cause, boolean enableSuppression,
                                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
