package com.first.chatapp.Utills;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.first.chatapp.ChatActivity;
import com.first.chatapp.MainActivity;
import com.first.chatapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "CHAT");
        builder.setContentTitle(title);
        builder.setContentText(body);

        Intent intent = null;
        String messageType = remoteMessage.getData().get("type");
        if (messageType != null) {
            if (messageType.equalsIgnoreCase("sms")) {
                intent = new Intent(this, ChatActivity.class);
                intent.putExtra("OtherUserId", remoteMessage.getData().get("userID"));
            } else {
                // Handle other message types or set a default intent
                intent = new Intent(this, MainActivity.class); // Example: Opening the MainActivity
            }
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 101, intent, PendingIntent.FLAG_IMMUTABLE);

        builder.setContentIntent(pendingIntent);

        builder.setSmallIcon(R.drawable.logo);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(123, builder.build());
    }
}