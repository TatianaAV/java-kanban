package ru.yandex.practicum.kanban;

import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Scanner scanner = new Scanner(System.in);
        Scanner in = new Scanner(System.in);
        Task task1 = new Task("Выучить Java", "Заниматься каждый день");
        manager.addTask(task1);

        Epic epic2 = new Epic("Эпик с ID2", "Описание ", "NEW", null);//создаем эпик
        int epicId2 = manager.addEpic(epic2);//получаем номер эпика

        SubTask subTask3 = new SubTask("Подзадача 3", " Описание", "NEW", epicId2);//создаем подзадачу для эпика 2
        SubTask subTask4 = new SubTask("Подзадача 4", " Описание", "NEW", epicId2);
        manager.addSubTask(subTask3);//добавляем в таблицу подзадач и получаем номер
        manager.addSubTask(subTask4);


        Epic epic5 = new Epic("Название 5", "Описание ", "NEW", null);//создаем эпик 5
        int epicId5 = manager.addEpic(epic5);//получаем номер для подзадачи

        SubTask subTask6 = new SubTask("Подзадача 6", " Описание1", "NEW", epicId5);//создаем подзадачу
        manager.addSubTask(subTask6);// записываем в таблицу

        exit:
        while (true) {

            printMenu();

            String command = scanner.next();

            switch (command) {
                case "1"://получить список всех задач
                    System.out.println(manager.getTasks());
                    System.out.println(manager.getEpics());
                    System.out.println(manager.getSubTasks());
                    break;

                case "2"://удалить все Tasks, Epics и subTasks
                    System.out.println("1 - удалить задачи.  2 - удалить подзадачи. 3 - удалить эпики.");
                    int choice1 = Integer.parseInt(scanner.next());

                    switch (choice1) {
                        case 1:
                            manager.deleteAllTask();
                            break;

                        case 2:
                            manager.deleteAllSubTasks();
                            break;

                        case 3:
                            manager.deleteAllEpic();
                            break;

                        default:
                            System.out.println("не верный выбор");
                    }
                    break;

                case "3"://удалить задачу по номеру
                    System.out.println("1 - удалить задачу.  2 - удалить подзадачу. 3 - удалить эпик.");
                    int choice2 = Integer.parseInt(scanner.next());

                    switch (choice2) {
                        case 1:
                            System.out.println("введите номер задачи для удаления");
                            int taskIdByDelete = scanner.nextInt();
                            manager.deleteTask(taskIdByDelete);

                            break;

                        case 2:
                            System.out.println("введите номер подзадачи для удаления");
                            int subTaskIdByDelete = scanner.nextInt();
                            manager.deleteSubTask(subTaskIdByDelete);


                            break;

                        case 3:
                            System.out.println("введите номер эпика для удаления");
                            int epicIdByDelete = scanner.nextInt();


                            manager.deleteEpic(epicIdByDelete);

                            break;
                        default:

                            System.out.println(" не верный выбор");
                    }
                    break;

                case "4"://создать задачу
                    System.out.println("Введите название задачи:");
                    String name = scanner.next();

                    System.out.println("Введите описание задачи:");
                    String description = scanner.next();
                    Task newTask = new Task(name, description);
                    manager.addTask(newTask);
                    break;

                case "5"://создать epic
                    System.out.println("Введите название эпика:");
                    String nameEpic = scanner.next();

                    System.out.println("Введите описание эпика:");
                    String descriptionEpic = scanner.next();
                    Epic newEpic = new Epic(nameEpic, descriptionEpic, "", null);
                    int newEpicId = manager.addEpic(newEpic);//получаем номер эпика для подзадачи
                    String input;

                    do {
                        System.out.println("Введите название подзадачи:");
                        String title = scanner.next();
                        System.out.println("Введите описание подзадачи:");
                        String descriptionSubTask = scanner.next();
                        SubTask newSubTask = new SubTask(title, descriptionSubTask, "NEW", newEpicId);//создаем подзадачу
                        manager.addSubTask(newSubTask);//записываем
                        System.out.println("Создать ещё подзадачу? Y - да/ N - нет");
                        input = in.next();
                    }
                    while (input.equals("Y"));
                    break;

                case "6": //Вывести список подзадач эпика.
                    System.out.println("Введите номер эпика:");
                    int epicId = Integer.parseInt(scanner.next());
                    System.out.println(manager.getSubTasksByEpic(epicId));
                    break;
                case "7"://изменить статус задачи и подзадачи и распечатать
                    System.out.println("1 - обновить задачу.  2 - обновить подзадачу.");
                    int choice3 = Integer.parseInt(scanner.next());
                    switch (choice3) {
                        case 1:
                            System.out.println("введите номер задачи для обновления");
                            int taskIdByUpgrade = scanner.nextInt();
                            Task changeTask = manager.getTaskById(taskIdByUpgrade);
                            System.out.println("Выберете статус для задачи. 1 - IN_PROGRESS. 2 - DONE");
                            int status1 = scanner.nextInt();

                            if (status1 == 1) {
                                changeTask.setStatus("IN_PROGRESS");
                            } else if (status1 == 2) {
                                changeTask.setStatus("DONE");
                            } else {
                                System.out.println("не верный выбор");
                            }

                            manager.updateTask(changeTask);
                            System.out.println(changeTask);
                            break;
                        case 2:
                            System.out.println("введите номер подзадачи для обновления");
                            int subTaskIdByUpgrade = scanner.nextInt();
                            SubTask changeSubTask = manager.getSubTaskById(subTaskIdByUpgrade);

                            if (changeSubTask != null) {
                                System.out.println("Выберете статус для задачи. 1 - NEW. 2 - IN_PROGRESS. 3 - DONE.");
                                int status2 = scanner.nextInt();

                                if (status2 == 1) {
                                    changeSubTask.setStatus("NEW");

                                } else if (status2 == 2) {
                                    changeSubTask.setStatus("IN_PROGRESS");

                                } else if (status2 == 3) {
                                    changeSubTask.setStatus("DONE");

                                } else {
                                    System.out.println("не верный выбор");
                                }
                                //отправить для изменения подзадачу
                                String newStatusEpic1 = manager.updateSubTask(changeSubTask);
                                System.out.println("Подзадача " + manager.getSubTaskById(subTaskIdByUpgrade) + " статус эпика " + (newStatusEpic1));
                            } else {
                                System.out.println("Подзадачи c таким номером нет");
                            }
                            break;

                        default:
                            System.out.println("не верный выбор");
                    }
                    break;

                case "0":
                    break exit;

                default:
                    System.out.println("Такой команды нет, попробуйте еще раз.");
                    break;
            }

        }

    }

    private static void printMenu() {

        System.out.println("\nКакое действие вы хотите выполнить?\n");
        System.out.println("1 – Получить список всех задач.");
        System.out.println("2 – Удалить все задачи.");
        System.out.println("3 – Удалить задачу по идентификатору.");
        System.out.println("4 – Создать задачу.");
        System.out.println("5 – Создать эпик.");
        System.out.println("6 – Вывести список подзадач эпика.");
        System.out.println("7 – Обновить задачи.");
        System.out.println("0 – Выход.");

    }
}