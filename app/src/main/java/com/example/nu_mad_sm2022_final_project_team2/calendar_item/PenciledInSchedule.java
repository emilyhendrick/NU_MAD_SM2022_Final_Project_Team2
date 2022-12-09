package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import android.text.format.DateUtils;

import com.example.nu_mad_sm2022_final_project_team2.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PenciledInSchedule {
    ArrayList<PenciledInItem> items;
    ArrayList<TaskPI> availableTasks;
    ArrayList<Event> availableEvents;
    ArrayList<TimeSlot> availableSlots;
    Date startConstraint;
    Date endConstraint;
    int INTERVAL = 15;
    int STARTHOUR = 9;
    int ENDHOUR = 17;

    public PenciledInSchedule(ArrayList<PenciledInItem> items, ArrayList<TaskPI> availableTasks, ArrayList<Event> availableEvents, ArrayList<TimeSlot> availableSlots, Date startConstraint, Date endConstraint) {
        this.items = items;
        this.availableTasks = availableTasks;
        this.availableEvents = availableEvents;
        this.availableSlots = availableSlots;
        this.startConstraint = startConstraint;
        this.endConstraint = endConstraint;
        validateAndCreate();
    }

    public PenciledInSchedule(User u, Date startConstraint, Date endConstraint, ArrayList<TimeSlot> availableSlots) {
        this.availableTasks = u.getTasks();
        this.availableEvents = u.getEvents();
        this.availableSlots = availableSlots;
        this.startConstraint = startConstraint;
        this.endConstraint = endConstraint;
        validateAndCreate();
    }

    public ArrayList<PenciledInItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<PenciledInItem> items) {
        this.items = items;
    }

    public ArrayList<TaskPI> getAvailableTasks() {
        return availableTasks;
    }

    public void setAvailableTasks(ArrayList<TaskPI> availableTasks) {
        this.availableTasks = availableTasks;
    }

    public ArrayList<Event> getAvailableEvents() {
        return availableEvents;
    }

    public void setAvailableEvents(ArrayList<Event> availableEvents) {
        this.availableEvents = availableEvents;
    }

    public Date getStartConstraint() {
        return startConstraint;
    }

    public void setStartConstraint(Date startConstraint) {
        this.startConstraint = startConstraint;
    }

    public Date getEndConstraint() {
        return endConstraint;
    }

    public void setEndConstraint(Date endConstraint) {
        this.endConstraint = endConstraint;
    }

    /**
     * Sort tasks by Increasing order of payoff
     */
    public ArrayList<TaskPI> sortTasksByPayoff(ArrayList<TaskPI>  atasks) {
        Collections.sort(atasks, new Comparator<TaskPI>(){
            public int compare(TaskPI o1, TaskPI o2){
                if(o1.getPriority() == o2.getPriority())
                    return 0;
                return o1.getPriority() < o2.getPriority() ? -1 : 1;
            }
        });
        return atasks;
    }

    public Date getLastDeadline() {
        ArrayList<TaskPI> ts =this.availableTasks;
        Collections.sort(ts, new Comparator<TaskPI>(){
            public int compare(TaskPI o1, TaskPI o2){
                Date d1 = o1.getEnd_date();
                Date d2 = o2.getEnd_date();
                return (d1.compareTo(d2));
            }
        });
        return ts.get(0).getEnd_date();
    }

    public Date getFirstStartDate() {
        ArrayList<TaskPI> ts =this.availableTasks;
        Collections.sort(ts, new Comparator<TaskPI>(){
            public int compare(TaskPI o1, TaskPI o2){
                Date d1 = o1.getStart_date();
                Date d2 = o2.getStart_date();
                return (d1.compareTo(d2));
            }
        });
        return ts.get(0).getStart_date();

    }


    public ArrayList<TimeSlot> createEmptySlots() {
        ArrayList<TimeSlot> slots = new ArrayList<>();
        Date s = this.getFirstStartDate();
        Date e = this.getLastDeadline();

        long diff = e.getTime() - s.getTime();
        long mins = TimeUnit.MILLISECONDS.toMinutes(diff);
        // split into intervals of time based on constant
        int ints = (int) Math.floor(mins/INTERVAL);

        for (int i=0; i<ints; i++) {
            Date startPlusIntr = this.addDurationtoDate(s, INTERVAL);
            long hourInDay = TimeUnit.MILLISECONDS.toHours(s.getTime());
            // if within working hours
            if (STARTHOUR < hourInDay & hourInDay < ENDHOUR) {
                TimeSlot ts = new TimeSlot(s, startPlusIntr);
                slots.add(ts);
            }
            s = this.addDurationtoDate(startPlusIntr, 1);
        }
        return slots;
    }

    public ArrayList<TimeSlot> addEventsToSlots(ArrayList<TimeSlot> emSlots) {
        if (this.availableSlots != null) {
        for (int i = 0; i < emSlots.size(); ++i) {

            for(int j = 0; j < this.availableEvents.size(); ++j) {
                if (eventAtDate(availableEvents.get(j), emSlots.get(i)) ) {
                    emSlots.get(i).setItem(availableEvents.get(j));
                }
            }
        }
        }
        return emSlots;
    }

    public ArrayList<TimeSlot> createWorstCaseSchedule() {
        ArrayList<TimeSlot> slots = this.createEmptySlots();
        this.addEventsToSlots(slots);
        ArrayList<TaskPI>  sortedT = sortTasksByPayoff(this.availableTasks);
        int n = sortedT.size();
        int s = slots.size();
        // iterate through all tasks
        for (int i = 0; i < n; ++ i) {
            // find free slot for task starting from last possible slot
            for (int j=s; j > 0; --j ){
                if (slots.get(j).isSlotAvailableForTask(sortedT.get(i))) {
                    slots.get(j).setItem(sortedT.get(i));
                }
            }
        }
        return slots;
    }



    public void createSchedule() {
        ArrayList<TimeSlot> slots = createWorstCaseSchedule();
        int n = slots.size();
        int firstEmpty = 0;
        Boolean isEmptySet = false;
        int i = 0;
        while (i < n) {
            if (slots.get(i).isFree()){
                if (firstEmpty > i) {
                    firstEmpty = i;
                    isEmptySet = true;
                    i ++;
                }
            }
            if (slots.get(i).hasTask() && isEmptySet) {
                if (i > firstEmpty) {
                    TimeSlot currSlot = slots.get(i);
                    TimeSlot newSlot = slots.get(firstEmpty);
                    ACalendarItem currTask = currSlot.getItem();
                    currSlot.removeItem();
                    newSlot.setItem(currTask);
                    isEmptySet = false;
                    i = firstEmpty + 1;
                }
            }
            else {
                i++;
            }
        }
        this.availableSlots = slots;
    }





    /**
     * Determines if argument event overlaps with argument TimeSlot
     * @param e event
     * @param slot time slot
     * @return true if there is an overlap
     */
    public Boolean eventAtDate(Event e, TimeSlot slot) {
        Date eventStart = e.getStart_date();
        Date eventEnd = e.getEnd_date();
        Date slotStart = slot.getStartTime();
        Date slotEnd = slot.getEndTime();

        // event ends before slot starts
        if (eventEnd.compareTo(slotStart) < 0) {
            return false;
        }
        // event ends after slot starts
        else {
            // event begins after or when slot begins
            if (eventStart.compareTo(slotStart) > 0 || eventStart.compareTo(slotStart) == 0) {
                return true;
            }
            // event begins before slot begins
            else{
                return false;
            }
        }
    }

    public void validateAndCreate() {
        if (this.availableTasks != null & this.availableSlots != null) {
            createSchedule();
        }
    }



    public Date addDurationtoDate(Date d, int duration) {
       long ms =  d.getTime();
       return new Date(ms + (duration * 60000L));
    }


    /**
     * Based on the scheduled available slots created corresponding PenciledInItems
     */
    public void createPIItems() {
        ArrayList<TimeSlot> slots = this.availableSlots;
        ArrayList<PenciledInItem> schedule = new ArrayList<>();
        int n = slots.size();
        for (int i = 0; i < n; i++) {
            // has task or event
            if (!(slots.get(i).isFree())) {
               PenciledInItem it = new PenciledInItem(slots.get(i).getItem());
               schedule.add(it);
            }
        }
        return;
    }

}
