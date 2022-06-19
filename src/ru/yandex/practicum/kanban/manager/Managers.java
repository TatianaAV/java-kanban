package ru.yandex.practicum.kanban.manager;

public final class Managers {

    public static TaskManager getDefaultHistory() {
        return new InMemoryTaskManager();
    }
}
