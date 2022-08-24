package ru.yandex.practicum.kanban.manager;

import java.io.IOException;

public class InvalidTimeException extends IOException {
    public InvalidTimeException(String message) {
        super(message);
    }

    public InvalidTimeException() {
    }

    public InvalidTimeException(String message, IOException cause) {
        super(message, cause);
    }
}