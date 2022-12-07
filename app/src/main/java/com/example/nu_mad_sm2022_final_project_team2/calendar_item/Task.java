package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import android.content.Context;
import android.widget.Toast;

import java.util.Date;

public class Task extends ACalendarItem{

    private Date due_date;

    public Task(String item_id, String item_name, Date start_date, Date due_date, String category) {
        super(item_id, item_name, start_date, category);
        this.due_date = due_date;
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
        Toast.makeText(applicationContext, "Task added!", Toast.LENGTH_SHORT).show();
    }
}
