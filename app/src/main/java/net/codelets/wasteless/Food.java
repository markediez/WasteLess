package net.codelets.wasteless;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by MarkDiez on 12/22/14.
 */

public class Food {
    private String name,key;
    private int id;
    private GregorianCalendar expire, notify;

    public Food (String name, String key, GregorianCalendar expire, int id) {
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
    public GregorianCalendar expireCal() { return this.expire; }
    public int getId() { return this.id; }
    public String expireString () {
        DateFormat df = DateFormat.getDateInstance();
        return "Expires: " + df.format(expire.getTime());
    }

    public int daysLeft() {
        Calendar curr = Calendar.getInstance();
        long currTime = curr.getTimeInMillis();
        long endTime = this.expire.getTimeInMillis();

        long remTime = endTime - currTime;

        return (int)remTime / 1000 / 60 / 60 / 24;
    }

}
