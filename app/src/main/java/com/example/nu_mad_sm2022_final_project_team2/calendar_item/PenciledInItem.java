package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class PenciledInItem {
    String itemName;
    Date taskStartDate;
    Date scheduledStartDate;
    Date scheduledEndDate;
    String category;
    Boolean done;
    Boolean scheduled;
    Boolean accepted;
    ItemType type;
    Date task_due_date;


    public PenciledInItem(String itemName, Date scheduledStartDate, Date scheduledEndDate, String category, Boolean done, Boolean scheduled, Boolean accepted, ItemType type, Date task_due_date) {
        this.itemName = itemName;
        this.task_due_date = task_due_date;
        this.scheduledStartDate = scheduledStartDate;
        this.scheduledEndDate = scheduledEndDate;
        this.category = category;
        this.done = done;
        this.scheduled = scheduled;
        this.accepted = accepted;
        this.type = type;
    }

    public PenciledInItem(ACalendarItem ca, Date startTime, Date scheduledEndTime) {
        if (ca.isTask()) {
            TaskPI t = (TaskPI) ca;
            this.itemName = t.item_name;
            this.scheduledStartDate = startTime;
            this.scheduledEndDate = scheduledEndTime;
            this.done = t.getDone();
            this.task_due_date = t.getEnd_date();
            this.type = ((TaskPI) ca).getEnumType();
            this.category = t.getCategory();
            this.scheduled = false;
            this.accepted = false;
        }
        else {
            Event e = (Event) ca;
            this.itemName = e.item_name;
            this.scheduledStartDate = e.getStart_date();
            this.scheduledEndDate = e.getEnd_date();
            this.category = e.getCategory();
            this.type = ItemType.TypeEvent;
            this.scheduled = true;

        }
    }

    /**
     * Turns a TaskPI into a PenciledInItem. These items of
     * TypeTask will not have a ScheduledStartDate and
     * ScheduledEndDate until they are schedueld by scheduling alg.
     * @param t TaskPI
     */
    public PenciledInItem(TaskPI t, Date schedStart, Date schedEnd ){
        this.itemName = t.item_name;
        this.task_due_date = t.getEnd_date();
        this.scheduledStartDate = schedStart;
        this.scheduledEndDate = schedEnd;
        this.done = t.getDone();
        this.type = t.getEnumType();
        this.category = t.getCategory();
        this.scheduled = false;
        this.accepted = false;
    }

    public String getDueDisplay() {
        if (this.type == ItemType.TypeTask) {
            DateFormat mFormat = new SimpleDateFormat("MMM, d");
            Log.d("due display", this.itemName.toString());
            Log.d("due display", this.type.toString());
            return "Due: " + mFormat.format(this.task_due_date);
        }
        return "";
    }




    public Boolean isSameTask(PenciledInItem other) {
        return (this.itemName == other.itemName & this.category == other.category);
    }

    /**
     * Turns a Event into a PenciledInItem. These items of
     * TypeEvent already have a ScheduledStartDate and
     * ScheduledEndDate and cannot be rescheduled by the alg- bc they are already set appointments.
     * @param e Event
     */
    public PenciledInItem(Event e ){
        this.itemName = e.item_name;
        this.scheduledStartDate = e.getStart_date();
        this.scheduledEndDate = e.getEnd_date();
        this.category = e.getCategory();
        this.type = e.getEnumType();
        this.scheduled = true;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


    public Date getScheduledStartDate() {
        return scheduledStartDate;
    }

    public void setScheduledStartDate(Date scheduledStartDate) {
        this.scheduledStartDate = scheduledStartDate;
    }

    public Date getScheduledEndDate() {
        return scheduledEndDate;
    }

    public void setScheduledEndDate(Date scheduledEndDate) {
        this.scheduledEndDate = scheduledEndDate;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Boolean getScheduled() {
        return scheduled;
    }

    public void setScheduled(Boolean scheduled) {
        this.scheduled = scheduled;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getTask_due_date() {
        return task_due_date;
    }

    public void setTask_due_date(Date task_due_date) {
        this.task_due_date = task_due_date;
    }

    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
        String start = dateFormat.format(this.scheduledStartDate);
        String end = dateFormat.format(this.scheduledEndDate);
        return this.getItemName() + "starts: " + start + " ends: " + end;
    }
}



enum ItemType {
    TypeEvent,
    TypeTask
}