package com.example.tasklogger;

import java.io.Serializable;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id; // add this
    private String title;
    private String deadline; // storing as String for simplicity
    private String notes;

    // Constructor for creating a new task (before saving to DB)
    public Task(String title, String deadline, String notes) {
        this.title = title;
        this.deadline = deadline;
        this.notes = notes;
    }

    // Constructor for tasks loaded from the database (with id)
    public Task(int id, String title, String deadline, String notes) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.notes = notes;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDeadline() { return deadline; }
    public String getNotes() { return notes; }

    // Setters if needed
    // public void setTitle(String title) { this.title = title; }

    @Override
    public String toString() {
        return "Title: " + title + "\nDeadline: " + deadline + "\nNotes: " + notes;
    }
}