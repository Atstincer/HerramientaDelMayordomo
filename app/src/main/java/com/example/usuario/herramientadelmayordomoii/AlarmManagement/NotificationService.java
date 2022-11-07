package com.example.usuario.herramientadelmayordomoii.AlarmManagement;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.usuario.herramientadelmayordomoii.Entities.Recordatorio;
import com.example.usuario.herramientadelmayordomoii.Almacenamiento.MySharedPreferences;
import com.example.usuario.herramientadelmayordomoii.MainActivity;
import com.example.usuario.herramientadelmayordomoii.R;

/**
 * Created by usuario on 30/10/2022.
 */

public class NotificationService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*System.out.println("***********************************");
        System.out.println("En NotificationServices...");
        System.out.println("***********************************");*/

        String title = intent.getExtras().getString(Recordatorio.TITLE);
        String msg = intent.getExtras().getString(Recordatorio.MENSAGE);

        /*System.out.println(title);
        System.out.println(msg);
        System.out.println("************************************");*/

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntentRecordatoriosF = PendingIntent.getActivity(getApplicationContext(),0,intent1,0);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_main_icon)
                .setContentText(msg)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{100,250,100,500})
                .setContentIntent(pendingIntentRecordatoriosF)
                .setAutoCancel(true).build();

        int id = MySharedPreferences.getNotificationId(getApplicationContext());
        //System.out.println("Id: "+ id);
        notificationManager.notify(id,notification);

        MySharedPreferences.storeNotificationId(getApplicationContext(),id+1);

        //return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }
}
