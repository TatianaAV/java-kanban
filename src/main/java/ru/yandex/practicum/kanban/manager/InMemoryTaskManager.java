package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasksMap = new HashMap<>();
    protected final HashMap<Integer, Epic> epicsMap = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTasksMap = new HashMap<>();
    protected TreeSet<Task> priorityTask =
            new TreeSet<>(Comparator.comparing(task -> task.getStartTime()));

    protected int generatedId = 0;

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    public List<Task> getHistoryManager() {
        return historyManager.getHistory();
    }

    public int generatedId() {
        ++generatedId;
        return generatedId;
    }

    @Override
    public void addTask(Epic epic) {
        int id = generatedId();
        epic.setId(id);
        epic.setStatus(StatusTask.NEW);
        try {
            validateTaskInTime(epic.getStartTime(), epic.getDuration());
            epicsMap.put(id, epic);
            epic.setSubTaskIds(new ArrayList<>());
            updateEpicDurationAndStartTime(id);
        } catch (InvalidTimeException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getTime());
        }
    }

    @Override
    public void addTask(SubTask subTask) {
        int id = generatedId();
        subTask.setId(id);
        subTask.setStatus(StatusTask.NEW);
        try {

            validateTaskInTime(subTask.getStartTime(), subTask.getDuration());

            subTasksMap.put(id, subTask);
            Epic epic = epicsMap.get(subTask.getEpicId());
            getSubTaskIds(epic).add(id);
            updateEpicStatus(epic);

            updateEpicDurationAndStartTime(epic.getId());
        } catch (InvalidTimeException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getTime());
        }
    }

    @Override
    public void addTask(Task task) {
        int id = generatedId();
        task.setId(id);
        task.setStatus(StatusTask.NEW);
        task.setId(id);
        try {
            validateTaskInTime(task.getStartTime(), task.getDuration());
            tasksMap.put(id, task);
        } catch (InvalidTimeException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getTime());
        }
    }

    @Override
    public Task getTaskById(int id) {//задача из таблицы сохраняется в истории просмотра
        if (!tasksMap.containsKey(id)) {
            return null;
        } else {
            Task task = tasksMap.get(id);
            historyManager.addHistory(task);
            return task;
        }
    }

    public ArrayList<Integer> getSubTaskIds(Epic epic) {//список подзадач из эпика
        return epic.getSubTaskIds();
    }

    @Override
    public Epic getEpicById(int id) {//эпик из таблицы по номеру сохраняется в истории просмотра
        if (!epicsMap.containsKey(id)) {
            return null;
        } else {
            Epic task = epicsMap.get(id);
            historyManager.addHistory(task);

            return task;
        }
    }

    @Override
    public SubTask getSubTaskById(int id) {//подзадача из таблицы сохраняется в истории просмотра
        if (!subTasksMap.containsKey(id)) {
            return null;
        } else {
            SubTask task = subTasksMap.get(id);
            historyManager.addHistory(task);
            return task;
        }
    }

    @Override
    public void updateTask(Task task) {
        try {
            if (task != null)
                tasksMap.put(task.getId(), task);
        } catch (NullPointerException ignored) {
            System.out.println("Task is null");
        }
    }


    @Override
    public void updateEpicStatus(Epic epic) {
        try {
            if (epic != null) {
                ArrayList<Integer> changeEpic = getSubTaskIds(epic);
                if (!changeEpic.isEmpty()) {
                    int countNew = 0;
                    int countDone = 0;

                    for (Integer subTaskId : changeEpic) {
                        StatusTask statusSubTask = subTasksMap.get(subTaskId).getStatus();//СТАТУС ПОДЗАДАЧИ

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
        } catch (NullPointerException ignored) {
            System.out.println("Task is null");
        }
    }

    private void updateEpicDurationAndStartTime(int epicId) {
        Epic epic = epicsMap.get(epicId);
        ArrayList<Integer> subTaskIds = epic.getSubTaskIds();
        if (subTaskIds.isEmpty()) {
            epic.setDuration(Duration.ofMinutes(0));
            return;
        }
        LocalDateTime startEpic = LocalDateTime.MAX;
        LocalDateTime endEpic = LocalDateTime.MIN;
        Duration durationEpic = Duration.ofDays(0);

        for (int id : subTaskIds) {
            SubTask subTask = subTasksMap.get(id);
            LocalDateTime  startTime = subTask.getStartTime();
            Duration duration = subTask.getDuration();

            if (startTime == null & duration == null) {
                epic.setStartTime(startEpic);
                epic.setDuration(durationEpic);
                return;
            }
            if (startTime.isBefore(startEpic)) {
                startEpic = startTime;
            }
            LocalDateTime endTime = subTask.getEndTime();
            if (endTime.isAfter(endEpic)) {
                endEpic = endTime;
            }
            durationEpic.plus(subTask.getDuration());
        }
        epic.setStartTime(startEpic);
        epic.setDuration(durationEpic);
    }

    @Override
    public StatusTask updateSubTask(SubTask subTask) {
        try {
            final int id = subTask.getId();
            subTasksMap.put(id, subTask);//обновляем подзадачу
            Epic epic = epicsMap.get(subTask.getEpicId());
            updateEpicStatus(epic);
            updateEpicDurationAndStartTime(epic.getId());
            return epic.getStatus();
        } catch (NullPointerException ignored) {
            System.out.println("Task is null");
        }
        return null;
    }

    @Override
    public List<Task> getPrioritizedTasks() {//Отсортируйте все задачи по приоритету —
        // то есть по startTime. Если дата старта не задана, добавьте задачу в конец списка задач,
        // подзадач, отсортированных по startTime. Напишите новый метод getPrioritizedTasks,
        // возвращающий список задач и подзадач в заданном порядке.
        priorityTask.clear();
        for (Task task : tasksMap.values()) {
            if (task.getStartTime() != null) {
                priorityTask.add(task);
            }
        }
            for (SubTask subTask : subTasksMap.values()) {
                if (subTask.getStartTime() != null) {
                    priorityTask.add(subTask);
                }
            }

       /* priorityTask.stream().forEach(System.out::println);*/

        List<Task> list = new ArrayList<Task>(priorityTask);
        for (SubTask subTask : subTasksMap.values()) {
            if (subTask.getStartTime() == null) {
                list.add(subTask);
            }

            for (Task task1 : tasksMap.values()) {
                if (task1.getStartTime() == null) {
                    list.add(task1);
                }
            }
        }
        return list;
    }

    public void validateTaskInTime(LocalDateTime startTime, Duration duration) throws InvalidTimeException {

        if (startTime != null && duration != null) {
            LocalDateTime endTime = startTime.plus(duration);

            for (Task task : tasksMap.values()) {
                if (task.getStartTime() != null) {
                    priorityTask.add(task);
                }
            }
            for (Epic epic : epicsMap.values()) {
                if (epic.getStartTime() != null) {
                    priorityTask.add(epic);
                }
            }
            if (priorityTask.isEmpty()) {
                return;
            } else if (!priorityTask.isEmpty()) {
                LocalDateTime startFirstTask = priorityTask.first().getStartTime();
                LocalDateTime ednFirstTask = priorityTask.first().getEndTime();
                LocalDateTime startLastTask = priorityTask.last().getStartTime();
                LocalDateTime ednLastTask = priorityTask.last().getEndTime();
                if (startTime.isBefore(startFirstTask) && endTime.isBefore(ednFirstTask)) {
                    return;
                } else if (startTime.isBefore(startLastTask) && endTime.isBefore(ednLastTask)) {
                    return;
                } else if (startTime.isAfter(ednFirstTask) && endTime.isAfter(ednFirstTask)) {
                    return;
                } else {
                    throw new InvalidTimeException("Для этого времени задача не может быть добавлена. ", startTime);
                }
            }
        } else return;
    }


    @Override
    public ArrayList<Task> getTasks() {//получение списка задач
        ArrayList<Task> allTasks = new ArrayList<>();
        if (tasksMap.isEmpty()) {
            return allTasks;
        }
        allTasks = new ArrayList<>(tasksMap.values());
        return allTasks;
    }

    @Override
    public ArrayList<Epic> getEpics() {//получение списка эпиков
        if (epicsMap.isEmpty()) {
            return null;
        }
        ArrayList<Epic> allEpics = new ArrayList<>(epicsMap.values());
        return allEpics;
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {//получение списка всех подзадач
        if (subTasksMap.isEmpty()) {
            return null;
        }
        ArrayList<SubTask> allSubTasks = new ArrayList<>(subTasksMap.values());
        return allSubTasks;
    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpic(Integer epicId) {//получение списка подзадач эпика
        ArrayList<SubTask> listSubTask = new ArrayList<>();
        if (subTasksMap.isEmpty()) {
            return listSubTask;
        } else if (!epicsMap.isEmpty() & epicsMap.containsKey(epicId)) {
            ArrayList<Integer> listIdSubTask = getSubTaskIds(epicsMap.get(epicId));

            for (int idSubTask : listIdSubTask) {
                SubTask subTask = subTasksMap.get(idSubTask);
                listSubTask.add(subTask);
            }
        }
        return listSubTask;
    }

    @Override
    public void deleteAllTask() {
        if (!tasksMap.isEmpty()) {
            for (Integer id : tasksMap.keySet()) {
                historyManager.remove(id);
            }
            tasksMap.clear();
        }
    }

    @Override
    public void deleteAllSubTasks() {
        if (!subTasksMap.isEmpty()) {
            for (Integer id : epicsMap.keySet()) {//в мапе ищем номер эпика
                Epic epicDelete = getEpicById(id);
                ArrayList<Integer> subTaskDelete = getSubTaskIds(epicDelete);
                for (int subTask : subTaskDelete) {
                    historyManager.remove(subTask);
                }
                subTaskDelete.clear();
                epicDelete.setSubTaskIds(subTaskDelete);
                updateEpicStatus(epicDelete);//обновление эпика
            }
            subTasksMap.clear();
        }
    }

    @Override
    public void deleteAllEpic() {//SubTasks не может быть без Epics
        if (!epicsMap.isEmpty() & !subTasksMap.isEmpty()) {
            for (Integer id : subTasksMap.keySet()) {
                historyManager.remove(id);
            }
            subTasksMap.clear();
            for (Integer id : epicsMap.keySet()) {
                historyManager.remove(id);
            }
            epicsMap.clear();
        } else if (!epicsMap.isEmpty() & subTasksMap.isEmpty()) {
            epicsMap.clear();
        }
    }

    @Override
    public void deleteTask(Integer taskId) {//удаление задачи по номеру
        if (tasksMap.containsKey(taskId)) {
            tasksMap.remove(taskId);
            historyManager.remove(taskId);
        }
    }

    @Override
    public void deleteSubTask(Integer subTaskId) {//удаление подзадачи по номеру и удаление информации в эпике и обновление эпика
        if (subTasksMap.containsKey(subTaskId)) {
            int epicId = subTasksMap.get(subTaskId).getEpicId();//извлекаем Id epic
            Epic epicDelete = epicsMap.get(epicId);
            ArrayList<Integer> subTaskDeleteNumber = getSubTaskIds(epicDelete);//список подзадач эпика
            subTaskDeleteNumber.remove(subTaskId);//заходим в список и удаляем номер подзадачи
            epicDelete.setSubTaskIds(subTaskDeleteNumber);//перезаписываем измененный список
            subTasksMap.remove(subTaskId);
            historyManager.remove(subTaskId);
            updateEpicStatus(epicDelete);
        }
    }

    @Override
    public void deleteEpic(int epicId) {//удаление эпика и подзадач с ним связанных
        if (!epicsMap.isEmpty() & epicsMap.containsKey(epicId)) {
            Epic epic = epicsMap.get(epicId);
            ArrayList<Integer> subTaskDelete = getSubTaskIds(epic);
            for (int idSubTask : subTaskDelete) {
                subTasksMap.remove(idSubTask);
                historyManager.remove(idSubTask);

            }
            epicsMap.remove(epicId);
            historyManager.remove(epicId);
        }
    }
}