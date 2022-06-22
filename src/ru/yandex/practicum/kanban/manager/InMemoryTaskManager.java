package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasksMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicsMap = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasksMap = new HashMap<>();

    private int generatedId = 0;

    HistoryManager historyManager = Managers.getDefaultHistory();

    public List<Task> getHistoryManager() {
        return historyManager.getHistory();
    }

    public int generatedId() {
        ++generatedId;
        return generatedId;
    }

    @Override
    public int addTask(Task task) {
        int id = generatedId();
        task.setId(id);
        if (task instanceof Epic) {

            epicsMap.put(id, (Epic) task);
            Epic epic = getEpicById(id);
            epic.setId(id);
            epic.setSubTaskIds(new ArrayList<>());
        } else if (task instanceof SubTask) {

            subTasksMap.put(id, (SubTask) task);
            SubTask subTask = getSubTaskById(id);
            subTask.setId(id);
            Epic epic = getEpicById(subTask.getEpicId());
            getSubTaskIds(epic).add(id);
            updateEpic(epic);

        } else {

            task.setStatus(StatusTask.NEW);
            task.setId(id);
            tasksMap.put(id, task);
        }
        return id;
    }

    @Override
    public Task getTaskById(int id) {//задача из таблицы по идентификатору
        if (!tasksMap.containsKey(id)) {
            return null;
        } else {
            Task task = tasksMap.get(id);
            historyManager.add(task);// я вообще не понимаю, почему это должно использоваться здесь? должны же считать количество вызовов
            // из меню, так как этот метод используется не только для просмотра задач, но и в других методах
            return task;
        }
    }

    public ArrayList<Integer> getSubTaskIds(Epic epic) {//список подзадач из эпика
        return epic.getSubTaskIds();
    }

    @Override
    public Epic getEpicById(int id) {//эпик из таблицы по номеру
        if (!epicsMap.containsKey(id)) {
            return null;
        } else {
            Epic task = epicsMap.get(id);//как правильно? так?
            historyManager.add(task);

            return task;
        }
    }


    @Override
    public SubTask getSubTaskById(int id) {//подзадача из таблицы
        if (!subTasksMap.containsKey(id)) {
            return null;
        } else {
            historyManager.add(subTasksMap.get(id));// или так?
            return subTasksMap.get(id);
        }
    }

    @Override
    public void updateTask(Task task) {
        tasksMap.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        ArrayList<Integer> changeEpic = getSubTaskIds(epic);
        if (!changeEpic.isEmpty()) {
            int countNew = 0;
            int countDone = 0;

            for (Integer subTaskId : changeEpic) {
                StatusTask statusSubTask = getSubTaskById(subTaskId).getStatus();//СТАТУС ПОДЗАДАЧИ

                switch (statusSubTask) {

                    case NEW:
                        ++countNew;
                        break;

                    case DONE:
                        ++countDone;
                        break;

                    case IN_PROGRESS:
                        epic.setStatus(StatusTask.IN_PROGRESS);
                        break;

                    default:
                        break;
                }
            }

            if (countNew == changeEpic.size()) {
                epic.setStatus(StatusTask.NEW);
            } else if (countDone == changeEpic.size()) {
                epic.setStatus(StatusTask.DONE);
            } else {
                epic.setStatus(StatusTask.IN_PROGRESS);
            }
        } else {
            epic.setStatus(StatusTask.NEW);
        }
        epicsMap.put(epic.getId(), epic);
    }

    @Override
    public StatusTask updateSubTask(SubTask subTask) {
        subTasksMap.put(subTask.getId(), subTask);//обновляем подзадачу
        Epic epic = getEpicById(subTask.getEpicId());
        updateEpic(epic);
        return epic.getStatus();
    }

    @Override
    public ArrayList<Task> getTasks() {//получение списка задач
        if (tasksMap.isEmpty()) {
            System.out.println("Задач нет");
            return null;
        }
        ArrayList<Task> allTasks = new ArrayList<>(tasksMap.values());
        return allTasks;
    }

    @Override
    public ArrayList<Epic> getEpics() {//получение списка эпиков
        if (epicsMap.isEmpty()) {
            System.out.println("Эпиков нет");
            return null;
        }
        ArrayList<Epic> allEpics = new ArrayList<>(epicsMap.values());
        return allEpics;
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {//получение списка всех подзадач
        if (subTasksMap.isEmpty()) {
            System.out.println("Подзадач нет");
            return null;
        }
        ArrayList<SubTask> allSubTasks = new ArrayList<>(subTasksMap.values());
        return allSubTasks;
    }


    @Override
    public ArrayList<SubTask> getSubTasksByEpic(Integer epicId) {//получение списка подзадач эпика
        ArrayList<SubTask> listSubTask = new ArrayList<>();
        if (subTasksMap.isEmpty()) {
            System.out.println("Подзадач нет");
            return null;
        } else if (!epicsMap.isEmpty() & epicsMap.containsKey(epicId)) {
            ArrayList<Integer> listIdSubTask = getSubTaskIds(getEpicById(epicId));

            for (int idSubTask : listIdSubTask) {
                SubTask subTask = getSubTaskById(idSubTask);
                listSubTask.add(subTask);
            }
        }
        return listSubTask;
    }

    @Override
    public void deleteAllTask() {
        if (!tasksMap.isEmpty()) {
            tasksMap.clear();
            System.out.println("Задачи удалены.");
        } else {
            System.out.println("Задач нет.");
        }
    }

    @Override
    public void deleteAllSubTasks() {
        if (!subTasksMap.isEmpty()) {
            for (Integer id : epicsMap.keySet()) {//в мапе ищем номер эпика
                Epic epicDelete = getEpicById(id);
                ArrayList<Integer> subTaskDelete = getSubTaskIds(epicDelete);
                subTaskDelete.clear();
                epicDelete.setSubTaskIds(subTaskDelete);
                updateEpic(epicDelete);//обновление эпика
            }
            subTasksMap.clear();
            System.out.println("Подзадачи удалены.");
        } else {
            System.out.println("Подзадач нет.");
        }
    }

    @Override
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

    @Override
    public void deleteTask(Integer taskId) {//удаление задачи по номеру
        if (tasksMap.containsKey(taskId)) {
            tasksMap.remove(taskId);
            System.out.println("Задача удалена.");
        } else {
            System.out.println("Такой задачи нет.");
        }
    }

    @Override
    public void deleteSubTask(Integer subTaskId) {//удаление подзадачи по номеру и удаление информации в эпике и обновление эпика
        if (subTasksMap.containsKey(subTaskId)) {
            int epicId = getSubTaskById(subTaskId).getEpicId();//извлекаем Id epic
            Epic epicDelete = getEpicById(epicId);
            ArrayList<Integer> subTaskDeleteNumber = getSubTaskIds(epicDelete);//список подзадач эпика
            System.out.println("подзадача " + subTaskId);
            subTaskDeleteNumber.remove(subTaskId);//заходим в список и удаляем номер подзадачи
            epicDelete.setSubTaskIds(subTaskDeleteNumber);//перезаписываем измененный список
            subTasksMap.remove(subTaskId);
            updateEpic(epicDelete);
            System.out.println("Удалена.");
            System.out.println("статус эпика " + epicDelete.getStatus());
        } else {
            System.out.println("Такой подзадачи нет.");
        }
    }

    @Override
    public void deleteEpic(int epicId) {//удаление эпика и подзадач с ним связанных
        if (!epicsMap.isEmpty() & epicsMap.containsKey(epicId)) {
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