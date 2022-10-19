package ru.yandex.practicum.kanban.manager.HTTPmanager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.manager.FileBackedTasksManager;
import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class KVServerTest {
    HTTPTaskManager httpTaskManager;

    @BeforeAll
    static void createFileForTests() {
        var fileManager = Managers.getDefaultFileManager();
        fileManager.addTask(new Task( LocalDateTime.now()
                , Duration.ofSeconds(20), "Test task1",  "task 1"));
        fileManager.addTask(new Task(LocalDateTime.now().plusMinutes(30)
                , Duration.ofSeconds(20), "Task task 2", "task 2"));
        fileManager.addTask(new Epic("Epic id 3",  "epic 3"));
        fileManager.addTask(new SubTask( LocalDateTime.now().plusMinutes(60)
                , Duration.ofSeconds(20), "subtask 4",  "subtask 4 epic 3", 3));
        fileManager.addTask(new SubTask(LocalDateTime.now().plusMinutes(90)
                , Duration.ofSeconds(20), "SubTask 5", "subtask 5 epic 3", 3 ));
        fileManager.addTask(new SubTask(LocalDateTime.now().plusMinutes(120)
                , Duration.ofSeconds(200), "SubTask 6", "subtask 6 epic 3", 3 ));

        fileManager.getTaskById(1);
        fileManager.getEpicById(3);
    }
    @Test
    void saveAndLoadTasksKVServerTest() throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        httpTaskManager = Managers.loadedHTTPTasksManager();

        assertNull(httpTaskManager.getAllTasks().get(1), "задачи должны быть null");

        httpTaskManager.getToken();
        httpTaskManager.saveTasks();
        httpTaskManager.loadTasks();
        var allTask = httpTaskManager.getAllTasks();

        assertEquals(allTask.values().size(),6, "Задачи не загружены");

        kvServer.stop();

    }
}