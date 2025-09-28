package com.astronaut.schedule;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ScheduleManager manager = ScheduleManager.getInstance();
        TaskObserver observer = new ConflictObserver();
        manager.addObserver(observer);

        boolean exit = false;
        while (!exit) {
            System.out.println("\n=== Astronaut Daily Schedule Organizer ===");
            System.out.println("1. Add Task");
            System.out.println("2. Remove Task");
            System.out.println("3. View Tasks");
            System.out.println("4. Edit Task");
            System.out.println("5. Mark Task Completed");
            System.out.println("6. View Tasks by Priority");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter description: ");
                        String desc = scanner.nextLine();
                        System.out.print("Enter start time (HH:MM): ");
                        String start = scanner.nextLine();
                        System.out.print("Enter end time (HH:MM): ");
                        String end = scanner.nextLine();
                        System.out.print("Enter priority (High/Medium/Low): ");
                        String priority = scanner.nextLine();

                        Task task = TaskFactory.createTask(desc, start, end, priority);
                        manager.addTask(task);
                    }
                    case 2 -> {
                        System.out.print("Enter task description to remove: ");
                        String desc = scanner.nextLine();
                        manager.removeTask(desc);
                    }
                    case 3 -> manager.viewTasks();
                    case 4 -> {
                        System.out.print("Enter task description to edit: ");
                        String desc = scanner.nextLine();
                        System.out.print("Enter new start time (HH:MM): ");
                        String start = scanner.nextLine();
                        System.out.print("Enter new end time (HH:MM): ");
                        String end = scanner.nextLine();
                        manager.editTask(desc, start, end);
                    }
                    case 5 -> {
                        System.out.print("Enter task description to mark complete: ");
                        String desc = scanner.nextLine();
                        manager.markTaskCompleted(desc);
                    }
                    case 6 -> {
                        System.out.print("Enter priority (High/Medium/Low): ");
                        String priority = scanner.nextLine();
                        manager.viewTasksByPriority(priority);
                    }
                    case 7 -> {
                        exit = true;
                        logger.info("Application exited by user.");
                    }
                    default -> System.out.println("Invalid option. Try again.");
                }
            } catch (Exception e) {
                logger.severe("Error: " + e.getMessage());
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }
}


class Task {
    private final String description;
    private String startTime;
    private String endTime;
    private final String priority;
    private boolean completed;

    public Task(String description, String startTime, String endTime, String priority) {
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
        this.completed = false;
    }

    public String getDescription() { return description; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getPriority() { return priority; }
    public boolean isCompleted() { return completed; }

    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void markCompleted() { this.completed = true; }

    @Override
    public String toString() {
        return startTime + " - " + endTime + ": " + description +
                " [" + priority + "]" + (completed ? " ✅" : "");
    }
}


class TaskFactory {
    public static Task createTask(String description, String startTime, String endTime, String priority) {
        if (!TimeValidator.isValidTime(startTime) || !TimeValidator.isValidTime(endTime)) {
            throw new IllegalArgumentException("Invalid time format.");
        }
        return new Task(description, startTime, endTime, priority);
    }
}


class TimeValidator {
    private static final Pattern TIME_PATTERN = Pattern.compile("([01]?[0-9]|2[0-3]):[0-5][0-9]");

    public static boolean isValidTime(String time) {
        return TIME_PATTERN.matcher(time).matches();
    }
}


interface TaskObserver {
    void onConflict(Task newTask, Task conflictingTask);
}


class ConflictObserver implements TaskObserver {
    @Override
    public void onConflict(Task newTask, Task conflictingTask) {
        System.out.println("Error: Task conflicts with existing task \"" + conflictingTask.getDescription() + "\".");
    }
}


class ScheduleManager {
    private static final Logger logger = Logger.getLogger(ScheduleManager.class.getName());
    private static ScheduleManager instance;
    private final List<Task> tasks;
    private final List<TaskObserver> observers;

    private ScheduleManager() {
        tasks = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public static synchronized ScheduleManager getInstance() {
        if (instance == null) {
            instance = new ScheduleManager();
        }
        return instance;
    }

    public void addObserver(TaskObserver observer) {
        observers.add(observer);
    }

    public void addTask(Task task) {
        for (Task existing : tasks) {
            if (isOverlap(task, existing)) {
                notifyConflict(task, existing);
                return;
            }
        }
        tasks.add(task);
        logger.info("Task added: " + task.getDescription());
        System.out.println("Task added successfully. No conflicts.");
    }

    public void removeTask(String description) {
        Optional<Task> taskOpt = tasks.stream()
                .filter(t -> t.getDescription().equalsIgnoreCase(description))
                .findFirst();

        if (taskOpt.isPresent()) {
            tasks.remove(taskOpt.get());
            logger.info("Task removed: " + description);
            System.out.println("Task removed successfully.");
        } else {
            System.out.println("Error: Task not found.");
        }
    }

    public void editTask(String description, String newStart, String newEnd) {
        Optional<Task> taskOpt = tasks.stream()
                .filter(t -> t.getDescription().equalsIgnoreCase(description))
                .findFirst();

        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            if (!TimeValidator.isValidTime(newStart) || !TimeValidator.isValidTime(newEnd)) {
                System.out.println("Error: Invalid time format.");
                return;
            }
            task.setStartTime(newStart);
            task.setEndTime(newEnd);
            System.out.println("Task updated successfully.");
        } else {
            System.out.println("Error: Task not found.");
        }
    }

    public void markTaskCompleted(String description) {
        Optional<Task> taskOpt = tasks.stream()
                .filter(t -> t.getDescription().equalsIgnoreCase(description))
                .findFirst();

        if (taskOpt.isPresent()) {
            taskOpt.get().markCompleted();
            System.out.println("Task marked as completed.");
        } else {
            System.out.println("Error: Task not found.");
        }
    }

    public void viewTasksByPriority(String priority) {
        List<Task> filtered = tasks.stream()
                .filter(t -> t.getPriority().equalsIgnoreCase(priority))
                .toList();

        if (filtered.isEmpty()) {
            System.out.println("No tasks found with priority: " + priority);
        } else {
            filtered.forEach(System.out::println);
        }
    }

    public void viewTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks scheduled for the day.");
            return;
        }
        tasks.sort(Comparator.comparing(Task::getStartTime));
        for (Task task : tasks) {
            System.out.println(task);
        }
    }

    private boolean isOverlap(Task newTask, Task existing) {
        return !(newTask.getEndTime().compareTo(existing.getStartTime()) <= 0 ||
                 newTask.getStartTime().compareTo(existing.getEndTime()) >= 0);
    }

    private void notifyConflict(Task newTask, Task conflictingTask) {
        for (TaskObserver observer : observers) {
            observer.onConflict(newTask, conflictingTask);
        }
    }
}
