package ru.yandex.practicum.kanban.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.manager.StatusTask;
import ru.yandex.practicum.kanban.manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpicTest {

    TaskManager manager = Managers.getDefault();

    Epic epic;
    SubTask subTask;


    @BeforeEach
    void beforeAll() {
        epic = new Epic("Задача со временем",
                "Проверка записи в лист");
        manager.addTask(epic);
        subTask = new SubTask(
                LocalDateTime.of(2022, 8, 25, 10, 0),
                Duration.ofMinutes(59),
                "Задача со временем",
                "Проверка записи в лист",
                epic.getId());
        manager.addTask(subTask);
        subTask = new SubTask(
                LocalDateTime.of(2022, 8, 25, 11, 0),
                Duration.ofMinutes(59),
                "Задача со временем",
                "Проверка записи в лист",
                epic.getId());
        manager.addTask(subTask);
    }

    @Test
    void getSubTaskIds() {

        List<Epic> epicList = manager.getEpics();
        Epic epic = epicList.get(0);
        ArrayList<Integer> subTaskIds = epic.getSubTaskIds();
        SubTask subTask1 = manager.getSubTaskById(subTaskIds.get(0));
        SubTask subTask2 = manager.getSubTaskById(subTaskIds.get(1));


        assertNotNull(subTaskIds, "Задачи не получаются");
        assertEquals(2, subTaskIds.size(), "Неверное количество задач.");
        assertEquals(subTask1.getId(), subTaskIds.get(0), "Задачи не совпадают.");
        assertEquals(subTask2.getId(), subTaskIds.get(1), "Задачи не совпадают.");
    }


    @Test
    void updateEpicStatus() {//пустой список SubTask

        Epic epic = manager.getEpicById(1);

        assertEquals(StatusTask.NEW, epic.getStatus(), "Статусы не совпадают.");

        subTask.setStatus(StatusTask.IN_PROGRESS);
        manager.updateSubTask(subTask);
        Epic updatedEpic = manager.getEpicById(subTask.getEpicId());

        assertEquals(subTask.getStatus(), updatedEpic.getStatus(), "Статусы не изменились.");
        assertEquals(StatusTask.IN_PROGRESS, updatedEpic.getStatus(), "Статусы не совпадают.");

        manager.deleteAllSubTasks();
        updatedEpic = manager.getEpicById(subTask.getEpicId());

        assertEquals(StatusTask.NEW, updatedEpic.getStatus(), "Статусы не изменились.");


    }

    @Test
    void updateEpicStatusDone() { //SubTask DONE

        ArrayList<Integer> subTaskIds = epic.getSubTaskIds();
        SubTask subTask1 = manager.getSubTaskById(subTaskIds.get(0));
        SubTask subTask2 = manager.getSubTaskById(subTaskIds.get(1));

        assertEquals(StatusTask.NEW, epic.getStatus(), "Статусы не совпадают.");

        subTask1.setStatus(StatusTask.DONE);
        manager.updateSubTask(subTask1);
        subTask2.setStatus(StatusTask.DONE);
        manager.updateSubTask(subTask2);
        Epic updatedEpic = manager.getEpicById(subTask.getEpicId());

        assertEquals(StatusTask.DONE, updatedEpic.getStatus(), "Статусы не изменились.");
    }

    @Test
    void updateEpicStatusNewDone() {
        ArrayList<Integer> subTaskIds = epic.getSubTaskIds();
        SubTask subTask1 = manager.getSubTaskById(subTaskIds.get(0));

        assertEquals(StatusTask.NEW, epic.getStatus(), "Статусы не совпадают.");

        subTask1.setStatus(StatusTask.DONE);
        Epic updatedEpic = manager.getEpicById(subTask.getEpicId());
        manager.updateSubTask(subTask1);

        assertEquals(StatusTask.IN_PROGRESS, updatedEpic.getStatus(), "Статусы не совпадают.");
    }

    @Test
    void updateEpicStatusInProgress() {
        ArrayList<Integer> subTaskIds = epic.getSubTaskIds();
        SubTask subTask1 = manager.getSubTaskById(subTaskIds.get(0));
        SubTask subTask2 = manager.getSubTaskById(subTaskIds.get(1));

        assertEquals(StatusTask.NEW, epic.getStatus(), "Статусы не совпадают.");

        subTask1.setStatus(StatusTask.IN_PROGRESS);
        Epic updatedEpic = manager.getEpicById(subTask.getEpicId());
        manager.updateSubTask(subTask1);

        assertEquals(StatusTask.IN_PROGRESS, updatedEpic.getStatus(), "Статусы не совпадают.");

        subTask2.setStatus(StatusTask.IN_PROGRESS);
        manager.updateSubTask(subTask2);
        updatedEpic = manager.getEpicById(subTask.getEpicId());

        assertEquals(StatusTask.IN_PROGRESS, updatedEpic.getStatus(), "Статусы не изменились.");
    }

    @Test
    void updateEpicStatusNew() {
        ArrayList<Integer> subTaskIds = epic.getSubTaskIds();
        SubTask subTask1 = manager.getSubTaskById(subTaskIds.get(0));
        SubTask subTask2 = manager.getSubTaskById(subTaskIds.get(1));

        assertEquals(StatusTask.NEW, epic.getStatus(), "Статусы не совпадают.");

        subTask1.setStatus(StatusTask.DONE);
        Epic updatedEpic = manager.getEpicById(subTask.getEpicId());
        manager.updateSubTask(subTask1);

        assertEquals(StatusTask.IN_PROGRESS, updatedEpic.getStatus(), "Статусы не совпадают.");

        subTask1.setStatus(StatusTask.NEW);
        subTask2.setStatus(StatusTask.NEW);
        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);
        updatedEpic = manager.getEpicById(subTask.getEpicId());

        assertEquals(StatusTask.NEW, updatedEpic.getStatus(), "Статусы не изменились.");
    }


}