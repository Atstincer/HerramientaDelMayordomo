package com.example.usuario.herramientadelmayordomoii.Util;


import java.util.Calendar;

/**
 * Created by usuario on 18/10/2022.
 */

public class DateHandler {

    public static final int FECHA_FORMATO_MOSTRAR = 0;
    public static final int FECHA_FORMATO_BD = 1;

    public static String toDosLugares(int x){
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

}
