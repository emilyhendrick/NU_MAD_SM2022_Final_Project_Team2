package com.example.nu_mad_sm2022_final_project_team2.calendar_item;

import android.text.format.DateUtils;
import android.util.Log;

import com.example.nu_mad_sm2022_final_project_team2.User;
import com.google.api.LogDescriptor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PenciledInSchedule {
    ArrayList<PenciledInItem> items;
    ArrayList<TaskPI> availableTasks;
    ArrayList<TaskPI> tasksInProgress;
    ArrayList<Event> availableEvents;
    ArrayList<TimeSlot> availableSlots;
    Date startConstraint;
    Date endConstraint;
    int INTERVAL = 15;
    // in 24 hours time;
    int STARTHOUR = 9;
    int ENDHOUR = 17;

    public PenciledInSchedule(ArrayList<PenciledInItem> items, ArrayList<TaskPI> availableTasks, ArrayList<Event> availableEvents, ArrayList<TimeSlot> availableSlots, Date startConstraint, Date endConstraint) {
        this.items = items;
        this.tasksInProgress = availableTasks;
        this.availableTasks = availableTasks;
        this.availableEvents = availableEvents;
        this.availableSlots = availableSlots;
        validateAndCreate();

    }

    public PenciledInSchedule(User u) {
        this.availableTasks = u.getTasks();
        this.tasksInProgress = u.getTasks();
        this.availableEvents = u.getEvents();
        validateAndCreate();
    }

    public PenciledInSchedule( ArrayList<TaskPI> availableTasks, ArrayList<Event> availableEvents) {
        this.availableTasks = availableTasks;
        this.tasksInProgress = availableTasks;
        this.availableEvents = availableEvents;
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
        this.tasksInProgress = availableTasks;
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



    public ArrayList<TimeSlot> createEmptySlots() {
        ArrayList<TimeSlot> slotsEmpty = new ArrayList<>();
        Date s = TaskUtils.getFirstStartDate(this.availableTasks, this.availableEvents);
        Date e = TaskUtils.getLastDeadline(this.availableTasks);

        long diff = e.getTime() - s.getTime();
        long mins = TimeUnit.MILLISECONDS.toMinutes(diff);
        // split into intervals of time based on constant
        int ints = (int) Math.floor(mins/INTERVAL);
        Log.d("intervals", Integer.toString(ints));

        for (int i=0; i<ints; i++) {
            DateFormat dateFormat = new SimpleDateFormat("k");
            String strHour = dateFormat.format(s);
            long hourInDay = Integer.parseInt(strHour);
            Date endTime = addDurationtoDate(s, INTERVAL);
            // if within working hours add to slots
            if (STARTHOUR <= hourInDay & hourInDay <= ENDHOUR) {
                TimeSlot ts = new TimeSlot(s, endTime);
                slotsEmpty.add(ts);
                s = addDurationtoDate(endTime, 1);
            }
            else {
                s = this.addDurationtoDate(endTime, 1);
            }
        }
        Log.d("miaSTARTDATE", s.toString());
        Log.d("miaENDDATE", e.toString());
        Log.d("slotsemptynoevents", slotsEmpty.toString());
        return slotsEmpty;
    }

    public ArrayList<TimeSlot> addEventsToSlots(ArrayList<TimeSlot> emSlots) {
        if (this.availableEvents != null) {
        for (TimeSlot ts : emSlots) {
            for (Event e :  this.availableEvents) {
                if (eventAtDate(e, ts) ) {
                    ts.setItem(e);
                }
            }
        }
        }
        Log.d("slotwithevents", emSlots.toString());
        return emSlots;
    }

    /**
     * Schedules all high priority items right before their deadline and then
     * fills in lower priority items.
     * @return TimeSlots with tasks scheduled right before they are due
     */
    public ArrayList<TimeSlot> createWorstCaseSchedule(ArrayList<TimeSlot> slots) {
        if (this.tasksInProgress.size() == 0 ) {
            return slots;
        }
        for (TaskPI t : tasksInProgress) {
            placeTaskInSlots(t, slots);
        }
        Log.d("worst -tasks ", this.tasksInProgress.toString());
        Log.d("worst", slots.toString());
        Collections.reverse(slots);
        return slots;
    }

    public void placeTaskInSlots(TaskPI task, ArrayList<TimeSlot> slots) {
        for (TimeSlot ts : slots) {
            if (ts.isSlotAvailableForTask(task))  {
                ts.setItem(task);
                TaskUtils.removeIfZero(this.tasksInProgress, task);
                updateTaskDurationbySlot(task, ts);
            }
        }
    }

    public ArrayList<TimeSlot> initializeTimeSlots() {
        ArrayList<TimeSlot> slotsEmptyEvents = this.createEmptySlots();
        slotsEmptyEvents = addEventsToSlots(slotsEmptyEvents);

        Collections.reverse(slotsEmptyEvents);
        Log.d("slotsemptynoevents", slotsEmptyEvents.toString());
        return slotsEmptyEvents;
    }

    /**
     * Removes slot time from the task duration and updates start and end time
     * @param task task to be scheduled into slot
     * @param ts slot
     * @return task representing remaining task after slot is filled
     */
    public void updateTaskDurationbySlot(TaskPI task, TimeSlot ts) {
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
        // use new duration to update the task
        task.setDuration((int) newDuration);
    }


    /**
     * If there is free time before tasks are due move tasks up so
     * they are not done at the last minute.
     */
    public void createSchedule() {
        ArrayList<TimeSlot> slots = createWorstCaseSchedule(initializeTimeSlots());
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
        Log.d("BEST", slots.toString());
        this.availableSlots = slots;
        this.items = createPIItemsSimple();
        //createPIItems();
    }





    /**
     * Determines if argument event overlaps with argument TimeSlot
     * @param e event
     * @param slot time slot
     * @return true if there is an overlap
     */
    public Boolean eventAtDate(Event e, TimeSlot slot) {
        Date start1 = e.getStart_date();
        Date end1 = e.getEnd_date();
        Date start2 = slot.getStartTime();
        Date end2 = slot.getEndTime();
        Log.d("test 1 event",e.toString());
        Log.d("test 1slot",slot.toString());
        Log.d("test 1overlapping", String.valueOf(start1.before(end2) && start2.before(end1)));

        return start1.before(end2) && start2.before(end1);

    }

    public void validateAndCreate() {
        if (this.availableTasks != null & this.availableEvents != null) {
            if (this.availableTasks.size() > 0) {
                createSchedule();
            }
            else {
                createPIItemsEventsOnly();
            }
        }
    }



    public Date addDurationtoDate(Date d, int duration) {
       long ms =  d.getTime();
       return new Date(ms + (duration * 60000L));
    }


    /**
     * Based on the scheduled available slots created corresponding PenciledInItems
     */
    public ArrayList<PenciledInItem>  createPIItemsSimple() {
        ArrayList<TimeSlot> slots = this.availableSlots;
        ArrayList<PenciledInItem> schedule = new ArrayList<>();
        int n = slots.size();
        for (int i = 0; i < n; i++) {
            // has task or event
            if (!(slots.get(i).isFree())) {
                TimeSlot slot = slots.get(i);
               PenciledInItem it = new PenciledInItem(slot.getItem(), slot.getStartTime(), slot.getEndTime());
               schedule.add(it);
            }
        }
        Log.d("repeated in slots", schedule.toString());
        return schedule;
    }


    /**
     * Collapse slots that are the same into one Item
     */
    public void createPIItems () {
        ArrayList<PenciledInItem> oldSchedule = createPIItemsSimple();
        ArrayList<PenciledInItem> collapsedScheduled = new ArrayList<>();
        int n = oldSchedule.size();
        int i = 1;
        while (i < n) {
            PenciledInItem prev = oldSchedule.get(i - 1);
            PenciledInItem curr = oldSchedule.get(i);
            if (prev.isSameTask(curr)) {
                PenciledInItem newCombo = combine(prev, curr);
                oldSchedule.set(i, newCombo);
                oldSchedule.remove(prev);
                n--;
            }
            else {
                collapsedScheduled.add(curr);
                i++;
            }
        }
        Log.d("collapsed in slots", oldSchedule.toString());
        this.items = oldSchedule;
    }


    /**
     * Combine to PenciledInItems into one with correct start and end time.
     * @param prev previous event/task
     * @param curr current event/task
     * @return event/task of both combined
     */
    public PenciledInItem combine(PenciledInItem prev, PenciledInItem curr) {
        return new PenciledInItem(prev.itemName, prev.getScheduledStartDate(),
                curr.getScheduledEndDate(), prev.getCategory(), prev.getDone(), true, false, prev.type, prev.task_due_date);
    }

    /**
     * Based on the scheduled available slots created corresponding PenciledInItems
     */
    public void createPIItemsEventsOnly() {
        ArrayList<PenciledInItem> schedule = new ArrayList<>();
        if (this.availableEvents != null) {
            ArrayList<Event> currEvents = TaskUtils.filterForCurrentEvents(this.availableEvents);
            int n = currEvents.size();
            for (int i = 0; i < n; i++) {
                PenciledInItem it = new PenciledInItem(availableEvents.get(i));
                schedule.add(it);
            }
            this.items = schedule;
        }
    }

}

