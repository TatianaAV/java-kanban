package ru.yandex.practicum.kanban.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    HistoryManager historyManager;
    private Task task;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    void setUp() {

        historyManager = new InMemoryHistoryManager();

        task = new Task(1, "Заголовок задачи", StatusTask.NEW, "Описание задачи");
        epic = new Epic(2, "Заголовок эпика", StatusTask.NEW, "Описание Эпика");//создаем эпик
        subTask = new SubTask(3, "Заголовок подзадачи", StatusTask.NEW, " Описание подзадачи", epic.getId());
    }

    @AfterEach
    void tearDown() {
        List<Task> history = historyManager.getHistory();

        for (Task is : history) {
            historyManager.remove(is.getId());
        }

        history = historyManager.getHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    void addHistory() {

        historyManager.addHistory(task);

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void addTwice() {

        historyManager.addHistory(task);
        historyManager.addHistory(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void removeFirst() {

        historyManager.addHistory(task);
        historyManager.addHistory(epic);
        historyManager.addHistory(subTask);

        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История содержит 3 задачи.");

        historyManager.remove(task.getId());
        history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История содержит 3 задачи.");
        assertEquals(epic, history.get(0), "Порядок добавления.");
        assertEquals(subTask, history.get(1), "Порядок добавления.");
    }

    @Test
    void removeMiddle() {
        historyManager.addHistory(task);
        historyManager.addHistory(epic);
        historyManager.addHistory(subTask);

        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История содержит 3 задачи.");

        historyManager.remove(epic.getId());
        history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История содержит 3 задачи.");
        assertEquals(task, history.get(0), "Порядок добавления.");
        assertEquals(subTask, history.get(1), "Порядок добавления.");
    }

    @Test
    void removeLast() {
        historyManager.addHistory(task);
        historyManager.addHistory(epic);
        historyManager.addHistory(subTask);

        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История содержит 3 задачи.");

        historyManager.remove(subTask.getId());
        history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История содержит 3 задачи.");
        assertEquals(task, history.get(0), "Порядок добавления.");
        assertEquals(epic, history.get(1), "Порядок добавления.");
    }

    @Test
    void getHistory() {
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "Пустая история задач.");
        assertTrue(history.isEmpty(), "Пустая история задач.");

        List<Task> history2 = historyManager.getHistory();

        assertEquals(history, history2, "Списки истории не равны");
    }
}