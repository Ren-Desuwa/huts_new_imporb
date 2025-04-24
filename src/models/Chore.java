package models;

import java.time.LocalDate;

public class Chore {
    private Integer id;
    private Integer userId;
    private String choreName;
    private String description;
    private LocalDate dueDate;
    private LocalDate completionDate;
    private boolean completed;
    private String frequency; // daily, weekly, monthly, one-time
    private String assignedTo;
    private Integer priority; // 1-5 (1 highest, 5 lowest)

    // Constructor with all fields
    public Chore(Integer id, Integer userId, String choreName, String description, 
                LocalDate dueDate, LocalDate completionDate, boolean completed, 
                String frequency, String assignedTo, Integer priority) {
        this.id = id;
        this.userId = userId;
        this.choreName = choreName;
        this.description = description;
        this.dueDate = dueDate;
        this.completionDate = completionDate;
        this.completed = completed;
        this.frequency = frequency;
        this.assignedTo = assignedTo;
        this.priority = priority;
    }

    // Constructor for new chore (without ID and completion date)
    public Chore(Integer userId, String choreName, String description, LocalDate dueDate, 
                boolean completed, String frequency, String assignedTo, Integer priority) {
        this(null, userId, choreName, description, dueDate, null, completed, 
             frequency, assignedTo, priority);
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getChoreName() {
        return choreName;
    }

    public void setChoreName(String choreName) {
        this.choreName = choreName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Chore{" +
               "id=" + id +
               ", userId=" + userId +
               ", choreName='" + choreName + '\'' +
               ", dueDate=" + dueDate +
               ", completed=" + completed +
               ", assignedTo='" + assignedTo + '\'' +
               '}';
    }
}