package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.manager.StatusTask.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected Task task;
    protected Epic epic;
    protected SubTask subTask;

    protected void taskManagerSetUp() {

        task = new Task(1, "Заголовок задачи", NEW, "Описание задачи");
        taskManager.addTask(task);
        epic = new Epic(2, "Заголовок эпика", NEW, "Описание Эпика");//создаем эпик
        taskManager.addTask(epic);
        subTask = new SubTask(3, "Заголовок подзадачи", NEW, " Описание подзадачи", epic.getId());
        taskManager.addTask(subTask);
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void testAddNewEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.addTask(epic);

        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(1), "Задачи не совпадают.");
    }

    @Test
    void testAddNewSubTask() {
        SubTask subTask2 = new SubTask("Test addNewSubTask", "Test addNewSubTask description", epic.getId());
        final int subTaskId2 = taskManager.addTask(subTask2);

        final SubTask savedSubTask = taskManager.getSubTaskById(subTaskId2);

        assertNotNull(savedSubTask, "Задача не найдена.");
        assertEquals(subTask2, savedSubTask, "Задачи не совпадают.");

        final List<SubTask> subTasks = taskManager.getSubTasks();

        assertNotNull(subTasks, "Задачи на возвращаются.");
        assertEquals(2, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask2, subTasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void getTaskById() {
        final Task task1 = taskManager.getTaskById(task.getId());

        assertNotNull(task1, "Задача не найдена.");
        assertEquals(task, task1, "Задачи не совпадают.");
    }

    @Test
    void getEpicById() {
        final Epic epic1 = taskManager.getEpicById(epic.getId());

        assertNotNull(epic1, "Задача не найдена.");
        assertEquals(epic, epic1, "Задачи не совпадают.");
    }

    @Test
    void getSubTaskById() {
        final SubTask subTask1 = taskManager.getSubTaskById(subTask.getId());

        assertNotNull(subTask1, "Задача не найдена.");
        assertEquals(subTask, subTask1, "Задачи не совпадают.");
    }

    @Test
    void updateTask() {
        final Task task1 = taskManager.getTaskById(task.getId());

        assertNotNull(task1, "Задача не найдена.");
        task1.setStatus(IN_PROGRESS);
        assertEquals(task, task1, "Задачи не совпадают.");
        task1.setStatus(DONE);
        assertEquals(task, task1, "Задачи не совпадают.");
    }

    @Test
    void updateSubTask() {
        SubTask subTask1 = taskManager.getSubTaskById(subTask.getId());

        assertNotNull(subTask1, "Задача не найдена.");

        subTask1.setStatus(IN_PROGRESS);
        StatusTask statusEpic = taskManager.updateSubTask(subTask1);
        subTask1 = taskManager.getSubTaskById(subTask.getId());

        assertEquals(subTask, subTask1, "Задачи не совпадают.");
        assertEquals(statusEpic, subTask1.getStatus(), "Эпик подзадачи не обновлен.");

        subTask1.setStatus(DONE);
        statusEpic = taskManager.updateSubTask(subTask1);
        Epic epic1 = taskManager.getEpicById(subTask1.getEpicId());
        subTask1 = taskManager.getSubTaskById(subTask.getId());


        assertEquals(subTask, subTask1, "Задачи не совпадают.");
        assertEquals(epic1.getStatus(), statusEpic, "Эпик подзадачи не обновлен");
    }

    @Disabled
    @Test
    void updateEpic() {

    }

    @Test
    void getSubTasksByEpic() {
        final int epicId = epic.getId();

        final ArrayList<SubTask> subTasksByEpic = taskManager.getSubTasksByEpic(epicId);

        assertNotNull(subTasksByEpic, "Подзадачи не возвращаются.");
        assertEquals(1, subTasksByEpic.size(), "Неверное количество подзадач.");
        assertEquals(subTask, subTasksByEpic.get(0), "Задачи не совпадают.");
    }

    @Test
    void deleteAllTask() {
        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не получаются");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        taskManager.deleteAllTask();

        assertNull(taskManager.getTasks(), "Подзадачи не удалены.");
    }

    @Test
    void deleteAllSubTasks() {
        List<SubTask> subTasks = taskManager.getSubTasks();

        assertNotNull(subTasks, "Задачи не получаются");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask, subTasks.get(0), "Задачи не совпадают.");

        taskManager.deleteAllSubTasks();

        assertNull(taskManager.getSubTasks(), "Подзадачи не удалены.");
    }

    @Test
    void deleteAllEpic() {
        List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не получаются");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");

        taskManager.deleteAllEpic();

        assertNull(taskManager.getEpics(), "Подзадачи не удалены.");
        assertNull(taskManager.getSubTasks(), "Подзадачи не удалены.");

    }

    @Test
    void deleteTask() {
        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не получаются");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        taskManager.deleteTask(task.getId());

        assertNull(taskManager.getTasks(), "Подзадачи не удалены.");


    }

    @Test
    void deleteSubTask() {
        List<SubTask> subTasks = taskManager.getSubTasks();

        assertNotNull(subTasks, "Задачи не получаются");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask, subTasks.get(0), "Задачи не совпадают.");

        taskManager.deleteSubTask(subTask.getId());

        assertNull(taskManager.getSubTasks(), "Подзадачи не удалены.");
    }

    @Test
    void deleteEpic() {
        List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не получаются");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");

        taskManager.deleteEpic(epic.getId());

        assertNull(taskManager.getEpics(), "Подзадачи не удалены.");
        assertNull(taskManager.getSubTasks(), "Подзадачи не удалены.");
    }

    @Test
    void getTasks() {
        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не получаются");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getEpics() {
        List<Epic> epics = taskManager.getEpics();

        assertNotNull(epic.getSubTaskIds(),"Подзадачи не добавились");
        assertNotNull(epics, "Задачи не получаются");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void getSubTasks() {
        List<SubTask> subTasks = taskManager.getSubTasks();

        assertNotNull(subTasks.get(0).getEpicId(), "Эпика у подзадачи нет");
        assertNotNull(subTasks, "Задачи не получаются");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask.getEpicId(), subTasks.get(0).getEpicId(), "Epic id не совпадают.");
    }

    @Test
    void getHistoryManager() {
        final Task task1 = taskManager.getTaskById(task.getId());

        List<Task>  history = taskManager.getHistoryManager();
        assertNotNull(history, "Задачи не получаются");
        assertEquals(1, history.size(), "Неверное количество задач.");
        assertEquals(task, history.get(0), "Задачи не совпадают.");
    }
}