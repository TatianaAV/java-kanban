package ru.yandex.practicum.kanban.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.manager.json.HttpTaskServer;
import ru.yandex.practicum.kanban.tasks.Task;


import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

        private HttpTaskServer server;
        private TaskManager taskManager;
        private Task task;

        private Gson gson = Managers.getGson();

        @BeforeEach
        void init() throws IOException {
            taskManager = Managers.getDefaultFileBackedTaskManager();


            server = new HttpTaskServer(taskManager);
            task = new Task( LocalDateTime.now(), Duration.ofSeconds(3600),
                    "Test task", "Test task description");
            taskManager.addTask(task);
            Task task2 = new Task( "Test task2", "Test description2");
            taskManager.addTask(task2);
            Task task3 = new Task( "Test task3", "Test  description3");
            taskManager.addTask(task3);

            server.start();
        }

        @AfterEach
        void stop() {
            server.stop();
        }

        @Test
        void getTasks() throws IOException, InterruptedException {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks/task");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            Type taskType = new TypeToken<ArrayList<Task>>() {

            }.getType();
            List<Task> tasks = gson.fromJson(response.body(), taskType);
            tasks.forEach(System.out::println);
            assertNotNull(tasks, "Пользователи не возвращаются");
            assertEquals(3, tasks.size(), "Не верное количество задач");
            assertEquals(task, tasks.get(0), "Задачи не совпадают");
            assertEquals(taskManager.getTaskById(2), tasks.get(1), "Задачи не совпадают");
        }

        @Test
        void getTasksById() throws IOException, InterruptedException {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            Type taskType = new TypeToken<Task>() {

            }.getType();

            Task received = gson.fromJson(response.body(), taskType);
            assertNotNull(received, "Задачи не возвращаются");
            assertEquals(task, received, "Задачи не совпадают");
        }


        @Test
        void deleteTask() throws IOException, InterruptedException {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks/task");
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());


            client = HttpClient.newHttpClient();
            url = URI.create("http:http://localhost:8080/tasks/");
            request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            Type taskType = new TypeToken<ArrayList<Task>>() {

            }.getType();
            List<Task> tasks = gson.fromJson(response.body(), taskType);
            assertNotNull(tasks, "Пользователи не возвращаются");
            assertEquals(0, tasks.size(), "Не верное количество пользователей");
        }

    }
