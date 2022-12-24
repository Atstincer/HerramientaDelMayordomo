package com.example.usuario.herramientadelmayordomo.Entities;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.usuario.herramientadelmayordomo.R;
import com.example.usuario.herramientadelmayordomo.Util.DateHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by usuario on 17/10/2022.
 */

public class Reporte {

    public static final String TABLE_NAME = "Reportes";
    public static final String CAMPO_ESTANCIA_ID = "estanciaId";
    public static final String CAMPO_FECHA = "fecha";
    public static final String CAMPO_REPORTE_MAÑANA = "reporteMañana";
    public static final String CAMPO_REPORTE_TARDE = "reporteTarde";
    public static final String CAMPO_REPORTE_NOCHE = "reporteNoche";

    public static final int LAYOUT_EN_ESTANCIA = 0;
    public static final int LAYOUT_EN_REPORTES = 1;


    private long id, estanciaId;
    private String fecha,reporteMañana, reporteTarde, reporteNoche;

    public Reporte(){}

    public Reporte(long id, String fecha, String reporteMañana, String reporteTarde, String reporteNoche, int estanciaId) {
        this.id = id;
        this.fecha = fecha;
        this.reporteMañana = reporteMañana;
        this.reporteTarde = reporteTarde;
        this.reporteNoche = reporteNoche;
        this.estanciaId = estanciaId;
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

    public String getReporteMañana() {
        return reporteMañana;
    }

    public void setReporteMañana(String reporteMañana) {
        this.reporteMañana = reporteMañana;
    }

    public String getReporteTarde() {
        return reporteTarde;
    }

    public void setReporteTarde(String reporteTarde) {
        this.reporteTarde = reporteTarde;
    }

    public String getReporteNoche() {
        return reporteNoche;
    }

    public void setReporteNoche(String reporteNoche) {
        this.reporteNoche = reporteNoche;
    }

    public long getEstanciaId() {
        return estanciaId;
    }

    public void setEstanciaId(long estanciaId) {
        this.estanciaId = estanciaId;
    }

    public static Map<String,String> getInfoMail(Context ctx, List<Reporte> listaReportes){
        if(listaReportes.size()<0){return null;}
        String asunto = ctx.getString(R.string.reportes);
        List<String> fechas = new ArrayList<>();
        for(Reporte r:listaReportes){
            boolean existe = false;
            for(String fecha:fechas){
                if(fecha.equals(r.getFecha())){
                    existe = true;
                }
            }
            if(!existe){fechas.add(r.getFecha());}
        }
        for(String fecha:fechas){asunto += " - " + fecha;}

        String cuerpo = asunto + "\n\n";

        for(Reporte r:listaReportes){
            Estancia estancia = Estancia.getEstanciaFromDB(ctx,r.getEstanciaId());
            cuerpo += ctx.getString(R.string.Hab) + ":" + estancia.getNo_hab() + "\n" +
                    "" + estancia.getFamilyName() + "\n" +
                    "" + ctx.getString(R.string.estancia) + ": " + Estancia.formatPeriodoToShow(ctx, estancia.getDesde(), estancia.getHasta()) + "\n" +
                    "" + ctx.getString(R.string.reporte_mañana) + ": " +
                    "" + r.getReporteMañana() + "\n" +
                    "" + ctx.getString(R.string.reporte_tarde) + ": " +
                    "" + r.getReporteTarde() + "\n" +
                    "" + ctx.getString(R.string.reporte_noche) + ": " +
                    "" + r.getReporteNoche()+ "\n\n";
        }

        Map<String,String> mapInfoMail = new HashMap<>();
        mapInfoMail.put(MyEmail.CAMPO_ASUNTO_MAIL,asunto);
        mapInfoMail.put(MyEmail.CAMPO_CUERPO_MAIL,cuerpo);
        return mapInfoMail;
    }

    public static ContentValues getContentValues(Reporte reporte){
        ContentValues values = new ContentValues();
        values.put(Reporte.CAMPO_ESTANCIA_ID,reporte.getEstanciaId());
        values.put(Reporte.CAMPO_FECHA, DateHandler.formatDateToStoreInDB(reporte.getFecha()));
        values.put(Reporte.CAMPO_REPORTE_MAÑANA,reporte.getReporteMañana());
        values.put(Reporte.CAMPO_REPORTE_TARDE,reporte.getReporteTarde());
        values.put(Reporte.CAMPO_REPORTE_NOCHE,reporte.getReporteNoche());
        return values;
    }

    public static Reporte getReporte(Cursor cursor){
        Reporte reporte = new Reporte();
        reporte.setId(cursor.getLong(0));
        reporte.setEstanciaId(cursor.getLong(cursor.getColumnIndex(Reporte.CAMPO_ESTANCIA_ID)));
        reporte.setFecha(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_FECHA))));
        reporte.setReporteMañana(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_MAÑANA)));
        reporte.setReporteTarde(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_TARDE)));
        reporte.setReporteNoche(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_NOCHE)));
        return reporte;
    }

    public static Comparator<Reporte> dateAscending = new Comparator<Reporte>() {
        @Override
        public int compare(Reporte reporte1, Reporte reporte2) {
            return DateHandler.getDate(reporte1.getFecha(),DateHandler.FECHA_FORMATO_MOSTRAR).
                    compareTo(DateHandler.getDate(reporte2.getFecha(),DateHandler.FECHA_FORMATO_MOSTRAR));
        }
    };
}
