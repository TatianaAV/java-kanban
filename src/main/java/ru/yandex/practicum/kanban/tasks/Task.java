package ru.yandex.practicum.kanban.tasks;

import ru.yandex.practicum.kanban.manager.StatusTask;

import java.util.Objects;

public class Task {

    protected int id;
    protected String title;
    protected String description;
    protected StatusTask status;
    protected TypeTasks type;


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

    @Override
    public String toString() {
        return " id " + id +
                ", " + type +
                ", " + status +
                ", " + title +
                ", " + description;
    }

    public int getId() {

        return id;
    }

    public String toCSVDescription() {
        return id +
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
