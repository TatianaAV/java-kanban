package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> listHistory = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return listHistory;
    }

    @Override
    public void add(Task task) {
        if (!Objects.isNull(task)) {
            if (listHistory.size() == 10) {
                listHistory.remove(0);
            }
            listHistory.add(task);
        }
    }
}
