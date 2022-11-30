package com.example.nu_mad_sm2022_final_project_team2.alarm;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditAlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditAlarmFragment extends Fragment {

    private static Alarm alarm;
    private static int index;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private IEditAlarmFragmentAction mListener;

    private EditText editAlarmTimeInput, editAlarmMessageInput;
    private Switch editAlarmAMPMSwitch, editAlarmWakeUpTaskSwitch;
    private TextView editAlarmAMPMText;
    private RadioGroup radioGroup;
    private RadioButton onceOption, dailyOption, weeklyOption, biweeklyOption;
    private Button editAlarmSaveButton, deleteButton;
    private ImageView editAlarmLeftArrow;

    private ArrayList<AlarmFrequency> alarmFrequencies;

    public EditAlarmFragment() {}

    public static EditAlarmFragment newInstance(Alarm alarmArg, int indexArg) {
        EditAlarmFragment fragment = new EditAlarmFragment();
        Bundle args = new Bundle();
        alarm = alarmArg;
        index = indexArg;
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
        if (context instanceof IEditAlarmFragmentAction) {
            this.mListener = (IEditAlarmFragmentAction) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_alarm, container, false);

        editAlarmTimeInput = view.findViewById(R.id.editAlarmTimeInput);
        editAlarmAMPMSwitch = view.findViewById(R.id.editAlarmAMPMSwitch);
        editAlarmWakeUpTaskSwitch = view.findViewById(R.id.editAlarmWakeUpTaskSwitch);
        editAlarmAMPMText = view.findViewById(R.id.editAlarmAMPMText);
        editAlarmLeftArrow = view.findViewById(R.id.editAlarmLeftArrow);
        editAlarmSaveButton = view.findViewById(R.id.editAlarmSaveButton);
        editAlarmMessageInput = view.findViewById(R.id.editAlarmMessageInput);
        deleteButton = view.findViewById(R.id.deleteAlarmButton);

        radioGroup = view.findViewById(R.id.editAlarmRadioGroup);
        onceOption = view.findViewById(R.id.editAlarmOnceOption);
        dailyOption = view.findViewById(R.id.editAlarmDailyOption);
        weeklyOption = view.findViewById(R.id.editAlarmWeeklyOption);
        biweeklyOption = view.findViewById(R.id.editAlarmBiweeklyOption);

        populateInitialAlarm();

        editAlarmTimeInput.setClickable(true);
        editAlarmTimeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR);
                int currentMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String selectedHourStr = selectedHour < 10 ? "0" + selectedHour : String.valueOf(selectedHour);
                        String selectedMinuteStr = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);
                        editAlarmTimeInput.setText(selectedHour + ":" + selectedMinuteStr);
                    }
                }, currentHour, currentMinute, true);
            }
        });

        editAlarmAMPMSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String amOrPm = b ? "PM" : "AM";
                editAlarmAMPMText.setText(amOrPm);
            }
        });

        editAlarmLeftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.editAlarmBackArrowClicked();
            }
        });

        editAlarmSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeStr = editAlarmTimeInput.getText().toString();
                int index = timeStr.indexOf(":");
                String hourStr = timeStr.substring(0, index);
                String minuteStr = timeStr.substring(index + 1);
                int hour = Integer.parseInt(hourStr);
                int minute = Integer.parseInt(minuteStr);

                String message = editAlarmMessageInput.getText().toString();
                if (message.length() == 0) {
                    message = "Alarm";
                }
                boolean isAm = !editAlarmAMPMSwitch.isChecked();
                boolean isWakeUpTask = editAlarmWakeUpTaskSwitch.isChecked();

                int chosenOptionId = radioGroup.getCheckedRadioButtonId();
                AlarmFrequency alarmFrequency;
                switch (chosenOptionId) {
                    case R.id.addAlarmOnceOption:
                        alarmFrequency = AlarmFrequency.ONCE;
                        break;
                    case R.id.addAlarmDailyOption:
                        alarmFrequency = AlarmFrequency.DAILY;
                        break;
                    case R.id.addAlarmWeeklyOption:
                        alarmFrequency = AlarmFrequency.WEEKLY;
                        break;
                    case R.id.addAlarmBiweeklyOption:
                        alarmFrequency = AlarmFrequency.BIWEEKLY;
                        break;
                    default:
                        alarmFrequency = AlarmFrequency.ONCE;
                        break;
                }

                Alarm newAlarm = new Alarm(hour, minute, isAm, isWakeUpTask, alarm.isOn(), alarmFrequency, message);
                updateAlarmInDatabase(newAlarm);
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alarm.isOn()) {
                    alarm.cancelAlarm(getActivity().getApplicationContext());
                }

                db.collection("users")
                        .document(mUser.getEmail())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    User user = task.getResult().toObject(User.class);
                                    ArrayList<Alarm> alarms = user.getAlarms();
                                    alarms.remove(alarm);
                                    updateAlarms(alarms);
                                    mListener.editAlarmDone();
                                }
                            }
                        });
            }
        });


        return view;
    }

    private void populateInitialAlarm() {
        editAlarmTimeInput.setText(alarm.toString());
        editAlarmAMPMSwitch.setChecked(!alarm.isAM()); // checked = PM
        editAlarmAMPMText.setText(alarm.getAmOrPm());
        editAlarmMessageInput.setText(alarm.getMessage());

        editAlarmWakeUpTaskSwitch.setChecked(alarm.isWakeUpTask());

        AlarmFrequency alarmFrequency = alarm.getAlarmFrequency();
        switch (alarmFrequency) {
            case ONCE:
                radioGroup.check(R.id.addAlarmOnceOption);
                break;
            case DAILY:
                radioGroup.check(R.id.addAlarmDailyOption);
                break;
            case WEEKLY:
                radioGroup.check(R.id.addAlarmWeeklyOption);
                break;
            case BIWEEKLY:
                radioGroup.check(R.id.addAlarmBiweeklyOption);
                break;
            default:
                radioGroup.check(R.id.addAlarmOnceOption);
                break;
        }

    }

    private void updateAlarmInDatabase(Alarm newAlarm) {
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
                            newAlarm.schedule(getActivity().getApplicationContext());
                            mListener.editAlarmDone();
                        }
                    }
                });
    }

    private void updateAlarms(ArrayList<Alarm> alarms) {
        db.collection("users")
                .document(mUser.getEmail())
                .update("alarms", alarms);
    }

    public interface IEditAlarmFragmentAction {
        void editAlarmBackArrowClicked();
        void editAlarmDone();
    }
}