package ru.yandex.practicum.kanban.manager.json;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.manager.UserManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpUserServer {

    public static final int PORT = 8080;
    private HttpServer server;
    private Gson gson;

    private UserManager userManager;
    private TaskManager taskManager;

    public HttpUserServer() throws IOException {
        this(Managers.getDefaultUser());
    }

    public HttpUserServer(UserManager userManager) throws IOException {
        this.userManager = userManager;
        this.taskManager = userManager.getTaskManager();
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/api/v1/users", this::handleUsers);
    }

    public static void main(String[] args) throws IOException {
        HttpUserServer httpUserServer = new HttpUserServer();
        httpUserServer.start();
    }

    private void handleUsers(HttpExchange httpExchange) {
        try {
            System.out.println("\n/api/v1/users: " + httpExchange.getRequestURI());
            String requestMethod = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            switch (requestMethod) {
                case "GET": {
                    if (Pattern.matches("^/api/v1/users$", path)) {
                        final String response = gson.toJson(userManager.getAll());
                        sendText(httpExchange, response);
                        return;
                    }

                    if (Pattern.matches("^/api/v1/users/\\d+$", path)) {
                        String idString = path.replaceFirst("/api/v1/users/", "");
                        int id = parsePathId(idString);
                        if (id != -1) {
                            final String response = gson.toJson(userManager.getById(id));
                            sendText(httpExchange, response);
                            return;
                        } else {
                            System.out.println("Нет пользователя с идентификатором -" + id);
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                    }

                    if (Pattern.matches("^/api/v1/users/\\d+/tasks$", path)) {
                        String idString = path.replaceFirst("/api/v1/users/", "")
                                .replaceFirst("/tasks", "");
                        int id = parsePathId(idString);
                        if (id != -1) {
                            final String response = gson.toJson(userManager.getUserTasks(id));
                            sendText(httpExchange, response);
                            return;
                        } else {
                            System.out.println("Нет пользователя с идентификатором -" + id);
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                    }

                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("^/api/v1/users/\\d+$", path)) {
                        String idString = path.replaceFirst("/api/v1/users/", "");
                        int id = parsePathId(idString);
                        if (id != -1) {
                            userManager.delete(id);
                            System.out.println("Удалили пользователя с идентификатором -" + id);
                            httpExchange.sendResponseHeaders(200, 0);
                        }
                    }
                    break;
                }
                default: {
                    System.out.println("/ ждем GET-запрос или DELETE-запрос, а получили - " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }
        } catch (Exception exception) {
            System.out.println("Ошибка при обработка запроса");
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
        System.out.println("http://localhost:" + PORT + "/api/v1/users");
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

