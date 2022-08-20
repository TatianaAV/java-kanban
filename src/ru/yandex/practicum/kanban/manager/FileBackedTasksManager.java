package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;
import ru.yandex.practicum.kanban.tasks.TypeTasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager {

    public static void main(String[] args) throws FileNotFoundException {

       loadFromFile(new File("resources/tasks.csv"));
    }

    protected void save() {
        try (FileWriter fileWriter = new FileWriter("resources/tasks.csv")) {
            fileWriter.write("id,type,name,status,description,epic" + System.lineSeparator());
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
            System.out.println("Произошла ошибка чтения файла.");
        }
    }



        @Override
        public int addTask (Task task){
            int id = super.addTask(task);
            save();
            return id;
        }

        @Override
        public int addTask (Epic epic){
            int id = super.addTask(epic);
            save();
            return id;
        }

        @Override
        public int addTask (SubTask subTask){
            int id = super.addTask(subTask);
            save();
            return id;
        }

        @Override
        public Task getTaskById ( int id){
            var task = super.getTaskById(id);
            save();
            return task;
        }

        @Override
        public Epic getEpicById ( int id){
            Epic epic = super.getEpicById(id);
            save();
            return epic;
        }

        @Override
        public void updateTask (Task task){
            super.updateTask(task);
            save();
        }

        @Override
        public void updateEpic (Epic epic){
            super.updateEpic(epic);
            save();
        }

        @Override
        public StatusTask updateSubTask (SubTask subTask){
            StatusTask status = super.updateSubTask(subTask);
            save();
            return status;
        }

        @Override
        public SubTask getSubTaskById ( int id){
            SubTask subTask = super.getSubTaskById(id);
            save();
            return subTask;
        }

        @Override
        public void deleteAllTask () {
            super.deleteAllTask();
            save();
        }

        @Override
        public void deleteAllSubTasks () {
            super.deleteAllSubTasks();
            save();
        }

        @Override
        public void deleteAllEpic () {
            super.deleteAllEpic();
            save();
        }

        @Override
        public void deleteTask (Integer taskId){
            super.deleteTask(taskId);
            save();
        }

        @Override
        public void deleteSubTask (Integer subTaskId){
            super.deleteSubTask(subTaskId);
            save();
        }

        @Override
        public void deleteEpic ( int epicId){
            super.deleteEpic(epicId);
            save();
        }

        public static void loadFromFile (File file) throws FileNotFoundException {
            System.out.println("Проверка загрузки из файла " + file);
            FileBackedTasksManager tasksManager = new FileBackedTasksManager();
            try {

                System.out.println("Создание задач 1 ");
        tasksManager.addTask(new Task("Сентябрь", "Отправить сына в институт"));
        Epic epic2 = new Epic("Октябрь", "Съездить в отпуск");
        tasksManager.addTask(epic2);
        tasksManager.addTask(new SubTask("Отпуск", "Привет кукушечка", epic2.getId()));
        tasksManager.addTask(new SubTask("Школа", "Репетиторы", epic2.getId()));
        //просмотр задач
        System.out.println("История просмотра после создания: " + "\n" + tasksManager.getHistoryManager());

        tasksManager.getTaskById(1);
        tasksManager.getSubTaskById(3);
        tasksManager.getSubTaskById(3);
        tasksManager.getSubTaskById(4);
        System.out.println("История просмотра после просмотра: 1 3 3 4 " + "\n" + CSVFormatter.historyToString(tasksManager.historyManager));
        System.out.println("Чтение из файла: 1 " + "\n");

                try {
                    String csv = Files.readString(file.toPath()); //читает файл
                    if (!csv.isBlank()) {
                        String[] lines = csv.split(System.lineSeparator());
                        for (int i = 1; i < lines.length; i++) {
                            if (!lines[i].isEmpty()) {
                                Task task = CSVFormatter.fromString(lines[i]);
                                int id = 0;
                                if (task != null) {
                                    id = task.getId();
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
                                            int epicId = tasksManager.getSubTaskById(id).getEpicId();
                                            ArrayList<Integer> listIdSubTask = tasksManager.getSubTaskIds(tasksManager.getEpicById(epicId));
                                            listIdSubTask.add(id);
                                    }
                                }
                            } else {
                                List<Task> historyList = tasksManager.getHistoryManager();
                                List<Integer> history = CSVFormatter.historyFromString(lines[i + 1]);
                                for (Integer taskId : history) {
                                    if (tasksManager.tasksMap.containsKey(taskId)) {
                                        historyList.add(tasksManager.getTaskById(taskId));
                                    } else if (tasksManager.epicsMap.containsKey(taskId)) {
                                        historyList.add(tasksManager.getEpicById(taskId));

                                    } else if (tasksManager.subTasksMap.containsKey(taskId)) {
                                        historyList.add(tasksManager.getSubTaskById(taskId));
                                    }
                                }
                                break;
                            }
                        }
                    } else {
                        System.out.println("Файл пуст");
                    }
                } catch(IOException e){
                    throw new ManagerSaveException("Не могу прочитать файл: " + file.getName(), e);
                }


        tasksManager.getTasks().forEach(System.out::println);
        tasksManager.getEpics().forEach(System.out::println);
        tasksManager.getSubTasks().forEach(System.out::println);

        System.out.println("\n" + "Создание задач 2 ");
        tasksManager.addTask(new Task("Декабрь", "Скоро новый год"));
        tasksManager.addTask(new Epic("Ноябрь", "Так далеко"));

        tasksManager.getTaskById(5);
        tasksManager.getEpicById(6);

        System.out.println("История просмотра после просмотра: 5 6 " + "\n" + CSVFormatter.historyToString(tasksManager.historyManager));

        System.out.println("Чтение из файла: 2 " + "\n");

                FileBackedTasksManager tasksManager2 = new FileBackedTasksManager();

                try {
                    String csv = Files.readString(file.toPath()); //читает файл
                    if (!csv.isBlank()) {
                        String[] lines = csv.split(System.lineSeparator());
                        for (int i = 1; i < lines.length; i++) {
                            if (!lines[i].isEmpty()) {
                                Task task = CSVFormatter.fromString(lines[i]);
                                int id = 0;
                                if (task != null) {
                                    id = task.getId();
                                    tasksManager2.generatedId = id;
                                    TypeTasks type = task.getType();
                                    switch (type) {
                                        case TASK:
                                            tasksManager2.tasksMap.put(id, task);
                                            break;
                                        case EPIC:
                                            tasksManager2.epicsMap.put(id, (Epic) task);
                                            break;
                                        case SUBTASK:
                                            tasksManager2.subTasksMap.put(id, (SubTask) task);
                                            int epicId = tasksManager.getSubTaskById(id).getEpicId();
                                            ArrayList<Integer> listIdSubTask = tasksManager.getSubTaskIds(tasksManager.getEpicById(epicId));
                                            listIdSubTask.add(id);
                                    }
                                }
                            } else {
                                List<Task> historyList = tasksManager2.getHistoryManager();
                                List<Integer> history = CSVFormatter.historyFromString(lines[i + 1]);
                                for (Integer taskId : history) {
                                    if (tasksManager2.tasksMap.containsKey(taskId)) {
                                        historyList.add(tasksManager2.getTaskById(taskId));
                                    } else if (tasksManager2.epicsMap.containsKey(taskId)) {
                                        historyList.add(tasksManager2.getEpicById(taskId));

                                    } else if (tasksManager2.subTasksMap.containsKey(taskId)) {
                                        historyList.add(tasksManager2.getSubTaskById(taskId));
                                    }
                                }
                                break;
                            }
                        }
                    } else {
                        System.out.println("Файл пуст");
                    }
                } catch(IOException e){
                    throw new ManagerSaveException("Не могу прочитать файл: " + file.getName(), e);
                }
        tasksManager2.getTasks().forEach(System.out::println);
        tasksManager2.getEpics().forEach(System.out::println);
        tasksManager2.getSubTasks().forEach(System.out::println);
        Task task1 = tasksManager2.getTaskById(1);
        task1.setStatus(StatusTask.IN_PROGRESS);
        tasksManager.getEpicById(2);
        System.out.println("\n" + "История просмотра после просмотра: 1 2 " + "\n"
                + CSVFormatter.historyToString(tasksManager.historyManager));
                tasksManager2.getTasks().forEach(System.out::println);
                tasksManager2.getEpics().forEach(System.out::println);
                tasksManager2.getSubTasks().forEach(System.out::println);
            } catch (NullPointerException e) {
                System.out.println("Ошибка " + e);
            }
        }
}