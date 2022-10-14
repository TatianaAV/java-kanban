package ru.yandex.practicum.kanban.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.kanban.manager.adapter.DurationAdapter;
import ru.yandex.practicum.kanban.manager.adapter.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public final class Managers {

    private Managers() { }

    public static TaskManager  getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager  getDefaultFileBackedTaskManager() {
        return new FileBackedTasksManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting()
          .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }


}
