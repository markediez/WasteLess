package net.codelets.wasteless;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Created by MarkDiez on 1/10/15.
 */
public class Dismiss extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        // Receive Food key
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String key = intent.getStringExtra("dismiss");
        MainActivity util = new MainActivity();
        Food foodAlarm = util.getFood(key, sp);

        // Clears the notification
        int id = foodAlarm.getId();
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);

        // Clear Alarm
        foodAlarm.clearAlarm(context, Individual.class);

        // Notify user
        Toast.makeText(context, "Dismissed", Toast.LENGTH_SHORT).show();
    }
}
