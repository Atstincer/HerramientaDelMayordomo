package com.example.usuario.herramientadelmayordomoii.BD_conexion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by usuario on 29/10/2021.
 */

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String BD_NAME = "MiBD";
    private static AdminSQLiteOpenHelper instancia;

    private AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static AdminSQLiteOpenHelper getInstance(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        if(instancia==null){
            instancia = new AdminSQLiteOpenHelper(context,name,factory,version);
        }
        return instancia;
    }

    @Override
    public void onCreate(SQLiteDatabase bd) {

        bd.execSQL("CREATE TABLE Estancias(" +
                "id INTEGER PRIMARY KEY," +
                "familyName INTEGER," +
                "adultos INTEGER," +
                "menores INTEGER," +
                "infantes INTEGER," +
                "desde TEXT," +
                "hasta TEXT," +
                "noHab TEXT," +
                "FOREIGN KEY(familyName) REFERENCES FamilyNames(id))");

        bd.execSQL("CREATE TABLE FamilyNames(" +
                "id INTEGER PRIMARY KEY," +
                "familyName TEXT)");

        bd.execSQL("CREATE TABLE Clientes(" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT," +
                "dob TEXT," +
                "origenPais TEXT," +
                "origenCiudad TEXT," +
                "pass TEXT," +
                "foto BLOB)");

        bd.execSQL("CREATE TABLE FamilyNames_Clientes(" +
                "familyNameId INTEGER," +
                "clienteId INTEGER," +
                "FOREIGN KEY(familyNameId) REFERENCES FamilyNames(id)," +
                "FOREIGN KEY(clienteId) REFERENCES Clientes(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
