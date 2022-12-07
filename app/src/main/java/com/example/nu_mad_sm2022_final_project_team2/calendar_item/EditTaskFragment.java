package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
 * Use the {@link EditTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditTaskFragment extends Fragment {


    private static TaskPI task;
    private static int index;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private EditTaskFragment.IEditTaskFragmentAction mListener;

    private EditText editTaskNameInput, editDurationInput, editPriorityInput, editCategoryInput, taskLocationInput, taskBlockerInput, editTextStartDate, editEndDate;
    private Button saveChangesButton;

    public EditTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment EditTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditTaskFragment newInstance(TaskPI taskArg, int indexArg) {
        EditTaskFragment fragment = new EditTaskFragment();
        Bundle args = new Bundle();
        task = taskArg;
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
        if (context instanceof EditTaskFragment.IEditTaskFragmentAction) {
            this.mListener = (EditTaskFragment.IEditTaskFragmentAction) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_task, container, false);

        editTaskNameInput = view.findViewById(R.id.editTaskNameInput);
        editDurationInput = view.findViewById(R.id.editDurationInput);
        editPriorityInput = view.findViewById(R.id.editPriorityInput);
        editCategoryInput = view.findViewById(R.id.editCategoryInput);
        taskLocationInput = view.findViewById(R.id.taskLocationInput);
        taskBlockerInput = view.findViewById(R.id.taskBlockerInput);
        editTextStartDate = view.findViewById(R.id.editTextStartDate);
        editEndDate = view.findViewById(R.id.editEndDate);
        saveChangesButton = view.findViewById(R.id.saveChangesButton);

        editTextStartDate.setClickable(true);
        editTextStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(editTextStartDate);
            }
        });

        editEndDate.setClickable(true);
        editEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(editEndDate);
            }
        });

        return view;
    }


    private void showTimePicker(EditText changed) {
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
                changed.setText(selectedHourStr + ":" + selectedMinuteStr);
            }
        }, currentHour, currentMinute, true);
    }

    private void updateAlarmInDatabase(TaskPI newTask) {
        db.collection("users")
                .document(mUser.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Toast.makeText(getContext(), "Alarms updated!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to update alarms.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);
                            ArrayList<TaskPI> tasks = user.getTasks();
                            tasks.set(index, newTask);
                            updateTasks(tasks);
                            newTask.schedule(getActivity().getApplicationContext());
                            mListener.editTaskDone();
                        }
                    }
                });

    }

    private void deleteTask() {
        db.collection("users")
                .document(mUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);
                            ArrayList<TaskPI> tasks = user.getTasks();
                            tasks.remove(task);
                            updateTasks(tasks);

                            mListener.editTaskDone();
                        }
                    }
                });
    }

    private void updateTasks(ArrayList<TaskPI>tasks) {
        db.collection("users")
                .document(mUser.getEmail())
                .update("tasks", tasks);
    }


    public interface IEditTaskFragmentAction {
        void taskBackArrowClicked();
        void editTaskDone();
    }
}
