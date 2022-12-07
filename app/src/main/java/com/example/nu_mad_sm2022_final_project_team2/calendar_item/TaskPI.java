package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import android.content.Context;
import android.widget.Toast;

import com.example.nu_mad_sm2022_final_project_team2.alarm.AlarmFrequency;

import java.util.Date;
import java.util.Random;

public class TaskPI extends ACalendarItem{

    private Date due_date;
    private int duration;

    public TaskPI(String item_name, Date start_date, Date due_date, String category, int duration) {
        super(item_name, start_date, category);
        this.due_date = due_date;
        this.duration = duration;
    }



    @Override
    public String name() {
        return null;
    }

    @Override
    public double category() {
        return 0;
    }

    @Override
    public void addCalendarItemInDatabase(ACalendarItem c) {

    }

    @Override
    public void schedule(Context applicationContext) {
        Toast.makeText(applicationContext, "TaskPI added!", Toast.LENGTH_SHORT).show();
    }
}
