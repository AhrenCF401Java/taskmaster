package com.example.taskmaster;

public class Task {
    private String title;
    private String body;
    private String state;

    public Task(String title, String body) {
        this.title = title;
        this.body = body;
        this.state = "New";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getState() {
        return state;
    }

    public void setStateNew() {
        this.state = "New";
    }
    public void setStateCompleted() {
        this.state = "Completed";
    }
    public void setStateAssigned() {
        this.state = "Assigned";
    }
    public void setStateInProgress() {
        this.state = "In Progress";
    }
}
