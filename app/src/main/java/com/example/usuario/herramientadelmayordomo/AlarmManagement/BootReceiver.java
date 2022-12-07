package com.example.usuario.herramientadelmayordomo.AlarmManagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.usuario.herramientadelmayordomo.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomo.Entities.Recordatorio;
import com.example.usuario.herramientadelmayordomo.Util.DateHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 29/11/2022.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            //System.out.println("Capturando evento BOOT_COMPLETED");
            reloadRecordatorios(context);
        }
    }

    private void reloadRecordatorios(Context ctx){
        List<Recordatorio> recordatorioList = getRecordatoriosFromDB(ctx);
        if(recordatorioList!=null){
            if(recordatorioList.size()>0){
                for(Recordatorio recordatorio:recordatorioList){
                    Recordatorio.setAlarm(ctx,recordatorio,false);
                }
            }
        }
    }

    private List<Recordatorio> getRecordatoriosFromDB(Context ctx){
        List<Recordatorio> recordatorioList = new ArrayList<>();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(ctx,AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+Recordatorio.TABLA_NAME+" WHERE "+Recordatorio.CAMPO_FECHA+">=" +
                "'"+ DateHandler.getToday(DateHandler.FECHA_FORMATO_BD)+"'",null);
        if(cursor.moveToFirst()){
            do{
                Recordatorio recordatorio = Recordatorio.getRecordatorio(cursor);
                if(Recordatorio.validarTimeStamp(recordatorio.getFecha())){
                    recordatorioList.add(recordatorio);
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return recordatorioList;
    }
}
