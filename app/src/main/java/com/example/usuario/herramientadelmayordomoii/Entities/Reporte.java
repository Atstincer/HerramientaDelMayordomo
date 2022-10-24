package com.example.usuario.herramientadelmayordomoii.Entities;


import android.content.ContentValues;

import com.example.usuario.herramientadelmayordomoii.Util.DateHandler;

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

    public static ContentValues getContentValues(Reporte reporte){
        ContentValues values = new ContentValues();
        values.put(Reporte.CAMPO_ESTANCIA_ID,reporte.getEstanciaId());
        values.put(Reporte.CAMPO_FECHA, DateHandler.formatDateToStoreInDB(reporte.getFecha()));
        values.put(Reporte.CAMPO_REPORTE_MAÑANA,reporte.getReporteMañana());
        values.put(Reporte.CAMPO_REPORTE_TARDE,reporte.getReporteTarde());
        values.put(Reporte.CAMPO_REPORTE_NOCHE,reporte.getReporteNoche());
        return values;
    }
}
