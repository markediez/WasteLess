package net.codelets.wasteless;

import java.text.DateFormat;
import java.util.GregorianCalendar;

/**
 * Created by MarkDiez on 12/22/14.
 */
public class Food {
    private String name,key;
    private GregorianCalendar expire;

    public Food (String name, String key, GregorianCalendar expire) {
        this.name = name;
        this.key = key;
        this.expire = expire;
    }

    public String getKey() {
        return this.key;
    }
    public String specify() {
        return this.name;
    }
    public GregorianCalendar expireCal() { return this.expire; }
    public String expireString () {
        DateFormat df = DateFormat.getDateInstance();
        return "Expires: " + df.format(expire.getTime());
    }
}
