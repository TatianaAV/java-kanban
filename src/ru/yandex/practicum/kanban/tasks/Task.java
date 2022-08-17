package ru.yandex.practicum.kanban.tasks;

import ru.yandex.practicum.kanban.manager.StatusTask;

import java.util.Objects;

public class Task {
    protected int id;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    protected String title;
    protected String description;
    protected StatusTask status;
    protected TypeTasks type;


    public Task(String title, String description, StatusTask status) {
        this.title = title;
        this.description = description;
        this.status = status;
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

    public TypeTasks getType() {
        return type;
    }
    public void setType(TypeTasks type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return   " id " + id +
                ", " + type +
                ", " + status +
                ", " + title +
                ", " + description ;
    }

    public int getId() {

        return id;
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
