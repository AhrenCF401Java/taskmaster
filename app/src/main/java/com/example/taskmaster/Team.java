package com.example.taskmaster;

import androidx.room.Entity;

import com.amazonaws.amplify.generated.graphql.ListTasksQuery;

import java.util.LinkedList;

@Entity
public class Team{
    String Team;
    LinkedList<Task> teamTasks;
}
