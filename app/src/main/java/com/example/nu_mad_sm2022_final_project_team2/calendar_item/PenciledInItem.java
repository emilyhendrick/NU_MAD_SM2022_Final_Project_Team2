package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import java.util.ArrayList;
import java.util.Date;


public class PenciledInItem {
    String itemName;
    Date taskStartDate;
    Date taskDueDate;
    Date scheduledStartDate;
    Date scheduledEndDate;
    String category;
    Boolean done;
    Boolean scheduled;
    Boolean accepted;
    ItemType type;

    public PenciledInItem(String itemName, Date taskStartDate, Date taskDueDate, Date scheduledStartDate, Date scheduledEndDate, String category, Boolean done, Boolean scheduled, Boolean accepted, ItemType type) {
        this.itemName = itemName;
        this.taskStartDate = taskStartDate;
        this.taskDueDate = taskDueDate;
        this.scheduledStartDate = scheduledStartDate;
        this.scheduledEndDate = scheduledEndDate;
        this.category = category;
        this.done = done;
        this.scheduled = scheduled;
        this.accepted = accepted;
        this.type = type;
    }

    public PenciledInItem(ACalendarItem ca, Date starttime, Date endtime) {
        if (ca.isTask()) {
            TaskPI t = (TaskPI) ca;
            this.itemName = t.item_name;
            this.taskStartDate = t.getStart_date();
            this.taskDueDate = t.getEnd_date();
            this.scheduledStartDate = starttime;
            this.scheduledEndDate = endtime;
            this.done = t.getDone();
            this.type = ItemType.TypeTask;
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
    public PenciledInItem(TaskPI t ){
        this.itemName = t.item_name;
        this.taskStartDate = t.getStart_date();
        this.taskDueDate = t.getEnd_date();
        this.done = t.getDone();
        this.type = ItemType.TypeTask;
        this.category = t.getCategory();
        this.scheduled = false;
        this.accepted = false;
    }

    /**
     * Turns a Event into a PenciledInItem. These items of
     * TypeEvent alreaady have a ScheduledStartDate and
     * ScheduledEndDate and cannot be rescheduled by the alg- bc they are already set appointments.
     * @param e Event
     */
    public PenciledInItem(Event e ){
        this.itemName = e.item_name;
        this.scheduledStartDate = e.getStart_date();
        this.scheduledEndDate = e.getEnd_date();
        this.category = e.getCategory();
        this.type = ItemType.TypeEvent;
        this.scheduled = true;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Date getTaskStartDate() {
        return taskStartDate;
    }

    public void setTaskStartDate(Date taskStartDate) {
        this.taskStartDate = taskStartDate;
    }

    public Date getTaskDueDate() {
        return taskDueDate;
    }

    public void setTaskDueDate(Date taskDueDate) {
        this.taskDueDate = taskDueDate;
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
}

enum ItemType {
    TypeEvent,
    TypeTask
}