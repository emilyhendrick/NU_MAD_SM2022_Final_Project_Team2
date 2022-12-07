package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nu_mad_sm2022_final_project_team2.R;
import com.example.nu_mad_sm2022_final_project_team2.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TasksFragment extends Fragment {

    private static final String ARG_TASKS = "tasksarray";
    private TextView addTaskButton;
    private RecyclerView taskRecyclerView;
    private RecyclerView.LayoutManager taskViewLayoutManager;

    private TasksAdaptor tasksAdaptor;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private ITaskFragmentAction mListener;

    private ArrayList<TaskPI> mTasks;
    private ArrayList<Event> mEvents;

    public TasksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TasksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TasksFragment newInstance() {
        TasksFragment fragment = new TasksFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TASKS, new ArrayList<ACalendarItem>());
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ARG_TASKS)) {
                mTasks = (ArrayList<TaskPI>) args.getSerializable(ARG_TASKS);
            }

            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();

            loadData();
        }
        getActivity().setTitle("Alarms");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TasksFragment.ITaskFragmentAction) {
            this.mListener = (TasksFragment.ITaskFragmentAction) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        // SET UP RECYCLER VIEW
        tasksAdaptor = new TasksAdaptor(mTasks, getContext());
        taskRecyclerView = view.findViewById(R.id.taskRecyclerView);
        taskViewLayoutManager = new LinearLayoutManager(getContext());
        taskRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        taskRecyclerView.setLayoutManager(taskViewLayoutManager);
        taskRecyclerView.setAdapter(tasksAdaptor);

        addTaskButton = view.findViewById(R.id.taskPlus);
        addTaskButton.setClickable(true);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addTaskClicked();
            }
        });


        return view;
    }
    public void updateRecyclerView(ArrayList<TaskPI> tasks) {
        this.mTasks = tasks;
        tasksAdaptor.setTasks(tasks);
        tasksAdaptor.notifyDataSetChanged();
    }

    private void loadData() {
        db.collection("users")
                .document(mUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            User user = task.getResult().toObject(User.class);
                            updateRecyclerView(user.getTasks());
                            mTasks = user.getTasks();
                            mUser.reload();
                        }
                    }
                });
    }

    public interface ITaskFragmentAction {
        void taskBackArrowClicked();
        void addTaskClicked();
    }
}