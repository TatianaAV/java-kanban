package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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

    @Test
    void updateEpicDuration(){
        Epic epicTime1 = new Epic(
                 "Задача со временем3",
                "Проверка записи в лист");
        taskManager.addTask(epicTime1);
        SubTask taskTime2 = new SubTask(
                LocalDateTime.now().plusMinutes(2), Duration.ofMinutes(1), "Задача со временем2",
                "Проверка записи в лист", epicTime1.getId());
        taskManager.addTask(taskTime2);
        SubTask taskTime1 = new SubTask(
                LocalDateTime.now(), Duration.ofMinutes(1), "Задача со временем1",
                "Проверка записи в лист", epicTime1.getId());
        taskManager.addTask(taskTime1);
        taskManager.getEpics().forEach(System.out::println);
        taskManager.getSubTasks().forEach(System.out::println);
    }

    @Test
    void getPrioritizedTasks() {
       /* taskManager.deleteAllTask();
        taskManager.deleteAllEpic();*/
        Task taskTime3 = new Task(
                LocalDateTime.now().plusMinutes(4), Duration.ofMinutes(1), "Задача со временем3",
                "Проверка записи в лист");
        taskManager.addTask(taskTime3);
        Task taskTime2 = new Task(
                LocalDateTime.now().plusMinutes(2), Duration.ofMinutes(1), "Задача со временем2",
                "Проверка записи в лист");
        taskManager.addTask(taskTime2);
        Task taskTime1 = new Task(
                LocalDateTime.now(), Duration.ofMinutes(1), "Задача со временем1",
                "Проверка записи в лист");
        taskManager.addTask(taskTime1);


        List<Task> sorted = taskManager.getPrioritizedTasks();
        assertNotNull(sorted, "Приоритетный лист не заполняется");
        assertEquals(5, sorted.size(), "Неверное количество задач.");
        assertEquals(taskTime1, sorted.get(0), "Задачи не совпадают.");
        assertEquals(taskTime2, sorted.get(1), "Задачи не совпадают.");
        assertEquals(taskTime3, sorted.get(2), "Задачи не совпадают.");
    }

    @Test
    void validateTaskInTimeAdd() {
        Task taskTime1 = new Task(
                LocalDateTime.of(2022, 8, 25, 10, 00), Duration.ofHours(1), "Задача со временем",
                "Проверка записи в лист  c startTime");
        taskManager.addTask(taskTime1);
        int taskId1 = taskTime1.getId();

        Task taskTime2 = new Task(
                LocalDateTime.of(2022, 8, 25, 11, 01), Duration.ofHours(1), "Задача со временем2",
                "Проверка записи в лист c startTime");
        taskManager.addTask(taskTime2);
        int taskId2 = taskTime2.getId();
        final Task savedTask1 = taskManager.getTaskById(taskId1);
        final Task savedTask2 = taskManager.getTaskById(taskId2);

        assertNotNull(savedTask1, "Задача не найдена.");
        assertNotNull(savedTask2, "Задача не найдена.");
        assertEquals(taskTime2, savedTask2, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");
        assertEquals(savedTask1, tasks.get(1), "Задачи не совпадают.");
        assertEquals(savedTask2, tasks.get(2), "Задачи не совпадают.");
        System.out.println(savedTask1 + "\n" + savedTask2);
    }

    @Test
    void testValidateTimeNotAdd() {
        Task taskTime = new Task(
                LocalDateTime.now(), Duration.ofDays(1), "Задача со временем",
                "Проверка записи в лист");
        taskManager.addTask(taskTime);

        Task taskTime2 = new Task(
                LocalDateTime.now(), Duration.ofDays(1), "Задача со временем2",
                "Проверка записи в лист c startTime == now()");
        taskManager.addTask(taskTime2);

        assertNotNull(taskManager.getTaskById(taskTime.getId()), "Задача должна быть добавлена");
        assertNull(taskManager.getTaskById(taskTime2.getId()), "Задача не должна быть добавлена");
    }


}