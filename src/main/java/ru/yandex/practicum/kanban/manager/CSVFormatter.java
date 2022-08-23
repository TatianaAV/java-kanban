package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;
import ru.yandex.practicum.kanban.tasks.TypeTasks;

import java.util.ArrayList;
import java.util.List;

public class CSVFormatter {

    protected static String historyToString(HistoryManager history) { //запись истории в строку
        var historyList = history.getHistory();
        List<String> ids = new ArrayList<>();
        for (Task task : historyList) {
            int id = task.getId();
            ids.add(String.valueOf(id));
        }
        return String.join(",", ids);
    }

    public static Task fromString(String value) {// создание задачи из строки
        String[] split = value.split(",");
        TypeTasks type = TypeTasks.valueOf(split[1]);
        int id = Integer.parseInt(split[0]);
        String title = split[2];
        String description = split[4];
        StatusTask status = StatusTask.valueOf(split[3]);

        switch (type) {
            case TASK:
                return new Task(id, title, status, description);
            case EPIC:

                return new Epic(id, title, status, description);
            case SUBTASK:
                int epicId = Integer.parseInt(split[6]);
                return new SubTask(id, title, status, description, epicId);
        }
        return null;
    }

    public static List<Integer> historyFromString(String value) {
        String[] split = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (int i = 0; i <= split.length - 1; i++) {
            history.add(Integer.valueOf(split[i]));
        }
        return history; //создание списка истории из строки
    }
}
