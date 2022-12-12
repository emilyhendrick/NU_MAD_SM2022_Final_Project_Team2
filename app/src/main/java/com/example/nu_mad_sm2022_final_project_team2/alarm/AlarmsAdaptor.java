package com.example.nu_mad_sm2022_final_project_team2.alarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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

public class AlarmsAdaptor extends RecyclerView.Adapter<AlarmsAdaptor.ViewHolder> {

    private ArrayList<Alarm> alarms;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private IAlarmsListRecyclerAction mListener;
    private static View view;

    public AlarmsAdaptor() {}

    public AlarmsAdaptor(ArrayList<Alarm> alarms, Context context) {
        this.alarms = alarms;
        if (context instanceof IAlarmsListRecyclerAction) {
            this.mListener = (IAlarmsListRecyclerAction) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    public ArrayList<Alarm> getAlarms() {
        return alarms;
    }

    public void setAlarms(ArrayList<Alarm> alarms) {
        this.alarms = alarms;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView alarmTime, alarmFrequency, alarmMessage;
        private final Switch alarmSwitch;
        private final ConstraintLayout alarmLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            alarmTime = itemView.findViewById(R.id.taskTitle);
            alarmFrequency = itemView.findViewById(R.id.category);
            alarmMessage = itemView.findViewById(R.id.dueDate);
            alarmSwitch = itemView.findViewById(R.id.alarmSwitch);
            alarmLayout = itemView.findViewById(R.id.alarmLayout);
        }

        public TextView getAlarmTime() {
            return alarmTime;
        }

        public TextView getAlarmFrequency() {
            return alarmFrequency;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemRecyclerView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.alarm_row, parent, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        return new AlarmsAdaptor.ViewHolder(itemRecyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alarm alarm = this.alarms.get(position);
        holder.getAlarmTime().setText(alarm.toString() + " " + alarm.getAmOrPm());
        if (alarm.getAlarmFrequency().equals(AlarmFrequency.ONCE)) {
            holder.getAlarmFrequency().setText(alarm.getAlarmFrequency().toString());
        } else {
            holder.getAlarmFrequency().setText("Repeats " + alarm.getAlarmFrequency().toString());
        }
        holder.getAlarmMessage().setText(alarm.getMessage());
        holder.getAlarmSwitch().setChecked(alarm.isOn());

        holder.getAlarmLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.editAlarmClicked(alarm, holder.getAdapterPosition());
            }
        });

        holder.getAlarmSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                alarm.setOn(b);
                updateAlarmInDatabase(alarm, holder.getAdapterPosition());
                mListener.alarmSwitched(alarm);
            }
        });
    }

    private void updateAlarmInDatabase(Alarm newAlarm, int index) {
        db.collection("users")
                .document(mUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);
                            ArrayList<Alarm> alarms = user.getAlarms();
                            alarms.set(index, newAlarm);
                            updateAlarms(alarms);
                        }
                    }
                });
    }

    private void updateAlarms(ArrayList<Alarm> alarms) {
        db.collection("users")
                .document(mUser.getEmail())
                .update("alarms", alarms);
    }

    @Override
    public int getItemCount() {
        return this.alarms.size();
    }

    public interface IAlarmsListRecyclerAction {
        void alarmSwitched(Alarm alarm);
        void editAlarmClicked(Alarm alarm, int position);
    }
}
