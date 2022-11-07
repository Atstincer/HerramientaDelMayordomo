package com.example.usuario.herramientadelmayordomoii.Entities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomoii.AlarmManagement.AlarmReceiver;
import com.example.usuario.herramientadelmayordomoii.R;
import com.example.usuario.herramientadelmayordomoii.Util.DateHandler;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by usuario on 2/11/2022.
 */

public class Recordatorio {

    public static final String TITLE = "title";
    public static final String MENSAGE = "msg";


    public static boolean agregarRecordatorio(Context ctx,long timeStamp,String title,String msg){
        return validarTimeStamp(ctx,timeStamp) && setAlarm(ctx,title,msg,timeStamp);
    }


    public static boolean agregarRecordatorio(Context ctx,String fecha,String title,String msg){
        //Código original
        Calendar calendar = DateHandler.configurarHoraRecordatorio(DateHandler.getCalendar(fecha,DateHandler.FECHA_FORMATO_MOSTRAR));

        //Bloque de prueva
        /*Calendar calendar = DateHandler.getCalendar(fecha,DateHandler.FECHA_FORMATO_MOSTRAR);
        calendar.set(Calendar.SECOND,calendar.get(Calendar.SECOND)+5);*/

        System.out.println("Trying to set alarm to: "+calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR)+" " +
                ""+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND));

        return validarTimeStamp(ctx,calendar.getTimeInMillis()) && setAlarm(ctx,title,msg,calendar.getTimeInMillis());
    }

    private static boolean validarTimeStamp(Context ctx,long timeStamp){
        long timeStampNow = Calendar.getInstance().getTimeInMillis();
        if(timeStamp<timeStampNow){
            Toast.makeText(ctx,ctx.getResources().getString(R.string.la_fecha_no_es_correcta),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private static boolean setAlarm(Context ctx,String title,String msg,long timeStamp){
        AlarmManager alarmManager = (AlarmManager)ctx.getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(ctx, AlarmReceiver.class);
        alarmIntent.putExtra(Recordatorio.TITLE,title);
        alarmIntent.putExtra(Recordatorio.MENSAGE,msg);
        alarmIntent.setData((Uri.parse("custom://"+ System.currentTimeMillis())));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,0,alarmIntent,PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,timeStamp,pendingIntent);
        return true;
    }


}
