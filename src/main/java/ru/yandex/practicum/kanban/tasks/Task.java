package ru.yandex.practicum.kanban.tasks;

import ru.yandex.practicum.kanban.manager.StatusTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    protected int id;
    protected String title;
    protected String description;
    protected StatusTask status;
    protected TypeTasks type;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String title, String description, StatusTask status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.type = TypeTasks.TASK;
    }

    public Task(int id, String title, StatusTask status, String description) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.type = TypeTasks.TASK;
    }


    public Task(String title, String description) {

        this.title = title;
        this.description = description;
        this.type = TypeTasks.TASK;

    }

    public Task(
            LocalDateTime startTime,
            Duration duration,
            String title,
            String description
    ) {

        this.startTime = startTime;
        this.duration = duration;
        this.title = title;
        this.description = description;
        this.type = TypeTasks.TASK;
    }

    public Task(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = TypeTasks.TASK;
    }

    public Task(LocalDateTime startTime, Duration duration, int id, String title, StatusTask status, String description) {
        this.startTime = startTime;
        this.duration = duration;
        this.id = id;
        this.status = status;
        this.description = description;
        this.title = title;
        this.type = TypeTasks.TASK;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StatusTask getStatus() {
        return status;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TypeTasks getType() {
        return type;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        } else {
            return startTime.plus(duration);
        }
    }

    @Override
    public String toString() {
        return startTime +
                ", " + duration +
                ", " +
                " id " + id +
                ", " + type +
                ", " + status +
                ", " + title +
                ", " + description;
    }

    public int getId() {

        return id;
    }

    public String toCSVDescription() {

        return startTime + "," + duration +
                "," + id +
                "," + type +
                "," + title +
                "," + status +
                "," + description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id
                && title.equals(task.title)
                && description.equals(task.description)
                && status == task.status
                && type == task.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, type);
    }
}
