package com.example.tasklogger;

import java.io.Serializable;
import java.util.Date;
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String deadline; //storing as String for simplicity, or use Date object
    private String notes;

    public Task(String title, String deadline, String notes) {
        this.title = title;
        this.deadline = deadline;
        this.notes = notes;
    }

    //getters and setters
    public String getTitle() {
        return title;
    }
    public String getDeadline() {
        return deadline;
    }
    public String getNotes() {
        return notes;
    }

    // (Add Setters if needed for update functionality)
    // public void setTitle(String title) { this.title = title; }

    @Override
    public String toString() {
        return "Title: " + title + "\nDeadline: " + deadline + "\nNotes: " + notes;
    }
}
