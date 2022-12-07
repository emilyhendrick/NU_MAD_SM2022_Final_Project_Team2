package com.example.nu_mad_sm2022_final_project_team2.task;

public class Task {
    private int taskID;
    private int hour;
    private int minute;
    private boolean isAM;
    private String title;
    private String description;
    private boolean isDone;

    public Task(int taskID, int hour, int minute, boolean isAM, String title, String description, Boolean isDone) {
        this.taskID = taskID;
        this.hour = hour;
        this.minute = minute;
        this.isAM = isAM;
        this.title = title;
        this.description = description;
        this.isDone = isDone;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getHour() {
        return hour;
    }

    public int getHourIn24HourFormat() {
        if (this.isAM()) {
            return this.hour == 12 ? 0 : this.hour;
        } else {
            return this.hour == 12 ? this.hour : 12 + this.hour;
        }
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isAM() {
        return isAM;
    }

    public void setAM(boolean AM) {
        isAM = AM;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
