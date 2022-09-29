package ru.yandex.practicum.kanban.manager;

import java.time.LocalDateTime;

public class InvalidTimeException extends RuntimeException {
    private final LocalDateTime time;
    public LocalDateTime getTime(){return time;}
    public InvalidTimeException(String message, LocalDateTime startTime){

        super(message);
        time = startTime;
    }
}
