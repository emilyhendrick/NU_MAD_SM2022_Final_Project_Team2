package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import android.content.Context;
import android.widget.Toast;

import com.example.nu_mad_sm2022_final_project_team2.alarm.AlarmFrequency;

import java.util.Date;
import java.util.Random;

public class TaskPI extends ACalendarItem{

    private int duration;

    public TaskPI() {super("", "", "", "");}

    public TaskPI(String item_name, Date start_date, Date end_date, String category, int duration) {
        super(item_name, start_date, end_date, category);
        this.duration = duration;
    }

    public TaskPI(String item_name, String start_date, String end_date, String category, int duration) {
        super(item_name, start_date, end_date, category);
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
