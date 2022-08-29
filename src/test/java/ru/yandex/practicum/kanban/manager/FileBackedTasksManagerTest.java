package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        FileBackedTasksManager tasksManager = FileBackedTasksManager.loadFromFile(file);

        List<Task> tasks = tasksManager.getTasks();
        assertNotNull(tasks, "Задачи не получаются");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        List<Epic> epics = tasksManager.getEpics();
        assertNotNull(epics, "Эпики не получаются");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");

        List<SubTask> subTasks = tasksManager.getSubTasks();
        assertNotNull(subTasks, "Эпики не получаются");
        assertEquals(1, subTasks.size(), "Неверное количество эпиков.");
        assertEquals(subTasks, List.of(subTasks.get(0)), "Подзадачи не совпадают.");

        final List<Task> history = taskManager.getHistoryManager();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
//С IDE было что-то непонятное. тест не прроходил проверку. сейчас всё ок
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