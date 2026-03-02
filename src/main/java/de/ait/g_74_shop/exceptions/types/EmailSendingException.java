package de.ait.g_74_shop.exceptions.types;

public class EmailSendingException extends RuntimeException {

    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
