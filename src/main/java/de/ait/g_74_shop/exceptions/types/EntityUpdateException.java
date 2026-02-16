package de.ait.g_74_shop.exceptions.types;

// ств неперевіряємий Exception

public class EntityUpdateException extends RuntimeException {
    public EntityUpdateException(String message) {
        super(message);
    }
}