package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.tasks.Task;
import ru.yandex.practicum.kanban.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryUserManager implements UserManager {

    private final HashMap<Integer, User> users = new HashMap<>();
    private final TaskManager taskManager = Managers.getDefault();

    private int generatedId = 0;

    private int generateId() {
        return ++generatedId;
    }

    @Override
    public TaskManager getTaskManager() {
        return taskManager;
    }

    @Override
    public Integer add(User user) {
        int id = generateId();
        user.setId(id);
        users.put(id, user);
        return id;
    }

    @Override
    public void update(User user) {
        int id = user.getId();
        if(!users.containsKey(id)) {
            return;
        }
        users.put(id, user);
    }

    @Override
    public User getById(int id) {
        return users.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<Task> getUserTasks(int id) {
        return taskManager.getTasks().stream()
                .filter(task -> task.getUser().getId() == id)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(int id) {
        users.remove(id);
    }
}

