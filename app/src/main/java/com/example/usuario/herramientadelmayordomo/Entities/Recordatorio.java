package com.example.usuario.herramientadelmayordomo.Entities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.usuario.herramientadelmayordomo.AlarmManagement.AlarmReceiver;
import com.example.usuario.herramientadelmayordomo.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomo.Util.DateHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by usuario on 2/11/2022.
 */

public class Recordatorio {

    public static final String TABLA_NAME = "Recordatorios";
    public static final String CAMPO_FECHA = "fecha";
    private static final String CAMPO_TITULO = "titulo";
    private static final String CAMPO_MENSAJE = "mensaje";
    private static final String CAMPO_ESTANCIA_ID = "estanciaId";

    public static final String KEY_RECORDATORIO_ID = "id";

    private long id,estanciaId;
    private String fecha,title,mensaje;

    public Recordatorio(){}

    public Recordatorio(long id, String fecha, String title, String mensaje,long estanciaId) {
        this.id = id;
        this.fecha = fecha;
        this.title = title;
        this.mensaje = mensaje;
        this.estanciaId = estanciaId;
    }

    public Recordatorio(long id, long timeStamp, String title, String mensaje,long estanciaId) {
        this.id = id;
        this.fecha = DateHandler.getFecha(timeStamp,DateHandler.FECHA_FORMATO_MOSTRAR);
        this.title = title;
        this.mensaje = mensaje;
        this.estanciaId = estanciaId;
    }

    public static boolean validarTimeStamp(String fecha){
        Calendar calendar = DateHandler.configurarHoraRecordatorio(DateHandler.getCalendar(fecha,DateHandler.FECHA_FORMATO_MOSTRAR));
        return validarTimeStamp(calendar.getTimeInMillis());
    }

    private static boolean validarTimeStamp(long timeStamp){
        long timeStampNow = Calendar.getInstance().getTimeInMillis();
        return timeStampNow<timeStamp;
    }


    public static boolean setAlarm(Context ctx, Recordatorio recordatorio,boolean registrarEnBD){
        if(registrarEnBD){
            recordatorio.setId(registrarRecordatorio(ctx,recordatorio.getTitle(),recordatorio.getMensaje(),recordatorio.getFecha(),(int)recordatorio.getEstanciaId()));
        }
        AlarmManager alarmManager = (AlarmManager)ctx.getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(ctx, AlarmReceiver.class);

        alarmIntent.putExtra(KEY_RECORDATORIO_ID,recordatorio.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,(int)recordatorio.getId(),alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        long timeStamp = DateHandler.configurarHoraRecordatorio(DateHandler.getCalendar(recordatorio.getFecha(),DateHandler.FECHA_FORMATO_MOSTRAR)).getTimeInMillis();
        alarmManager.set(AlarmManager.RTC_WAKEUP,timeStamp,pendingIntent);
        return true;
    }

    public static void cancelarAlarma(Context ctx,Recordatorio recordatorio){
        AlarmManager alarmManager = (AlarmManager)ctx.getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(ctx, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,(int)recordatorio.getId(),alarmIntent,PendingIntent.FLAG_NO_CREATE);
        if(pendingIntent!=null && alarmManager!=null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private static long registrarRecordatorio(Context ctx,String title,String mensaje,String fecha,int estanciaId){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(ctx,AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Recordatorio.CAMPO_FECHA,DateHandler.formatDateToStoreInDB(fecha));
        values.put(Recordatorio.CAMPO_TITULO,title);
        values.put(Recordatorio.CAMPO_MENSAJE,mensaje);
        values.put(Recordatorio.CAMPO_ESTANCIA_ID,estanciaId);
        return db.insert(Recordatorio.TABLA_NAME,null,values);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setFecha(long timeStamp,int patron){
        this.fecha = DateHandler.getFecha(timeStamp,patron);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public long getEstanciaId() {
        return estanciaId;
    }

    public void setEstanciaId(long estanciaId) {
        this.estanciaId = estanciaId;
    }

    public static Recordatorio getRecordatorio(Cursor cursor){
        Recordatorio recordatorio = new Recordatorio();
        recordatorio.setId(cursor.getLong(0));
        recordatorio.setFecha(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Recordatorio.CAMPO_FECHA))));
        recordatorio.setTitle(cursor.getString(cursor.getColumnIndex(Recordatorio.CAMPO_TITULO)));
        recordatorio.setMensaje(cursor.getString(cursor.getColumnIndex(Recordatorio.CAMPO_MENSAJE)));
        recordatorio.setEstanciaId(cursor.getLong(cursor.getColumnIndex(Recordatorio.CAMPO_ESTANCIA_ID)));
        return recordatorio;
    }

    public static Recordatorio getRecordatorio (Context ctx,long id){
        if(id==0){return new Recordatorio();}
        Recordatorio recordatorio = new Recordatorio();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(ctx,AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+Recordatorio.TABLA_NAME+" WHERE id = ?",new String[]{String.valueOf(id)});
        if(cursor.moveToFirst()){
            recordatorio = getRecordatorio(cursor);
        }
        cursor.close();
        return recordatorio;
    }

    @Override
    public String toString() {
        return "id:"+id+"\nfecha: "+fecha+"\ntitulo: "+title+"\nmensaje: "+mensaje;
    }

    public static Comparator<Recordatorio> dateAscending = new Comparator<Recordatorio>() {
        @Override
        public int compare(Recordatorio r1, Recordatorio r2) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date e1Date = null;
            Date e2Date = null;
            try{
                e1Date = sdf.parse(r1.getFecha());
                e2Date = sdf.parse(r2.getFecha());
            }catch(ParseException e){
                System.out.println(e.getMessage());
            }
            if(e1Date != null && e2Date != null){
                return e1Date.compareTo(e2Date);
            }
            return 0;
        }
    };

}
