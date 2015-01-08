package net.codelets.wasteless;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Created by MarkDiez on 12/26/14.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        long id = intent.getLongExtra("alarm", -1);
        Intent notificationIntent;
        PendingIntent pIntent;
        Notification notification = new Notification();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (id == 6275) {
            notificationIntent = new Intent(context, MainActivity.class);
            pIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            // Build notification
            notification = new Notification.Builder(context).setContentTitle("Waste LESS!")
                    .setContentText("Cook before you waste what you have!").setSmallIcon(R.drawable.icon).setContentIntent(pIntent).build();
        } else {
            notificationIntent = new Intent(context, CloseUp.class);
            pIntent =  PendingIntent.getActivity(context, 0, notificationIntent, 0);
            MainActivity utils = new MainActivity();
            Food food = utils.getFood(intent.getStringExtra("key"), pref);
            String title = food.specify() + " is going bad!";
            String description = "You have " + Integer.toString(food.daysLeft()) + " days left!";

            // intents
            Intent snooze = new Intent(context,)
            // Pending intents
            PendingIntent psnooze = PendingIntent.getActivity(context, food.getId(), snooze, PendingIntent.FLAG_UPDATE_CURRENT);
            // Build notification
            notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setSmallIcon(R.drawable.icon)
                    .setContentIntent(pIntent)
                    .addAction(R.drawable.) // done
                    .addAction(R.drawable.snooze, "Snooze", ) // snooze
                    .build();
        }

        // Notifies
        notification.defaults |= Notification.DEFAULT_ALL;          // Vibrates, Ring, wakes on notification
        notification.flags |= Notification.FLAG_AUTO_CANCEL;        // hide the notification after its selected
        notificationManager.notify(0, notification);
    }
}

