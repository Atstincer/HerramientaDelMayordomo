package com.example.usuario.herramientadelmayordomo.AlarmManagement;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomo.Entities.Estancia;
import com.example.usuario.herramientadelmayordomo.Entities.MyEmail;
import com.example.usuario.herramientadelmayordomo.Entities.Recordatorio;
import com.example.usuario.herramientadelmayordomo.Almacenamiento.MySharedPreferences;
import com.example.usuario.herramientadelmayordomo.MainActivity;
import com.example.usuario.herramientadelmayordomo.R;

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

        Recordatorio recordatorio = Recordatorio.getRecordatorio(getApplicationContext(),intent.getExtras().getLong(Recordatorio.KEY_RECORDATORIO_ID,0));

        //Creamos un nuevo Intent que contiene la clase/Actividad a abrir
        Intent intentFinal = new Intent(getApplicationContext(), MainActivity.class);
        intentFinal.putExtra(Estancia.KEY_ESTANCIA_ID,recordatorio.getEstanciaId());

        //Definimos la nueva actividad como nueva tarea
        intentFinal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //Encapsulamos el intenFinal anteriormente creado dentro de un nuevo PendingIntent.
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intentFinal,
                PendingIntent.FLAG_UPDATE_CURRENT
        );


        try{
            NotificationCompat.Builder NotiBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_recordatorios)
                            .setContentTitle(recordatorio.getTitle())
                            .setContentText(recordatorio.getMensaje())
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setDefaults(Notification.DEFAULT_ALL)//Requiere permisos de Vibración.
                            .setContentIntent(resultPendingIntent)
                            .setAutoCancel(true);

            Notification notification;

            if(recordatorio.getEstanciaId()>0){
                Estancia estancia = Estancia.getEstanciaFromDB(getApplicationContext(),recordatorio.getEstanciaId());
                String uri = "@drawable/mail";
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Intent _intentAbrir = new Intent(this, MainActivity.class);
                _intentAbrir.putExtra(Estancia.KEY_ESTANCIA_ID,recordatorio.getEstanciaId());
                _intentAbrir.putExtra(MyEmail.KEY_ENVIAR_INFO,true);
                PendingIntent _pendingAbrir = PendingIntent.getActivity(getApplicationContext(), 1, _intentAbrir, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Action accion_abrir = new NotificationCompat.Action.Builder(
                        imageResource,
                        getString(R.string.enviar_por_mail),
                        _pendingAbrir).build();
                notification = NotiBuilder
                        //Métodos exclusivos para definir una notificacion como Big View.
                        .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(estancia.getEmailBody(getApplicationContext())))
                        .addAction(accion_abrir).build();
            } else {
                notification = NotiBuilder.build();
            }


            //Obtenemos una instancia del servicio de NotificactionManager
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            //Creamos y mostramos la notificación
            mNotifyMgr.notify(MySharedPreferences.getNotificationId(getApplicationContext()), notification);

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        //return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }
}
