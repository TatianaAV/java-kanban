package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        taskManagerSetUp();
    }

    @Test
    void getHistoryManager() {
    }

    @Test
    void generatedId() {
    }

    @Disabled
    @Test
    public void whenAddingNullToNonEmptyTreeSet_shouldThrowException() {
        Set treeSet = new TreeSet<>();
        treeSet.add("First");
        treeSet.add(null);
    }

    @Test
    void getPrioritizedTasks() {
        Task taskTime = new Task(
                LocalDateTime.now(), Duration.ofDays(1), "Задача со временем",
                "Проверка записи в лист");
        taskManager.addTask(taskTime);
        List<Task> sorted = taskManager.getPrioritizedTasks();
        assertNotNull(sorted, "Приоритетный лист не заполняется");
        assertEquals(3, sorted.size(), "Неверное количество задач.");
        assertEquals(taskTime, sorted.get(0), "Задачи не совпадают.");
    }

    @Test
    void validateTaskInTime() {
        Task taskTime = new Task(
                LocalDateTime.now(), Duration.ofDays(1), "Задача со временем",
                "Проверка записи в лист");
        taskManager.addTask(taskTime);

        Task taskTime2 = new Task(
                LocalDateTime.now(), Duration.ofDays(1), "Задача со временем2",
                "Проверка записи в лист c startTime == now()");
        taskManager.addTask(taskTime2);
    }

    @Test
    void testValidateException() {
        InvalidTimeException exception =
                assertThrows(
                        InvalidTimeException.class, () -> {
                            Task taskTime = new Task(
                                    LocalDateTime.now(), Duration.ofDays(1), "Задача со временем",
                                    "Проверка записи в лист");
                            taskManager.addTask(taskTime);

                            Task taskTime2 = new Task(
                                    LocalDateTime.now(), Duration.ofDays(1), "Задача со временем2",
                                    "Проверка записи в лист c startTime == now()");
                            taskManager.addTask(taskTime2);
                        });
        assertNotNull(exception.getMessage());
        assertFalse(exception.getMessage().isBlank());
    }
}