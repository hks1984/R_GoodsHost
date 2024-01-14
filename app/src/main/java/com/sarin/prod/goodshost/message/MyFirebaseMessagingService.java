package com.sarin.prod.goodshost.message;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sarin.prod.goodshost.MainApplication;

import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.item.UserItem;
import com.sarin.prod.goodshost.network.RetrofitApi;
import com.sarin.prod.goodshost.util.PreferenceManager;
import com.sarin.prod.goodshost.util.StringUtil;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static String TAG = MainApplication.TAG;
    String CHANNEL_ID = "SARIN_NOTI_ID";
    String CHANNEL_NAME = "SARIN_NOTI_NAME";

    RetrofitApi retrofitApi = RetrofitApi.getInstance();
    StringUtil stringUtil = StringUtil.getInstance();

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        String userId = PreferenceManager.getString(getApplicationContext(), "userId");
        if(!stringUtil.nullCheck(userId)){
            UserItem userItem = new UserItem();
            userItem.setUser_id(MainApplication.ANDROID_ID);
            userItem.setFcm_token(token);
            retrofitApi.setUserRegister(userItem);
        }

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG,"onMessageReceived : " + remoteMessage.getNotification().getTitle());
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        builder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher_background);

        Notification notification = builder.build();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, notification);
    }
}
