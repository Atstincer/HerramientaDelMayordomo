package com.example.usuario.herramientadelmayordomoii.Almacenamiento;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by usuario on 29/10/2021.
 */

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String BD_NAME = "MiBD";
    public static final int BD_VERSION = 1;
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
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onCreate(SQLiteDatabase bd) {

        bd.execSQL("CREATE TABLE Estancias(" +
                "id INTEGER PRIMARY KEY," +
                "familyName TEXT," +
                "adultos INTEGER," +
                "menores INTEGER," +
                "infantes INTEGER," +
                "desde TEXT," +
                "hasta TEXT," +
                "noHab TEXT," +
                "observaciones TEXT)");

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
                "foto BLOB," +
                "preferencias TEXT," +
                "limitaciones TEXT," +
                "observaciones TEXT)");

        bd.execSQL("CREATE TABLE Reportes(" +
                "id INTEGER PRIMARY KEY," +
                "estanciaId Integer," +
                "fecha TEXT," +
                "reporteMañana TEXT," +
                "reporteTarde TEXT," +
                "reporteNoche TEXT," +
                "FOREIGN KEY(estanciaId) REFERENCES Estancias(id) " +
                "ON UPDATE CASCADE " +
                "ON DELETE CASCADE)");

        bd.execSQL("CREATE TABLE FamilyNames_Clientes(" +
                "familyNameId INTEGER," +
                "clienteId INTEGER," +
                "FOREIGN KEY(familyNameId) REFERENCES FamilyNames(id) " +
                "ON UPDATE CASCADE " +
                "ON DELETE CASCADE," +
                "FOREIGN KEY(clienteId) REFERENCES Clientes(id) " +
                "ON UPDATE CASCADE " +
                "ON DELETE CASCADE)");

        bd.execSQL("CREATE TABLE Estancias_Clientes(" +
                "estanciaId INTEGER," +
                "clienteId INTEGER," +
                "FOREIGN KEY(estanciaId) REFERENCES Estancias(id) " +
                "ON UPDATE CASCADE " +
                "ON DELETE CASCADE," +
                "FOREIGN KEY(clienteId) REFERENCES Clientes(id) " +
                "ON UPDATE CASCADE " +
                "ON DELETE CASCADE)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
