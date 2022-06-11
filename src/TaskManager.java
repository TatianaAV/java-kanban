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


    public void addTask(Task task) {//добавление задачи в таблицу
        int id = generatedId();
        task.setId(id);
        task.setStatus("NEW");
        tasksMap.put(id, task);

    }

    public Task getTaskById(int id) {//задача из таблицы по идентификатору
        if (!tasksMap.containsKey(id)) {
            return null;
        }
        return tasksMap.get(id);
    }

    public ArrayList<Integer> getSubTaskIds(Epic epic) {//список подзадач из эпика
        return epic.getSubTaskIds();
    }

    public Epic getEpicById(int id) {//эпик из таблицы по номеру
        if (!epicsMap.containsKey(id)) {
            return null;
        }
        return epicsMap.get(id);
    }

    public SubTask getSubTaskById(int subTaskId) {//подзадача из таблицы
        if (!subTasksMap.containsKey(subTaskId)) {
            return null;
        }
        return subTasksMap.get(subTaskId);
    }

    public int addEpic(Epic epic) {//добавление эпика в таблицу
        int epicId = generatedId();
        epic.setId(epicId);
        epicsMap.put(epicId, epic);
        epic.setStatus("NEW");
        epic.setSubTaskIds(new ArrayList<>());
        return epicId;

    }

    void addSubTask(SubTask subTask) {// добавить подзадачу в таблицу
        int subTaskId = generatedId();
        subTask.setId(subTaskId);
        subTask.setStatus("NEW");
        subTasksMap.put(subTaskId, subTask);
        Epic epic = getEpicById(subTask.getEpicId());//меняем статус эпика
        getSubTaskIds(epic).add(subTaskId);
        epic.setStatus("IN_PROGRESS");

    }

    public void updateTask(Task task) {
        tasksMap.put(task.getId(), task);

    }

    public void updateEpic(Epic epic) {
        epicsMap.put(epic.getId(), epic);
    }

    public String updateSubTask(SubTask subTask) {
        subTasksMap.put(subTask.getId(), subTask);
        Epic epic = getEpicById(subTask.getEpicId());
        ArrayList<Integer> changeEpic = getSubTaskIds(epic);
        int count = 0;

        for (Integer subTaskId : changeEpic) {
            String statusSubTask = getSubTaskById(subTaskId).getStatus();//СТАТУС ПОДЗАДАЧИ
            if (statusSubTask.equals("DONE")) {
                ++count;
                if (count == changeEpic.size()) {
                    epic.setStatus("DONE");
                    count = 0;
                }
            } else {
                epic.setStatus("IN_PROGRESS");
            }
        }
return epic.getStatus();
    }

    public ArrayList<Task> getTasks() {//получение списка задач
        if (tasksMap.isEmpty()) {
            System.out.println("Задач нет");
            return null;
        }
        ArrayList<Task> allTasks = new ArrayList<>(tasksMap.values());
        return allTasks;
    }

    public ArrayList<Epic> getEpics() {//получение списка эпиков
        if (epicsMap.isEmpty()) {
            System.out.println("Эпиков нет");
            return null;

        }
        ArrayList<Epic> allEpics = new ArrayList<>(epicsMap.values());
        return allEpics;
    }

    public ArrayList<SubTask> getSubTasks() {//получение списка всех подзадач
        if (subTasksMap.isEmpty()) {
            System.out.println("Подзадач нет");
            return null;
        }
        ArrayList<SubTask> allSubTasks = new ArrayList<>(subTasksMap.values());
        return allSubTasks;
    }

    public ArrayList<Integer> getSubTasksByEpic(Integer epicId) {//получение списка подзадач эпика
        ArrayList<Integer> listSubTask = null;
        if (subTasksMap.isEmpty()) {
            System.out.println("Подзадач нет");
            return null;
        } else if (!epicsMap.isEmpty()) {
            Epic epic = getEpicById(epicId);
            listSubTask = getSubTaskIds(epic);

        }
        return listSubTask;
    }

    public void deleteAllTask() {
        if (!tasksMap.isEmpty()) {
            tasksMap.clear();
            System.out.println("Задачи удалены.");
        } else {
            System.out.println("Задач нет.");
        }
    }

    public void deleteAllSubTasks() {
        if (!subTasksMap.isEmpty()) {
            for (Integer id : epicsMap.keySet()) {//в мапе ищем номер эпика
                //если соответствует
                Epic epicDelete = getEpicById(id);
                ArrayList<Integer> subTaskDelete = getSubTaskIds(epicDelete);
                subTaskDelete.clear();
                epicDelete.setSubTaskIds(subTaskDelete);
                epicDelete.setStatus("NEW");
                updateEpic(epicDelete);//перезапись
            }
            subTasksMap.clear();
            System.out.println("Подзадачи удалены.");

        } else {
            System.out.println(epicsMap);
            System.out.println("Подзадач нет.");
        }
    }

    public void deleteAllEpic() {//SubTasks не может быть без Epics

        if (!epicsMap.isEmpty() & !subTasksMap.isEmpty()) {
            subTasksMap.clear();
            epicsMap.clear();
            System.out.println("Эпики и подзадачи удалены.");

        } else if (!epicsMap.isEmpty() & subTasksMap.isEmpty()) {
            epicsMap.clear();
            System.out.println("Эпики и подзадачи удалены.");

        } else {
            System.out.println("Эпиков нет.");
        }
    }

    public void deleteTask(Integer taskId) {//удаление задачи по номеру

        if (tasksMap.containsKey(taskId)) {
            tasksMap.remove(taskId);
            System.out.println("Задача удалена.");
        } else {
            System.out.println("Такой задачи нет.");
        }
    }

    public void deleteSubTask(Integer subTaskId) {//удаление подзадачи по номеру и удаление информации в эпике и обновление эпика
        if (subTasksMap.containsKey(subTaskId)) {
            Integer epicId = getSubTaskById(subTaskId).getEpicId();//извлекаем Id epic
            for (Integer id : epicsMap.keySet()) {//в мапе ищем номер эпика
                if (id.equals(epicId)) { //если соответствует
                    Epic epicDelete = getEpicById(id);
                    ArrayList<Integer> subTaskDeleteNumber = getSubTaskIds(epicDelete);
                    if (subTaskDeleteNumber.contains(subTaskId)) {
                        subTaskDeleteNumber.remove(subTaskId);
                        epicDelete.setSubTaskIds(subTaskDeleteNumber);//заходим в список и удаляем номер подзадачи
                        if (subTaskDeleteNumber.isEmpty()) {
                            epicDelete.setStatus("NEW");
                        }
                        updateEpic(epicDelete);//перезаписываем
                    }
                }

            }

            subTasksMap.remove(subTaskId);
            System.out.println("Подзадача удалена.");

        } else {
            System.out.println("Такой подзадачи нет.");
        }

    }

    public void deleteEpic(Integer epicId) {//удаление эпика и подзадач с ним связанных
        if (!epicsMap.isEmpty()) {
            ArrayList<Integer> subTaskDelete = getSubTasksByEpic(epicId);
            for (int idSubTask : subTaskDelete) {
                subTasksMap.remove(idSubTask);
            }
            epicsMap.remove(epicId);

        } else {
            System.out.println("Эпика нет.");
        }
    }
}



