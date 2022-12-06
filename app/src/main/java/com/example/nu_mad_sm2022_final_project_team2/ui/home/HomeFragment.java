package com.example.nu_mad_sm2022_final_project_team2.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nu_mad_sm2022_final_project_team2.databinding.FragmentHomeBinding;
import com.example.nu_mad_sm2022_final_project_team2.R;
import com.example.nu_mad_sm2022_final_project_team2.ui.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static ArrayList<Task> tasks;

    private FragmentHomeBinding binding;
    private RecyclerView calendarItems;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private CalendarViewAdapter taskAdapter;
    private TextView displayDate;

    public HomeFragment() {
        tasks = new ArrayList<Task>();
    }

    public static HomeFragment newInstance(ArrayList<Task> userTasks) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        tasks = userTasks;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        taskAdapter = new CalendarViewAdapter(tasks);
        calendarItems.setLayoutManager(recyclerViewLayoutManager);
        calendarItems.setAdapter(taskAdapter);

        displayDate = root.findViewById(R.id.currentDate);
        DateFormat df = new SimpleDateFormat("EEE, MMM d, ''yy", Locale.US);
        String date = df.format(Calendar.getInstance().getTime());
        displayDate.setText(date);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}