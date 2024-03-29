package ru.yandex.practicum.kanban.tasks;

import ru.yandex.practicum.kanban.manager.emums.StatusTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {

    private final int epicId;



   public SubTask(int id, String title, StatusTask  status, String description, int epicId) {

       super(id, title, status, description);
        this.epicId = epicId;
        this.type = TypeTasks.SUBTASK;
    }



    public SubTask(LocalDateTime startTime, Duration duration, int id, String title, StatusTask status, String description,  int epicId) {

        super(startTime, duration, id, title, status, description);
        this.type = TypeTasks.SUBTASK;
        this.epicId = epicId;
    }



    public SubTask(String title, String description, int epicId) {

        super(title, description);
        this.epicId = epicId;
        this.type = TypeTasks.SUBTASK;
    }

    public SubTask(LocalDateTime startTime, Duration duration, String title, String description, int epicId) {

        super(startTime, duration, title, description);
        this.epicId = epicId;
        this.type = TypeTasks.SUBTASK;
    }


    public int getEpicId() {
        return epicId;
    }


    @Override
    public String toString() {
        return super.toString() +
                    ", epicId " + epicId;
    }

    @Override
    public String toCSVDescription(){
        return super.toCSVDescription() +
                ",epic," + epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subTask = (SubTask) o;
        return super.equals(subTask)
                && epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
