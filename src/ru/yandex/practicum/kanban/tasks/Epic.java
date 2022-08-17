package ru.yandex.practicum.kanban.tasks;

import ru.yandex.practicum.kanban.manager.StatusTask;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIds;

    public Epic(String title, String description, StatusTask status, ArrayList<Integer> subTaskIds) {
        super(title, description, status);
        this.subTaskIds = subTaskIds;
    }

    public Epic(int id, String title, StatusTask status, String description) {
        super(id, title, status, description);
        this.type = TypeTasks.EPIC;
        this.subTaskIds = new ArrayList<>();

    }

    public Epic(String title, String description) {
        super(title, description);
    }

    public ArrayList<Integer> getSubTaskIds() {
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
