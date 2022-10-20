package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface TaskManager {


    void addTask(Epic epic);

    void addTask(SubTask subTask);

    void addTask(Task task);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int subTaskId);

    void updateTask(Task task);

    void updateEpic(int epicId);

    void updateSubTask(SubTask subTask);

    Map<Integer, Task> getTasksMap();

    Map<Integer, Epic> getEpicsMap();

    Map<Integer, SubTask> getSubTasksMap();

    ArrayList<SubTask> getSubTasksByEpic(Integer epicId);

    void deleteAllTask();

    void deleteAllSubTasks();

    void deleteAllEpic();

    void deleteTask(Integer taskId);

    void deleteSubTask(Integer subTaskId);

    void deleteEpic(int epicId);

    List<Task> getTasks();

    List<Task> getPrioritizedTasks();

    List<Epic> getEpics();

    List<SubTask> getSubTasks();

    List<Task> getHistoryManager();


}

