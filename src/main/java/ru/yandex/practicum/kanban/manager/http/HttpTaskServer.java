package ru.yandex.practicum.kanban.manager.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.manager.exceptions.InvalidTimeException;
import ru.yandex.practicum.kanban.manager.exceptions.ManagerSaveException;
import ru.yandex.practicum.kanban.manager.http.sendMessage.Message;
import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

public class HttpTaskServer {

    public static final int PORT = 8080;
    private final HttpServer server;
    private final Gson gson;
    private final Message message = new Message();
    private final TaskManager taskManager;


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
            String body = message.readText(httpExchange);
            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches("^/tasks/task$", path) && query == null) {
                        final String response = gson.toJson(taskManager.getTasks());
                        message.sendText200(httpExchange, response);
                        return;
                    }
                    if (Pattern.matches("^/tasks/task/$", path) && query != null) {
                        String idString = query.substring(3);
                        int id = parsePathId(idString);
                        if (id != -1) {
                            final String response = gson.toJson(taskManager.getTaskById(id));
                            message.sendText200(httpExchange, response);
                            return;
                        } else {
                            message.sendError404(httpExchange, "Нет задачи с идентификатором -" + id);
                        }
                    }
                    if (Pattern.matches("^/tasks/subtask$", path) && query == null) {
                        final String response = gson.toJson(taskManager.getSubTasks());
                        message.sendText200(httpExchange, response);
                        return;
                    }
                    if (Pattern.matches("^/tasks/subtask/$", path) && query != null) {
                        String idString = query.substring(3);
                        int id = parsePathId(idString);
                        if (id != -1) {
                            final String response = gson.toJson(taskManager.getSubTaskById(id));
                            message.sendText200(httpExchange, response);
                            return;
                        } else {
                            message.sendError404(httpExchange, "Нет подзадачи с идентификатором -" + id);
                        }
                    }
                    if (Pattern.matches("^/tasks/subtask/epic/$", path) && query != null) {
                        String idString = query.substring(3);
                        int id = parsePathId(idString);
                        if (id != -1) {
                            final String response = gson.toJson(taskManager.getSubTasksByEpic(id));
                            message.sendText200(httpExchange, response);
                            return;
                        } else {
                            message.sendError404(httpExchange, "Нет подзадач для эпика с идентификатором -" + id);
                        }
                    }
                    if (Pattern.matches("^/tasks/epic$", path) && query == null) {
                        final String response = gson.toJson(taskManager.getEpics());
                        message.sendText200(httpExchange, response);
                        return;
                    }
                    if (Pattern.matches("^/tasks/epic/$", path) && query != null) {
                        String idString = query.substring(3);
                        int id = parsePathId(idString);
                        if (id != -1) {
                            final String response = gson.toJson(taskManager.getEpicById(id));
                            message.sendText200(httpExchange, response);
                            return;
                        } else {
                            message.sendError404(httpExchange, "Нет эпика с идентификатором -" + id);
                        }
                    }
                    if (Pattern.matches("^/tasks/history$", path)) {
                        final String response = gson.toJson(taskManager.getHistoryManager());
                        message.sendText200(httpExchange, response);
                        return;
                    }
                    if (Pattern.matches("^/tasks$", path)) {
                        final String response = gson.toJson(taskManager.getPrioritizedTasks());
                        message.sendText200(httpExchange, response);
                        return;
                    }
                    break;
                case "POST":
                    if (body.contains("title")) {
                        if (Pattern.matches("^/tasks/task$", path)) {
                            Task task = gson.fromJson(body, Task.class);
                            if (task.getId() != 0 && task.getStatus() != null) {
                                taskManager.updateTask(task);
                                message.sendText200(httpExchange, "");
                            } else {
                                taskManager.addTask(task);
                                httpExchange.sendResponseHeaders(201, 0);
                            }
                            return;
                        }
                        if (Pattern.matches("^/tasks/epic$", path)) {
                            Epic epic = gson.fromJson(body, Epic.class);
                            if (epic.getId() != 0 && epic.getStatus() != null) {
                                taskManager.updateEpic(epic.getId());
                                message.sendText200(httpExchange, "");
                            } else {
                                taskManager.addTask(epic);
                                httpExchange.sendResponseHeaders(201, 0);
                            }
                            return;
                        }
                        if (Pattern.matches("^/tasks/subtask$", path)) {
                            SubTask subTask = gson.fromJson(body, SubTask.class);
                            if (subTask.getId() != 0 && subTask.getStatus() != null) {
                                taskManager.updateSubTask(subTask);
                                message.sendText200(httpExchange, "");
                            } else {
                                taskManager.addTask(subTask);
                                httpExchange.sendResponseHeaders(201, 0);
                            }
                            return;
                        }
                    } else {
                        message.sendError400(httpExchange, "Попробуйте еще раз добавить задачу, нет данных полей");
                        System.out.println("Попробуйте еще раз добавить задачу");
                        message.sendError405(httpExchange,"");
                    }
                    break;

                case "DELETE":
                    if (Pattern.matches("^/tasks/task$", path) && query == null) {
                        taskManager.deleteAllTask();
                        message.sendText200(httpExchange, "Удалены все задачи");
                        return;
                    }
                    if (Pattern.matches("^/tasks/task/$", path) && query != null) {
                        String idString = query.substring(3);
                        int id = parsePathId(idString);
                        if (id != -1) {
                            taskManager.deleteTask(id);
                            message.sendText200(httpExchange, "Задача " + id + " удалена");
                        } else {
                            message.sendError404(httpExchange, "Нет задачи с идентификатором -" + id);
                            }
                        return;
                    }
                    if (Pattern.matches("^/tasks/subtask$", path) && query == null) {
                        taskManager.deleteAllSubTasks();
                        message.sendText200(httpExchange, "Удалены все подзадачи");
                        return;
                    }
                    if (Pattern.matches("^/tasks/subtask/$", path) && query != null) {
                        String idString = query.substring(3);
                        int id = parsePathId(idString);
                        if (id != -1) {
                            taskManager.deleteSubTask(id);
                            message.sendText200(httpExchange, "удален " + id + " подзадача");
                        } else {
                            message.sendError404(httpExchange, "Нет подзадачи с идентификатором -" + id);
                        }
                        return;
                    }

                    if (Pattern.matches("^/tasks/epic$", path) && query == null) {
                        taskManager.deleteAllEpic();
                        message.sendText200(httpExchange, "Удалены /все/ эпики и их подзадачи");
                        return;
                    }
                    if (Pattern.matches("^/tasks/epic/$", path) && query != null) {
                        String idString = query.substring(3);
                        int id = parsePathId(idString);
                        if (id != -1) {
                            taskManager.deleteEpic(id);
                            message.sendText200(httpExchange, "Удален " + id + " эпик и его подзадачи");
                        } else {
                            message.sendError404(httpExchange, "Нет эпика с идентификатором -" + id);
                        }
                        return;
                    }
                    break;
                default: {
                    System.out.println("/ ждем GET-запрос, POST-запрос, DELETE-запрос, а получили - " + requestMethod);
                    message.sendError405(httpExchange,"");
                }
            }
        } catch (ManagerSaveException exception) {
            message.sendError412(httpExchange, exception.getMessage());
        } catch (InvalidTimeException exception) {
            System.out.println("Ошибка валидации времени");
            message.sendError412(httpExchange, exception.getMessage());
        } catch (Exception exception) {
            System.out.println("Ошибка обработки запроса");
            message.sendError405(httpExchange, exception.getMessage());
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
}

