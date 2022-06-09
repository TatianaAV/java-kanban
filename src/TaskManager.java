import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasksMap = new HashMap<>();
    HashMap<Integer, Epic> epicsMap = new HashMap<>();
    HashMap<Integer, SubTask> subTasksMap = new HashMap<>();

    private int generatedId = 0;

    public int generatedId() {
        ++generatedId;
        return generatedId;
    }


    public Integer addTask(Task task) {
        int id = generatedId();
        task.setId(id);
        tasksMap.put(id, task);

        return id;
    }

    public Task getTaskById(int id) {
        if (!tasksMap.containsKey(id)) {
            return null;
        }
        return tasksMap.get(id);
    }

    public Epic getEpicById(int id) {
        if (!epicsMap.containsKey(id)) {
            return null;
        }
        return epicsMap.get(id);
    }

    public SubTask getSubTaskById(int subTaskId) {
        if (!subTasksMap.containsKey(subTaskId)) {
            return null;
        }
        return subTasksMap.get(subTaskId);
    }


    public void addEpic(Epic epic) {
       // int epicId = generatedId();
       int epicId = epic.getId();
        epicsMap.put(epicId, epic);
        System.out.println(epicsMap);
        //return epicId;

    }
    Integer addEpicId(Epic epic) {
        int epicId = generatedId();
        epic.setId(epicId);
        //epicsMap.put(epicId, epic);

        return epicId;

    }

    Integer addSubTask(SubTask subTask) {
        int subTaskId = generatedId();
        subTask.setId(subTaskId);
        subTasksMap.put(subTaskId, subTask);

        return subTaskId;
    }



    public void updateTask(Task task) {

    }

    public void updateEpic(Epic epic) {

    }

    public void updateSubTask(SubTask subTask) {

    }

    public ArrayList<Task> getTasks() {
        if (tasksMap.isEmpty()) {
            return null;
        }
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasksMap.values());
        System.out.println(allTasks);

        return allTasks;
    }

    public ArrayList<Epic> getEpics() {
        if (epicsMap.isEmpty()) {
            return null;
        }
        ArrayList<Epic> allEpics = new ArrayList<>();
        allEpics.addAll(epicsMap.values());
        System.out.println(allEpics);

        return allEpics;
    }

    public ArrayList<SubTask> getSubTasks() {
        if (subTasksMap.isEmpty()) {
            return null;
        }
        ArrayList<SubTask> allSubTasks = new ArrayList<>();
        allSubTasks.addAll(subTasksMap.values());
        System.out.println(allSubTasks);

        return allSubTasks;
    }


    public void deleteAllTask() {
        if (!tasksMap.isEmpty()) {
            tasksMap.clear();
            System.out.println("Задачи удалены.");
            System.out.println(tasksMap);
        } else {
            System.out.println("Задач нет.");
        }
    }

    public void deleteAllSubTasks() {
        if (!subTasksMap.isEmpty()) {
            subTasksMap.clear();
            System.out.println("Подзадачи удалены.");
            System.out.println(subTasksMap);
        } else {
            System.out.println("Подзадач нет.");
        }
    }

    public void deleteAllEpic() {//SubTasks не может быть без Epics

        if (!epicsMap.isEmpty() & !subTasksMap.isEmpty()) {
            subTasksMap.clear();
            epicsMap.clear();
            System.out.println("Эпики и подзадачи удалены.");
            System.out.println(epicsMap);
            System.out.println(subTasksMap);
        } else if (!epicsMap.isEmpty() & subTasksMap.isEmpty()) {
            epicsMap.clear();
            System.out.println("Эпики и подзадачи удалены.");
            System.out.println(epicsMap);
            System.out.println(subTasksMap);

        } else {
            System.out.println("Эпиков нет.");
        }
    }

    public void deleteTask(Integer taskId) {

        if (tasksMap.containsKey(taskId)) {
            tasksMap.remove(taskId);
            System.out.println("Задача удалена.");
            System.out.println(tasksMap.get(taskId));
            System.out.println(tasksMap);

        } else { System.out.println(tasksMap);
            System.out.println("Такой задачи нет.");
        }
    }

    public void deleteSubTask(Integer subTaskId) {
        if (subTasksMap.containsKey(subTaskId)) {
            subTasksMap.remove(subTaskId);
            System.out.println("Подзадача удалена.");
            System.out.println(subTasksMap.get(subTaskId));
            System.out.println(subTasksMap);

        } else { System.out.println(subTasksMap);
            System.out.println("Такой подзадачи нет.");
        }

    }

   /* public void deleteEpic(Integer epicId) {
        if (!epicsMap.isEmpty()) {
            System.out.println(epicsMap);
            for (Integer id : epicsMap.keySet()) {

                if (id.equals(epicId)) {
                    ArrayList<Integer> deleteSubTask = Epic.getSubTaskIds();
                    System.out.println(deleteSubTask);

                    for (Integer subTaskId : deleteSubTask) {
                        if (subTasksMap.containsKey(subTaskId)) {
                            subTasksMap.remove(subTaskId);
                        }
                    }
                }
            }
            epicsMap.remove(epicId);
            System.out.println("Epic удален.");
            System.out.println(epicsMap.get(epicId));
        } else {
            System.out.println("Эпиков нет.");
        }
    }*/
}



