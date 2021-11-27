package com.example.usuario.herramientadelmayordomoii.Entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

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

    private int id;
    private String name,origenPais,origenCiudad,dob,pass;
    private Bitmap foto;

    public Cliente(int id, String name, String origenPais, String origenCiudad, String dob, String pass, Bitmap foto) {
        this.id = id;
        this.name = name;
        this.origenPais = origenPais;
        this.origenCiudad = origenCiudad;
        this.dob = dob;
        this.pass = pass;
        this.foto = foto;
    }

    public Cliente() {
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


}
