package ru.yandex.practicum.kanban.manager.http.sendMessage;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Message {
    public String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    //СПАСИБО!!!
    private void sendText(HttpExchange h, String text, int code) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
    }

    public void sendText200(HttpExchange h, String text) throws IOException {
        sendText(h, text, 200);
    }

    public void sendError400(HttpExchange h, String text) throws IOException {
        sendText(h, text, 400);
    }

    public void sendError404(HttpExchange h, String text) throws IOException {
        sendText(h, text, 404);
    }

    public void sendError405(HttpExchange h, String text) throws IOException {
        sendText(h, text, 405);
    }

    public void sendError412(HttpExchange h, String text) throws IOException {
        sendText(h, text, 412);
    }

    public void sendError403(HttpExchange h, String text) throws IOException {
        sendText(h, text, 403);
    }
}
