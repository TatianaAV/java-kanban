package ru.yandex.practicum.kanban.manager.json;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    public static final int PORT = 8080;
    private HttpServer server;
    private Gson gson;

    private TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefaultFileBackedTaskManager());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handle);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    private void handle(HttpExchange httpExchange) {
        try {
            System.out.println("\n" + httpExchange.getRequestURI());
            final String path = httpExchange.getRequestURI().getPath().replaceFirst("/tasks", "");
            String name = path.split("/")[1];
            switch (name) {

                case "task":
                    handleTasks(httpExchange);
                    break;
                case "subtask":
                    handleSubtask(httpExchange);
                    break;
                case "epic":
                    handleEpic(httpExchange);
                    break;
                case "history":
                    handleHistory(httpExchange);
                    break;
                case "":
                    handleGetPrioritizedTask(httpExchange);
                default: {
                    System.out.println("Ошибка при получении - " + path);
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }
        } catch (Exception exception) {
            System.out.println("Ошибка обработки запроса");
        } finally {
            httpExchange.close();
        }
    }

    private void handleGetPrioritizedTask(HttpExchange httpExchange) {

    }

    private void handleHistory(HttpExchange httpExchange) {
    }

    private void handleEpic(HttpExchange httpExchange) {

    }

    private void handleSubtask(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        String query = httpExchange.getRequestURI().getQuery();
        String body = readText(httpExchange);
        switch (requestMethod) {
            case "GET":
                if (Objects.nonNull(query)) {
                    String idString = query.substring(3);
                    int id = parsePathId(idString);
                    final String response = gson.toJson(taskManager.getTaskById(id));
                    if (id != -1 && !response.equals("null")) {
                        sendText(httpExchange, response);
                        return;
                    } else {
                        System.out.println("Нет задачи с идентификатором -" + id);
                        httpExchange.sendResponseHeaders(404, 0);

                    }
                } else {
                    final String response = gson.toJson(taskManager.getTasks());
                    sendText(httpExchange, response);
                }
                break;
            case "POST":
                Task task = gson.fromJson(body, Task.class);

                if (Objects.nonNull(task)) {
                    if (task.getId() != 0 && task.getStatus() != null) {
                        taskManager.updateTask(task);
                        httpExchange.sendResponseHeaders(201, 0);
                    } else {
                        taskManager.addTask(task);
                        httpExchange.sendResponseHeaders(201, 0);
                    }

                    return;
                } else {
                    System.out.println("Попробуйте еще раз добавить задачу");
                    httpExchange.sendResponseHeaders(405, 0);
                }
                break;
            case "DELETE":
                if (Objects.nonNull(query)) {
                    String idString = query.substring(3);
                    int id = parsePathId(idString);
                    if (id != -1) {
                        taskManager.deleteTask(id);
                        sendText(httpExchange, "Задача " + id + " удалена");
                        return;
                    } else {
                        System.out.println("Нет задачи с идентификатором -" + id);
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                } else {
                    taskManager.deleteAllTask();
                    sendText(httpExchange, "Удалены все задачи");
                }
                break;
            default: {
                System.out.println("/ ждем GET-запрос, POST-запрос, DELETE-запрос, а получили - " + requestMethod);
                httpExchange.sendResponseHeaders(405, 0);
            }
        }
    }

    private void handleTasks(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        String query = httpExchange.getRequestURI().getQuery();
        String body = readText(httpExchange);
        switch (requestMethod) {
            case "GET":
                if (Objects.nonNull(query)) {
                    String idString = query.substring(3);
                    int id = parsePathId(idString);
                    final String response = gson.toJson(taskManager.getTaskById(id));
                    if (id != -1 && !response.equals("null")) {
                        sendText(httpExchange, response);
                        return;
                    } else {
                        System.out.println("Нет задачи с идентификатором -" + id);
                        httpExchange.sendResponseHeaders(404, 0);

                    }
                } else {
                    final String response = gson.toJson(taskManager.getTasks());
                    sendText(httpExchange, response);
                }
                break;
            case "POST":
                Task task = gson.fromJson(body, Task.class);
                if (Objects.nonNull(task)) {
                    if (task.getId() != 0 && task.getStatus() != null) {
                        taskManager.updateTask(task);
                        httpExchange.sendResponseHeaders(201, 0);
                    } else {
                        taskManager.addTask(task);
                        httpExchange.sendResponseHeaders(201, 0);
                    }

                    return;
                } else {
                    System.out.println("Попробуйте еще раз добавить задачу");
                    httpExchange.sendResponseHeaders(405, 0);
                }
                break;
            case "DELETE":
                if (Objects.nonNull(query)) {
                    String idString = query.substring(3);
                    int id = Integer.parseInt(idString);
                    taskManager.deleteTask(id);
                    sendText(httpExchange, "Задача " + id + " удалена");
                    return;
                } else {
                    taskManager.deleteAllTask();
                    sendText(httpExchange, "Удалены все задачи");
                }
                break;
            default: {
                System.out.println("/ ждем GET-запрос, POST-запрос, DELETE-запрос, а получили - " + requestMethod);
                httpExchange.sendResponseHeaders(405, 0);
            }
        }

    }


    private int parsePathId(String idString) {
        try {
            return Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void start() {
        System.out.println("Started TaskServer " + PORT);
        System.out.println("http://localhost:" + PORT + "/tasks");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    private void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}

