import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Scanner scanner = new Scanner(System.in);
        Scanner in = new Scanner(System.in);
        Task task1 = new Task("Выучить Java", "Заниматься каждый день", "NEW");

        Integer taskId1 = manager.addTask(task1);
        Task changeTask = manager.getTaskById(taskId1);
        changeTask.setStatus("IN_PROGRESS");
        manager.updateTask(changeTask);

        Epic epic2 = new Epic("Эпик с ID2", "Описание ", "NEW", null);//создаем эпик
        Integer epicId2 = manager.addEpicId(epic2);//получаем номер эпика

        SubTask subTask3 = new SubTask("Подзадача 3", " Описание", "NEW", epicId2);//создаем подзадачу для эпика 2
        SubTask subTask4 = new SubTask("Подзадача 4", " Описание", "NEW", epicId2);
        Integer subTaskId3 = manager.addSubTask(subTask3);//добавляем в таблицу подзадач и получаем номер
        Integer subTaskId4 = manager.addSubTask(subTask4);

        ArrayList<Integer> subTaskIds2 = new ArrayList<>();//создаем список подзадач для эпика
        subTaskIds2.add(subTaskId3);//заполняем
        subTaskIds2.add(subTaskId4);
        epic2.setSubTaskIds(subTaskIds2);

        manager.addEpic(epic2);
       /* Epic  changeEpic2 = manager.getEpicById(epicId2);
        changeEpic2.setSubTaskIds(subTaskIds2);//добавляем в эпик 2*/

        Epic epic5 = new Epic("Название 5", "Описание ", "NEW", null);//создаем эпик 5
        Integer epicId5 = manager.addEpicId(epic5);//получаем номер для подзадачи

        SubTask subTask6 = new SubTask("Подзадача 6", " Описание1", "NEW", epicId5);//создаем подзадачу
        Integer subTaskId6 = manager.addSubTask(subTask6);// записываем в таблицу
        ArrayList<Integer> subTaskIds5 = new ArrayList<>();//создаем список подзадач
        subTaskIds5.add(subTaskId6);//заполняем список
        epic5.setSubTaskIds(subTaskIds5);//добавляем список в эпик
        manager.addEpic(epic5);
       /* Epic  changeEpic5 = manager.getEpicById(epicId5);
        changeEpic5.setSubTaskIds(subTaskIds5);*/

        //SubTask changeSubTask = manager.getSubTaskById(subTaskId);



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
                    int choice1 = Integer.parseInt(scanner.nextLine());
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
                            System.out.println(" не верный выбор");
                    }
                    break;
                case "3"://удалить задачу по номеру
                    System.out.println("1 - удалить задачу.  2 - удалить подзадачу. 3 - удалить эпик.");
                    int choice2 = Integer.parseInt(scanner.nextLine());
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
                    Task newTask = new Task(name, description, "NEW");
                    manager.addTask(newTask);

                    break;
                case "5"://создать epic
                    System.out.println("Введите название эпика:");
                    String nameEpic = scanner.next();

                    System.out.println("Введите описание эпика:");
                    String descriptionEpic = scanner.next();
                    ArrayList<Integer> newSubTaskIds = new ArrayList<>();
                    Epic newEpic = new Epic(nameEpic, descriptionEpic, "NEW", newSubTaskIds);
                    Integer newEpicId = manager.addEpicId(newEpic);//получаем номер эпика для подзадачи


                    String input;
                    do {
                        System.out.println("Введите название подзадачи:");
                        String title = scanner.next();

                        System.out.println("Введите описание подзадачи:");
                        String descriptionSubTask = scanner.next();
                        SubTask newSubTask = new SubTask(title, descriptionSubTask, "NEW", newEpicId);//создаем подзадачу

                        Integer newSubTaskId = manager.addSubTask(newSubTask);//записываем в мапу получаем номер подзадачи
                        newSubTaskIds.add(newSubTaskId);//добавляем в список


                        System.out.println("Создать ещё подзадачу? Y - да/ N - нет");
                        input = in.next();
                    }
                    while (input.equals("Y"));
                    newEpic.setSubTaskIds(newSubTaskIds);//записываем список подзадач в эпик
                    manager.addEpic(newEpic);


                    break;


                case "6": //изменить статус задачи и распечатать

                    break;
                case "7"://изменить статус подзадачи и распечатать

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

        System.out.println("Какое действие вы хотите выполнить?\n");
        System.out.println("1 – Получить список всех задач.");
        System.out.println("2 – Удалить все задачи.");
        System.out.println("3 – Удалить задачу по идентификатору.");
        System.out.println("4 – Создать задачу.");
        System.out.println("5 – Создать эпик.");
        System.out.println("0 – Выход.");

    }
}