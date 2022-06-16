import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasksMap = new HashMap<>();
    private HashMap<Integer, Epic> epicsMap = new HashMap<>();
    private HashMap<Integer, SubTask> subTasksMap = new HashMap<>();

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
            return null;//такую реализацию предложил использовать куратор для дальнейшего изменения со строкой вывода
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
        updateSubTask(subTask, "NEW");
        Epic epic = getEpicById(subTask.getEpicId());
        getSubTaskIds(epic).add(subTaskId);
    }

    public void updateTask(Task task) {
        tasksMap.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epicsMap.put(epic.getId(), epic);
    }

    public String updateSubTask(SubTask subTask, String status) {
        if (subTask == null) {
            System.out.println("Подзадачи c таким номером нет");
            return null;

        } else {
            subTask.setStatus(status);//записываем статус
            subTasksMap.put(subTask.getId(), subTask);//обновляем подзадачу
            Epic epic = getEpicById(subTask.getEpicId());
            ArrayList<Integer> changeEpic = getSubTaskIds(epic);
            int count = 0;
            for (Integer subTaskId : changeEpic) {
                String statusSubTask = getSubTaskById(subTaskId).getStatus();//СТАТУС ПОДЗАДАЧИ
                if (statusSubTask.equals(status)) {
                    ++count;
                    if (count == changeEpic.size()) {
                        epic.setStatus(status);
                        count = 0;
                    }
                } else {
                    epic.setStatus("IN_PROGRESS");
                }
            }
            return epic.getStatus();
        }
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

    public ArrayList<SubTask> getSubTasksByEpic(Integer epicId) {//получение списка подзадач эпика
        ArrayList<SubTask> listSubTask = new ArrayList<>();
        if (subTasksMap.isEmpty()) {
            System.out.println("Подзадач нет");
            return null;
        } else if (!epicsMap.isEmpty() & epicsMap.containsKey(epicId)) {
            ArrayList<Integer>  listIdSubTask = getSubTaskIds(getEpicById(epicId));

            for (int idSubTask : listIdSubTask) {
                SubTask subTask = getSubTaskById(idSubTask);
                listSubTask.add(subTask);
            }

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
            int epicId = getSubTaskById(subTaskId).getEpicId();//извлекаем Id epic
            Epic epicDelete = getEpicById(epicId);
            System.out.println("статус эпика " + epicDelete.getStatus());
            ArrayList<Integer> subTaskDeleteNumber = getSubTaskIds(epicDelete);//список подзадач эпика
            System.out.println("подзадача " + subTaskId);
            subTaskDeleteNumber.remove(subTaskId);//заходим в список и удаляем номер подзадачи
                epicDelete.setSubTaskIds(subTaskDeleteNumber);//перезаписываем измененный список
                if (subTaskDeleteNumber.isEmpty()) {
                    epicDelete.setStatus("NEW");
                    updateEpic(epicDelete);//перезаписываем

                }else {
                    subTaskId = subTaskDeleteNumber.get(0);
                   SubTask subTask = getSubTaskById(subTaskId);
                   String statusEpic = updateSubTask(subTask, getSubTaskById(subTaskId).getStatus());
                    System.out.println("статус эпика " + statusEpic);

                }

            subTasksMap.remove(subTaskId);
            System.out.println("Удалена.");

        } else {
            System.out.println("Такой подзадачи нет.");
        }
    }

    public void deleteEpic(int epicId) {//удаление эпика и подзадач с ним связанных
        if (!epicsMap.isEmpty()& epicsMap.containsKey(epicId)) {
            Epic epic = getEpicById(epicId);
            ArrayList<Integer> subTaskDelete = getSubTaskIds(epic);
            for (int idSubTask : subTaskDelete) {
                subTasksMap.remove(idSubTask);
            }
            epicsMap.remove(epicId);

        } else {
            System.out.println("Эпика нет.");
        }
    }
}



