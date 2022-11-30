package com.example.nu_mad_sm2022_final_project_team2.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.nu_mad_sm2022_final_project_team2.R;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    private static final String ALARM_ID = "ALARM_ID";
    private static final String WAKEUP_TASK = "WAKEUP_TASK";
    private static final String MESSAGE = "MESSAGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            startRescheduleAlarmsService(context);
        } else {
            boolean hasWakeUpTask = intent.getBooleanExtra(WAKEUP_TASK, false);
            Intent i;
            if (hasWakeUpTask) {
                i = new Intent(context, WakeUpTask.class);
            } else {
                i = new Intent(context, AlarmFragment.class);
            }

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, intent.getIntExtra("ALARM_ID", 0), i, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ALARM_CHANNEL")
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("Alarm")
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            String message = intent.getStringExtra(MESSAGE);
            if (message != null && message != "") {
                builder.setContentText(intent.getStringExtra(MESSAGE));
            }

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(123, builder.build());
        }
    }

    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RescheduleAlarmsService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }
}
