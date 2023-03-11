package com.example.usuario.herramientadelmayordomo.Entities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.usuario.herramientadelmayordomo.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomo.R;
import com.example.usuario.herramientadelmayordomo.Util.DateHandler;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
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

    public static final String KEY_ESTANCIA_ID = "estanciaId";

    private long id, familyName_id;
    private int adultos, menores, infantes;
    private String desde,hasta,no_hab,familyName,observaciones;
    private List<Cliente> listClientes;
    private List<Reporte> listReportes;

    public Estancia() {
    }

    public List<Cliente> getListClientes() {
        return listClientes;
    }

    public void setListClientes(List<Cliente> listClientes) {
        this.listClientes = listClientes;
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

    public List<Reporte> getListReportes() {
        return listReportes;
    }

    public void setListReportes(List<Reporte> listReportes) {
        this.listReportes = listReportes;
    }

    public String getEmailAsunto(){
        return familyName;
    }

    public String getTitleRecordatorio(){
        return getFamilyName();
    }

    public String getMensajeRecordatodio(Context ctx){
        return ctx.getResources().getString(R.string.llegando_el)+" "+getDesde();
    }

    // \
    public String getEmailBody(Context ctx){
        String body = ctx.getString(R.string.estancia)+": "+getFamilyName()+ "\n";
        if(!getNo_hab().equals("")){body+=ctx.getString(R.string.Hab) + " " + getNo_hab() + "\n";}
        if(getListClientes()!=null && getListClientes().size()>0){
            body += ctx.getString(R.string.clientes)+":\n";
            for(Cliente cliente:getListClientes()){
                body += "  - " + cliente.getName() + "\n";
            }
        }
        if(getAdultos()>1){body+=getAdultos() + " " + ctx.getString(R.string.adultos) + "\n";}
        else if(getAdultos()==1) {body+=getAdultos() + " " + ctx.getString(R.string.adulto) + "\n";}
        body += formatPeriodoToShow(ctx,getDesde(),getHasta()) +"\n" +
                ""+getObservaciones();
        return body;
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

    private static Estancia getEstancia(Cursor cursor){
        Estancia estancia = new Estancia();
        estancia.setId(cursor.getLong(0));
        estancia.setFamilyName(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_FAMILY_NAME)));
        estancia.setNo_hab(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_NO_HAB)));
        estancia.setDesde(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_DESDE))));
        estancia.setHasta(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_HASTA))));
        estancia.setAdultos(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_ADULTOS)));
        estancia.setMenores(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_MENORES)));
        estancia.setInfantes(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_INFANTES)));
        estancia.setObservaciones(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_OBSERVACIONES)));
        return estancia;
    }

    public static Estancia getEstanciaFromDB(Context ctx, long id){
        Estancia estancia = new Estancia();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(ctx,AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+Estancia.TABLE_NAME+" WHERE id=?",new String[]{String.valueOf(id)});
        if(cursor.moveToFirst()){
            estancia = Estancia.getEstancia(cursor);
        }
        cursor.close();
        return estancia;
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
