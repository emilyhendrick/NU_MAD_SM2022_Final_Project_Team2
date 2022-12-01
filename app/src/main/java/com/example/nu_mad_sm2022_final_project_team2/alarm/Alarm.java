package com.example.nu_mad_sm2022_final_project_team2.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

public class Alarm {

    private int alarmId;
    private int hour;
    private int minute;
    private boolean isAM;
    private boolean wakeUpTask;
    private boolean isOn;
    private AlarmFrequency alarmFrequency;
    private String message;

    public Alarm() {}

    public Alarm(int hour, int minute, boolean isAM, boolean wakeUpTask, boolean isOn, AlarmFrequency alarmFrequency, String message) {
        this.alarmId = new Random().nextInt(Integer.MAX_VALUE);
        this.hour = hour;
        this.minute = minute;
        this.isAM = isAM;
        this.wakeUpTask = wakeUpTask;
        this.isOn = isOn;
        this.alarmFrequency = alarmFrequency;
        this.message = message;
    }

    public int getId() {
        return this.alarmId;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getHourIn24HourFormat() {
        if (this.isAM()) {
            return this.hour == 12 ? 0 : this.hour;
        } else {
            return this.hour == 12 ? this.hour : 12 + this.hour;
        }
    }

    public String hourToString() {
        return this.hour < 10 ? "0" + this.hour : String.valueOf(this.hour);
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String minuteToString() {
        return this.minute < 10 ? "0" + this.minute : String.valueOf(this.minute);
    }

    public boolean isAM() {
        return isAM;
    }

    public void setAM(boolean AM) {
        isAM = AM;
    }

    public boolean isWakeUpTask() {
        return wakeUpTask;
    }

    public void setWakeUpTask(boolean wakeUpTask) {
        this.wakeUpTask = wakeUpTask;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public AlarmFrequency getAlarmFrequency() {
        return alarmFrequency;
    }

    public void setAlarmFrequency(AlarmFrequency alarmFrequency) {
        this.alarmFrequency = alarmFrequency;
    }

    public String getMessage() { return this.message;}

    public void setMessage(String message) { this.message = message;}

    public String getAmOrPm() {
        return isAM ? "AM" : "PM";
    }

    @Override
    public String toString() {
        String hourString = this.hour < 10 ? "0" + this.hour : String.valueOf(this.hour);
        String minuteString = this.minute < 10 ? "0" + this.minute : String.valueOf(this.minute);
        return hourString + ":" + minuteString;
    }

    public void schedule(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra("ALARM_ID", this.alarmId);
        intent.putExtra("WAKEUP_TASK", this.wakeUpTask);
        intent.putExtra("MESSAGE", this.message);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, this.alarmId, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, this.getHourIn24HourFormat());
        calendar.set(Calendar.MINUTE, this.minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        if (this.alarmFrequency.equals(AlarmFrequency.ONCE)) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
        } else if (this.alarmFrequency.equals(AlarmFrequency.DAILY)) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmPendingIntent);
        } else if (this.alarmFrequency.equals(AlarmFrequency.WEEKLY)) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmPendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 14, alarmPendingIntent);
        }

        this.isOn = true;

        Toast.makeText(context, "Alarm scheduled!", Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra("ALARM_ID", this.alarmId);
        intent.putExtra("WAKEUP_TASK", this.wakeUpTask);
        intent.putExtra("MESSAGE", this.message);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, this.alarmId, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(alarmPendingIntent);
        this.isOn = false;

        Toast.makeText(context, "Alarm cancelled.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alarm alarm = (Alarm) o;
        return alarmId == alarm.alarmId && hour == alarm.hour && minute == alarm.minute && isAM == alarm.isAM && wakeUpTask == alarm.wakeUpTask && isOn == alarm.isOn && alarmFrequency == alarm.alarmFrequency && Objects.equals(message, alarm.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alarmId, hour, minute, isAM, wakeUpTask, isOn, alarmFrequency, message);
    }
}
