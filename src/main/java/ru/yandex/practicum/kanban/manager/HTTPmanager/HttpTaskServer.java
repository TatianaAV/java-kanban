package ru.yandex.practicum.kanban.manager.HTTPmanager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.manager.exceptions.InvalidTimeException;
import ru.yandex.practicum.kanban.manager.exceptions.ManagerSaveException;
import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    public static final int PORT = 8080;
    private HttpServer server;
    private Gson gson;

    private TaskManager taskManager;


    public HttpTaskServer() throws IOException {
        this(Managers.loadedHTTPTasksManager());
        //Managers.loadedHTTPTasksManager();
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException, ManagerSaveException, InvalidTimeException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handle);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        new KVServer().start();
    }

    private void handle(HttpExchange httpExchange) throws IOException {

        try {
            System.out.println("\n" + httpExchange
                    .getRequestURI()
                    .getPath());

            final String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getQuery();
            String body = readText(httpExchange);
            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches("^/tasks/task$", path)) {
                        final String response = gson.toJson(taskManager.getTasks());
                        sendText(httpExchange, response);
                        return;
                    }
                    if (Pattern.matches("^/tasks/task/$", path)) {
                        String idString = query.substring(3);
                        int id = parsePathId(idString);
                        if (id != -1) {
                            final String response = gson.toJson(taskManager.getTaskById(id));
                            sendText(httpExchange, response);
                            return;
                        } else {
                            sendError404(httpExchange, "Нет задачи с идентификатором -" + id);
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                    }
                    if (Pattern.matches("^/tasks/subtask$", path)) {
                        final String response = gson.toJson(taskManager.getSubTasks());
                        sendText(httpExchange, response);
                        return;
                    }
                    if (Pattern.matches("^/tasks/subtask/$", path)) {
                        String idString = query.substring(3);
                        int id = parsePathId(idString);
                        if (id != -1) {
                            final String response = gson.toJson(taskManager.getSubTaskById(id));
                            sendText(httpExchange, response);
                            return;
                        } else {
                            sendError404(httpExchange, "Нет подзадачи с идентификатором -" + id);
                        }
                    }
                    if (Pattern.matches("^/tasks/subtask/epic/$", path)) {
                        String idString = query.substring(3);
                        int id = parsePathId(idString);
                        if (id != -1) {
                            final String response = gson.toJson(taskManager.getSubTasksByEpic(id));
                            sendText(httpExchange, response);
                            return;
                        } else {
                            sendError404(httpExchange, "Нет подзадач для эпика с идентификатором -" + id);
                        }
                    }
                    if (Pattern.matches("^/tasks/epic$", path)) {
                        final String response = gson.toJson(taskManager.getEpics());
                        sendText(httpExchange, response);
                        return;
                    }
                    if (Pattern.matches("^/tasks/epic/$", path)) {
                        String idString = query.substring(3);
                        int id = parsePathId(idString);
                        if (id != -1) {
                            final String response = gson.toJson(taskManager.getEpicById(id));
                            sendText(httpExchange, response);
                            return;
                        } else {
                            sendError404(httpExchange, "Нет эпика с идентификатором -" + id);
                        }
                    }
                    if (Pattern.matches("^/tasks/history$", path)) {
                        final String response = gson.toJson(taskManager.getHistoryManager());
                        sendText(httpExchange, response);
                        return;
                    }
                    if (Pattern.matches("^/tasks$", path)) {
                        final String response = gson.toJson(taskManager.getPrioritizedTasks());
                        sendText(httpExchange, response);
                        return;
                    }
                    break;
                case "POST":

                    if (body.contains("title")) {
                        if (Pattern.matches("^/tasks/task$", path)) {
                            Task task = gson.fromJson(body, Task.class);
                            if (task.getId() != 0 && task.getStatus() != null) {

                                taskManager.updateTask(task);
                            } else {
                                taskManager.addTask(task);
                            }
                            httpExchange.sendResponseHeaders(201, 0);
                            return;
                        }

                        if (Pattern.matches("^/tasks/epic$", path)) {
                            Epic epic = gson.fromJson(body, Epic.class);
                            if (epic.getId() != 0 && epic.getStatus() != null) {
                                taskManager.updateEpic(epic.getId());
                            } else {
                                taskManager.addTask(epic);
                            }
                            httpExchange.sendResponseHeaders(201, 0);
                            return;
                        }
                        if (Pattern.matches("^/tasks/subtask$", path)) {
                            SubTask subTask = gson.fromJson(body, SubTask.class);
                            if (subTask.getId() != 0 && subTask.getStatus() != null) {
                                taskManager.updateSubTask(subTask);
                            } else {
                                taskManager.addTask(subTask);
                            }
                            httpExchange.sendResponseHeaders(201, 0);
                            return;
                        }
                    } else {
                        sendError404(httpExchange, "Попробуйте еще раз добавить задачу, нет данных полей");
                        System.out.println("Попробуйте еще раз добавить задачу");
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                    break;

                case "DELETE":
                    if (Pattern.matches("^/tasks/task$", path)) {
                        taskManager.deleteAllTask();
                        sendText(httpExchange, "Удалены все задачи");
                        return;
                    }
                    if (Pattern.matches("^/tasks/task/$", path)) {
                        String idString = query.substring(3);
                        int id = parsePathId(idString);
                        if (id != -1) {
                            taskManager.deleteTask(id);
                            sendText(httpExchange, "Задача " + id + " удалена");
                        } else {
                            sendError404(httpExchange, "Нет задачи с идентификатором -" + id);
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                        return;
                    }
                    if (Pattern.matches("^/tasks/subtask$", path)) {
                        taskManager.deleteAllSubTasks();
                        sendText(httpExchange, "Удалены все подзадачи");
                        return;
                    }
                    if (Pattern.matches("^/tasks/subtask/$", path)) {
                        String idString = query.substring(3);
                        int id = parsePathId(idString);
                        if (id != -1) {
                            taskManager.deleteSubTask(id);
                            sendText(httpExchange, "удален " + id + " подзадача");
                        } else {
                            sendError404(httpExchange, "Нет подзадачи с идентификатором -" + id);
                        }
                        return;
                    }

                    if (Pattern.matches("^/tasks/epic$", path)) {
                        taskManager.deleteAllEpic();
                        sendText(httpExchange, "Удалены /все/ эпики и их подзадачи");
                        return;
                    }
                    if (Pattern.matches("^/tasks/epic/$", path)) {
                        String idString = query.substring(3);
                        int id = parsePathId(idString);
                        if (id != -1) {
                            taskManager.deleteEpic(id);
                            sendText(httpExchange, "Удален " + id + " эпик и его подзадачи");
                        } else {
                            sendError404(httpExchange, "Нет эпика с идентификатором -" + id);
                        }
                        return;
                    }
                    break;

                default: {
                    System.out.println("/ ждем GET-запрос, POST-запрос, DELETE-запрос, а получили - " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }
        } catch (ManagerSaveException exception) {
            sendError412(httpExchange, exception.getMessage());
        } catch (InvalidTimeException exception) {
            System.out.println("Ошибка валидации времени");
            sendError412(httpExchange, exception.getMessage());
        } catch (Exception exception) {
            System.out.println("Ошибка обработки запроса");
            sendText(httpExchange, exception.getMessage());
        } finally {
            httpExchange.close();
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

    private void sendError404(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(resp);
    }

    private void sendError412(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(412, resp.length);
        h.getResponseBody().write(resp);
    }
}

