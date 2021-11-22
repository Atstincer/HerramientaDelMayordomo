package com.example.usuario.herramientadelmayordomoii.Entities;

import java.util.ArrayList;

/**
 * Created by usuario on 31/10/2021.
 */

public class Family {

    public static final String TABLE_NAME = "FamilyNames";
    public static final String CAMPO_FAMILY_NAME = "familyName";

    private int id;
    private String familyName;
    private ArrayList<Cliente> clientes;

    public Family(int id, String familyName, ArrayList<Cliente> clientes) {
        this.id = id;
        this.familyName = familyName;
        this.clientes = clientes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public ArrayList<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(ArrayList<Cliente> clientes) {
        this.clientes = clientes;
    }
}
