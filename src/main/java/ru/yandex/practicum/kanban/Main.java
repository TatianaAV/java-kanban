package ru.yandex.practicum.kanban;

import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.manager.StatusTask;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        Scanner scanner = new Scanner(System.in);
        Scanner in = new Scanner(System.in);

        exit:
        while (true) {

            printMenu();

            int command = Integer.parseInt(scanner.next());
            scanner.nextLine();
            switch (command) {
                case 1://получить список всех задач
                    List<Task> allTask = manager.getTasks();
                    if (allTask != null) {
                        for (int i = 0; i <= allTask.size() - 1; i++) {
                            System.out.println((allTask.get(i).getId()) + " " + allTask.get(i));
                        }
                    }
                    System.out.println("\n");

                    List<Epic> allEpics = manager.getEpics();
                    if (allEpics != null) {
                        for (int i = 0; i <= allEpics.size() - 1; i++) {
                            System.out.println((allEpics.get(i).getId()) + " " + allEpics.get(i));
                        }
                    }
                    System.out.println("\n");

                    List<SubTask> allSubTask = manager.getSubTasks();
                    if (allSubTask != null) {
                        for (int i = 0; i <= allSubTask.size() - 1; i++) {
                            System.out.println((allSubTask.get(i).getId()) + " " + allSubTask.get(i));
                        }
                    }

                    break;

                case 2://удалить все Tasks, Epics и subTasks
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

                case 3://удалить задачу по номеру
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

                case 4://создать задачу
                    System.out.println("Введите название задачи:");
                    String name = scanner.nextLine();

                    System.out.println("Введите описание задачи:");
                    String description = scanner.nextLine();

                    Task newTask = new Task(name, description);
                    manager.addTask(newTask);
                    break;

                case 5://создать epic
                    System.out.println("Введите название эпика:");
                    String nameEpic = scanner.nextLine();

                    System.out.println("Введите описание эпика:");
                    String descriptionEpic = scanner.nextLine();

                    Epic newEpic = new Epic(nameEpic, descriptionEpic);
                    manager.addTask(newEpic);//получаем номер эпика для подзадачи
                    int newEpicId = newEpic.getId();
                    String input;

                    do {
                        System.out.println("Введите название подзадачи:");
                        String title = scanner.nextLine();
                        System.out.println("Введите описание подзадачи:");
                        String descriptionSubTask = scanner.nextLine();
                        SubTask newSubTask = new SubTask(title, descriptionSubTask, newEpicId);//создаем подзадачу
                        manager.addTask(newSubTask);//записываем
                        System.out.println("Создать ещё подзадачу? Y - да/ N - нет");
                        input = in.next();
                    }
                    while (input.equals("Y"));
                    break;

                case 6: //Вывести список подзадач эпика.
                    System.out.println("Введите номер эпика:");
                    int epicId = Integer.parseInt(scanner.next());
                    System.out.println(manager.getSubTasksByEpic(epicId));
                    break;
                case 7://изменить статус задачи и подзадачи и распечатать
                    System.out.println("1 - обновить задачу.  2 - обновить подзадачу.");
                    int choice3 = Integer.parseInt(scanner.next());
                    switch (choice3) {
                        case 1:
                            System.out.println("введите номер задачи для обновления");
                            int taskIdByUpgrade = scanner.nextInt();

                            Task changeTask = manager.getTaskById(taskIdByUpgrade);
                            if (changeTask != null) {
                                System.out.println("Выберете статус для задачи.\n 1 - NEW.\n 2 - IN_PROGRESS.\n 3 - DONE.");
                                int status1 = scanner.nextInt();

                                if (status1 == 1) {
                                    changeTask.setStatus(StatusTask.NEW);

                                } else if (status1 == 2) {
                                    changeTask.setStatus(StatusTask.IN_PROGRESS);

                                } else if (status1 == 3) {
                                    changeTask.setStatus(StatusTask.DONE);

                                } else {
                                    System.out.println("не верный выбор");
                                }

                                manager.updateTask(changeTask);
                                System.out.println(changeTask);
                            } else {
                                System.out.println("Задачи с " + taskIdByUpgrade +
                                        " номером нет.");
                            }
                            break;
                        case 2:
                            System.out.println("введите номер подзадачи для обновления");
                            int subTaskIdByUpgrade = scanner.nextInt();

                            SubTask changeSubTask = manager.getSubTaskById(subTaskIdByUpgrade);
                            if (changeSubTask != null) {
                                System.out.println("Выберете статус для задачи.\n 1 - NEW.\n 2 - IN_PROGRESS.\n 3 - DONE.");
                                int status2 = scanner.nextInt();

                                if (status2 == 1) {
                                    changeSubTask.setStatus(StatusTask.NEW);

                                } else if (status2 == 2) {
                                    changeSubTask.setStatus(StatusTask.IN_PROGRESS);

                                } else if (status2 == 3) {
                                    changeSubTask.setStatus(StatusTask.DONE);

                                } else {
                                    System.out.println("не верный выбор");
                                }
                                //отправить для изменения подзадачу
                               manager.updateSubTask(changeSubTask);
                                SubTask subTask = manager.getSubTaskById(subTaskIdByUpgrade);
                                System.out.println("Подзадача " + subTask
                                        + " статус эпика " + subTask.getStatus());
                            } else {
                                System.out.println("Подзадачи c " + subTaskIdByUpgrade +
                                        " номером нет");
                            }
                            break;

                        default:
                            System.out.println("не верный выбор");
                    }
                    break;

                case 8:
                    List<Task> history = manager.getHistoryManager();
                    for (int i = 0; i <= history.size() - 1; i++) {
                        System.out.println((i + 1) + " " + history.get(i));

                    }

                    break;

                case 9:
                    System.out.println("1 - Получить задачу.\n2 - Получить эпик.\n3 - Получить подзадачу.");
                    int choice4 = Integer.parseInt(scanner.next());
                    switch (choice4) {
                        case 1:
                            System.out.println("введите номер задачи");
                            int taskIdByUpgrade = scanner.nextInt();

                            Task task = manager.getTaskById(taskIdByUpgrade);
                            System.out.println(task);
                            break;
                        case 2:
                            System.out.println("введите номер эпика");
                            int epic = scanner.nextInt();

                            Epic printEpic = manager.getEpicById(epic);
                            System.out.println(printEpic);
                            break;
                        case 3:

                            System.out.println("введите номер подзадачи");
                            int subTask = scanner.nextInt();

                            SubTask printSubTask = manager.getSubTaskById(subTask);
                            System.out.println(printSubTask);
                            break;
                        default:
                            System.out.println("не верный выбор");
                    }
                    break;

                case 10:
                    System.out.println("Создание задач");

                    Task task1 = new Task("Задача 1", "Описание задачи 1");
                    Task task2 = new Task("Задача 2", "Описание задачи 2");
                    manager.addTask(task1);
                    manager.addTask(task2);

                    Epic epic3 = new Epic("Название эпика 3", "Описание 3");//создаем эпик
                    manager.addTask(epic3);//получаем номер эпика
                    int epicId3 = epic3.getId();
                    SubTask subTask4 = new SubTask("Подзадача 4", " Описание подзадачи 4", epicId3);
                    SubTask subTask5 = new SubTask("Подзадача 5", " Описание подзадачи 5", epicId3);
                    SubTask subTask6 = new SubTask("Подзадача 6", " Описание подзадачи 6", epicId3);
                    manager.addTask(subTask4);//добавляем в таблицу подзадач и получаем номер
                    manager.addTask(subTask5);
                    manager.addTask(subTask6);


                    Epic epic7 = new Epic("Название эпика 7", "Описание 7");
                    manager.addTask(epic7);

                    System.out.println("Печать всех Задач");

                    System.out.println(manager.getTasks());
                    System.out.println(manager.getEpics());
                    System.out.println(manager.getSubTasks());

                    System.out.println("Просмотр задач по номеру 1 - 2 - 1");

                    System.out.println(manager.getTaskById(1));
                    System.out.println(manager.getTaskById(2));
                    System.out.println(manager.getTaskById(1));

                    System.out.println("История просмотра");
                    System.out.println(manager.getHistoryManager());

                    System.out.println("Просмотр задач по номеру 3- 7 -7");
                    manager.getEpicById(3);
                    manager.getEpicById(7);
                    manager.getEpicById(7);

                    System.out.println("История просмотра");
                    System.out.println(manager.getHistoryManager());

                    System.out.println("Просмотр задач по номеру 4 5 6 4 5");
                    System.out.println(manager.getSubTaskById(4));
                    System.out.println(manager.getSubTaskById(5));
                    System.out.println(manager.getSubTaskById(6));
                    System.out.println(manager.getSubTaskById(7));//null
                    System.out.println(manager.getSubTaskById(8));//null
                    System.out.println(manager.getSubTaskById(4));
                    System.out.println(manager.getSubTaskById(5));

                    System.out.println("История просмотра");
                    System.out.println(manager.getHistoryManager());

                    System.out.println("Удаление задач по номеру 1 5 3");
                    //manager.deleteTask(1);
                    manager.deleteAllTask();

                    System.out.println("История просмотра");
                    System.out.println(manager.getHistoryManager());

                    //manager.deleteSubTask(5);
                    //manager.deleteAllSubTasks();

                    System.out.println("История просмотра");
                    System.out.println(manager.getHistoryManager());

                    //manager.deleteEpic(3);
                    manager.deleteAllEpic();

                    System.out.println("История просмотра");
                    System.out.println(manager.getHistoryManager());

                    System.out.println("Все задачи");
                    System.out.println(manager.getTasks());
                    System.out.println(manager.getEpics());
                    System.out.println(manager.getSubTasks());

                    break;

                case 0:
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
        System.out.println("8 – История просмотра.");
        System.out.println("9 – Получить задачу по номеру.");
        System.out.println("10 – ТЕСТ.");
        System.out.println("0 – Выход.");

    }
}