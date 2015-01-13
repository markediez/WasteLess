package net.codelets.wasteless;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by MarkDiez on 12/26/14.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Variables Set
        Intent notificationIntent;
        PendingIntent pIntent;
        Notification notification;
        NotificationManager notificationManager = (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Set Intents
        notificationIntent = new Intent(context, MainActivity.class);
        pIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        // Build notification
        notification = new Notification.Builder(context).setContentTitle("Waste LESS!")
                .setContentText("Cook before you waste what you have!")
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pIntent)
                .build();

        // Notifies
        notification.defaults |= Notification.DEFAULT_ALL;                                          // Vibrates, Ring, wakes on notification
        notification.flags |= Notification.FLAG_AUTO_CANCEL;                                        // hide the notification after its selected
        notificationManager.notify(0, notification);
    }
}

