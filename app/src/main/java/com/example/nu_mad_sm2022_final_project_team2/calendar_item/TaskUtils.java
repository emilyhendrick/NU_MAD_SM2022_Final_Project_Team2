package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public final class TaskUtils {


    /**
     * Removes slot time from the task duration and updates start and end time
     * @param task task to be scheduled into slot
     * @param ts slot
     * @return task representing remaining task after slot is filled
     */
    public static TaskPI updateTaskDurationbySlot(TaskPI task, TimeSlot ts) {
        Date slotStart = ts.getStartTime();
        Date slotEnd = ts.getEndTime();
        // slotDuration unit in milliseconds
        long slotDuration = Math.abs(slotEnd.getTime() - slotStart.getTime());
        // task Duration unit in milliseconds
        int taskDuration = task.getDuration() * 60000;
        long newDuration = taskDuration - slotDuration;
        // if duration is negative round to 0
        if (newDuration < 0) {
            newDuration = 0;
        }
        // convert milliseconds back to minutes
        newDuration = newDuration/60000;
        // use new duration to create a new task
        task.setDuration((int) newDuration);
        return task;


    }


    /**
     * Returns list of events that are within a week of today.
     * @param events list of available events
     * @return list of events filtered for current events
     */
    public static ArrayList<Event> filterForCurrentEvents(ArrayList<Event>  events) {
        Date today = new Date();
        Date weekFromToday = addDaysToDate(today, 7);
        ArrayList<Event> currentEvents = new ArrayList<>();
        for(Event e: events) {
            Date eventDate = e.getStart_date();
            if (eventDate.compareTo(today) >= 0 && eventDate.compareTo(weekFromToday) <= 0) {
                currentEvents.add(e);
            }
        }
        return currentEvents;
    }

    /**
     * Remove done tasks from list of tasks
     */
    public static ArrayList<TaskPI> filterDoneTask(ArrayList<TaskPI>  tasks) {
        ArrayList<TaskPI> noDones = new ArrayList<TaskPI>();
        if (tasks != null) {
            for( TaskPI t : tasks) {
                if (!(t.isDone)) {
                    noDones.add(t);
                }
            }
        }
        return noDones;
    }


    /**
     * Finds latest end date that is at most 1 week away. If there is none return date of week from today.
     * @return Date - latest or week from today
     */
    public static Date getLastDeadline(ArrayList<TaskPI> tasks, ArrayList<Event> events) {
        ArrayList<TaskPI> ts = sortByStartDate(tasks);
        ArrayList<Event> event = sortByStartDateEvents(events);
        TaskPI lastTask = ts.get(tasks.size());
        Event lastEvent = events.get(events.size());

        Date today = new Date();
        Date weekFromToday = addDaysToDate(today, 7);
        Date endDate  = weekFromToday;
        for (TaskPI t : ts) {
            if (t.getEnd_date().compareTo(weekFromToday) <= 0) {
                endDate = t.getEnd_date();
                break;
            }
        }
        return endDate;
    }

    /**
     * Add given number of days to given date
     * @param date target date
     * @param days days to be added
     * @return date with days added
     */
    public static Date addDaysToDate(final Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }



    /**
     * Finds earliest start date greater than or equal to today. If there is none return today.
     * @return Date - today or earliest
     */
    public static Date getFirstStartDate(ArrayList<TaskPI> tasks, ArrayList<Event> events) {
        Date earliestTask = getFirstStartDateTasks(tasks);
        Date earliestEvent = getFirstStartDateEvents(events);
        if (earliestTask.compareTo(earliestEvent) <= 0) {
            return earliestTask;
        }
        else {
            return earliestEvent;
        }
    }

    public static Date getFirstStartDateTasks(ArrayList<TaskPI> tasks) {
        ArrayList<TaskPI> ts = sortByStartDate(tasks);
        Date today = getStartofToday();
        Log.d("today", today.toString());
        Date startDate  = today;
        // sort by start date
        for (TaskPI t : ts) {
            if (t.getStart_date().compareTo(today) >= 0) {
                startDate = t.getStart_date();
                break;
            }
        }
        Log.d("tasks sorted", ts.toString());
        Log.d("returned start date", startDate.toString());
        return startDate;
    }

    public static Date getFirstStartDateEvents(ArrayList<Event> events) {
        ArrayList<Event> ts = sortByStartDateEvents(events);
        Date today = getStartofToday();
        Log.d("today", today.toString());
        Date startDate  = today;
        // sort by start date
        for (Event t : ts) {
            if (t.getStart_date().compareTo(today) >= 0) {
                startDate = t.getStart_date();
                break;
            }
        }
        return startDate;
    }


    public static Date getStartofToday() {
        Date today = new Date();
        DateFormat dateOnlyFormat = new SimpleDateFormat("dd/MM/yyyy");
        String todayString = dateOnlyFormat.format(today);
        String timeStart=" 00:00:00";
        Date dateToday= null;
        try {
            dateToday = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(todayString + timeStart);
        } catch (ParseException e) {
            return new Date();
        }

        return dateToday;
    }

    public static ArrayList<PenciledInItem> sortItemsByStartDate(ArrayList<PenciledInItem> pi) {
        if (pi != null) {
            ArrayList<PenciledInItem> slots = pi;
            // sort from earliest to latest
            Collections.sort(slots, new Comparator<PenciledInItem>() {
                public int compare(PenciledInItem o1, PenciledInItem o2) {
                    Date d1 = o1.getScheduledStartDate();
                    Date d2 = o2.getScheduledStartDate();
                    return (d1.compareTo(d2));
                }
            });
            return slots;
        }
        return new ArrayList<PenciledInItem>();
    }

    public static void removeIfZero(ArrayList<TaskPI> lot, TaskPI task) {
        if (task.getDuration() <= 0) {
            lot.remove(task);
        }
    }

    /**
     * Helper method for getFirstStartDate, sorts tasks from earliest to latest.
     * @return sorted ArrayList<TaskPI>
     */
    private static ArrayList<TaskPI> sortByStartDate(ArrayList<TaskPI> tasks) {
            ArrayList<TaskPI> ts = tasks;
        // sort from earliest to latest
            Collections.sort(ts, new Comparator<TaskPI>(){
                public int compare(TaskPI o1, TaskPI o2){
                    Date d1 = o1.getStart_date();
                    Date d2 = o2.getStart_date();
                    return (d1.compareTo(d2));
                }
            });
            return ts;
}

    private static ArrayList<Event> sortByStartDateEvents(ArrayList<Event> es) {
        ArrayList<Event> events = es;
        // sort from earliest to latest
        Collections.sort(events, new Comparator<Event>(){
            public int compare(Event o1, Event o2){
                Date d1 = o1.getStart_date();
                Date d2 = o2.getStart_date();
                return (d1.compareTo(d2));
            }
        });
        return events;
    }


}
