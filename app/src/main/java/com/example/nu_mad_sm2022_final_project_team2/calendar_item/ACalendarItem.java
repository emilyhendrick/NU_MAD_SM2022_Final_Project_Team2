package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public abstract class ACalendarItem implements ICalendarItem{
    static SimpleDateFormat EXP_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy'T'hh:mm aa");
    static SimpleDateFormat ACT_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy'T'HH:mm");
    int item_id;
    String item_name;
    Date start_date;
    Date end_date;
    String category;
    Boolean isDone;

    public ACalendarItem(String item_name, Date start_date, Date end_date, String category) {
        this.item_id = new Random().nextInt(Integer.MAX_VALUE);
        this.item_name = item_name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.category = category;
        this.isDone = false;
    }

    public ACalendarItem(String item_name, String start_date, String end_date, String category) {
        this.item_id = new Random().nextInt(Integer.MAX_VALUE);
        this.item_name = item_name;
        this.category = category;
        try {
            this.start_date = ACT_DATE_FORMAT.parse(ACT_DATE_FORMAT.format(EXP_DATE_FORMAT.parse(start_date)));
            this.end_date = ACT_DATE_FORMAT.parse(ACT_DATE_FORMAT.format(EXP_DATE_FORMAT.parse(end_date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.isDone = false;
    }


    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public String getEnd_date_asString() {
        LocalDateTime ldt = LocalDateTime.now();
        Date now = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        long diff = now.getTime() - end_date.getTime();
        long diffDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return "due in " + String.valueOf(diffDays) + " days";
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public String dateToDateString(Date d){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String strDate = dateFormat.format(d);
        return strDate;
    }
    public String timeToTimeString(Date t){
        DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        String strDate = dateFormat.format(t);
        return strDate;
    }

}
