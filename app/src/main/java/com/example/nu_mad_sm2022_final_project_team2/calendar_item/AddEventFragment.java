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
 * Use the {@link AddEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEventFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private AddEventFragment.IAddEventFragmentAction mListener;
    Button btn_add_task_nav, btn_save_event, btn_add_event, btn_start_date_date, btn_start_date_time, btn_due_date_date, btn_due_date_time;
    Spinner spin_categories;
    EditText inp_txt_event_name;
    EditText inp_txt_location;



    public AddEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddEventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddEventFragment newInstance() {
        AddEventFragment fragment = new AddEventFragment();
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
        if (context instanceof AddEventFragment.IAddEventFragmentAction) {
            this.mListener = (AddEventFragment.IAddEventFragmentAction) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);
        btn_add_task_nav = view.findViewById(R.id.btn_add_task_nav);
        btn_add_task_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addTaskButtonClicked();
            }
        });

        btn_start_date_date = view.findViewById(R.id.btn_start_date_date);
        btn_due_date_date = view.findViewById(R.id.btn_due_date_date);
        btn_start_date_time= view.findViewById(R.id.btn_start_date_time);
        btn_due_date_time = view.findViewById(R.id.btn_due_date_time);
        inp_txt_event_name = view.findViewById(R.id.inp_txt_event_name);
        inp_txt_location = view.findViewById(R.id.txt_inp_location);
        btn_add_event = view.findViewById(R.id.btn_add_event);
        btn_save_event = view.findViewById(R.id.btn_save_event);

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

        btn_save_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("here", "reached this onclik");
                if (validateInputs(inp_txt_event_name, inp_txt_location,
                        spin_categories, btn_start_date_date, btn_start_date_time,
                        btn_due_date_date, btn_due_date_time)) {
                    String item_name = inp_txt_event_name.getText().toString();
                    String location = inp_txt_location.getText().toString();
                    String start_date = btn_start_date_date.getText().toString();
                    String due_date = btn_due_date_date.getText().toString();
                    String start_time = btn_start_date_time.getText().toString();
                    String due_time = btn_due_date_time.getText().toString();
                    String category = spin_categories.getSelectedItem().toString();

                    List<String> fieldNames = Arrays.asList("eventName", "startDate", "dueDate", "startTime", "dueTime", "category", "location");
                    List<String> fields = Arrays.asList(item_name, start_date, due_date, start_time, due_time, category, location);
                    for (int i=0; i<fields.size(); i++) {
                        if (Objects.equals(fields.get(i), "")) {
                            Toast.makeText(getContext(), "Field '" + fieldNames.get(i) + "' is missing", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    String startDateTime = start_date + "T" + start_time;
                    String dueDateTime = due_date + "T" + due_time;
                    Event newItem = new Event(item_name, startDateTime, dueDateTime, category, location);
                    addEventInDatabase(newItem);
                }
                else {
                    Toast.makeText(getContext(), "You must complete the entire form.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_add_task_nav = view.findViewById(R.id.btn_add_task_nav);
        btn_add_task_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addTaskButtonClicked();
            }
        });


        return view;
    }

    private Boolean validateInputs(EditText eventname,
                                   EditText loc, Spinner cat, Button start_date,
                                   Button start_time, Button due_date, Button due_time) {
        return !(eventname == null || loc == null ||
                cat == null || start_date == null  ||
                start_time == null ||
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

    private void showDatePicker(Button btn) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String selectedYear = String.valueOf(year);
                String selectedMonth = String.valueOf(monthOfYear + 1);
                String selectedDay = String.valueOf(dayOfMonth);
                btn.setText(selectedMonth + "/" + selectedDay + "/" + selectedYear);
            }
        }, currentYear, currentMonth, currentDay);

        datePickerDialog.show();
    }



    private void addEventInDatabase(Event newEvent) {
        db.collection("users")
                .document(mUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);
                            ArrayList<Event> events = user.getEvents();
                            events = insertEventIntoSortedEvents(events, newEvent);
                            updateEvents(events, newEvent);
                            newEvent.schedule(getActivity().getApplicationContext());
                            mListener.addEventDone();
                        }
                    }
                });
    }

    private ArrayList<Event> insertEventIntoSortedEvents(ArrayList<Event> events, Event newEvent) {
        ArrayList<Event> retEvents;
        if (events == null) {
            retEvents = new ArrayList<Event>();
            retEvents.add(newEvent);
        }
        else {
            retEvents = events;
            retEvents.add(newEvent);
        }
        return retEvents;
    }


    private void updateEvents(ArrayList<Event> events, Event newTask) {
        db.collection("users")
                .document(mUser.getEmail())
                .update("events", events);
    }


    public interface IAddEventFragmentAction {
            public void addTaskButtonClicked();
            void addEventButtonClicked();
            void addEventDone();
        }
}