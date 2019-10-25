package com.example.taskmaster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public List<Task> tasks;
    protected OnTaskInteractionListener listener;



    public TaskAdapter(List<Task> tasks, OnTaskInteractionListener listener){
        this.tasks = tasks;
        this.listener = listener;
    }



    public static class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnTaskInteractionListener listener;
        TextView taskTitleView;
        TextView somthingElseView;
        Task myTask;


        public TaskViewHolder(@NonNull View itemView, OnTaskInteractionListener listener) {
            super(itemView);
            this.listener = listener;
            this.taskTitleView = itemView.findViewById(R.id.taskTitle);
            this.somthingElseView = itemView.findViewById(R.id.somthingElse);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            listener.taskCommand(this.myTask);
        }
    }




    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task, parent, false);
        TaskViewHolder holder = new TaskViewHolder(v, listener);
        return holder;
    }




    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task taskAtPosition = this.tasks.get(position);
        holder.myTask = taskAtPosition;
        holder.myTask = taskAtPosition;
        holder.taskTitleView.setText(taskAtPosition.getTitle());
        holder.somthingElseView.setText(taskAtPosition.getState());
    }




    @Override
    public int getItemCount() {
        return this.tasks.size();
    }




    public interface OnTaskInteractionListener{
        void taskCommand(Task task);

    }
}
