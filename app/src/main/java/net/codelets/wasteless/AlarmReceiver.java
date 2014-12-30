package net.codelets.wasteless;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by MarkDiez on 12/26/14.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent toNote = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, toNote, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(context).setContentTitle("Waste LESS!")
                .setContentText("Cook before you waste what you have!").setSmallIcon(R.drawable.icon).setContentIntent(pIntent).build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.defaults |= Notification.DEFAULT_ALL;
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);
    }
}

