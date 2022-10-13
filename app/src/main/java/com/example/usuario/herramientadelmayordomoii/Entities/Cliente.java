package com.example.usuario.herramientadelmayordomoii.Entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.Comparator;

/**
 * Created by usuario on 31/10/2021.
 */

public class Cliente {

    public static final String TABLE_NAME = "Clientes";
    public static final String CAMPO_NAME = "name";
    public static final String CAMPO_DOB = "dob";
    public static final String CAMPO_ORIGEN_PAIS = "origenPais";
    public static final String CAMPO_ORIGEN_CIUDAD = "origenCiudad";
    public static final String CAMPO_PASS = "pass";
    public static final String CAMPO_FOTO = "foto";
    public static final String CAMPO_PREFERENCIAS = "preferencias";
    public static final String CAMPO_LIMITACIONES = "limitaciones";
    public static final String CAMPO_OBSERVACIONES = "observaciones";

    private int id;
    private String name,origenPais,origenCiudad,dob,pass,preferencias,limitaciones,observaciones;
    private Bitmap foto;
    private boolean checked;

    public Cliente(int id, String name, String origenPais, String origenCiudad, String dob, String pass, Bitmap foto, boolean checked, String preferencias, String limitaciones, String observaciones) {
        this.id = id;
        this.name = name;
        this.origenPais = origenPais;
        this.origenCiudad = origenCiudad;
        this.dob = dob;
        this.pass = pass;
        this.foto = foto;
        this.checked = checked;
        this.preferencias = preferencias;
        this.limitaciones = limitaciones;
        this.observaciones = observaciones;
    }

    public Cliente() {
        this.checked = false;
    }

    public Bitmap getFoto() {
        return this.foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public void setFoto(byte[] fotoInBytes){
        if(fotoInBytes!=null) {
            this.foto = BitmapFactory.decodeByteArray(fotoInBytes, 0, fotoInBytes.length);
        }
    }

    public byte[] getFotoInBytes(){
        if(getFoto()==null){
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmapToStore = getFoto();
        bitmapToStore.compress(Bitmap.CompressFormat.JPEG,100,baos);
        return baos.toByteArray();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigenPais() {
        return origenPais;
    }

    public void setOrigenPais(String origenPais) {
        this.origenPais = origenPais;
    }

    public String getOrigenCiudad() {
        return origenCiudad;
    }

    public void setOrigenCiudad(String origenCiudad) {
        this.origenCiudad = origenCiudad;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(String preferencias) {
        this.preferencias = preferencias;
    }

    public String getLimitaciones() {
        return limitaciones;
    }

    public void setLimitaciones(String limitaciones) {
        this.limitaciones = limitaciones;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void checkUncheck(){
        if(this.checked){
            setChecked(false);
        }else{
            setChecked(true);
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    public static Comparator<Cliente> nameAscending = new Comparator<Cliente>() {
        @Override
        public int compare(Cliente c1, Cliente c2) {
            String name1 = c1.getName().toLowerCase();
            String name2 = c2.getName().toLowerCase();
            return name1.compareTo(name2);
        }
    };
}
