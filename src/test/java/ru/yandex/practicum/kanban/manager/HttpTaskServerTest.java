package ru.yandex.practicum.kanban.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.manager.http.HttpTaskServer;
import ru.yandex.practicum.kanban.manager.http.KVServer;
import ru.yandex.practicum.kanban.manager.emums.StatusTask;
import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskServerTest {

    private HttpTaskServer server;
    private KVServer serverKV;
    private TaskManager taskManager;
    private Task task;
    private Epic epic;
    private SubTask subtask;

    private final Gson gson = Managers.getGson();

    @BeforeEach
    void init() throws IOException {
        taskManager = Managers.getDefaultFileBackedTaskManager();


        server = new HttpTaskServer(taskManager);
        task = new Task(LocalDateTime.now(), Duration.ofSeconds(3599),
                "Test1 task", "Test task description");
        taskManager.addTask(task);
        Task task2 = new Task("Test task2", "Test description2");
        taskManager.addTask(task2);
        Task task3 = new Task("Test task3", "Test  description3");
        taskManager.addTask(task3);

        epic = new Epic("Epic id 4", "test");
        taskManager.addTask(epic);
        subtask = new SubTask(LocalDateTime.now().plusHours(2),
                Duration.ofSeconds(3599), "Subtask id 5", "Test", 4);
        taskManager.addTask(subtask);

        server.start();
        serverKV = new KVServer();
        serverKV.start();
    }

    @AfterEach
    void stop() {
        server.stop();
        serverKV.stop();
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
    void getTasksByIdNotExist() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(412, response.statusCode());
    }


    @Test
    void deleteTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(response.body(), taskType);

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(0, tasks.size(), "Задачи не удаляются");
    }

    @Test
    void addTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        //taskManager.getTasks().forEach(System.out::println);
        Task newTask = new Task(
                LocalDateTime.now().plusHours(1), Duration.ofSeconds(3599),
                "addTask", "description addTask");

        URI url = URI.create("http://localhost:8080/tasks/task");
        String json = gson.toJson(newTask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(response.body(), taskType);

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(4, tasks.size(), "Не верное количество задач");
    }

    @Test
    void addTaskIsEmpty() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Task newTask = null;
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson((Object) null);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<Task>() {
        }.getType();

        Task received = gson.fromJson(response.body(), taskType);
        received.setStatus(StatusTask.DONE);
        url = URI.create("http://localhost:8080/tasks/task");
        String json = gson.toJson(received);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/task/?id=1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        taskType = new TypeToken<Task>() {
        }.getType();
        Task updatedTask = gson.fromJson(response.body(), taskType);

        assertEquals(StatusTask.DONE, updatedTask.getStatus(), "Задачи не обновляются");
    }


    @Test
    void deleteTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());


        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder().uri(url).GET().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {

        }.getType();
        List<Task> tasks = gson.fromJson(response.body(), taskType);
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(2, tasks.size(), "Не верное количество задач");
    }

    //tests SubTask
    @Test
    void getSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<SubTask>>() {
        }.getType();
        List<SubTask> subTasks = gson.fromJson(response.body(), taskType);

        assertNotNull(subTasks, "Подзадачи не возвращаются");
        assertEquals(1, subTasks.size(), "Не верное количество подзадач");
        assertEquals(subtask, subTasks.get(0), "Задачи не совпадают");
        assertEquals(taskManager.getSubTaskById(5), subTasks.get(0), "Задачи не совпадают");
    }

    @Test
    void getSubTasksById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=5");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<SubTask>() {
        }.getType();
        SubTask received = gson.fromJson(response.body(), taskType);

        assertNotNull(received, "Задачи не возвращаются");
        assertEquals(subtask, received, "Задачи не совпадают");
    }

    @Test
    void getSubTasksByIdnNotExist() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(412, response.statusCode());
    }


    @Test
    void deleteSubTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(response.body(), taskType);

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(0, tasks.size(), "Задачи не удаляются");
    }

    @Test
    void addSubTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        SubTask newTask = new SubTask(
                LocalDateTime.now().plusHours(3), Duration.ofSeconds(3599),
                "addTask", "description addTask", 4);
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        String json = gson.toJson(newTask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        System.out.println(request);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/subtask");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<SubTask>>() {
        }.getType();
        List<SubTask> tasks = gson.fromJson(response.body(), taskType);

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(2, tasks.size(), "Не верное количество задач");
    }

    @Test
    void addSubTaskIsEmpty() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson((Object) null);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void updateSubTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=5");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<SubTask>() {
        }.getType();

        SubTask received = gson.fromJson(response.body(), taskType);
        received.setStatus(StatusTask.DONE);
        url = URI.create("http://localhost:8080/tasks/subtask");
        String json = gson.toJson(received);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/subtask/?id=5");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        taskType = new TypeToken<SubTask>() {
        }.getType();
        SubTask updatedTask = gson.fromJson(response.body(), taskType);

        assertEquals(StatusTask.DONE, updatedTask.getStatus(), "Задачи не обновляются");
    }


    @Test
    void deleteSubTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());


        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder().uri(url).GET().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {

        }.getType();
        List<Task> tasks = gson.fromJson(response.body(), taskType);
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(2, tasks.size(), "Не верное количество задач");
    }

    //tests for epics

    @Test
    void getEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(response.body(), taskType);

        assertNotNull(tasks, "Пользователи не возвращаются");
        assertEquals(3, tasks.size(), "Не верное количество задач");
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
        assertEquals(taskManager.getTaskById(2), tasks.get(1), "Задачи не совпадают");
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Epic>() {
        }.getType();
        Epic received = gson.fromJson(response.body(), taskType);

        assertNotNull(received, "Задачи не возвращаются");
        assertEquals(epic, received, "Задачи не совпадают");
    }

    @Test
    void getEpicByIdnNotExist() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(412, response.statusCode());
    }


    @Test
    void deleteEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(response.body(), taskType);

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(0, tasks.size(), "Задачи не удаляются");
    }

    @Test
    void addEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Epic newTask = new Epic(
                "addTask", "description addTask");
        URI url = URI.create("http://localhost:8080/tasks/epic");
        String json = gson.toJson(newTask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/epic");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<Epic> tasks = gson.fromJson(response.body(), taskType);

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(2, tasks.size(), "Не верное количество задач");
    }

    @Test
    void addEpicIsEmpty() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        String json = gson.toJson((Object) null);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void updateEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=5");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<SubTask>() {
        }.getType();
        SubTask received = gson.fromJson(response.body(), taskType);

        received.setStatus(StatusTask.DONE);

        url = URI.create("http://localhost:8080/tasks/subtask");
        String json = gson.toJson(received);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/epic/?id=4");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        taskType = new TypeToken<Epic>() {
        }.getType();
        Epic updatedTask = gson.fromJson(response.body(), taskType);

        assertEquals(StatusTask.DONE, updatedTask.getStatus(), "Задачи не обновляются");
    }


    @Test
    void deleteEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());


        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/epic");
        request = HttpRequest.newBuilder().uri(url).GET().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Epic>>() {

        }.getType();
        List<Epic> tasks = gson.fromJson(response.body(), taskType);
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(0, tasks.size(), "Не верное количество задач");
    }


}
