package com.example.taskmaster;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.amazonaws.amplify.generated.graphql.ListTasksQuery;

@Entity
public class Task {
    @ColumnInfo(name = "task_title") //sets the below vars for column data
    private String title;
    private String body;
    private String state;



    //  needed to auto gen ID # gor each entry
    @PrimaryKey(autoGenerate = true)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

//      Task constructor
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

    public void setState(String state) {
        this.state = state;
    }

}
