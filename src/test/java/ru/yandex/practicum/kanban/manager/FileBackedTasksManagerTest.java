package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.manager.emums.StatusTask;
import ru.yandex.practicum.kanban.manager.exceptions.ManagerSaveException;
import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private File file;

    @BeforeEach
    void setUp() {
        file = new File("resources/tasks.csv");
        taskManager = new FileBackedTasksManager();
        taskManagerSetUp();
    }

    @AfterEach
    void tearDown() {
        //assertTrue(file.delete());
    }

    @Test
    void fileIsExist() {
        Path path = Paths.get("resources/tasks.csv");

        assertTrue(Files.exists(path), "Файл не создается");
    }

    @Test
    void loadFromFile() {
        taskManager.getTaskById(task.getId());//история задач = 1
        SubTask subTask1 = new SubTask(
                LocalDateTime.of(2022, 8, 25, 11, 0), Duration.ofHours(1), "Задача со временем2",
                "Проверка записи в лист c startTime", epic.getId());
        taskManager.addTask(subTask1);

        SubTask subTask2 = new SubTask(
                LocalDateTime.of(2022, 8, 25, 12, 1), Duration.ofHours(1), "Задача со временем2",
                "Проверка записи в лист c startTime", epic.getId());
        taskManager.addTask(subTask2);

        SubTask subTask3 = new SubTask(LocalDateTime.now().plusMinutes(3), Duration.ofSeconds(59), "subtask now", "Description", epic.getId());
        taskManager.addTask(subTask3);
        subTask3.setStatus(StatusTask.IN_PROGRESS);
        taskManager.updateSubTask(subTask3);

        FileBackedTasksManager tasksManager = FileBackedTasksManager.loadFromFile(file);

        List<Task> tasks = tasksManager.getTasks();
        assertNotNull(tasks, "Задачи не получаются");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        List<Epic> epics = tasksManager.getEpics();
        assertNotNull(epics, "Эпики не получаются");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
        assertNotNull(epic.getEndTime(), "у эпика не обновилось время при считывании из файла");
        assertEquals(epic.getDuration(), Duration.between(subTask1.getStartTime(), subTask3.getEndTime()),
                "Обновление времени не происходит");


        List<SubTask> subTasks = tasksManager.getSubTasks();
        assertNotNull(subTasks, "Подзадачи не получаются");
        assertEquals(4, subTasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask, subTasks.get(0), "Подзадачи не совпадают.");

        final List<Task> history = taskManager.getHistoryManager();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void testExpectedException() {
        ManagerSaveException exception =
                assertThrows(
                        ManagerSaveException.class, () -> {
                            file = new File("resources/tasks1.csv");
                            FileBackedTasksManager.loadFromFile(file);
                        });
        Assertions.assertNotNull(exception.getMessage());
        Assertions.assertFalse(exception.getMessage().isBlank());
    }
}