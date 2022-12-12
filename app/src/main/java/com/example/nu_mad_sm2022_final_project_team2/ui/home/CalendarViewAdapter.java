package com.example.nu_mad_sm2022_final_project_team2.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nu_mad_sm2022_final_project_team2.R;
import com.example.nu_mad_sm2022_final_project_team2.calendar_item.ACalendarItem;
import com.example.nu_mad_sm2022_final_project_team2.calendar_item.PenciledInItem;
import com.example.nu_mad_sm2022_final_project_team2.calendar_item.PenciledInSchedule;
import com.example.nu_mad_sm2022_final_project_team2.calendar_item.TaskPI;
import com.example.nu_mad_sm2022_final_project_team2.calendar_item.TaskUtils;
import com.google.android.gms.common.api.internal.TaskUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class CalendarViewAdapter extends RecyclerView.Adapter<CalendarViewAdapter.TaskViewHolder> {

    private PenciledInSchedule schedule;
    private ArrayList<PenciledInItem> items;
    TextView txt_start_time, txt_end_time, task_name, txt_category, txt_due, txt_date;


    public CalendarViewAdapter(PenciledInSchedule schedule) {
        this.schedule = schedule;
        this.items = TaskUtils.sortItemsByStartDate(schedule.getItems());
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder{

        private final TextView txt_start_time, txt_end_time, task_name, txt_category, txt_date, txt_due;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txt_start_time = itemView.findViewById(R.id.txt_start_time);
            this.txt_end_time = itemView.findViewById(R.id.txt_end_time);
            this.task_name = itemView.findViewById(R.id.task_name);
            this.txt_category =  itemView.findViewById(R.id.txt_category);
            this.txt_date = itemView.findViewById(R.id.txt_date);
            this.txt_due = itemView.findViewById(R.id.txt_due);
        }

        public TextView getTextViewStartTime() {
            return txt_start_time;
        }
        public TextView getTextViewEndTime() {
            return txt_end_time;
        }
        public TextView getTextViewTaskName() {
            return task_name;
        }
        public TextView getTextViewCategory() {
            return txt_category;
        }
        public TextView getTextViewDate() { return txt_date; }
        public TextView getTextDueDate() { return txt_due; }

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
        PenciledInItem curTask = this.items.get(position);

        holder.task_name.setText(curTask.getItemName());
        holder.txt_category.setText(curTask.getCategory());
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        DateFormat mFormat = new SimpleDateFormat("MMM, d");
        holder.txt_start_time.setText(dateFormat.format(curTask.getScheduledStartDate()));
        holder.txt_end_time.setText(dateFormat.format(curTask.getScheduledEndDate()));
        holder.txt_date.setText(mFormat.format(curTask.getScheduledStartDate()));
        holder.txt_due.setText(curTask.getDueDisplay());
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        else {
            return this.items.size();
        }
    }
}
