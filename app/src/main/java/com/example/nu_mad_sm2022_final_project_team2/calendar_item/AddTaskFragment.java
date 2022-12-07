package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;

import com.example.nu_mad_sm2022_final_project_team2.R;
import com.example.nu_mad_sm2022_final_project_team2.User;
import com.example.nu_mad_sm2022_final_project_team2.alarm.AlarmFrequency;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTaskFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private AddTaskFragment.IAddTaskFragmentAction mListener;

    private Button btn_add_event, btn_add_task, btn_add_task_nav,  btn_start_date_date, btn_due_date_date, btn_start_date_time, btn_due_date_time;
    private EditText inp_txt_task_name, inp_duration_number, inp_start_date, inp_due_date;
    private Switch start_date_am_pm, due_date_am_pm;
    private DatePicker pick_date;
    private TimePicker pick_time;
    private Spinner spin_categories;


    public AddTaskFragment() {
        // Required empty public constructor
    }


    public static AddTaskFragment newInstance() {
        AddTaskFragment fragment = new AddTaskFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IAddTaskFragmentAction) {
            this.mListener = (IAddTaskFragmentAction) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);
        // btn_set_start_date, btn_set_due_date;
        btn_start_date_date = view.findViewById(R.id.btn_start_date_date);
        btn_due_date_date = view.findViewById(R.id.btn_due_date_date);
        btn_start_date_time= view.findViewById(R.id.btn_start_date_time);
        btn_due_date_time = view.findViewById(R.id.btn_due_date_time);
        btn_add_task_nav = view.findViewById(R.id.btn_add_task_nav);
        start_date_am_pm = view.findViewById(R.id.start_date_am_pm);
        due_date_am_pm = view.findViewById(R.id.due_date_am_pm);
        btn_add_task = view.findViewById(R.id.btn_add_task);


        // spinner
        Spinner cat = view.findViewById(R.id.spin_categories);



        btn_start_date_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(btn_start_date_time);
            }
        });

        btn_due_date_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(btn_due_date_time);
            }
        });

        btn_start_date_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(btn_start_date_date);
            }
        });

        btn_due_date_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(btn_due_date_date);
            }
        });


        btn_add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if
                String timeStr = addAlarmTimeInput.getText().toString();
                int index = timeStr.indexOf(":");
                String hourStr = timeStr.substring(0, index);
                String minuteStr = timeStr.substring(index + 1);
                int hour = Integer.parseInt(hourStr);
                int minute = Integer.parseInt(minuteStr);

                String message = addAlarmMessageInput.getText().toString();
                if (message == null || message.equals("")) {
                    message = "No Des";
                }
                boolean isAm = !addAlarmAMPMSwitch.isChecked();
                boolean isWakeUpTask = addAlarmWakeUpTaskSwitch.isChecked();

                int chosenOptionId = radioGroup.getCheckedRadioButtonId();
                AlarmFrequency alarmFrequency = getAlarmFrequency(chosenOptionId);

                TaskPI newItem = new TaskPI(item)
                addTaskInDatabase(newItem);
            }
        });

        return view;



    }

    private void showTimePicker(Button btn) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (selectedHour == 0) {
                    selectedHour = 12;
                } if (selectedHour > 12) {
                    selectedHour = selectedHour - 12;
                }
                String selectedHourStr = selectedHour < 10 ? "0" + selectedHour : String.valueOf(selectedHour);
                String selectedMinuteStr = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);
                btn.setText(selectedHourStr + ":" + selectedMinuteStr + " ");
            }
        }, currentHour, currentMinute, true);

        timePickerDialog.show();
    }


    private String getAMPM(Switch ampm) {
        if (ampm.isChecked()) {
            return "am";
        }
        else {
            return "pm";
        }
    }
    private void showDatePicker(Button btn) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String selectedYear = String.valueOf(year);
                String selectedMonth = String.valueOf(monthOfYear);
                String selectedDay = String.valueOf(dayOfMonth);
                btn.setText(selectedMonth + "/" + selectedDay + "/" + selectedYear);
            }
        }, currentYear, currentMonth, currentDay);

        datePickerDialog.show();
    }



    private void addTaskInDatabase(ACalendarItem newTask) {
        db.collection("users")
                .document(mUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);
                            ArrayList<ACalendarItem> tasks = user.getTasks();
                            tasks = insertTaskIntoSortedTasks(tasks, newTask);
                            updateTasks(tasks, newTask);
                            newTask.schedule(getActivity().getApplicationContext());
                            mListener.addTaskDone();
                        }
                    }
                });
    }

    private ArrayList<ACalendarItem> insertTaskIntoSortedTasks(ArrayList<ACalendarItem> tasks, ACalendarItem newTask) {
        if (tasks.size() == 0) {
            tasks.add(newTask);
            return tasks;
        }
   return tasks;
    }

    private void updateTasks(ArrayList<ACalendarItem> tasks, ACalendarItem newTask) {
        db.collection("users")
                .document(mUser.getEmail())
                .update("tasks", tasks);
    }

    public interface IAddTaskFragmentAction {
        void addTaskBackArrowClicked();
        void addTaskDone();
    }
}