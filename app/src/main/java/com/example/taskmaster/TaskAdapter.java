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
    private OnTaskInteractionListener listener;

    public TaskAdapter(List<Task> tasks, MainActivity listener)

    public static class TaskViewHolder extends RecyclerView.ViewHolder{

        TextView taskTitleView;
        TextView somthingElseView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.taskTitleView = itemView.findViewById(R.id.taskTitle);
            this.somthingElseView = itemView.findViewById(R.id.somthingElse);

        }
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task, parent, false);
        TaskViewHolder holder = new TaskViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if(position == 0){
            holder.taskTitleView.setText("Ahren");
        }else
            holder.taskTitleView.setText("Everyone Else");
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public interface OnTaskInteractionListener{
        public void taskCommand(Task task);

    }
}
