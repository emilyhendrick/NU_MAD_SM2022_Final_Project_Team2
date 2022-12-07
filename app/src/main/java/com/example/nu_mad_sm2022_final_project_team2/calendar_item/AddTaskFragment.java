package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

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

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

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

    private Button btn_add_event, btn_add_task, btn_set_start_date, btn_set_due_date, btn_close_start;
    private EditText inp_txt_task_name, inp_duration_number, inp_start_date, inp_due_date;
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
        pick_time = view.findViewById(R.id.pick_time);
        pick_date = view.findViewById(R.id.pick_date);
        btn_set_start_date = view.findViewById(R.id.btn_start_date);
        btn_close_start = view.findViewById(R.id.btn_close_start);

        pick_time.setVisibility(View.INVISIBLE);
        pick_date.setVisibility(View.INVISIBLE);



        btn_set_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pick_time.setVisibility(View.VISIBLE);
                pick_date.setVisibility(View.VISIBLE);
            }
        });

        btn_close_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // btn_set_start_date, btn_set_due_date;
                pick_time.setVisibility(View.INVISIBLE);
                pick_date.setVisibility(View.INVISIBLE);
            }
        });

        return view;



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