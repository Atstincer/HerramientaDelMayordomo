package com.example.usuario.herramientadelmayordomoii.Entities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by usuario on 31/10/2021.
 */

public class FamilyName {

    public static final String TABLE_NAME = "FamilyNames";
    public static final String CAMPO_FAMILY_NAME = "familyName";

    private int id;
    private String familyName;
    private List<Cliente> clientesMiembros;

    public FamilyName() {
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

    public List<Cliente> getClientesMiembros() {
        return clientesMiembros;
    }

    public void setClientesMiembros(List<Cliente> clientesMiembros) {
        this.clientesMiembros = clientesMiembros;
    }

    public void addClienteMiembro(Cliente c){
        if(clientesMiembros==null){clientesMiembros = new ArrayList<>();}
        clientesMiembros.add(c);
    }

    public void popUpClienteMiembro(Cliente c){
        if(clientesMiembros.contains(c)){clientesMiembros.remove(c);}
    }

    @Override
    public String toString() {
        return getFamilyName();
    }

    public static Comparator<FamilyName> nameAscending = new Comparator<FamilyName>() {
        @Override
        public int compare(FamilyName familyName1, FamilyName familyName2) {
            return familyName1.getFamilyName().toLowerCase().compareTo(familyName2.getFamilyName().toLowerCase());
        }
    };
}
