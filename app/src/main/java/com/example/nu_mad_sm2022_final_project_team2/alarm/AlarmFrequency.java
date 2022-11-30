package com.example.nu_mad_sm2022_final_project_team2.alarm;

public enum AlarmFrequency {
    ONCE("Once"), DAILY("Daily"), WEEKLY("Weekly"), BIWEEKLY("Bi-Weekly");

    private String frequency;

    private AlarmFrequency(String frequency) { this.frequency = frequency; }

    public String toString() {
        return this.frequency;
    }
}
