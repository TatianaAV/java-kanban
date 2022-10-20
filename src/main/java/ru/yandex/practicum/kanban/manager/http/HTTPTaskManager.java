package ru.yandex.practicum.kanban.manager.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.kanban.manager.CSVFormatter;
import ru.yandex.practicum.kanban.manager.FileBackedTasksManager;
import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;


public class HTTPTaskManager extends FileBackedTasksManager {

    private static final Gson gson = Managers.getGson();

    protected KVTaskClient kvTaskClient;

    protected String path;

    public HTTPTaskManager(String path) {
        this.path = path;
    }

    public void getToken() {
        kvTaskClient = new KVTaskClient(path);
        kvTaskClient.register();
    }

    public void saveTasks() throws IOException {
        if (kvTaskClient == null) {
            System.out.println("Требуется регистрация");
            return;
        }

        this.loadFromFile();
        kvTaskClient.put("/tasks", gson.toJson(getTasksMap().values()));
        kvTaskClient.put("/epics", gson.toJson(getEpicsMap().values()));
        kvTaskClient.put("/subtasks", gson.toJson(getSubTasksMap().values()));
        kvTaskClient.put("/history", gson.toJson(CSVFormatter.historyToString(historyManager)));
    }

    public void loadTasks() throws IOException {
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
            addEpicFromKVServer(epic);
        }
        allTasks.putAll(getEpicsMap());

        json = kvTaskClient.load("/subtasks");
        type = new TypeToken<ArrayList<SubTask>>() {
        }.getType();
        ArrayList<SubTask> subtasksList = gson.fromJson(json, type);
        for (SubTask subtask : subtasksList) {
            addSubtaskFromKVServer(subtask);
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
        save();
    }

    public void addTaskFromKVServer(Task task) throws IOException {
        super.updateTask(task);
        saveTasks();
    }

    public void addEpicFromKVServer(Epic epic) throws IOException {
        super.updateEpicStatus(epic);
        saveTasks();
    }

    public void addSubtaskFromKVServer(SubTask subtask) throws IOException {
        super.updateSubTask(subtask);
        saveTasks();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        try {
            saveTasks();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addTask(Epic epic) {
        super.addTask(epic);
        try {
            saveTasks();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addTask(SubTask subtask) {
        super.addTask(subtask);
        try {
            saveTasks();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private int parsePathId(String idString) {
        try {
            return Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
