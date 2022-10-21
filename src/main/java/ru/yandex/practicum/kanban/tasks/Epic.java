package ru.yandex.practicum.kanban.tasks;

import ru.yandex.practicum.kanban.manager.emums.StatusTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIds;
    private LocalDateTime endTime;

    public Epic(String title, String description, StatusTask status) {
        super(title, description, status);
        this.subTaskIds = new ArrayList<>();
        this.type = TypeTasks.EPIC;
    }

    public Epic(LocalDateTime startTime, Duration duration, int id, String title, StatusTask status, String description) {
        super(startTime, duration, id, title, status, description);
        this.subTaskIds = new ArrayList<>();
        this.type = TypeTasks.EPIC;
    }

    public Epic(int id, String title, StatusTask status, String description) {
        super(id, title, status, description);
        this.subTaskIds = new ArrayList<>();
        this.type = TypeTasks.EPIC;
    }


    public Epic(String title, String description) {
        super(title, description);
        this.type = TypeTasks.EPIC;
        this.subTaskIds = new ArrayList<>();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }


    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public ArrayList<Integer> getSubTaskIds() {
        if (subTaskIds == null) {
            return new ArrayList<>();
        }
        return subTaskIds;
    }

    public void setSubTaskIds(ArrayList<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", subTaskIds" + subTaskIds;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return super.equals(epic)
                && Objects.equals(subTaskIds, epic.subTaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIds);
    }
}
