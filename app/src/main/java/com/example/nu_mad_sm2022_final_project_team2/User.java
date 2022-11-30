package com.example.nu_mad_sm2022_final_project_team2;

import com.example.nu_mad_sm2022_final_project_team2.alarm.Alarm;

import java.util.ArrayList;

public class User {

    private String firstName;
    private String lastName;
    private String pronouns;
    private String email;
    private String birthday;
    private String avatarUri;
    private ArrayList<Alarm> alarms;

    public User() {}

    public User(String firstName, String lastName, String pronouns, String birthday, String email, String avatarUri, ArrayList<Alarm> alarms) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pronouns = pronouns;
        this.birthday = birthday;
        this.email = email;
        this.avatarUri = avatarUri;
        this.alarms = alarms;
    }

    public User(String firstName, String lastName, String pronouns, String birthday, String email, ArrayList<Alarm> alarms) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pronouns = pronouns;
        this.birthday = birthday;
        this.email = email;
        this.alarms = alarms;

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
}