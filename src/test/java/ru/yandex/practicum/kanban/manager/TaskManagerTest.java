package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.manager.exceptions.ManagerSaveException;
import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.manager.emums.StatusTask.*;

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
    void addNewTaskStandard() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.addTask(task);
        int taskId = task.getId();
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(1), "Задачи не совпадают.");
    }


    @Test
    void testAddNewEpicStandard() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        taskManager.addTask(epic);
        final int epicId = epic.getId();
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(1), "Задачи не совпадают.");
    }

    @Test
    void testAddNewSubTaskStandard() {
        SubTask subTask2 = new SubTask("Test addNewSubTask", "Test addNewSubTask description", epic.getId());
        taskManager.addTask(subTask2);
        final int subTaskId2 = subTask2.getId();

        final SubTask savedSubTask = taskManager.getSubTaskById(subTaskId2);

        assertNotNull(savedSubTask, "Задача не найдена.");
        assertEquals(subTask2, savedSubTask, "Задачи не совпадают.");

        final List<SubTask> subTasks = taskManager.getSubTasks();

        assertNotNull(subTasks, "Задачи на возвращаются.");
        assertEquals(2, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask2, subTasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void getTaskByIdStandard() {
        final Task task1 = taskManager.getTaskById(task.getId());

        assertNotNull(task1, "Задача не найдена.");
        assertEquals(task, task1, "Задачи не совпадают.");
    }

    @Test
    void getTaskByIdIsEmpty() {
        taskManager.deleteAllTask();
        ManagerSaveException exception =
                assertThrows(
                        ManagerSaveException.class, () -> taskManager.getTaskById(2));
        Assertions.assertNotNull(exception.getMessage());
        Assertions.assertFalse(exception.getMessage().isBlank());
    }

    @Test
    void getEpicByIdStandard() {
        final Epic epic1 = taskManager.getEpicById(epic.getId());

        assertNotNull(epic1, "Задача не найдена.");
        assertEquals(epic, epic1, "Задачи не совпадают.");
    }

    @Test
    void getEpicByIdIsEmpty() {
        taskManager.deleteAllEpic();
        ManagerSaveException exception =
                assertThrows(
                        ManagerSaveException.class, () -> taskManager.getTaskById(2));
        Assertions.assertNotNull(exception.getMessage());
        Assertions.assertFalse(exception.getMessage().isBlank());
    }

    @Test
    void getEpicByIdIsNull() {
        ManagerSaveException exception =
                assertThrows(
                        ManagerSaveException.class, () -> taskManager.getEpicById(task.getId()));
        Assertions.assertNotNull(exception.getMessage());
        Assertions.assertFalse(exception.getMessage().isBlank());
    }

    @Test
    void getSubTaskByIdStandard() {
        final SubTask subTask1 = taskManager.getSubTaskById(subTask.getId());

        assertNotNull(subTask1, "Задача не найдена.");
        assertEquals(subTask, subTask1, "Задачи не совпадают.");
    }

    @Test
    void getSubTaskByIdIsEmpty() {
        taskManager.deleteAllSubTasks();
        ManagerSaveException exception =
                assertThrows(
                        ManagerSaveException.class, () -> {
                            taskManager.deleteAllSubTasks();
                            taskManager.getSubTaskById(subTask.getId());
                        });
        Assertions.assertNotNull(exception.getMessage());
        Assertions.assertFalse(exception.getMessage().isBlank());
    }

    @Test
    void getSubTaskByIdIsNull() {
        ManagerSaveException exception =
                assertThrows(
                        ManagerSaveException.class, () -> {
                            taskManager.deleteAllSubTasks();
                            taskManager.getSubTaskById(task.getId());
                        });
        Assertions.assertNotNull(exception.getMessage());
        Assertions.assertFalse(exception.getMessage().isBlank());
    }

    @Test
    void updateTask() {
        final Task task1 = taskManager.getTaskById(task.getId());

        assertNotNull(task1, "Задача не найдена.");

        task1.setStatus(IN_PROGRESS);
        taskManager.updateTask(task1);
        assertEquals(task, task1, "Задачи не совпадают.");

        task1.setStatus(DONE);

        assertEquals(task, task1, "Задачи не совпадают.");
    }

    @Test
    void updateSubtaskTest() {
        SubTask id4 = new SubTask(LocalDateTime.now().plusMinutes(3), Duration.ofSeconds(59), "subtask now", "Description", epic.getId());
        taskManager.addTask(id4);

        final SubTask subTaskId3 = taskManager.getSubTaskById(subTask.getId());
        final SubTask subTaskId4 = taskManager.getSubTaskById(id4.getId());

        assertEquals(taskManager.getPrioritizedTasks().get(0), subTaskId4, "Задача не найдена.");
        assertEquals(taskManager.getPrioritizedTasks().get(2), subTaskId3, "Задача не найдена.");
        assertNotNull(subTaskId3, "Задача не найдена.");
        assertNotNull(subTaskId4, "Задача не найдена.");

        subTaskId3.setStatus(IN_PROGRESS);
        subTaskId4.setStatus(IN_PROGRESS);

        taskManager.updateSubTask(subTaskId3);
        taskManager.updateSubTask(subTaskId4);

        assertEquals(subTask, subTaskId3, "Задачи не совпадают.");
        assertEquals(id4, subTaskId4, "Задачи не совпадают.");
        assertEquals(epic.getStatus(), IN_PROGRESS, "Статус эпика не изменился");

        subTaskId3.setStatus(DONE);
        subTaskId4.setStatus(DONE);
        taskManager.updateSubTask(subTaskId3);
        taskManager.updateSubTask(subTaskId4);

        assertEquals(taskManager.getPrioritizedTasks().get(0), subTaskId4, "Задача не найдена.");
        assertEquals(taskManager.getPrioritizedTasks().get(2), subTaskId3, "Задача не найдена.");
        assertEquals(subTask, subTaskId3, "Задачи не совпадают.");
        assertEquals(id4, subTaskId4, "Задачи не совпадают.");
        assertEquals(epic.getStatus(), DONE, "Задачи не совпадают.");
        assertEquals(epic.getStartTime(), subTaskId4.getStartTime(), "Задачи не совпадают.");
        assertEquals(epic.getEndTime(), subTaskId4.getEndTime(), "Задачи не совпадают.");
        assertEquals(epic.getDuration(), subTaskId4.getDuration(), "Задачи не совпадают.");
    }

    @Test
    void getSubTasksByEpicStandard() {
        final int epicId = epic.getId();

        final ArrayList<SubTask> subTasksByEpic = taskManager.getSubTasksByEpic(epicId);

        assertNotNull(subTasksByEpic, "Подзадачи не возвращаются.");
        assertEquals(1, subTasksByEpic.size(), "Неверное количество подзадач.");
        assertEquals(subTask, subTasksByEpic.get(0), "Задачи не совпадают.");
    }

    @Test
    void getSubTasksByEpicIsNull() {
        final int epicId = epic.getId();
        taskManager.deleteAllSubTasks();
        final ArrayList<SubTask> subTasksByEpic = taskManager.getSubTasksByEpic(epicId);

        assertNotNull(subTasksByEpic, "Подзадачи не возвращаются.");
        assertEquals(0, subTasksByEpic.size(), "Неверное количество подзадач.");
    }

    @Test
    void getSubTasksByEpicIsEmpty() {
        final int epicId = epic.getId();
        taskManager.deleteAllEpic();
        final ArrayList<SubTask> subTasksByEpic = taskManager.getSubTasksByEpic(epicId);

        assertNotNull(subTasksByEpic, "Подзадач нет.");
        assertEquals(0, subTasksByEpic.size(), "Неверное количество подзадач.");
    }

    @Test
    void deleteAllTaskReturnListEmpty() {
        Task task1 = new Task(
                LocalDateTime.of(2022, 8, 25, 11, 0), Duration.ofHours(1), "Задача со временем2",
                "Проверка записи в лист c startTime");
        taskManager.addTask(task1);

        List<Task> tasks = taskManager.getTasks();
        List<Task> priority = taskManager.getPrioritizedTasks();

        assertNotNull(priority, "Задачи не получаются");
        assertNotNull(tasks, "Задачи не получаются");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        taskManager.deleteAllTask();
        assertEquals(taskManager.getPrioritizedTasks(), List.of(subTask));
        assertEquals(taskManager.getTasks(), List.of());
    }

    @Test
    void deleteAllSubTasksStandard() {
        SubTask subTask1 = new SubTask(
                LocalDateTime.of(2022, 8, 25, 11, 0), Duration.ofHours(1), "Задача со временем2",
                "Проверка записи в лист c startTime", epic.getId());
        taskManager.addTask(subTask1);

        List<SubTask> subTasks = taskManager.getSubTasks();
        List<Task> priority = taskManager.getPrioritizedTasks();

        assertNotNull(priority, "Задачи не получаются");
        assertNotNull(subTasks, "Задачи не получаются");
        assertEquals(2, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask, subTasks.get(0), "Задачи не совпадают.");

        taskManager.deleteAllSubTasks();

        assertEquals(taskManager.getPrioritizedTasks(), List.of(task));
        assertEquals(taskManager.getSubTasks(), List.of(), "Подзадачи не удалены.");

    }

    @Test
    void deleteAllEpicStandard() {
        SubTask subTask1 = new SubTask(
                LocalDateTime.of(2022, 8, 25, 11, 0), Duration.ofHours(1), "Задача со временем2",
                "Проверка записи в лист c startTime", epic.getId());
        taskManager.addTask(subTask1);

        List<Task> priority = taskManager.getPrioritizedTasks();
        List<Epic> epics = taskManager.getEpics();

        assertNotNull(priority, "Задачи не получаются");
        assertNotNull(epics, "Задачи не получаются");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");

        taskManager.deleteAllEpic();

        assertEquals(taskManager.getPrioritizedTasks(), List.of(task));
        assertEquals(taskManager.getEpics(), List.of(), "Подзадачи не удалены.");
        assertEquals(taskManager.getSubTasks(), List.of(), "Подзадачи не удалены.");

    }

    @Test
    void deleteTaskStandard() {
        Task task1 = new Task(
                LocalDateTime.of(2022, 8, 25, 11, 0), Duration.ofHours(1), "Задача со временем2",
                "Проверка записи в лист c startTime");
        taskManager.addTask(task1);
        List<Task> tasks = taskManager.getTasks();
        List<Task> priority = taskManager.getPrioritizedTasks();

        assertNotNull(priority, "Задачи не получаются");

        assertNotNull(tasks, "Задачи не получаются");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        taskManager.deleteTask(task.getId());

        assertEquals(taskManager.getPrioritizedTasks(), List.of(task1, subTask));
        assertEquals(taskManager.getTasks(), List.of(task1), "Подзадачи не удалены.");


    }

    @Test
    void deleteSubTaskStandard() {
        SubTask subTask1 = new SubTask(
                LocalDateTime.of(2022, 8, 25, 11, 0), Duration.ofHours(1), "Задача со временем2",
                "Проверка записи в лист c startTime", epic.getId());
        taskManager.addTask(subTask1);

        List<SubTask> subTasks = taskManager.getSubTasks();
        List<Task> priority = taskManager.getPrioritizedTasks();

        assertNotNull(priority, "Задачи не получаются");
        assertNotNull(subTasks, "Задачи не получаются");
        assertEquals(2, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask, subTasks.get(0), "Задачи не совпадают.");

        taskManager.deleteSubTask(subTask.getId());

        assertEquals(taskManager.getPrioritizedTasks(), List.of(subTask1, task));
        assertEquals(taskManager.getSubTasks(), List.of(subTask1), "Подзадачи не удалены.");
    }

    @Test
    void deleteEpicStandard() {
        List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не получаются");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");

        taskManager.deleteEpic(epic.getId());

        assertEquals(taskManager.getEpics(), List.of(), "Неверное количество задач.");
        assertEquals(taskManager.getSubTasks(), List.of(), "Подзадачи не удалены.");

    }

    @Test
    void getTasksStandard() {
        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не получаются");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getEpicsStandard() {
        List<Epic> epics = taskManager.getEpics();

        assertNotNull(epic.getSubTaskIds(), "Подзадачи не добавились");
        assertNotNull(epics, "Задачи не получаются");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void getSubTasksStandard() {
        List<SubTask> subTasks = taskManager.getSubTasks();

        assertNotNull(subTasks.get(0).getEpicId(), "Эпика у подзадачи нет");
        assertNotNull(subTasks, "Задачи не получаются");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask.getEpicId(), subTasks.get(0).getEpicId(), "Epic id не совпадают.");
    }

    @Test
    void getHistoryManagerStandard() {
        taskManager.getTaskById(task.getId());

        List<Task> history = taskManager.getHistoryManager();
        assertNotNull(history, "Задачи не получаются");
        assertEquals(1, history.size(), "Неверное количество задач.");
        assertEquals(task, history.get(0), "Задачи не совпадают.");
    }

    @Test
    void getHistoryManagerIsEmpty() {

        List<Task> history = taskManager.getHistoryManager();
        assertNotNull(history, "Задачи не получаются");
        assertEquals(history, List.of(), "Неверное количество задач.");
    }
}