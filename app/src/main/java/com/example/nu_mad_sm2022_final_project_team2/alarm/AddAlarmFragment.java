package com.example.nu_mad_sm2022_final_project_team2.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.nu_mad_sm2022_final_project_team2.R;
import com.example.nu_mad_sm2022_final_project_team2.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class AddAlarmFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private IAddAlarmFragmentAction mListener;

    private EditText addAlarmTimeInput, addAlarmMessageInput;
    private Switch addAlarmAMPMSwitch, addAlarmWakeUpTaskSwitch;
    private TextView addAlarmAMPMText;
    private RadioGroup radioGroup;
    private RadioButton onceOption, dailyOption, weeklyOption, biweeklyOption;
    private Button addAlarmSaveButton;
    private ImageView addAlarmLeftArrow;

    public AddAlarmFragment() {}

    public static AddAlarmFragment newInstance() {
        AddAlarmFragment fragment = new AddAlarmFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IAddAlarmFragmentAction) {
            this.mListener = (IAddAlarmFragmentAction) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_alarm, container, false);

        addAlarmTimeInput = view.findViewById(R.id.addAlarmTimeInput);
        addAlarmAMPMSwitch = view.findViewById(R.id.addAlarmAMPMSwitch);
        addAlarmWakeUpTaskSwitch = view.findViewById(R.id.addAlarmWakeUpTaskSwitch);
        addAlarmAMPMText = view.findViewById(R.id.addAlarmAMPMText);
        addAlarmLeftArrow = view.findViewById(R.id.addAlarmLeftArrow);
        addAlarmSaveButton = view.findViewById(R.id.addAlarmSaveButton);
        addAlarmMessageInput = view.findViewById(R.id.addAlarmMessageInput);

        radioGroup = view.findViewById(R.id.addAlarmRadioGroup);
        onceOption = view.findViewById(R.id.addAlarmOnceOption);
        dailyOption = view.findViewById(R.id.addAlarmDailyOption);
        weeklyOption = view.findViewById(R.id.addAlarmWeeklyOption);
        biweeklyOption = view.findViewById(R.id.addAlarmBiweeklyOption);
        radioGroup.check(R.id.addAlarmOnceOption);

        setCurrentTime();

        addAlarmTimeInput.setClickable(true);
        addAlarmTimeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        addAlarmAMPMSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String amOrPm = b ? "PM" : "AM";
                addAlarmAMPMText.setText(amOrPm);
            }
        });

        addAlarmLeftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addAlarmBackArrowClicked();
            }
        });


        addAlarmSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeStr = addAlarmTimeInput.getText().toString();
                int index = timeStr.indexOf(":");
                String hourStr = timeStr.substring(0, index);
                String minuteStr = timeStr.substring(index + 1);
                int hour = Integer.parseInt(hourStr);
                int minute = Integer.parseInt(minuteStr);

                String message = addAlarmMessageInput.getText().toString();
                boolean isAm = !addAlarmAMPMSwitch.isChecked();
                boolean isWakeUpTask = addAlarmWakeUpTaskSwitch.isChecked();

                int chosenOptionId = radioGroup.getCheckedRadioButtonId();
                AlarmFrequency alarmFrequency = getAlarmFrequency(chosenOptionId);

                Alarm newAlarm = new Alarm(hour, minute, isAm, isWakeUpTask, true, alarmFrequency, message);
                addAlarmInDatabase(newAlarm);
                mListener.addAlarmDone();
            }
        });


        return view;
    }

    private void setCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (hour == 0) {
            hour = 12;
        } else if (hour > 12) {
            hour = hour - 12;
        }

        String hourStr = hour < 10 ? "0" + hour : String.valueOf(hour);
        String minuteStr = minute < 10 ? "0" + minute : String.valueOf(minute);
        String currentTime = hourStr + ":" + minuteStr;

        addAlarmTimeInput.setText(currentTime);
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String selectedHourStr = selectedHour < 10 ? "0" + selectedHour : String.valueOf(selectedHour);
                String selectedMinuteStr = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);
                addAlarmTimeInput.setText(selectedHourStr + ":" + selectedMinuteStr);
            }
        }, currentHour, currentMinute, true);

        timePickerDialog.show();
    }

    private AlarmFrequency getAlarmFrequency(int chosenOptionId) {
        switch (chosenOptionId) {
            case R.id.addAlarmOnceOption: return AlarmFrequency.ONCE;
            case R.id.addAlarmDailyOption: return AlarmFrequency.DAILY;
            case R.id.addAlarmWeeklyOption: return AlarmFrequency.WEEKLY;
            case R.id.addAlarmBiweeklyOption: return AlarmFrequency.BIWEEKLY;
            default: return AlarmFrequency.ONCE;
        }
    }

    private void addAlarmInDatabase(Alarm newAlarm) {
        db.collection("users")
                .document(mUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);
                            ArrayList<Alarm> alarms = user.getAlarms();
                            alarms = insertAlarmIntoSortedAlarms(alarms, newAlarm);
                            updateAlarms(alarms, newAlarm);
                        }
                    }
                });
    }

    private void updateAlarms(ArrayList<Alarm> alarms, Alarm newAlarm) {
        db.collection("users")
                .document(mUser.getEmail())
                .update("alarms", alarms)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Alarm added!", Toast.LENGTH_SHORT).show();
                        newAlarm.schedule(getActivity().getApplicationContext());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to add alarm.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private ArrayList<Alarm> insertAlarmIntoSortedAlarms(ArrayList<Alarm> alarms, Alarm newAlarm) {
        if (alarms.size() == 0) {
            alarms.add(newAlarm);
            return alarms;
        }

        if (newAlarm.isAM()) {
            alarms = newAlarm.getHour() == 12 ? insert12AM(alarms, newAlarm) : insertAMAlarm(alarms, newAlarm);
            return alarms;
        } else {
            alarms = newAlarm.getHour() == 12 ? insert12PM(alarms, newAlarm) : insertPMAlarm(alarms, newAlarm);
            return alarms;
        }
    }

    private ArrayList<Alarm> insertAMAlarm(ArrayList<Alarm> alarms, Alarm newAlarm) {
        for (int i = 0; i < alarms.size(); i++) {
            Alarm alarm = alarms.get(i);
            if (!alarm.isAM()) {
                alarms.add(i, newAlarm);
                return alarms;
            }

            if (alarm.getHour() == newAlarm.getHour()) {
                if (alarm.getMinute() > newAlarm.getMinute()) {
                    alarms.add(i, newAlarm);
                    return alarms;
                }
            }

            if (alarm.getHour() != 12 && alarm.getHour() > newAlarm.getHour()) {
                alarms.add(i, newAlarm);
                return alarms;
            }
        }

        alarms.add(newAlarm);
        return alarms;
    }

    private ArrayList<Alarm> insert12AM(ArrayList<Alarm> alarms, Alarm newAlarm) {
        int i = 0;
        while (i < alarms.size() && alarms.get(i).isAM()) {
            if (alarms.get(i).getHour() == 12) {
                if (alarms.get(i).getMinute() > newAlarm.getMinute()) {
                    alarms.add(i, newAlarm);
                    return alarms;
                }
            } else {
                alarms.add(i, newAlarm);
                return alarms;
            }
            i++;
        }

        alarms.add(newAlarm);
        return alarms;
    }

    private ArrayList<Alarm> insertPMAlarm(ArrayList<Alarm> alarms, Alarm newAlarm) {
        for (int i = 0; i < alarms.size(); i++) {
            Alarm alarm = alarms.get(i);
            if (!alarm.isAM()) {
                if (alarm.getHour() == newAlarm.getHour()) {
                    if (alarm.getMinute() > newAlarm.getMinute()) {
                        alarms.add(i, newAlarm);
                        return alarms;
                    }
                }

                if (alarm.getHour() != 12 && alarm.getHour() > newAlarm.getHour()) {
                    alarms.add(i, newAlarm);
                    return alarms;
                }
            }
        }

        alarms.add(newAlarm);
        return alarms;
    }

    private ArrayList<Alarm> insert12PM(ArrayList<Alarm> alarms, Alarm newAlarm) {
        int i = 0;
        while (i < alarms.size()) {
            if (!alarms.get(i).isAM()) {
                if (alarms.get(i).getHour() == 12) {
                    if (alarms.get(i).getMinute() > newAlarm.getMinute()) {
                        alarms.add(i, newAlarm);
                        return alarms;
                    }
                } else {
                    alarms.add(i, newAlarm);
                    return alarms;
                }
            }
            i++;
        }

        alarms.add(newAlarm);
        return alarms;
    }

    public interface IAddAlarmFragmentAction {
        void addAlarmBackArrowClicked();
        void addAlarmDone();
    }
}