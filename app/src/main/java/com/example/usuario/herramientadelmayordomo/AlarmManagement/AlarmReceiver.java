package com.example.usuario.herramientadelmayordomo.AlarmManagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.usuario.herramientadelmayordomo.Entities.Recordatorio;

/**
 * Created by usuario on 30/10/2022.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        /*String title = intent.getExtras().getString(Recordatorio.CAMPO_TITULO);
        String msg = intent.getExtras().getString(Recordatorio.CAMPO_MENSAJE);
        long estanciaId = intent.getExtras().getLong(Recordatorio.CAMPO_ESTANCIA_ID);*/

        Intent service1 = new Intent(context,NotificationService.class);
        //service1.putExtra(Recordatorio.CAMPO_TITULO,title);
        //service1.putExtra(Recordatorio.CAMPO_MENSAJE,msg);
        //service1.putExtra(Recordatorio.CAMPO_ESTANCIA_ID,estanciaId);
        service1.putExtra(Recordatorio.KEY_RECORDATORIO_ID,intent.getExtras().getLong(Recordatorio.KEY_RECORDATORIO_ID));
        //service1.setData(Uri.parse("custom://"+System.currentTimeMillis()));
        //ContextCompat.startForegroundService();
        context.startService(service1);
    }
}
