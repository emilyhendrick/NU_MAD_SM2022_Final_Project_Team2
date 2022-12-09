package com.example.nu_mad_sm2022_final_project_team2.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nu_mad_sm2022_final_project_team2.R;
import com.example.nu_mad_sm2022_final_project_team2.calendar_item.ACalendarItem;
import com.example.nu_mad_sm2022_final_project_team2.calendar_item.TaskPI;

import java.util.ArrayList;


public class CalendarViewAdapter extends RecyclerView.Adapter<CalendarViewAdapter.TaskViewHolder> {

    private ArrayList<TaskPI> tasks;

    public CalendarViewAdapter(ArrayList<TaskPI> tasks) {
        this.tasks = tasks;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder{

        private final TextView calendarEventTime;
        private final TextView calendarEventName;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.calendarEventTime = itemView.findViewById(R.id.calendarEventTime);
            this.calendarEventName = itemView.findViewById(R.id.calendarEventName);
        }

        public TextView getTextViewName() {
            return calendarEventTime;
        }

        public TextView getTextViewMessage() {
            return calendarEventName;
        }

    }

    @NonNull
    @Override
    public CalendarViewAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemRecyclerView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.event_list_item, parent, false);

        return new TaskViewHolder(itemRecyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        ACalendarItem curTask = this.tasks.get(position);

        holder.calendarEventName.setText(curTask.getItem_name());
        holder.calendarEventTime.setText(curTask.getItem_name());
    }

    @Override
    public int getItemCount() {
        if (tasks == null) {
            return 0;
        }
        else {
            return this.tasks.size();
        }
    }
}
