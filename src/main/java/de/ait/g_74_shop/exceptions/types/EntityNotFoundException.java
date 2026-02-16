package de.ait.g_74_shop.exceptions.types;

// ств неперевіряємий Exception

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Class<?> entityType, Long id) {
        // Прописуємо універсальний конструктор який буде генерувати повідомлення
        // не важливо від якого класа прийде інфа
        super(String.format("%s with id %d not found", entityType.getSimpleName(), id));
    }
}
