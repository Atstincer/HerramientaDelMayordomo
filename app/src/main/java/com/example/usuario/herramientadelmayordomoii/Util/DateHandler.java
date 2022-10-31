package com.example.usuario.herramientadelmayordomoii.Util;


import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by usuario on 18/10/2022.
 */

public class DateHandler {

    public static final int FECHA_FORMATO_MOSTRAR = 0;
    public static final int FECHA_FORMATO_BD = 1;

    private static String toDosLugares(int x){
        String cad = String.valueOf(x);
        if (cad.length() == 1){
            cad = "0" + x;
        }
        return cad;
    }

    public static String formatDateToStoreInDB(String date){
        return date.substring(6)+"-"+date.substring(3,5)+"-"+date.substring(0,2);
    }

    public static String formatDateToStoreInDB(int day, int month, int year){
        return year+"-"+toDosLugares(month)+"-"+toDosLugares(day);
    }

    public static String formatDateToShow(String date){
        return date.substring(8) + "/" + date.substring(5,7) + "/" + date.substring(0,4);
    }

    public static String formatDateToShow(int day,int month,int year){
        return toDosLugares(day)+"/"+toDosLugares(month)+"/"+toDosLugares(year);
    }

    public static String getToday(int formato){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);
        if(formato==DateHandler.FECHA_FORMATO_BD) {
            return DateHandler.formatDateToStoreInDB(day,month,year);
        }else if(formato==DateHandler.FECHA_FORMATO_MOSTRAR){
            return DateHandler.formatDateToShow(day,month,year);
        }
        return "";
    }

    public static Date getDate(String fecha, int patron){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        try {
            if (patron == DateHandler.FECHA_FORMATO_BD) {
                return sdf.parse(fecha);
            }else if(patron == DateHandler.FECHA_FORMATO_MOSTRAR){
                return sdf.parse(DateHandler.formatDateToStoreInDB(fecha));
            }
        }catch (Exception e){
            System.out.println("Exception catch: "+e.getMessage());
        }
        return new Date();
    }

    public static int getDiasEntreFechas(Date desde, Date hasta){
        if(desde.after(hasta)){return 0;}
        return (int)(hasta.getTime()-desde.getTime())/(24*60*60*1000);
    }

    public static boolean areDatesInOrder(String firstDate,String secondDate,int formato){
        if(formato==FECHA_FORMATO_MOSTRAR){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date date1 = sdf.parse(firstDate);
                Date date2 = sdf.parse(secondDate);
                return date1.before(date2);
            }catch (Exception e){
                System.out.println("Exception capturada parseando fechas: "+e.getMessage());
            }
        }
        return false;
    }



}
