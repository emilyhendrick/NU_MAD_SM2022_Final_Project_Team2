package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.nu_mad_sm2022_final_project_team2.R;
import com.example.nu_mad_sm2022_final_project_team2.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

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
    private EditText inp_txt_task_name, inp_duration_number, inp_start_date, inp_due_date, inp_priority;
    private Switch start_date_am_pm, due_date_am_pm;
    private DatePicker pick_date;
    private TimePicker pick_time;
    private User user;
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
        btn_add_task = view.findViewById(R.id.btn_save_event);
        inp_txt_task_name = view.findViewById(R.id.inp_txt_task_name);
        inp_priority = view.findViewById(R.id.inp_priority);
        inp_duration_number = view.findViewById(R.id.txt_inp_location);
        btn_add_event = view.findViewById(R.id.btn_add_event);


        // spinner
        Spinner spin_categories = view.findViewById(R.id.spin_categories);



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

                if (validateInputs(inp_txt_task_name, inp_duration_number,
                        spin_categories, btn_start_date_date, btn_start_date_time,
                        btn_due_date_date, btn_due_date_time, inp_priority)) {
                    String item_name = inp_txt_task_name.getText().toString();
                    String start_date = btn_start_date_date.getText().toString();
                    String due_date = btn_due_date_date.getText().toString();
                    Log.d("DUE", due_date);
                    String start_time = btn_start_date_time.getText().toString();
                    String due_time = btn_due_date_time.getText().toString();
                    String category = spin_categories.getSelectedItem().toString();
                    String duration_str = inp_duration_number.getText().toString();
                    String priority_str = inp_priority.getText().toString();

                    List<String> fieldNames = Arrays.asList("taskName", "startDate", "dueDate", "startTime", "dueTime", "category", "duration", "priority");
                    List<String> fields = Arrays.asList(item_name, start_date, due_date, start_time, due_time, category, duration_str, priority_str);
                    for (int i=0; i<fields.size(); i++) {
                        if (Objects.equals(fields.get(i), "")) {
                            Toast.makeText(getContext(), "Field '" + fieldNames.get(i) + "' is missing", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    int duration = Integer.parseInt(duration_str);
                    int priority = Integer.parseInt(priority_str);
                    String startDateTime = start_date + "T" + start_time;
                    String dueDateTime = due_date + "T" + due_time;
                    TaskPI newItem = new TaskPI(item_name, startDateTime, dueDateTime, category, duration, priority);
                    addTaskInDatabase(newItem);
                }
                else {
                    Toast.makeText(getContext(), "You must complete the entire form.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_add_event = view.findViewById(R.id.btn_add_event);
        btn_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addEventButtonClicked();
            }
        });


        return view;



    }


    private Boolean validateInputs(EditText taskname,
                                   EditText dur, Spinner cat, Button start_date,
                                   Button start_time, Button due_date, Button due_time, EditText priority) {
        Log.d("valTaskname", String.valueOf(taskname==null));
        Log.d("valDur", String.valueOf(dur==null));
        Log.d("valCat", String.valueOf(cat==null));
        Log.d("valStartDate", String.valueOf(start_date==null));
        Log.d("valStartTime", String.valueOf(start_time==null));
        return !(taskname == null || dur == null ||
                cat == null || start_date == null  ||
                start_time == null || priority == null ||
                validateDateTime(start_date, start_time) || validateDateTime(due_date, due_time));

    }

    private Boolean validateDateTime(Button bdate, Button btime) {
        return (bdate.getText().toString() == "Pick Date" || btime.getText().toString() == "Pick Time");
    }


    private void showTimePicker(Button btn) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String amOrPm = selectedHour < 12 ? "AM" : "PM";
                if (selectedHour == 0) {
                    selectedHour = 12;
                } if (selectedHour > 12) {
                    selectedHour = selectedHour - 12;
                }
                String selectedHourStr = selectedHour < 10 ? "0" + selectedHour : String.valueOf(selectedHour);
                String selectedMinuteStr = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);
                btn.setText(selectedHourStr + ":" + selectedMinuteStr + " " + amOrPm);
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



    private void addTaskInDatabase(TaskPI newTask) {
        db.collection("users")
                .document(mUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);
                            ArrayList<TaskPI> tasks = user.getTasks();
                            tasks = insertTaskIntoSortedTasks(tasks, newTask);
                            updateTasks(tasks, newTask);
                            newTask.schedule(getActivity().getApplicationContext());
                            mListener.addTaskDone();
                        }
                    }
                });
    }

    private ArrayList<TaskPI> insertTaskIntoSortedTasks(ArrayList<TaskPI> tasks, TaskPI newTask) {
        ArrayList<TaskPI> retTasks;
        if (tasks == null) {
            retTasks = new ArrayList<TaskPI>();
            retTasks.add(newTask);
        }
        else {
            retTasks = tasks;
            retTasks.add(newTask);
        }
        return retTasks;
    }

    private void updateTasks(ArrayList<TaskPI> tasks, TaskPI newTask) {
        db.collection("users")
                .document(mUser.getEmail())
                .update("tasks", tasks);
    }

    public interface IAddTaskFragmentAction {
        void addTaskBackArrowClicked();
        void addEventButtonClicked();
        void addTaskDone();
    }
}