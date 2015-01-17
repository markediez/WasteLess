package net.codelets.wasteless;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by MarkDiez on 1/11/15.
 */
public class Individual extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Grab food object
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String key = intent.getStringExtra("notification");
        MainActivity util = new MainActivity();
        Food notificationFood = util.getFood(key, sp);

        // Set intents
        Intent nIntent = new Intent(context, MainActivity.class);
        Intent nSnooze = new Intent(context, Snooze.class);
        nSnooze.putExtra("snooze", key);
        Intent nDismiss = new Intent(context, Dismiss.class);
        nDismiss.putExtra("dismiss", key);
        PendingIntent pIntent= PendingIntent.getActivity(context, notificationFood.getId(),
                nIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pSnooze= PendingIntent.getBroadcast(context, notificationFood.getId(),
                nSnooze,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pDismiss= PendingIntent.getBroadcast(context, notificationFood.getId(),
                nDismiss,PendingIntent.FLAG_UPDATE_CURRENT);

        // Set up content for notification
//        int left = notificationFood.daysLeft(notificationFood.expireCal(), Calendar.getInstance());
        String food = notificationFood.specify();
        String expire = food + " is about to expire!";
        String content = "You have a few days left!";
        if(notificationFood.isExpired()) {                                                          // Change content when item is expired
            expire = food + " has expired!";
            content = "Get rid of this item now!";
        }
        // Build notification
        Notification notification = new Notification.Builder(context)
                .setContentTitle(expire)
                .setContentText(content)
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pIntent)
                .addAction(R.drawable.snooze, "Snooze", pSnooze)
                .addAction(R.drawable.dismiss, "Dismiss", pDismiss)
                .build();

        // Notifies
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notification.defaults |= Notification.DEFAULT_ALL;                                          // Vibrates, Ring, wakes on notification
        notification.flags |= Notification.FLAG_AUTO_CANCEL;                                        // hide the notification after its selected
        notificationManager.notify(notificationFood.getId(), notification);
    }
}
