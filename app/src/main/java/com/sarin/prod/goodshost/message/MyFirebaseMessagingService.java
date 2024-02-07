package com.sarin.prod.goodshost.message;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import com.sarin.prod.goodshost.item.ReturnMsgItem;
import com.sarin.prod.goodshost.item.UserItem;
import com.sarin.prod.goodshost.network.RetrofitApi;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.PreferenceManager;
import com.sarin.prod.goodshost.util.StringUtil;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static String TAG = MainApplication.TAG;

    String CHANNEL_NAME = "SARIN_NOTI_NAME";

    StringUtil stringUtil = StringUtil.getInstance();

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        String userId = PreferenceManager.getString(getApplicationContext(), "userId");
        if(!stringUtil.nullCheck(userId)){
            UserItem userItem = new UserItem();
            userItem.setUser_id(MainApplication.ANDROID_ID);
            userItem.setFcm_token(token);
            setUserRegister(userItem);
        }

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();

        // 데이터 페이로드에서 필요한 정보 추출
        String title = data.get("title");
        String body = data.get("body");

        PendingIntent pendingIntent;
        Intent notificationIntent = new Intent();
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //notificationIntent.setClass(this, MainActivity.class);

        int notificationId = (int) System.currentTimeMillis();

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
            pendingIntent = PendingIntent.getActivity(getApplicationContext(),notificationId, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT); //Activity를 시작하는 인텐트 생성
        }else{
            pendingIntent = PendingIntent.getActivity(getApplicationContext(),notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder builder;

        String CHANNEL_ID = "service_channel";
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "smart_channel",
                NotificationManager.IMPORTANCE_HIGH);
/*
                1. IMPORTANCE_HIGH = 알림음이 울리고 헤드업 알림으로 표시
                2. IMPORTANCE_DEFAULT = 알림음 울림
                3. IMPORTANCE_LOW = 알림음 없음
                4. IMPORTANCE_MIN = 알림음 없고 상태줄 표시 X

 */

        ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE))
                .createNotificationChannel(channel);

        builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);

        builder.setSmallIcon(R.mipmap.ic_launcher_foreground)
                //.setLargeIcon(mLargeIcon)
                // .setContent(remoteViews)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                .setColor(getApplicationContext().getResources().getColor(R.color.black))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setGroup("sarin")
                // 사용자가 탭을 클릭하면 자동 제거
                //.setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(body)
        ;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
                || !"1".equals(PreferenceManager.getString(getApplicationContext(), "isAlarmStatus"))
        ) {
            Log.d(TAG, "알림 거부 상태.");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(notificationId, builder.build()); //알람 생성

        NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Example Group")
                // 그룹에 속한 알림의 수를 요약 텍스트에 반영합니다.
                .setContentText("You have " + notificationId + " new messages")
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                // setGroup()을 사용하여 이 알림을 동일한 그룹에 속하게 합니다.
                .setGroup("sarin")
                // setGroupSummary()를 true로 설정하여 이 알림을 그룹의 요약 알림으로 만듭니다.
                .setGroupSummary(true);

        notificationManager.notify(0, summaryBuilder.build());

    }


    public void setUserRegister(UserItem userItem){

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<ReturnMsgItem> call = service.setUserRegister("setUserRegister", userItem.getUser_id(), userItem.getFcm_token());
        call.enqueue(new Callback<ReturnMsgItem>() {
            @Override
            public void onResponse(Call<ReturnMsgItem> call, Response<ReturnMsgItem> response) {
                if(response.isSuccessful()){
                    ReturnMsgItem returnMsgItem = response.body();
                    Log.d(TAG, "setUserRegister : " + returnMsgItem.toString());

                    PreferenceManager.setString(getApplicationContext(), "userId", MainApplication.ANDROID_ID);

                }
                else{
                    Log.e(TAG, "실패 코드 확인 : " + response.code());
                    Log.e(TAG, "연결 주소 확인 : " + response.raw().request().url().url());
                }
            }
            @Override
            public void onFailure(Call<ReturnMsgItem> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });


    }


}
