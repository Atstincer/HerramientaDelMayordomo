package com.example.usuario.herramientadelmayordomoii.Entities;

import android.app.DatePickerDialog;
import android.content.Context;

import com.example.usuario.herramientadelmayordomoii.R;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * Created by usuario on 7/10/2021.
 */

public class Estancia {

    public static final String TABLE_NAME = "Estancias";
    public static final String CAMPO_FAMILY_NAME = "familyName";
    public static final String CAMPO_ADULTOS = "adultos";
    public static final String CAMPO_MENORES = "menores";
    public static final String CAMPO_INFANTES = "infantes";
    public static final String CAMPO_DESDE = "desde";
    public static final String CAMPO_HASTA = "hasta";
    public static final String CAMPO_NO_HAB = "noHab";
    public static final String CAMPO_OBSERVACIONES = "observaciones";

    private long id, familyName_id;
    private int adultos, menores, infantes;
    private String desde,hasta,no_hab,familyName,observaciones;

    public Estancia() {
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public int getAdultos() {
        return adultos;
    }

    public void setAdultos(int adultos) {
        this.adultos = adultos;
    }

    public int getMenores() {
        return menores;
    }

    public void setMenores(int menores) {
        this.menores = menores;
    }

    public int getInfantes() {
        return infantes;
    }

    public void setInfantes(int infantes) {
        this.infantes = infantes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNo_noches() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        int noches = 0;
        try{
            Date desde = sdf.parse(getDesde());
            Date hasta = sdf.parse(getHasta());
            noches = (int) ((hasta.getTime()-desde.getTime())/86400000);
            System.out.println("Numero de noches: " + noches);
        } catch (ParseException e) {
            System.out.println("ParseException: " + e.getMessage());
        }
        return String.valueOf(noches);
    }

    public long getFamilyName_id() {
        return familyName_id;
    }

    public void setFamilyName_id(long familyName_id) {
        this.familyName_id = familyName_id;
    }

    public String getDesde() {
        return desde;
    }

    public void setDesde(String desde) {
        this.desde = desde;
    }

    public String getHasta() {
        return hasta;
    }

    public void setHasta(String hasta) {
        this.hasta = hasta;
    }

    public String getNo_hab() {
        return no_hab;
    }

    public void setNo_hab(String no_hab) {
        this.no_hab = no_hab;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public static String formatPeriodoToShow(Context ctx, String desde, String hasta){
        String fechas = "";
        if(desde.substring(3,5).equals(hasta.substring(3,5))){
            fechas = desde.substring(0,2) + " "+ ctx.getResources().getString(R.string.al) + " " + hasta;
        }else {
            if(desde.substring(6).equals(hasta.substring(6))){
                fechas = desde.substring(0,5) + " " + ctx.getResources().getString(R.string.al) + " " + hasta;
            } else {
                fechas = desde + " " + ctx.getResources().getString(R.string.al) + " " + hasta;
            }
        }
        return fechas;
    }

    @Override
    public String toString() {
        return "Estancia{" +
                "id=" + id +
                ", familyName='" + familyName + '\'' +
                ", desde='" + desde + '\'' +
                ", hasta='" + hasta + '\'' +
                ", no_hab='" + no_hab + '\'' +
                '}';
    }

    public static Comparator<Estancia> dateAscending = new Comparator<Estancia>() {
        @Override
        public int compare(Estancia e1, Estancia e2) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
            Date e1Date = null;
            Date e2Date = null;
            try{
                e1Date = sdf.parse(e1.getDesde());
                e2Date = sdf.parse(e2.getDesde());
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
