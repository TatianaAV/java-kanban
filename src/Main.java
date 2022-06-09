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


        //manager.addTaskManager("Не сойти с ума", "Не забывать отдыхать и  не лениться ходить в спортзал");
       /* manager.addEpicManager("Не сойти с ума", "Не забывать отдыхать и  не лениться ходить в спортзал");
        manager.addSubTaskManager("подзадача 1", "Не забывать отдыхать и  не лениться ходить в спортзал");*/
        exit:
        while (true) {

            printMenu();

            String command = scanner.nextLine();

            switch (command) {
                case "1"://получить список всех задач
                    manager.getTasks();
                    manager.getEpics();
                    manager.getSubTasks();
                    break;
                case "2"://удалить все Tasks, Epics и subTasks
                    manager.deleteAllTask();
                    manager.deleteAllSubTasks();
                    manager.deleteAllEpic();
                    break;
                case "3"://удалить задачу по номеру
                   /* System.out.println("введите номер задачи для удаления");
                    int taskIdВуDelete = scanner.nextInt();
                    manager.deleteTask(taskIdВуDelete);

                    System.out.println("введите номер подзадачи для удаления");
                    int subTaskIdВуDelete = scanner.nextInt();
                    manager.deleteSubTask(subTaskIdВуDelete);
*/
                    System.out.println("введите номер эпика для удаления");
                    int epicIdВуDelete = scanner.nextInt();

                   // manager.deleteEpic(epicIdВуDelete);
                    break;
                case "4"://создать задачу

                    System.out.println("Введите название задачи:");
                    String name = scanner.nextLine();

                    System.out.println("Введите описание задачи:");
                    String description = scanner.nextLine();
                    Task newTask = new Task(name, description, "NEW");
                    manager.addTask(newTask);

                    break;
                case "5"://создать epic
                    System.out.println("Введите название эпика:");
                    String nameEpic = scanner.nextLine();

                    System.out.println("Введите описание эпика:");
                    String descriptionEpic = scanner.nextLine();
                    ArrayList<Integer> newSubTaskIds = new ArrayList<>();
                    Epic newEpic = new Epic(nameEpic, descriptionEpic, "NEW", newSubTaskIds);
                    Integer newEpicId = manager.addEpicId(newEpic);//получаем номер эпика для подзадачи


                    String input;
                    do {
                        System.out.println("Введите название подзадачи:");
                        String title = scanner.nextLine();

                        System.out.println("Введите описание подзадачи:");
                        String descriptionSubTask = scanner.nextLine();
                        SubTask newSubTask = new SubTask(title, descriptionSubTask, "NEW", newEpicId);//создаем подзадачу

                        Integer newSubTaskId = manager.addSubTask(newSubTask);//записываем в мапу получаем номер подзадачи
                        newSubTaskIds.add(newSubTaskId);//добавляем в список



                        System.out.println("Создать ещё подзадачу? Y - да/ N - нет");
                        input = in.next();
                    }
                    while (input.equals("Y"));
                    newEpic.setSubTaskIds(newSubTaskIds);
                    manager.addEpic(newEpic);

                  //записываем список подзадач в эпик

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
        System.out.println("0 – Break .");

    }
}