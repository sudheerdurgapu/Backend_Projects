

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Task {

    private int id;
    private String description;
    private String status;
    private String createdAt;
    private String updatedAt;

    // Constructor
    public Task(int id, String description, String status, String createdAt, String updatedAt) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Serialize Task to String (for simplicity)
    @Override
    public String toString() {
        return id + ";" + description + ";" + status + ";" + createdAt + ";" + updatedAt;
    }

    // Deserialize Task from String
    public static Task fromString(String data) {
        String[] parts = data.split(";");
        return new Task(
                Integer.parseInt(parts[0]),
                parts[1],
                parts[2],
                parts[3],
                parts[4]
        );
    }
}

class TaskTracker {

    private static final String FILE_PATH = "tasks.json";

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0];
        try {
            switch (command) {
                case "add":
                    if (args.length < 2) {
                        System.out.println("Usage: task-cli add \"<task description>\"");
                        return;
                    }
                    addTask(args[1]);
                    break;
                case "update":
                    if (args.length < 3) {
                        System.out.println("Usage: task-cli update <task-id> \"<new description>\"");
                        return;
                    }
                    updateTask(Integer.parseInt(args[1]), args[2]);
                    break;
                case "delete":
                    if (args.length < 2) {
                        System.out.println("Usage: task-cli delete <task-id>");
                        return;
                    }
                    deleteTask(Integer.parseInt(args[1]));
                    break;
                case "mark-in-progress":
                    if (args.length < 2) {
                        System.out.println("Usage: task-cli mark-in-progress <task-id>");
                        return;
                    }
                    markTaskStatus(Integer.parseInt(args[1]), "in-progress");
                    break;
                case "mark-done":
                    if (args.length < 2) {
                        System.out.println("Usage: task-cli mark-done <task-id>");
                        return;
                    }
                    markTaskStatus(Integer.parseInt(args[1]), "done");
                    break;
                case "list":
                    if (args.length == 1) {
                        listAllTasks();
                    } else {
                        listTasksByStatus(args[1]);
                    }
                    break;
                default:
                    System.out.println("Invalid Command.");
                    printUsage();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("Usage: task-cli <command> [arguments]");
        System.out.println("Commands:");
        System.out.println("  add <description>        - Add a new task");
        System.out.println("  update <id> <description> - Update a task");
        System.out.println("  delete <id>             - Delete a task");
        System.out.println("  mark-in-progress <id>   - Mark a task as in-progress");
        System.out.println("  mark-done <id>          - Mark a task as done");
        System.out.println("  list [status]           - List tasks (optional: done, todo, in-progress)");
    }

    private static void addTask(String description) throws IOException {
        List<Task> tasks = loadTasks();
        int newId = tasks.isEmpty() ? 1 : tasks.get(tasks.size() - 1).getId() + 1;
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Task task = new Task(newId, description, "todo", timestamp, timestamp);
        tasks.add(task);
        saveTasks(tasks);
        System.out.println("Task added successfully (ID: " + newId + ")");
    }

    private static void updateTask(int id, String newDescription) throws IOException {
        List<Task> tasks = loadTasks();
        for (Task task : tasks) {
            if (task.getId() == id) {
                task.setDescription(newDescription);
                task.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                saveTasks(tasks);
                System.out.println("Task updated successfully (ID: " + id + ")");
                return;
            }
        }
        System.out.println("Task not found (ID: " + id + ")");
    }

    private static void deleteTask(int id) throws IOException {
        List<Task> tasks = loadTasks();
        tasks.removeIf(task -> task.getId() == id);
        saveTasks(tasks);
        System.out.println("Task deleted successfully (ID: " + id + ")");
    }

    private static void markTaskStatus(int id, String status) throws IOException {
        List<Task> tasks = loadTasks();
        for (Task task : tasks) {
            if (task.getId() == id) {
                task.setStatus(status);
                task.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                saveTasks(tasks);
                System.out.println("Task marked as " + status + " (ID: " + id + ")");
                return;
            }
        }
        System.out.println("Task not found (ID: " + id + ")");
    }

    private static void listAllTasks() throws IOException {
        List<Task> tasks = loadTasks();
        for (Task task : tasks) {
            System.out.println(task);
        }
    }

    private static void listTasksByStatus(String status) throws IOException {
        List<Task> tasks = loadTasks();
        for (Task task : tasks) {
            if (task.getStatus().equalsIgnoreCase(status)) {
                System.out.println(task);
            }
        }
    }

    private static List<Task> loadTasks() throws IOException {
        if (!Files.exists(Paths.get(FILE_PATH))) {
            return new ArrayList<>();
        }
        List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
        List<Task> tasks = new ArrayList<>();
        for (String line : lines) {
            tasks.add(Task.fromString(line));
        }
        return tasks;
    }

    private static void saveTasks(List<Task> tasks) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.toString());
        }
        Files.write(Paths.get(FILE_PATH), lines);
    }
}
