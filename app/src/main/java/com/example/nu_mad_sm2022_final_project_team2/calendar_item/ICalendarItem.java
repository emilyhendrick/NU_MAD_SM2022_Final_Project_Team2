package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import android.content.Context;

interface ICalendarItem {
    // name of event or task
    public String name();

    // get duration
    public double category();

    public void addCalendarItemInDatabase(ACalendarItem c);

    public void schedule(Context applicationContext);

}

