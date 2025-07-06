package com.soumen.listongo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagingService extends FirebaseMessagingService {
    private static final @org.jspecify.annotations.NonNull String CHANNEL_ID = "fcm_message";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage.getNotification().getBody());
        }

        if (!remoteMessage.getData().isEmpty()) {
            sendNotification(remoteMessage.getData().get("message"));
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    public void sendNotification(String messageBody) {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", true);
        if (!notificationsEnabled) {
            return;
        }
        SecurePrefsHelper prefsHelper = new SecurePrefsHelper(this);
        String email=prefsHelper.getEmail();
        String password=prefsHelper.getPassword();
        Long id=prefsHelper.getUserId();
        String userName=prefsHelper.getUsername();

        login(email, password, new LoginCallBack() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(MessagingService.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("email",email);
                intent.putExtra("UserId",id);
                intent.putExtra("userName",userName);
                PendingIntent pendingIntent = PendingIntent.getActivity(MessagingService.this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(MessagingService.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.brand)
                                .setContentTitle("ListOnGo")
                                .setContentText(messageBody)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(
                            CHANNEL_ID, "FCM Notifications", NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }

                notificationManager.notify(0, notificationBuilder.build());
            }

            @Override
            public void onFailure(String error) {

            }
        });


    }

    public void login(String email,String password,LoginCallBack loginCallBack){
        LogInUserModel logInUserModel=new LogInUserModel(email,password);
        ApiService apiService=ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> loginSuc=apiService.loginUser(logInUserModel);
        loginSuc.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    loginCallBack.onSuccess();
                }
                else{
                    loginCallBack.onFailure("Failed with code"+response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {

            }
        });
    }
}
