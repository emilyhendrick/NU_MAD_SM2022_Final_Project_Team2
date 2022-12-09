package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import android.content.Context;
import android.widget.Toast;

import com.example.nu_mad_sm2022_final_project_team2.alarm.AlarmFrequency;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class TaskPI extends ACalendarItem implements Comparable<TaskPI> {

    private int duration;
    private int priority;

    public TaskPI() {super("", "", "", "");}

    public TaskPI(String item_name, Date start_date, Date end_date, String category, int duration, int priority) {
        super(item_name, start_date, end_date, category);
        this.duration = duration;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public TaskPI(String item_name, String start_date, String end_date, String category, int duration, int priority) {
        super(item_name, start_date, end_date, category);
        this.duration = duration;
        this.priority = priority;
    }

    public TaskPI(String item_name, String start_date, String end_date, String category, int duration) {
        super(item_name, start_date, end_date, category);
        this.duration = duration;
        this.priority = 0;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
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

    @Override
    public int compareTo(TaskPI o) {
        if(this.getPriority() > o.getPriority()) {
            return 1;
        }
        else if (this.getPriority() == o.getPriority()) {
            return 0;
        }
        return -1;
    }

    @Override
    Boolean isTask() {
        return true;
    }

    public void check() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {

        }
        this.isDone = true;
    }

    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
        String start = dateFormat.format(this.start_date);
        String end = dateFormat.format(this.end_date);
        return this.getItem_name() + "starts: " + start + " ends: " + end;
    }

    @Override
    ItemType getEnumType() {
        return ItemType.TypeTask;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final TaskPI other = (TaskPI) obj;
        if ((this.item_name == null) ? (other.item_name != null) : !this.item_name.equals(other.item_name)) {
            return false;
        }

        if (this.start_date != other.start_date) {
            return false;
        }

        if (this.start_date != other.start_date) {
            return false;
        }

        if (this.category != other.category) {
            return false;
        }
        if (this.getEnumType() != other.getEnumType()) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.item_name != null ? this.item_name.hashCode() : 0);
        hash = 53 * hash + this.duration;
        return hash;
    }


}
