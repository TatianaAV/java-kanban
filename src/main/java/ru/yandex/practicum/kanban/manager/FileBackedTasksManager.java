package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.manager.exceptions.ManagerSaveException;
import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;
import ru.yandex.practicum.kanban.tasks.TypeTasks;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final static String PATH = "resources\\tasks.csv";
    private final static String HEAD = "startTime,duration,id,type,name,status,description,epic";

    public static FileBackedTasksManager loadedFromFileTasksManager() {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        fileBackedTasksManager.loadFromFile();
        return fileBackedTasksManager;
    }

    public Map<Integer, Task> allTasks = new HashMap<>();

    protected void save() {

        try (FileWriter fileWriter = new FileWriter(PATH)) {
            fileWriter.write(HEAD + System.lineSeparator());
            for (Task task : tasksMap.values()) {
                fileWriter.write(task.toCSVDescription() + System.lineSeparator());
            }
            for (Epic epic : epicsMap.values()) {
                fileWriter.write(epic.toCSVDescription() + System.lineSeparator());
            }
            for (SubTask subTask : subTasksMap.values()) {
                fileWriter.write(subTask.toCSVDescription() + System.lineSeparator());
            }
            fileWriter.write(System.lineSeparator());
            fileWriter.write(CSVFormatter.historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка записи файла.", e);
        }
    }


    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addTask(Epic epic) {
        super.addTask(epic);
        save();
    }

    @Override
    public void addTask(SubTask subTask) {
        super.addTask(subTask);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        var task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpicTime(int id) {
        super.updateEpicTime(id);
        save();
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteTask(Integer taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteSubTask(Integer subTaskId) {
        super.deleteSubTask(subTaskId);
        save();
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        save();
    }

    public Map<Integer, Task> getAllTasks() {
        return allTasks;
    }

    public void loadFromFile() {
        //  FileBackedTasksManager tasksManager = new FileBackedTasksManager();
        Path path = Path.of(PATH);
        try {

            String csv = Files.readString(path); //читает файл
            if (!csv.isBlank()) {

                String[] lines = csv.split(System.lineSeparator());
                for (int i = 1; i < lines.length; i++) {
                    if (!lines[i].isEmpty()) {
                        Task task = CSVFormatter.fromString(lines[i]);

                        if (task != null) {
                            int id = task.getId();
                            generatedId = id;
                            TypeTasks type = task.getType();
                            switch (type) {
                                case TASK:
                                    tasksMap.put(id, task);
                                    updateTask(task);
                                    break;
                                case EPIC:
                                    epicsMap.put(id, (Epic) task);
                                    updateEpicTime(task.getId());
                                    break;
                                case SUBTASK:
                                    subTasksMap.put(id, (SubTask) task);
                                    int epicId = subTasksMap.get(id).getEpicId();
                                    ArrayList<Integer> listIdSubTask =
                                            epicsMap.get(epicId).getSubTaskIds();
                                    listIdSubTask.add(id);
                                    updateSubTask((SubTask) task);
                            }
                        }
                    } else {
                        for (Integer taskId : CSVFormatter.historyFromString(lines[i + 1])) {
                            if (tasksMap.containsKey(taskId)) {
                                getTaskById(taskId);
                            } else if (epicsMap.containsKey(taskId)) {
                                getEpicById(taskId);
                            } else if (subTasksMap.containsKey(taskId)) {
                                getSubTaskById(taskId);
                            }
                        }

                        break;
                    }
                }


            } else {
                System.out.println("Файл пуст");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не могу прочитать файл: " + path, e);
        }
        //   return tasksManager;
    }
}