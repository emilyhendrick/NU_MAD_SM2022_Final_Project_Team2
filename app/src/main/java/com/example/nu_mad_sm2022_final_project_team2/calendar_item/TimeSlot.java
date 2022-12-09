package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import java.util.Date;

public class TimeSlot {
    Date startTime;
    Date endTime;
    ACalendarItem item;


    public TimeSlot(Date startTime, Date endTime, ACalendarItem item) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.item = item;
    }

    public TimeSlot(Date startTime, Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public ACalendarItem getItem() {
        return item;
    }

    public void setItem(ACalendarItem item) {
        this.item = item;
    }

    public Boolean isFree() {
        return (this.item==null);
    }

    public Boolean hasTask() {
        return this.item.isTask();
    }

    public void removeItem() {
        this.item = null;
    }

    public Boolean isSlotAvailableForTask(TaskPI t) {
        Date taskStart = t.getStart_date();
        Date slotStart = this.getStartTime();
        Date taskDue = t.getEnd_date();
        Date slotEnd = this.getEndTime();
        // slot is free and task starts at the
        // same time or after timeslot starts
        return (this.isFree() && (taskStart.compareTo(slotStart) >= 0) && (taskDue.compareTo(slotEnd) < 0));
    }

}
