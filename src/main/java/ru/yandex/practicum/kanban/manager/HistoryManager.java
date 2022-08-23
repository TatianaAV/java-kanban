package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.tasks.Task;

import java.util.List;

public interface HistoryManager {

    void addHistory(Task task);

    void remove(int id);

    List<Task> getHistory();

}
