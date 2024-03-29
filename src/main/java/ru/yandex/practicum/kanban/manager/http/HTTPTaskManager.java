package ru.yandex.practicum.kanban.manager.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.kanban.manager.CSVFormatter;
import ru.yandex.practicum.kanban.manager.FileBackedTasksManager;
import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;
import ru.yandex.practicum.kanban.tasks.TypeTasks;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HTTPTaskManager extends FileBackedTasksManager {

    private static final Gson gson = Managers.getGson();

    protected KVTaskClient kvTaskClient;

    protected String path;
    private Map<Integer, Task> allTasks = new HashMap<>();

    public HTTPTaskManager(String path) {
        this.path = path;
    }

    public void getToken() {
        kvTaskClient = new KVTaskClient(path);
        kvTaskClient.register();
    }

    public void saveTasks() {
        if (kvTaskClient == null) {
            System.out.println("Требуется регистрация");
            return;
        }

        kvTaskClient.put("/tasks", gson.toJson(getTasksMap().values()));
        kvTaskClient.put("/epics", gson.toJson(getEpicsMap().values()));
        kvTaskClient.put("/subtasks", gson.toJson(getSubTasksMap().values()));
        kvTaskClient.put("/history", gson.toJson(CSVFormatter.historyToString(historyManager)));
    }

    public void loadTasks() {
        //tasksMap.clear(); для проверки выгрузки
        //subTasksMap.clear();
        //epicsMap.clear();
        //priorityTask.clear();
allTasks.clear();
        String json = kvTaskClient.load("/tasks");
        Type type = new TypeToken<ArrayList<Task>>() {
        }.getType();
        ArrayList<Task> tasksList = gson.fromJson(json, type);
        for (Task task : tasksList) {
            addTaskFromKVServer(task);
        }
        allTasks.putAll(getTasksMap());

        json = kvTaskClient.load("/epics");
        type = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        ArrayList<Epic> epicsList = gson.fromJson(json, type);
        for (Epic epic : epicsList) {
            addTaskFromKVServer(epic);
        }
        allTasks.putAll(getEpicsMap());

        json = kvTaskClient.load("/subtasks");
        type = new TypeToken<ArrayList<SubTask>>() {
        }.getType();
        ArrayList<SubTask> subtasksList = gson.fromJson(json, type);
        for (SubTask subtask : subtasksList) {
            addTaskFromKVServer(subtask);
        }
        allTasks.putAll(getSubTasksMap());

        json = kvTaskClient.load("/history");
        String historyLine = json.substring(1, json.length() - 1);
        if (!historyLine.equals("\"\"")) {
            String[] historyLineContents = historyLine.split(",");
            for (String s : historyLineContents) {
                historyManager.addHistory(allTasks.get(parsePathId(s)));
            }
        }
    }

    private void addTaskFromKVServer(Task task) {
        if (task != null) {
            int id = task.getId();
            if (generatedId <= id) {
                generatedId = id;
            }
            TypeTasks type = task.getType();
            switch (type) {
                case TASK:
                    priorityTask.add(task);
                    tasksMap.put(id, task);
                    break;
                case EPIC:
                    epicsMap.put(id, (Epic) task);
                    break;
                case SUBTASK:
                    subTasksMap.put(id, (SubTask) task);
                    priorityTask.add(task);
            }
        }
    }

    private int parsePathId(String idString) {
        try {
            return Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public Map<Integer, Task> getAllTasks() {
   return allTasks;
    }
}
