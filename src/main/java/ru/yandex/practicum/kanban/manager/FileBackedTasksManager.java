package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.manager.emums.StatusTask;
import ru.yandex.practicum.kanban.manager.exceptions.ManagerSaveException;
import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;
import ru.yandex.practicum.kanban.tasks.TypeTasks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public static void main(String[] args) {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager();

        try {
            Task taskTime1 = new Task(
                    LocalDateTime.of(2022, 8, 25, 10, 0),
                    Duration.ofMinutes(14), "Задача со временем",
                    "Проверка записи в лист  c startTime");
            tasksManager.addTask(taskTime1);

            Task taskTime2 = new Task(
                    LocalDateTime.of(2022, 8, 25, 10, 16),
                    Duration.ofMinutes(14), "Задача со временем2",
                    "Проверка записи в лист c startTime2");
            tasksManager.addTask(taskTime2);
            System.out.println("Создание задач 1 ");
            tasksManager.addTask(new Task(LocalDateTime.of(2022, 8, 10, 10, 0), Duration.ofMinutes(10), "Сентябрь", "Отправить сына в институт"));

            tasksManager.getPrioritizedTasks().forEach(System.out::println);

            Epic epic2 = new Epic("Октябрь", "Съездить в отпуск");
            tasksManager.addTask(epic2);
            tasksManager.addTask(new SubTask("Отпуск", "Привет кукушечка", epic2.getId()));
            tasksManager.addTask(new SubTask("Школа", "Репетиторы", epic2.getId()));


            //просмотр задач
            System.out.println("История просмотра после создания: " + "\n");
            tasksManager.getHistoryManager().forEach(System.out::println);
            tasksManager.getTaskById(1);
            tasksManager.getSubTaskById(5);
            tasksManager.getSubTaskById(6);
            tasksManager.getSubTaskById(6);
            System.out.println("История просмотра после просмотра: 1 5 6 6 " + "\n" + CSVFormatter.historyToString(tasksManager.historyManager));

            System.out.println("Чтение из файла: 1 " + "\n");

            tasksManager = loadFromFile(new File("resources/tasks.csv"));


            Task task1 = tasksManager.getTaskById(1);
            task1.setStatus(StatusTask.IN_PROGRESS);
            tasksManager.getEpicById(2);

            System.out.println("\n" + "История просмотра после просмотра: 1 2 " + "\n"
                    + CSVFormatter.historyToString(tasksManager.historyManager));

            System.out.println("\n" + "Создание задач 2 ");

            tasksManager.addTask(new Epic("Ноябрь", "Скоро новый год!"));
            tasksManager.addTask(new Task("Декабрь", "Скоро новый год"));
            tasksManager.addTask(new Epic("Ноябрь", "Description November Epic 5"));
            tasksManager.getTaskById(3);
            tasksManager.getEpicById(5);

            System.out.println("История просмотра после просмотра: 3 5 " + "\n" + CSVFormatter.historyToString(tasksManager.historyManager));

            System.out.println("Чтение из файла: 2 " + "\n");

            tasksManager = loadFromFile(new File("resources/tasks.csv"));
            //tasksManager.getTasks().forEach(System.out::println);
            //tasksManager.getEpics().forEach(System.out::println);
            //tasksManager.getSubTasks().forEach(System.out::println);

        } catch (NullPointerException e) {
            System.out.println("Ошибка чтения файла");
        }
    }

    protected void save() {
        try (FileWriter fileWriter = new FileWriter("resources/tasks.csv")) {
            fileWriter.write("startTime,duration,id,type,name,status,description,epic" + System.lineSeparator());
            for (Task task : tasksMap.values()) {
                fileWriter.write(task.toCSVDescription() + System.lineSeparator());
            }
            for (Epic epic : epicsMap.values()) {
                fileWriter.write(epic.toCSVDescription() + System.lineSeparator());
            }
            for (SubTask subTask : subTasksMap.values()) {
                fileWriter.write(subTask.toCSVDescription() + System.lineSeparator());
            }
            fileWriter.write(System.lineSeparator());
            fileWriter.write(CSVFormatter.historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка записи файла.", e);
        }
    }


    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addTask(Epic epic) {
        super.addTask(epic);
        save();
    }

    @Override
    public void addTask(SubTask subTask) {
        super.addTask(subTask);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        var task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpicTime(int id) {
        super.updateEpicTime(id);
        save();
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteTask(Integer taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteSubTask(Integer subTaskId) {
        super.deleteSubTask(subTaskId);
        save();
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        save();
    }

    public static FileBackedTasksManager loadFromFile(File file) {


        FileBackedTasksManager tasksManager = new FileBackedTasksManager();
        //Для всех прочитанных эпиков нужно рассчитать endTime.
        try {
            String csv = Files.readString(file.toPath()); //читает файл
            if (!csv.isBlank()) {
                String[] lines = csv.split(System.lineSeparator());
                for (int i = 1; i < lines.length; i++) {
                    if (!lines[i].isEmpty()) {
                        Task task = CSVFormatter.fromString(lines[i]);

                        if (task != null) {
                            int id = task.getId();
                            tasksManager.generatedId = id;
                            TypeTasks type = task.getType();
                            switch (type) {
                                case TASK:
                                    tasksManager.tasksMap.put(id, task);
                                    break;
                                case EPIC:
                                    tasksManager.epicsMap.put(id, (Epic) task);
                                    break;
                                case SUBTASK:
                                    tasksManager.subTasksMap.put(id, (SubTask) task);
                                    int epicId = tasksManager.subTasksMap.get(id).getEpicId();
                                    ArrayList<Integer> listIdSubTask =
                                            tasksManager.epicsMap.get(epicId).getSubTaskIds();
                                    listIdSubTask.add(id);
                            }
                        }
                    } else {
                        for (Integer taskId : CSVFormatter.historyFromString(lines[i + 1])) {
                            if (tasksManager.tasksMap.containsKey(taskId)) {
                                tasksManager.getTaskById(taskId);
                            } else if (tasksManager.epicsMap.containsKey(taskId)) {
                                tasksManager.getEpicById(taskId);
                            } else if (tasksManager.subTasksMap.containsKey(taskId)) {
                                tasksManager.getSubTaskById(taskId);
                            }
                        }
                        break;
                    }
                }
            } else {
                System.out.println("Файл пуст");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не могу прочитать файл: " + file.getName(), e);
        }
        return tasksManager;
    }
}

