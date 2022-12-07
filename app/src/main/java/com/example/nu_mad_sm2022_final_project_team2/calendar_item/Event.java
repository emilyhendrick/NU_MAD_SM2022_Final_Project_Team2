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

class Event extends ACalendarItem {
    private Date end_time;

    public Event(String item_id, String item_name, Date start_date, Date end_time, String category) {
        super(item_id, item_name, start_date, category);
        this.end_time = end_time;
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
