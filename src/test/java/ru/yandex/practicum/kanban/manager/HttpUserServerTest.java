package ru.yandex.practicum.kanban.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.manager.json.HttpUserServer;
import ru.yandex.practicum.kanban.tasks.Task;
import ru.yandex.practicum.kanban.user.User;

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

class HttpUserServerTest {

    private HttpUserServer server;
    private UserManager userManager;
    private TaskManager taskManager;

    private User user;
    private Task task;

    private Gson gson = Managers.getGson();

    @BeforeEach
    void init() throws IOException {
        userManager = Managers.getDefaultUser();
        taskManager = userManager.getTaskManager();

        server = new HttpUserServer(userManager);

        user = new User("Тестовый Юзер");
        userManager.add(user);

        task = new Task( LocalDateTime.now(), Duration.ofSeconds(3600), "Test task", "Test task description", user)
        ;
        taskManager.addTask(task);

        server.start();
    }

    @AfterEach
    void stop() {
        server.stop();
    }

    @Test
    void getUsers() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/users");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type userType = new TypeToken<ArrayList<User>>() {

        }.getType();
        List<User> users = gson.fromJson(response.body(), userType);
        assertNotNull(users, "Пользователи не возвращаются");
        assertEquals(1, users.size(), "Не верное количество пользователей");
        assertEquals(user, users.get(0), "Пользователи не совпадают");
    }

    @Test
    void getUserById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/users/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type userType = new TypeToken<User>() {

        }.getType();

        User received = gson.fromJson(response.body(), userType);
        assertNotNull(received, "Пользователи не возвращаются");
        assertEquals(user, received, "Пользователи не совпадают");
    }

    @Test
    void getUserTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/users/1/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<List<Task>>() {

        }.getType();

        List<Task> tasks = gson.fromJson(response.body(), taskType);
        assertNotNull(tasks, "Пользователи не возвращаются");
        assertEquals(1, tasks.size(), "Не верное количество пользователей");
        assertEquals(task, tasks.get(0), "Пользователи не совпадают");
    }

    @Test
    void deleteUser() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/users/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());


        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/api/v1/users");
        request = HttpRequest.newBuilder().uri(url).GET().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type userType = new TypeToken<ArrayList<User>>() {

        }.getType();
        List<User> users = gson.fromJson(response.body(), userType);
        assertNotNull(users, "Пользователи не возвращаются");
        assertEquals(0, users.size(), "Не верное количество пользователей");
    }

}
