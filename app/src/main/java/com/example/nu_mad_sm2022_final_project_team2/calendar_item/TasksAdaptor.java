package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nu_mad_sm2022_final_project_team2.R;
import com.example.nu_mad_sm2022_final_project_team2.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class TasksAdaptor extends RecyclerView.Adapter<TasksAdaptor.ViewHolder> {

    private ArrayList<TaskPI> tasks;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private TasksAdaptor.ITasksListRecyclerAction mListener;
    private static View view;

    public TasksAdaptor() {}

    public TasksAdaptor(ArrayList<TaskPI> tasks, Context context) {
        this.tasks = tasks;
        if (context instanceof TasksAdaptor.ITasksListRecyclerAction) {
            this.mListener = (TasksAdaptor.ITasksListRecyclerAction) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    public ArrayList<TaskPI> getItems() {
        return tasks;
    }

    public void setTasks(ArrayList<TaskPI> items) {
        this.tasks = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textTitle, dueDate, category;
        private final CheckBox isDone;
        private final ConstraintLayout taskLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            textTitle = itemView.findViewById(R.id.taskTitle);
            dueDate = itemView.findViewById(R.id.dueDate);
            category = itemView.findViewById(R.id.category);
            isDone = itemView.findViewById(R.id.done);
            taskLayout = itemView.findViewById(R.id.taskLayout);
        }

        public TextView getTaskTitle() {
            return textTitle;
        }

        public TextView getDueDate() {
            return dueDate;
        }

        public TextView getTaskCategory() {
            return category;
        }

        public CheckBox getIsDone() {
            return isDone;
        }

        public ConstraintLayout getTaskLayout() {
            return taskLayout;
        }
    }

    @NonNull
    @Override
    public TasksAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View taskRecyclerView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.task_row, parent, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        return new TasksAdaptor.ViewHolder(taskRecyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksAdaptor.ViewHolder holder, int position) {
        TaskPI task = this.tasks.get(position);
        holder.getTaskTitle().setText(task.getItem_name());
        holder.getDueDate().setText(task.getEnd_date_asString());
        holder.getTaskCategory().setText(task.getCategory());
        holder.getIsDone().setChecked(task.isDone);

        holder.getTaskLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.editTaskClicked(task, holder.getAdapterPosition());
            }

        });
    }

    private void updateTaskInDatabase(TaskPI newItem, int index) {
        db.collection("users")
                .document(mUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);
                            ArrayList<TaskPI> tasks = user.getTasks();
                            tasks.set(index, newItem);
                            updateTasks(tasks);
                        }
                    }
                });
    }

    private void updateTasks(ArrayList<TaskPI> tasks) {
        db.collection("users")
                .document(mUser.getEmail())
                .update("tasks", tasks);
    }

    @Override
    public int getItemCount() {
        try {
            return this.tasks.size();
        }
        catch(Exception e) {
            return 0;
        }
    }


    public interface ITasksListRecyclerAction {
        void editTaskClicked(TaskPI task, int position);
    }
}

