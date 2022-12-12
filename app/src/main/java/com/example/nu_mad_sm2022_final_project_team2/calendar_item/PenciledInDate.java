package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This Class uses composition to add utils for the Java Date class
 */
public class PenciledInDate {
    private final Date date;


    PenciledInDate(long time) {
        this.date = new Date(time);
    }

    private Date stringToDate(String date,String format) {
        if (date==null) {
            return null;
        }
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(format);
        Date stringDate = simpledateformat.parse(date, pos);
        return stringDate;
    }

}
