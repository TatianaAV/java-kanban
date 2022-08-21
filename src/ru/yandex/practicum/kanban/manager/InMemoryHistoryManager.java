package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;
    private final Map<Integer, Node> nodeMap = new HashMap<>();


    @Override
    public List<Task> getHistory() {

        return getListTask();
    }

    private List<Task> getListTask() {
        List<Task> listTask = new ArrayList<>();
        Node node = head;
        while ((node != null)) {
            listTask.add(node.getTask());
            node = node.next;
        }
        return listTask;
    }

    @Override
    public void addHistory(Task task) {
        if (!Objects.isNull(task)) {
            final int id = task.getId();
            linkLast(task);
            if (nodeMap.containsKey(id)) {
                remove(id);
            }
            nodeMap.put(id, tail);
        }
    }

    private void linkLast(Task task) {
        final Node node = new Node(tail, task, null);
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.get(id);
        removeNode(node);
        nodeMap.remove(id);
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        if (node.prev != null && node.next == null) {
            node.prev.next = node.next;
            tail = node.prev;
        }
        if (node.prev != null && node.next != null) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        if (node.prev == null && node.next != null) {
            node.next.prev = node.prev;
            head = node.next;
        }
        if (node.prev == null && node.next == null) {
            tail = node.prev;
            head = node.next;
        }
    }

    public static class Node {

        private Node prev;
        private Task task;
        private Node next;



        public Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }

        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }
}
