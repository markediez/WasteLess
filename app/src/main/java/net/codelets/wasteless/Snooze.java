package net.codelets.wasteless;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by MarkDiez on 1/7/15.
 */
public class Snooze extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Receive Food key
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String key = intent.getStringExtra("snooze");
        MainActivity util = new MainActivity();
        Food notificationFood = util.getFood(key, sp);

        // Clears the notification
        int id = notificationFood.getId();
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);

        // Specifices snooze duration
        Calendar alarmCal = Calendar.getInstance();                                                 // Grabs wanted time
        alarmCal.add(Calendar.MINUTE, 15);                                                          // TODO make time variable
        long wantedTime = alarmCal.getTimeInMillis();                                               // Covert time to milliseconds

        // Sets up intent
        Intent alarmIntent = new Intent(context, Individual.class);                                 // Go to Notification class
        alarmIntent.putExtra("individual", key);                                                    // Store key
        PendingIntent pi = PendingIntent
                .getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // set snooze
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, wantedTime,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi);
        Toast.makeText(context, "Snoozed", Toast.LENGTH_SHORT).show();
    }
}
