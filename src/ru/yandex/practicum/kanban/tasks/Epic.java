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

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(ArrayList<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskIds=" + subTaskIds +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return super.equals(epic) && Objects.equals(subTaskIds, epic.subTaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIds);
    }
}
