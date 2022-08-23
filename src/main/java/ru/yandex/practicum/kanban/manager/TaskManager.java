package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {



    int addTask(Epic epic);

    int addTask(SubTask subTask);

    int addTask(Task task);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int subTaskId);

    void updateTask(Task task);

    StatusTask updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    ArrayList<SubTask> getSubTasksByEpic(Integer epicId);

    void deleteAllTask();

    void deleteAllSubTasks();

    void deleteAllEpic();

    void deleteTask(Integer taskId);

    void deleteSubTask(Integer subTaskId);

    void deleteEpic(int epicId);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<SubTask> getSubTasks();

    List<Task> getHistoryManager();


}

