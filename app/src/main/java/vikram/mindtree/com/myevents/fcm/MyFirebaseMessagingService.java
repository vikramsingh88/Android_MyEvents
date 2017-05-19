package vikram.mindtree.com.myevents.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;

import vikram.mindtree.com.myevents.ComingSoonActivity;
import vikram.mindtree.com.myevents.Main2Activity;
import vikram.mindtree.com.myevents.R;
import vikram.mindtree.com.myevents.teaser.TeaserActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("MessagingService", "From: " + remoteMessage.getFrom());
        Log.d("MessagingService", "Message Body: " + remoteMessage.getData());
        if (remoteMessage.getData().get("path") != null ) {
            sendComingSoonNotificatio(remoteMessage.getData().get("path"));
        } else if(remoteMessage.getData().get("info") != null){
            sendInformNotification(remoteMessage.getData().get("info"));
        } else {
            sendNotification(remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("detail"),
                    remoteMessage.getData().get("time"),
                    remoteMessage.getData().get("date"));
        }
    }

    private void sendNotification(String title, String detail, String time, String date) {
        Intent intent = new Intent(this, Main2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setLargeIcon(BitmapFactory.decodeResource( getResources(), R.mipmap.ic_launcher))
                        .setSmallIcon(R.drawable.ic_stat_logo)
                        .setContentTitle(title)
                        .setContentText(detail)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(detail).setBigContentTitle(title).setSummaryText("Time : "+time +" Date : "+date))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(m, builder.build());
    }

    private void sendComingSoonNotificatio(String path) {
        Intent intent = new Intent(this, TeaserActivity.class);
        intent.putExtra("path", path);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource( getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_stat_logo)
                .setContentTitle("Coming soon")
                .setContentText("Click to see more")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(m, notificationBuilder.build());
    }

    private void sendInformNotification(String comment) {
        Intent intent = new Intent(this, Main2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setLargeIcon(BitmapFactory.decodeResource( getResources(), R.mipmap.ic_launcher))
                        .setSmallIcon(R.drawable.ic_stat_logo)
                        .setContentTitle("Info")
                        .setContentText("About event")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(comment))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(m, builder.build());
    }
}
