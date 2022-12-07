package com.example.usuario.herramientadelmayordomo.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.usuario.herramientadelmayordomo.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomo.Entities.Recordatorio;

/**
 * Created by usuario on 24/10/2022.
 */

public class MyApp {

    public static final int STATE_REGULAR = 0;
    public static final int STATE_NEW = 1;
    public static final int STATE_UPDATE = 2;
    public static final int STATE_EN_CASA = 3;
    public static final int STATE_SEGUN_PERIODO = 4;
    public static final int STATE_SEGUN_CLIENTE = 5;
    public static final int STATE_SEGUN_HAB = 6;

    public static boolean isInt(String cad){
        try{
            int numero = Integer.parseInt(cad);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static void mantenimientoBD(final Context ctx){
        new Thread(new Runnable() {
            @Override
            public void run() {
                AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(ctx,AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
                SQLiteDatabase db = admin.getWritableDatabase();

                //eliminando recordatorios viejos
                db.delete(Recordatorio.TABLA_NAME,Recordatorio.CAMPO_FECHA+"<?",new String[]{DateHandler.getToday(DateHandler.FECHA_FORMATO_BD)});
            }
        }).start();
    }

}
