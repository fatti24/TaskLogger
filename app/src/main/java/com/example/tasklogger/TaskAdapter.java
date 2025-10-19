package com.example.tasklogger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Task task);
        void onItemLongClick(Task task);
        void onItemDelete(Task task);
    }

    private List<Task> taskList;
    private OnItemClickListener listener;

    public TaskAdapter(List<Task> taskList, OnItemClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task, listener);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDeadline;
        Button btnDeleteTask;

        public TaskViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDeadline = itemView.findViewById(R.id.textViewDeadline);
            btnDeleteTask = itemView.findViewById(R.id.btnDeleteTask);
        }

        public void bind(Task task, OnItemClickListener listener) {
            textViewTitle.setText(task.getTitle());
            textViewDeadline.setText(task.getDeadline());

            itemView.setOnClickListener(v -> listener.onItemClick(task));
            itemView.setOnLongClickListener(v -> {
                listener.onItemLongClick(task);
                return true;
            });

            btnDeleteTask.setOnClickListener(v -> listener.onItemDelete(task));
        }
    }
}
