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
    private Calendar expire;

    public Food (String name, String key, Calendar expire, int id) {
        this.name = name;
        this.key = key;
        this.expire = expire;
        this.id = id;
    }

    public String getKey() {
        return this.key;
    }
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
    // Returns a calendar clone of the expiration calendar
    // *******************************************************************
    public Calendar expireCal() {
        int d,m,y;
        Calendar clone = Calendar.getInstance();
        d = this.expire.get(Calendar.DAY_OF_MONTH);
        m = this.expire.get(Calendar.MONTH);
        y = this.expire.get(Calendar.YEAR);
        clone.set(y,m,d);
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
