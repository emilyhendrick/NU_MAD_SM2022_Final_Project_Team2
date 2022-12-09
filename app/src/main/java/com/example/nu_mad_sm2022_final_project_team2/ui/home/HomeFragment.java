package com.example.nu_mad_sm2022_final_project_team2.ui.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nu_mad_sm2022_final_project_team2.User;
import com.example.nu_mad_sm2022_final_project_team2.calendar_item.ACalendarItem;
import com.example.nu_mad_sm2022_final_project_team2.calendar_item.Event;
import com.example.nu_mad_sm2022_final_project_team2.calendar_item.PenciledInSchedule;
import com.example.nu_mad_sm2022_final_project_team2.calendar_item.TaskPI;
import com.example.nu_mad_sm2022_final_project_team2.databinding.FragmentHomeBinding;
import com.example.nu_mad_sm2022_final_project_team2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final String ARG_TASKS = "tasksarray";
    private static final String ARG_EVENTS = "eventsarray";
    private static ArrayList<TaskPI> tasks;
    private static ArrayList<Event> events;
    private static ArrayList<TaskPI> taskstest;
    private static ArrayList<Event> eventstest;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    User currUser;

    private FragmentHomeBinding binding;
    private RecyclerView calendarItems;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private CalendarViewAdapter taskAdapter;
    private TextView displayDate;

    public HomeFragment() {

        tasks = new ArrayList<TaskPI>();
        events = new ArrayList<Event>();
    }

    public static HomeFragment newInstance(ArrayList<TaskPI> userTasks, ArrayList<Event> userEvents) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        tasks = userTasks;
        events = userEvents;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();
            Bundle args = getArguments();
            if (args != null) {
                if (args.containsKey(ARG_TASKS)) {
                    taskstest = (ArrayList<TaskPI>) args.getSerializable(ARG_TASKS);
                }
                if (args.containsKey(ARG_EVENTS)) {
                    eventstest = (ArrayList<Event>) args.getSerializable(ARG_EVENTS);
                }

        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        calendarItems = root.findViewById(R.id.calendarRecyclerView);
        recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        PenciledInSchedule sched = new PenciledInSchedule(tasks, events);
        taskAdapter = new CalendarViewAdapter(sched);
        calendarItems.setLayoutManager(recyclerViewLayoutManager);
        calendarItems.setAdapter(taskAdapter);
        displayDate = root.findViewById(R.id.currentDate);
        DateFormat df = new SimpleDateFormat("EEE, MMM d, ''yy", Locale.US);
        String date = df.format(Calendar.getInstance().getTime());
        displayDate.setText(date);

        loadProfile(root);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void loadProfile(View view) {
        db.collection("users").document(mUser.getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        ArrayList<TaskPI> t = user.getTasks();
                        ArrayList<Event> e = user.getEvents();
                        PenciledInSchedule sched = new PenciledInSchedule(t, e);
                        sched.getItems();
                        //String log = "Items" + sched.getItems().toString();
                        //Log.d("ITEMS", log);
                        Log.d("Events", user.getEvents().toString());
                        Log.d("Tasks", user.getTasks().toString());
                        Toast.makeText(getContext(), user.getEvents().toString(), Toast.LENGTH_SHORT).show();
                        taskAdapter = new CalendarViewAdapter(sched);
                        calendarItems.setLayoutManager(recyclerViewLayoutManager);
                        calendarItems.setAdapter(taskAdapter);
                    }
                });
    }





}