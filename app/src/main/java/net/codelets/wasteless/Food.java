package net.codelets.wasteless;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by MarkDiez on 12/22/14.
 */

public class Food {
    private String name,key;
    private int id;
    private Calendar expire, notify;

    public Food (String n, String k, Calendar c, int i) {
        this.name = n;
        this.key = k;
        this.expire = c;
        this.id = i;
        notify = Calendar.getInstance();
    }

    /* Setters */
    public void setName (String n) {this.name = n;}
    public void setExpire (Calendar e) {this.expire = e;}
    public void setNotify (Calendar n) {this.notify = n;}
    public void setTime (Context context, int hour, int minute) {
        Intent individual = new Intent(context, Individual.class);
        individual.putExtra("notification", key);
        PendingIntent pIndividual = PendingIntent.getBroadcast(context,
                id,individual,PendingIntent.FLAG_UPDATE_CURRENT);
        notify.set(Calendar.HOUR, hour);
        notify.set(Calendar.MINUTE, minute);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notify.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pIndividual);
    }

    /* Getters */
    public String getKey() {return this.key; }
    public String specify() {
        return this.name;
    }
    public int getId() { return this.id; }

    // *******************************************************************
    // Returns a string date format for the expiration date
    // *******************************************************************
    public String expireString () {
        DateFormat df = DateFormat.getDateInstance();
        return "Expires: " + df.format(expire.getTime());
    }
    // *******************************************************************
    // Returns a string date format for the expiration date
    // *******************************************************************
    public String notifyString () {
        DateFormat df = DateFormat.getDateInstance();
        return "Expires: " + df.format(notify.getTime());
    }

    // *******************************************************************
    // Returns a calendar clone of the expiration calendar
    // *******************************************************************
    public Calendar expireCal() {
        int d,m,y,h,min;
        Calendar clone = Calendar.getInstance();
        d = expire.get(Calendar.DAY_OF_MONTH);
        m = expire.get(Calendar.MONTH);
        y = expire.get(Calendar.YEAR);
        h = expire.get(Calendar.HOUR);
        min = expire.get(Calendar.MINUTE);
        clone.set(y,m,d,h,min);
        return clone;
    }
    // *******************************************************************
    // Returns a calendar clone of the notification calendar
    // *******************************************************************
    public Calendar notifyCal() {
        int d,m,y,h,min;
        Calendar clone = Calendar.getInstance();
        d = notify.get(Calendar.DAY_OF_MONTH);
        m = notify.get(Calendar.MONTH);
        y = notify.get(Calendar.YEAR);
        h = notify.get(Calendar.HOUR);
        min = notify.get(Calendar.MINUTE);
        clone.set(y,m,d,h,min);
        return clone;
    }

    // *******************************************************************
    // Returns time remaining to expiration date
    // *******************************************************************
    public int daysLeft(Calendar expire, Calendar date) {
        long currTime = date.getTimeInMillis();
        long endTime = expire.getTimeInMillis();
        long remTime = (endTime - currTime)/ 1000 / 60 / 60 / 24;
        return (int)remTime;
    }

    // *******************************************************************
    // Clears the alarm
    // pre: takes in context
    // post: alarm is cleared
    // *******************************************************************
    public void clearAlarm(Context context, Class receiver) {
        Intent intent = new Intent(context, receiver);                                              // Recreate
        intent.putExtra("notification", key);                                                       //      Alarm Intents
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        am.cancel(pendingIntent);                                                                   // DELETE
    }

    // *******************************************************************
    // Checks if the food object is expired
    // Post: Returns true or false accordingly
    // *******************************************************************
    public boolean isExpired() {
        boolean x = false;
        Calendar curr = Calendar.getInstance();
        if (curr.getTimeInMillis() > expire.getTimeInMillis())
            x = true;
        return x;
    }
}
