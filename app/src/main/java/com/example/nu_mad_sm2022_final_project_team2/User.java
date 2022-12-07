package com.example.nu_mad_sm2022_final_project_team2;

import com.example.nu_mad_sm2022_final_project_team2.alarm.Alarm;
import com.example.nu_mad_sm2022_final_project_team2.calendar_item.ACalendarItem;
import com.example.nu_mad_sm2022_final_project_team2.calendar_item.TaskPI;
import com.example.nu_mad_sm2022_final_project_team2.calendar_item.Event;

import java.util.ArrayList;

public class User {

    private String firstName;
    private String lastName;
    private String pronouns;
    private String email;
    private String birthday;
    private String avatarUri;
    private ArrayList<Alarm> alarms;
    private ArrayList<TaskPI> tasks;
    private ArrayList<Event> events;

    public User() {}

    public User(String firstName, String lastName, String pronouns, String birthday, String email, String avatarUri, ArrayList<Alarm> alarms, ArrayList<TaskPI> tasks, ArrayList<Event> events) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pronouns = pronouns;
        this.birthday = birthday;
        this.email = email;
        this.avatarUri = avatarUri;
        this.alarms = alarms;
        this.tasks = tasks;
        this.events = events;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public ArrayList<Alarm> getAlarms() {
        return alarms;
    }

    public void setAlarms(ArrayList<Alarm> alarms) {
        this.alarms = alarms;
    }

    public ArrayList<TaskPI> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<TaskPI> tasks) {
        this.tasks = tasks;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public User(String firstName, String lastName, String pronouns, String birthday, String email, ArrayList<Alarm> alarms, ArrayList<TaskPI> tasks, ArrayList<Event> events) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pronouns = pronouns;
        this.birthday = birthday;
        this.email = email;
        this.alarms = alarms;
        this.tasks = tasks;
        this.events = events;

    }




}
