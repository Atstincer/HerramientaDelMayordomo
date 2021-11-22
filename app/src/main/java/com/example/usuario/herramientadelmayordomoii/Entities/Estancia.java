package com.example.usuario.herramientadelmayordomoii.Entities;

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;


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

    private int id, familyName_id, adultos, menores, infantes;
    private String desde,hasta,no_hab,familyName;

    public Estancia() {
    }

    public Estancia(int id, int familyName_id, int adultos, int menores, int infantes, String desde, String hasta, String no_hab, String familyName) {
        this.id = id;
        this.familyName_id = familyName_id;
        this.adultos = adultos;
        this.menores = menores;
        this.infantes = infantes;
        this.desde = desde;
        this.hasta = hasta;
        this.no_hab = no_hab;
        this.familyName = familyName;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNo_noches() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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

    public int getFamilyName_id() {
        return familyName_id;
    }

    public void setFamilyName_id(int familyName_id) {
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

    @Override
    public String toString() {
        return "Estancia{" +
                "id=" + id +
                ", familyName_id='" + familyName_id + '\'' +
                ", desde='" + desde + '\'' +
                ", hasta='" + hasta + '\'' +
                ", no_hab='" + no_hab + '\'' +
                '}';
    }
}
