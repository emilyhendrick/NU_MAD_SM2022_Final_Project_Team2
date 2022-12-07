package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class ItemsAdaptor extends RecyclerView.Adapter<com.example.nu_mad_sm2022_final_project_team2.calendar_item.ItemsAdaptor.ViewHolder> {

    private ArrayList<ACalendarItem> tasks;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private com.example.nu_mad_sm2022_final_project_team2.calendar_item.ItemsAdaptor.ITasksListRecyclerAction mListener;
    private static View view;

    public ItemsAdaptor() {}

    public ItemsAdaptor(ArrayList<ACalendarItem> tasks, Context context) {
        this.tasks = tasks;
        if (context instanceof com.example.nu_mad_sm2022_final_project_team2.calendar_item.ItemsAdaptor.ITasksListRecyclerAction) {
            this.mListener = (com.example.nu_mad_sm2022_final_project_team2.calendar_item.ItemsAdaptor.ITasksListRecyclerAction) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    public ArrayList<ACalendarItem> getItems() {
        return tasks;
    }

    public void setTasks(ArrayList<ACalendarItem> items) {
        this.tasks = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView alarmTime, alarmMessage;
        private final Switch alarmSwitch;
        private final ConstraintLayout alarmLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            alarmTime = itemView.findViewById(R.id.alarmTime);
            alarmMessage = itemView.findViewById(R.id.alarmMessage);
            alarmSwitch = itemView.findViewById(R.id.alarmSwitch);
            alarmLayout = itemView.findViewById(R.id.alarmLayout);
        }

        public TextView getAlarmTime() {
            return alarmTime;
        }


        public TextView getAlarmMessage() {
            return alarmMessage;
        }

        public Switch getAlarmSwitch() {
            return alarmSwitch;
        }

        public ConstraintLayout getAlarmLayout() {
            return alarmLayout;
        }
    }

    @NonNull
    @Override
    public com.example.nu_mad_sm2022_final_project_team2.calendar_item.ItemsAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemRecyclerView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.alarm_row, parent, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        return new com.example.nu_mad_sm2022_final_project_team2.calendar_item.ItemsAdaptor.ViewHolder(itemRecyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.nu_mad_sm2022_final_project_team2.calendar_item.ItemsAdaptor.ViewHolder holder, int position) {
        ACalendarItem task = this.tasks.get(position);
        //holder.getAlarmTime().setText(task.toString() + " " + alarm.getAmOrPm());
        //holder.getAlarmMessage().setText(task.getMessage());
       // holder.getAlarmSwitch().setChecked(task.isOn());

    }

    private void updateTaskInDatabase(ACalendarItem newItem, int index) {
        db.collection("users")
                .document(mUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);
                            ArrayList<ACalendarItem> tasks = user.getTasks();
                            tasks.set(index, newItem);
                            updateTasks(tasks);
                        }
                    }
                });
    }

    private void updateTasks(ArrayList<ACalendarItem> tasks) {
        db.collection("users")
                .document(mUser.getEmail())
                .update("tasks", tasks);
    }

    @Override
    public int getItemCount() {

        if (this.tasks == null) {
            return 0;
        } else {
            return this.tasks.size();
        }

    }

    public interface ITasksListRecyclerAction {
        void editTaskClicked(ACalendarItem task, int position);
    }
}

