package com.example.usuario.herramientadelmayordomoii.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomoii.BD_conexion.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomoii.Entities.Estancia;
import com.example.usuario.herramientadelmayordomoii.Entities.Reporte;
import com.example.usuario.herramientadelmayordomoii.R;
import com.example.usuario.herramientadelmayordomoii.Util.DateHandler;
import com.example.usuario.herramientadelmayordomoii.Util.MyApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by usuario on 17/10/2022.
 */

public class ReporteFragment extends Fragment {

    public static final String TAG = "ReporteFragment";

    /*
    public static final String STATE_REGULAR_MODE = "STATE_REPORTE_REGULAR_MODE";
    public static final String STATE_NEW_REPORTE_MODE = "STATE_NEW_REPORTE_MODE";*/

    private EditText etReporteMañana, etReporteTarde, etReporteNoche;
    private TextView tvDate;
    private Button btn;

    private long idEstanciaRelacionada;
    private long idReporteRelacionado;
    private CallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_reporte,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myCallBack = (CallBack) context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindComponents(view);
        if(getArguments()!=null){
            idEstanciaRelacionada = (long)getArguments().get("estanciaId");
            idReporteRelacionado = (long)getArguments().get("reporteId");
            if(idReporteRelacionado>0){
                showInfoFragment(idReporteRelacionado);return;}
        }
        if(myCallBack.getCurrentStateReporteFragment()==MyApp.STATE_REGULAR){
            setUpRegularMode();
        }else if(myCallBack.getCurrentStateReporteFragment()==MyApp.STATE_NEW){
            setCurrentDate();
        }
    }

    private void bindComponents(View v){
        etReporteMañana = (EditText)v.findViewById(R.id.et_reporte_mañana);
        etReporteTarde = (EditText)v.findViewById(R.id.et_reporte_tarde);
        etReporteNoche = (EditText)v.findViewById(R.id.et_reporte_noche);
        tvDate = (TextView)v.findViewById(R.id.tv_date);
        btn = (Button)v.findViewById(R.id.btn_reporte_fragment);

        tvDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myCallBack.getCurrentStateReporteFragment()==MyApp.STATE_NEW){
                    registrar();
                }else if(myCallBack.getCurrentStateReporteFragment()==MyApp.STATE_REGULAR){
                    actualizar();
                }
            }
        });
    }

    private void setUpRegularMode(){
        if(myCallBack.getCurrentStateReporteFragment()!=MyApp.STATE_REGULAR){
            myCallBack.setCurrentStateReporteFragment(MyApp.STATE_REGULAR);
        }
        btn.setText(getResources().getString(R.string.actualizar));
    }

    private void setUpNewReportMode(){
        if(myCallBack.getCurrentStateReporteFragment()!=MyApp.STATE_NEW){
            myCallBack.setCurrentStateReporteFragment(MyApp.STATE_NEW);
        }
        resetFragment();
        btn.setText(getResources().getString(R.string.registrar));
    }

    private void registrar(){
        if(!validarReporte()){return;}
        Reporte newReporte = getNewReporte();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues info = new ContentValues();
        info.put(Reporte.CAMPO_ESTANCIA_ID,newReporte.getEstanciaId());
        info.put(Reporte.CAMPO_FECHA,DateHandler.formatDateToStoreInDB(newReporte.getFecha()));
        info.put(Reporte.CAMPO_REPORTE_MAÑANA,newReporte.getReporteMañana());
        info.put(Reporte.CAMPO_REPORTE_TARDE,newReporte.getReporteTarde());
        info.put(Reporte.CAMPO_REPORTE_NOCHE,newReporte.getReporteNoche());
        long id = db.insert(Reporte.TABLE_NAME,null,info);
        if(id!=0){
            Toast.makeText(getContext(),getResources().getString(R.string.registro_correcto),Toast.LENGTH_SHORT).show();
            idReporteRelacionado = id;
            setUpRegularMode();
        }
    }

    private void actualizar(){
        if(!validarReporte()||idReporteRelacionado==0){return;}
        Reporte reporte = getNewReporte();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues values = Reporte.getContentValues(reporte);
        db.update(Reporte.TABLE_NAME,values,"id=?",new String[]{String.valueOf(idReporteRelacionado)});
        Toast.makeText(getContext(),getResources().getString(R.string.actualizacion_correcta),Toast.LENGTH_SHORT).show();
    }

    private void eliminarReporte(){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        db.delete(Reporte.TABLE_NAME,"id=?",new String[]{String.valueOf(idReporteRelacionado)});
        Toast.makeText(getContext(),getResources().getString(R.string.registro_eliminado_correctamente),Toast.LENGTH_SHORT).show();
        setUpNewReportMode();
    }

    private void confirmarEliminarReporte() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getResources().getString(R.string.confirmar_eliminar_reporte));
        builder.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                eliminarReporte();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        AlertDialog confirmationDialog = builder.create();
        confirmationDialog.show();
    }

    private boolean validarReporte(){
        if(etReporteMañana.getText().toString().equals("") && etReporteTarde.getText().toString().equals("") && etReporteNoche.getText().toString().equals("")){
            Toast.makeText(getContext(),getResources().getString(R.string.reporte_no_valido),Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private Reporte getNewReporte(){
        Reporte reporte = new Reporte();
        reporte.setEstanciaId(idEstanciaRelacionada);
        reporte.setFecha(tvDate.getText().toString());
        reporte.setReporteMañana(etReporteMañana.getText().toString());
        reporte.setReporteTarde(etReporteTarde.getText().toString());
        reporte.setReporteNoche(etReporteNoche.getText().toString());
        return reporte;
    }

    private void showDatePicker(){
        String tv_str = tvDate.getText().toString();
        int year = Integer.parseInt(tv_str.substring(6));
        int month = Integer.parseInt(tv_str.substring(3,5)) - 1;
        int day = Integer.parseInt(tv_str.substring(0,2));

        new DatePickerDialog(getContext(),new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //tvDate.setText(DateHandler.formatDateToShow(day,month+1,year));
                showDateIfValid(DateHandler.formatDateToShow(day,month+1,year));
                checkReportsForNewDate();
            }
        },year,month,day).show();
    }

    private void checkReportsForNewDate(){
        String fechaBD = DateHandler.formatDateToStoreInDB(tvDate.getText().toString());
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+Reporte.TABLE_NAME+" WHERE "+Reporte.CAMPO_FECHA+" = '"+fechaBD+"' AND "+Reporte.CAMPO_ESTANCIA_ID+" = " +
                ""+idEstanciaRelacionada,null);
        List<Reporte> listReportes = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Reporte reporte = new Reporte();
                reporte.setId(cursor.getInt(0));
                reporte.setEstanciaId(cursor.getInt(cursor.getColumnIndex(Reporte.CAMPO_ESTANCIA_ID)));
                reporte.setFecha(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_FECHA))));
                reporte.setReporteMañana(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_MAÑANA)));
                reporte.setReporteTarde(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_TARDE)));
                reporte.setReporteNoche(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_NOCHE)));
                listReportes.add(reporte);
            }while (cursor.moveToNext());
        }
        cursor.close();
        if(!listReportes.isEmpty()){
            if(listReportes.size()>1){
                Toast.makeText(getContext(),getResources().getString(R.string.varios_reportes_misma_fecha),Toast.LENGTH_SHORT).show();
            }
            showInfoFragment(listReportes.get(0));
            myCallBack.setCurrentStateReporteFragment(MyApp.STATE_REGULAR);
            setUpRegularMode();
        }else{
            myCallBack.setCurrentStateReporteFragment(MyApp.STATE_NEW);
            setUpNewReportMode();
        }
    }

    private void showInfoFragment(long reporteId){
        showInfoFragment(getReporteFromDB(reporteId));
        setUpRegularMode();
    }

    private void showInfoFragment(Reporte reporte){
        if(reporte==null){return;}
        idReporteRelacionado = reporte.getId();
        if(idEstanciaRelacionada!=reporte.getEstanciaId()) {
            idEstanciaRelacionada = reporte.getEstanciaId();
        }
        if(!tvDate.getText().toString().equals(reporte.getFecha())) {
            tvDate.setText(reporte.getFecha());
        }
        cleanET();
        etReporteMañana.setText(reporte.getReporteMañana());
        etReporteTarde.setText(reporte.getReporteTarde());
        etReporteNoche.setText(reporte.getReporteNoche());
    }

    private Reporte getReporteFromDB(long reporteId){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+Reporte.TABLE_NAME+" WHERE id=? AND "+Reporte.CAMPO_ESTANCIA_ID+"=?",
                new String[]{String.valueOf(reporteId),String.valueOf(idEstanciaRelacionada)});
        Reporte reporte = new Reporte();
        if(cursor.moveToFirst()){
            reporte.setId(cursor.getLong(0));
            reporte.setEstanciaId(cursor.getInt(cursor.getColumnIndex(Reporte.CAMPO_ESTANCIA_ID)));
            reporte.setFecha(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_FECHA))));
            reporte.setReporteMañana(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_MAÑANA)));
            reporte.setReporteTarde(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_TARDE)));
            reporte.setReporteNoche(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_NOCHE)));
        }
        cursor.close();
        return reporte;
    }

    private void setCurrentDate(){
        Calendar today = Calendar.getInstance();
        int day = today.get(Calendar.DAY_OF_MONTH);
        int month = today.get(Calendar.MONTH);
        int year = today.get(Calendar.YEAR);
        showDateIfValid(DateHandler.formatDateToShow(day,month+1,year));
        //tvDate.setText(DateHandler.formatDateToShow(day,month+1,year));
        checkReportsForNewDate();
    }

    private void showDateIfValid(String fecha){
        String fechaAnterior = tvDate.getText().toString();
        Estancia estancia = getEstanciaRelacionada();
        /*if(DateHandler.areDatesInOrder(estancia.getDesde(),fecha,DateHandler.FECHA_FORMATO_MOSTRAR)&&
                DateHandler.areDatesInOrder(fecha,estancia.getHasta(),DateHandler.FECHA_FORMATO_MOSTRAR)){
            tvDate.setText(fecha);
            return;
        }*/
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date desdeDate = sdf.parse(estancia.getDesde());
            Date hastaDate = sdf.parse(estancia.getHasta());
            Date newDate = sdf.parse(fecha);
            if(newDate.after(desdeDate) && newDate.before(hastaDate) || newDate.equals(desdeDate) || newDate.equals(hastaDate)){
                tvDate.setText(fecha);
                return;
            }
        }catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(getContext(),R.string.la_fecha_no_es_correcta,Toast.LENGTH_SHORT).show();
        if(!fechaAnterior.equals("")){tvDate.setText(fechaAnterior);}
        else {tvDate.setText(estancia.getDesde());}
    }

    private Estancia getEstanciaRelacionada(){
        Estancia estancia = new Estancia();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+Estancia.CAMPO_DESDE+","+Estancia.CAMPO_HASTA+" FROM "+Estancia.TABLE_NAME+" WHERE id = "+idEstanciaRelacionada,null);
        if(cursor.moveToFirst()){
            estancia.setId(idEstanciaRelacionada);
            estancia.setDesde(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_DESDE))));
            estancia.setHasta(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_HASTA))));
        }
        cursor.close();
        return estancia;
    }

    private void resetFragment(){
        idReporteRelacionado = 0;
        cleanET();
    }

    private void cleanET(){
        etReporteMañana.setText("");
        etReporteTarde.setText("");
        etReporteNoche.setText("");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu!=null){menu.clear();}
        switch (myCallBack.getCurrentStateReporteFragment()){
            case MyApp.STATE_REGULAR:
                inflater.inflate(R.menu.menu_reporte_fragment_regular,menu);
                break;
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_eliminar_reporte:
                confirmarEliminarReporte();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface CallBack{
        int getCurrentStateReporteFragment();
        void setCurrentStateReporteFragment(int state);
    }
}
