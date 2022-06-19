package ru.yandex.practicum.kanban.tasks;
import ru.yandex.practicum.kanban.manager.StatusTask;

import java.util.Objects;

public class Task {
    protected int id;
    protected String title;
    protected String description;
    protected StatusTask status;

    public Task(String title, String description, StatusTask status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
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

    @Override
    public String toString() {
        return "ru.yandex.practicum.kanban.tasks.Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id
                && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
    }
}