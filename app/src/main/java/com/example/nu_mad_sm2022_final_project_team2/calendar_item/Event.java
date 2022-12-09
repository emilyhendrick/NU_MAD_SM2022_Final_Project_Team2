package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.nu_mad_sm2022_final_project_team2.alarm.AlarmBroadcastReceiver;
import com.example.nu_mad_sm2022_final_project_team2.alarm.AlarmFrequency;

import java.util.Calendar;
import java.util.Date;

public class Event extends ACalendarItem {
    private Date end_time;
    private String location;

    public Event() {super("", "", "", "");}

    public Event(String item_name, Date start_date, Date end_date, String category, String location) {
        super(item_name, start_date, end_date, category);
        this.location = location;
    }


    public Event(String item_name, String start_date, String end_date, String category, String location) {
        super(item_name, start_date, end_date, category);
        this.location = location;
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

        Toast.makeText(applicationContext, "Event added!", Toast.LENGTH_SHORT).show();
    }

    @Override
    Boolean isTask() {
        return false;
    }
}
