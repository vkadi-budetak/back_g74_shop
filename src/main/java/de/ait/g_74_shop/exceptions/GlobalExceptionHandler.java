package de.ait.g_74_shop.exceptions;

import de.ait.g_74_shop.exceptions.types.EntityNotFoundException;
import de.ait.g_74_shop.exceptions.types.EntityUpdateException;
import de.ait.g_74_shop.exceptions.types.FileUploadException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

// глобальний клас для обробки помилок

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ResponseEntity - це обєкт куди ми кладемо інфу для клієнта
    @ExceptionHandler(EntityNotFoundException.class)
    // ця анотація гооврить що якщо проект зловить помилку він повинен визивати handleException
    public ResponseEntity<String> handleException(EntityNotFoundException e) {
        String message = e.getMessage();
        logger.warn(message);
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleException(NullPointerException e) {
        String message = e.getMessage();
        logger.error(message);
        return new ResponseEntity<>(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR); // повертаємо статус внутрушньої помилки сервера
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<String>> handleException(ConstraintViolationException e) {
        List<String> messages = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        messages.forEach(logger::warn);
        return new ResponseEntity<>(messages, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityUpdateException.class)
    public ResponseEntity<String> handleException(EntityUpdateException e) {
        String message = e.getMessage();
        logger.warn(message, e);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<String> handleException(FileUploadException e) {
        String message = e.getMessage();
        logger.warn(message, e);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
