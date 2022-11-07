package com.example.usuario.herramientadelmayordomoii.AlarmManagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import com.example.usuario.herramientadelmayordomoii.Entities.Recordatorio;
//import androidx.core.content.ContextCompat;

/**
 * Created by usuario on 30/10/2022.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        /*System.out.println("***********************************");
        System.out.println("En AlarmReceiver...");
        System.out.println("***********************************");*/

        String title = intent.getExtras().getString(Recordatorio.TITLE);
        String msg = intent.getExtras().getString(Recordatorio.MENSAGE);
        Intent service1 = new Intent(context,NotificationService.class);
        service1.putExtra(Recordatorio.TITLE,title);
        service1.putExtra(Recordatorio.MENSAGE,msg);
        //service1.setData(Uri.parse("custom://"+System.currentTimeMillis()));
        //ContextCompat.startForegroundService();
        context.startService(service1);
    }
}
