package com.example.usuario.herramientadelmayordomo.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomo.Adapters.ReportesRVAdapter;
import com.example.usuario.herramientadelmayordomo.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomo.Almacenamiento.MySharedPreferences;
import com.example.usuario.herramientadelmayordomo.Entities.MyEmail;
import com.example.usuario.herramientadelmayordomo.Entities.Reporte;
import com.example.usuario.herramientadelmayordomo.R;
import com.example.usuario.herramientadelmayordomo.Util.DateHandler;
import com.example.usuario.herramientadelmayordomo.Util.MyApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by usuario on 20/12/2022.
 */

public class ReportesFragment extends Fragment implements ReportesRVAdapter.CallBack {

    public static final String TAG = "ReportesFragment";

    private RecyclerView rvReportes;
    private TextView tvFecha,tvNoReportesRegistrados;

    private CallBack myCallBack;
    private ReportesRVAdapter adapter;

    private List<Reporte> listaReportes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reportes,container,false);
        bindComponents(view);
        myCallBack.udActivity(TAG);
        /*System.out.println("****************************");
        System.out.println(savedInstanceState==null);
        if (savedInstanceState!=null){
            System.out.println(savedInstanceState.get("fecha"));
        }
        System.out.println("****************************");*/
        if(savedInstanceState!=null && savedInstanceState.get("fecha")!=null){tvFecha.setText(savedInstanceState.getString("fecha"));}
        //reLoadInfo();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        reLoadInfo();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            myCallBack = (CallBack)context;
        }catch (ClassCastException e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null && savedInstanceState.get("fecha")!=null){
            tvFecha.setText(savedInstanceState.getString("fecha"));
        }else {
            System.out.println("*****************************");
            System.out.println("Saveinstance: ");
            System.out.println(savedInstanceState==null);
            System.out.println("*****************************");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("fecha",tvFecha.getText().toString());
    }

    private void bindComponents(View view){
        tvFecha = (TextView)view.findViewById(R.id.tv_fecha_reportes_layout);
        tvNoReportesRegistrados = (TextView)view.findViewById(R.id.tv_no_reporte_registrado);
        rvReportes = (RecyclerView)view.findViewById(R.id.rv_reportes_layout);

        tvFecha.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DateHandler.showDatePicker(getContext(),tvFecha, new DateHandler.DatePickerCallBack(){
                    @Override
                    public void dateSelected() {
                        reLoadInfo();
                    }
                });
            }
        });

        adapter = new ReportesRVAdapter(getContext(),this,Reporte.LAYOUT_EN_REPORTES);
        rvReportes.setAdapter(adapter);
        rvReportes.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void reLoadInfo(){
        if(tvFecha.getText().toString().equals(getString(R.string.seleccionar_fecha))){tvFecha.setText(DateHandler.getToday(DateHandler.FECHA_FORMATO_MOSTRAR));}
        getReportesFromDB();
        showInfo();
    }

    private void getReportesFromDB(){
        if(listaReportes==null){listaReportes = new ArrayList<>();}
        else {listaReportes.clear();}
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        String fechaSeleccionada = DateHandler.formatDateToStoreInDB(tvFecha.getText().toString());
        Cursor cursor = db.rawQuery("SELECT * FROM "+Reporte.TABLE_NAME+" WHERE "+Reporte.CAMPO_FECHA+"=?",new String[]{fechaSeleccionada},null);
        if(cursor.moveToFirst()){
            do{
                Reporte reporte = Reporte.getReporte(cursor);
                listaReportes.add(reporte);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void udRVReportes(){
        adapter.setListReportes(listaReportes);
    }

    private void showInfo(){
        if(listaReportes.size()>0){
            tvNoReportesRegistrados.setVisibility(View.GONE);
            udRVReportes();
            rvReportes.setVisibility(View.VISIBLE);
        }else {
            rvReportes.setVisibility(View.GONE);
            tvNoReportesRegistrados.setVisibility(View.VISIBLE);
        }
    }

    private void setUpMail(){
        Map<String,String> mapInfoMail = Reporte.getInfoMail(getContext(),listaReportes);
        String asunto = "";
        if(mapInfoMail!=null && mapInfoMail.containsKey(MyEmail.CAMPO_ASUNTO_MAIL)){asunto = mapInfoMail.get(MyEmail.CAMPO_ASUNTO_MAIL);}
        String cuerpo = "";
        if(mapInfoMail!=null && mapInfoMail.containsKey(MyEmail.CAMPO_CUERPO_MAIL)){cuerpo = mapInfoMail.get(MyEmail.CAMPO_CUERPO_MAIL);}
        MyEmail.setUpEmail(getContext(),new MyEmail(new String[]{MySharedPreferences.getDefaultMail(getContext())},asunto,cuerpo));
    }

    @Override
    public void itemClicked(int position) {
        Reporte reporte = listaReportes.get(position);
        myCallBack.setUpReportFragment(reporte.getEstanciaId(),reporte.getId());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        if(menu!=null){menu.clear();}
        inflater.inflate(R.menu.menu_reportes_fragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_enviar_mail_reportes_fragment) {
            setUpMail();
        }
        return super.onOptionsItemSelected(item);
    }

    public interface CallBack{
        void udActivity(String tag);
        void setUpReportFragment(long estanciaId, long reporteId);
    }
}
