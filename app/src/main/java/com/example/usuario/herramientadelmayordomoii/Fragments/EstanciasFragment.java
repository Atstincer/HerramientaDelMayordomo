package com.example.usuario.herramientadelmayordomoii.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomoii.Adapters.EstanciasRVAdapter;
import com.example.usuario.herramientadelmayordomoii.BD_conexion.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomoii.Entities.Cliente;
import com.example.usuario.herramientadelmayordomoii.Entities.Estancia;
import com.example.usuario.herramientadelmayordomoii.Entities.Estancias_Clientes;
import com.example.usuario.herramientadelmayordomoii.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by usuario on 5/11/2021.
 */

public class EstanciasFragment extends Fragment implements EstanciasRVAdapter.CallBack {

    public static final String TAG = "EstanciasFragment";
    public static final String STATE_EN_CASA = "STATE_EN_CASA";
    public static final String STATE_SEGUN_PERIODO = "STATE_SEGUN_PERIODO";
    public static final String STATE_SEGUN_CLIENTE = "STATE_SEGUN_CLIENTE";
    public static final String STATE_SEGUN_HAB = "STATE_SEGUN_HAB";


    private RecyclerView rvEstancias;
    private TextView tvNoEstanciasRegistradas, desde, hasta;
    private LinearLayout layoutBuscarEstanciaPeriodo, layoutBuscarEstanciaHabitacion;
    private RelativeLayout layoutBuscarEstanciaCliente;
    private AutoCompleteTextView actvClientes;
    private EditText etNoHab;


    private List<Estancia> listEstancias;

    private EstanciasRVAdapter adapter;
    private CallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_estancias,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvEstancias = (RecyclerView)view.findViewById(R.id.rv_estancias);
        tvNoEstanciasRegistradas = (TextView)view.findViewById(R.id.tv_no_estancias_registradas);
        desde = (TextView)view.findViewById(R.id.tv_desde_estancias);
        desde.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                handleDates(desde);
            }
        });
        hasta = (TextView)view.findViewById(R.id.tv_hasta_estancias);
        hasta.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                handleDates(hasta);
            }
        });
        layoutBuscarEstanciaPeriodo = (LinearLayout)view.findViewById(R.id.layout_buscar_estancia_periodo);
        myCallBack.udActivity(EstanciasFragment.TAG);
        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab_estancias);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                myCallBack.setUpNewEstanciaFragment();
            }
        });
        layoutBuscarEstanciaCliente = (RelativeLayout)view.findViewById(R.id.layout_buscar_estancia_cliente);
        layoutBuscarEstanciaHabitacion = (LinearLayout)view.findViewById(R.id.layout_buscar_estancia_hab);
        actvClientes = (AutoCompleteTextView)view.findViewById(R.id.actv_clientes);
        actvClientes.setAdapter(new ArrayAdapter<Cliente>(getContext(),R.layout.my_simple_dropdown_item_1line,getListClientesFromDB()));
        actvClientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cliente c = (Cliente)adapterView.getItemAtPosition(position);
                getEstanciasSegunCliente(c.getId());
                showEstanciasIfExist();
            }
        });
        etNoHab = (EditText)view.findViewById(R.id.et_no_hab_estancias_fragment);
        Button btnIr = (Button)view.findViewById(R.id.btn_ir);
        btnIr.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "Btn clicked", Toast.LENGTH_SHORT).show();
                getEstanciasSegunHab();
                showEstanciasIfExist();
            }
        });
        adapter = new EstanciasRVAdapter(getContext(),this);
        getEstanciasEnCasa();
        setUpRV();
        showEstanciasIfExist();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myCallBack = (CallBack)context;
    }

    private List getListClientesFromDB(){
        List<Cliente> listClientes = new ArrayList<>();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor cursor = bd.rawQuery("SELECT * FROM " + Cliente.TABLE_NAME,null);
        if(cursor.moveToFirst()){
            do{
                Cliente c = new Cliente();
                c.setId(cursor.getInt(0));
                c.setName(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_NAME)));
                c.setOrigenPais(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_ORIGEN_PAIS)));
                c.setOrigenCiudad(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_ORIGEN_CIUDAD)));
                c.setDob(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_DOB)));
                c.setPass(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_PASS)));
                c.setFoto(cursor.getBlob(cursor.getColumnIndex(Cliente.CAMPO_FOTO)));
                listClientes.add(c);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return listClientes;
    }

    private void getEstanciasEnCasa(){
        emptyListEstancias();
        getEstanciasFromDB("SELECT * FROM "+Estancia.TABLE_NAME+" WHERE DATE('now') >= "+Estancia.CAMPO_DESDE+" AND DATE('now') <= "+Estancia.CAMPO_HASTA);
        if(listEstancias!=null&&listEstancias.size()>0){Collections.sort(listEstancias,Estancia.dateAscending);}
    }

    private void getEstanciasSegunHab(){
        emptyListEstancias();
        if(etNoHab.getText().toString().equals("")){return;}
        getEstanciasFromDB("SELECT * FROM " + Estancia.TABLE_NAME + " WHERE " + Estancia.CAMPO_NO_HAB + " = " + etNoHab.getText().toString());
        if(listEstancias!=null&&listEstancias.size()>0){Collections.sort(listEstancias,Estancia.dateAscending);}
    }

    private void getEstanciasSegunCliente(int idCliente){
        emptyListEstancias();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor cursor = bd.rawQuery("SELECT " + Estancias_Clientes.CAMPO_ESTANCIA_ID + " FROM " + Estancias_Clientes.TABLE_NAME + " WHERE " +
                Estancias_Clientes.CAMPO_CLIENTE_ID + " = " + idCliente,null);
        List<Integer> estanciasIdList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                estanciasIdList.add(cursor.getInt(0));
            }while (cursor.moveToNext());
        }
        cursor.close();

        if(estanciasIdList.size()==0){return;}
        for(Integer id:estanciasIdList){
            getEstanciasFromDB("SELECT * FROM "+Estancia.TABLE_NAME+" WHERE id = " + id);
        }
        if(listEstancias!=null&&listEstancias.size()>0){Collections.sort(listEstancias,Estancia.dateAscending);}
    }

    private void getEstanciasFromDB(String query){
        //emptyListEstancias();
        if(query.equals("")){return;}
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor cursor = bd.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do{
                Estancia e = new Estancia();
                e.setId(cursor.getInt(0));
                e.setNo_hab(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_NO_HAB)));
                e.setFamilyName(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_FAMILY_NAME)));
                e.setDesde(formatDateToShow(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_DESDE))));
                e.setHasta(formatDateToShow(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_HASTA))));
                e.setAdultos(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_ADULTOS)));
                e.setMenores(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_MENORES)));
                e.setInfantes(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_INFANTES)));
                listEstancias.add(e);
            }while(cursor.moveToNext());
        }
        cursor.close();
    }

    private void getEstanciasFromDB(String desde, String hasta){
        emptyListEstancias();

        if(this.desde.getText().toString().equals(getResources().getString(R.string.desde)) ||
                this.hasta.getText().toString().equals(getResources().getString(R.string.hasta))){
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat sdfBD = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar desdeCalendar = Calendar.getInstance();
        Calendar hastaCalendar = Calendar.getInstance();

        try {
            desdeCalendar.setTime(sdf.parse(desde));
            hastaCalendar.setTime(sdf.parse(hasta));
        }catch(ParseException e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            return;
        }

        if(desdeCalendar.after(hastaCalendar)){
            return;
        }

        long cantDias = (hastaCalendar.getTime().getTime() - desdeCalendar.getTime().getTime())/(24*60*60*1000);

        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();

        for(int i=0; i<=cantDias; i++){
            Cursor cursor = bd.rawQuery("SELECT * FROM "+Estancia.TABLE_NAME+" WHERE DATE('"+ sdfBD.format(desdeCalendar.getTime()) +"') >= DATE("+Estancia.CAMPO_DESDE+") " +
                    "AND DATE('" + sdfBD.format(desdeCalendar.getTime()) +"') <= DATE("+Estancia.CAMPO_HASTA + ")",null);

            if(cursor.moveToFirst()){
                do{
                    Estancia e = new Estancia();
                    e.setId(cursor.getInt(0));
                    e.setNo_hab(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_NO_HAB)));
                    e.setFamilyName(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_FAMILY_NAME)));
                    e.setDesde(formatDateToShow(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_DESDE))));
                    e.setHasta(formatDateToShow(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_HASTA))));
                    e.setAdultos(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_ADULTOS)));
                    e.setMenores(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_MENORES)));
                    e.setInfantes(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_INFANTES)));
                    boolean found = false;
                    for(Estancia f: listEstancias){
                        if(f.getId()==e.getId()){
                            found = true;
                        }
                    }
                    if(!found){listEstancias.add(e);}
                }while(cursor.moveToNext());
            }
            cursor.close();
            desdeCalendar.add(Calendar.DATE,1);
        }
    }

    @Override
    public void itemClicked(int position) {
        myCallBack.setUpEstanciaFragment(listEstancias.get(position).getId());
    }

    private void handleDates(final TextView tv) {
        String currentDate = tv.getText().toString();
        int year = 0;
        int month = 0;
        int day = 0;
        if (currentDate.equals("") || currentDate.equals(getResources().getString(R.string.desde)) || currentDate.equals(getResources().getString(R.string.hasta))) {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            day = Integer.parseInt(currentDate.substring(0,2));
            month = Integer.parseInt(currentDate.substring(3,5))-1;
            year = Integer.parseInt(currentDate.substring(6));
        }

        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                String month_str = toDosLugares(m+1);
                String day_str = toDosLugares(d);

                String full_date = day_str + "/" + month_str + "/" + y;
                tv.setText(full_date);
                getEstanciasFromDB(desde.getText().toString(),hasta.getText().toString());
                showEstanciasIfExist();
            }
        }, year, month, day).show();
    }

    private String toDosLugares(int x) {
        String cad = String.valueOf(x);
        if (cad.length() == 1) {
            cad = "0" + x;
        }
        return cad;
    }

    private void showEstanciasIfExist(){
        if(listEstancias.size()>0){
            udRV();
            showMsgNoEstancias(false);
        }else{
            showMsgNoEstancias(true);
        }
    }

    private void setUpRV(){
        rvEstancias.setAdapter(adapter);
        rvEstancias.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void udRV(){
        adapter.setListEstancias(listEstancias);
    }

    private void showMsgNoEstancias(boolean show){
        if(show){
            rvEstancias.setVisibility(View.GONE);
            tvNoEstanciasRegistradas.setVisibility(View.VISIBLE);
        }else{
            rvEstancias.setVisibility(View.VISIBLE);
            tvNoEstanciasRegistradas.setVisibility(View.GONE);
        }
    }

    private String formatDateToShow(String date){
        return date.substring(8)+"/"+date.substring(5,7)+"/"+date.substring(0,4);
    }

    private void showLayout(String tag){
        hideAllLayouts();
        switch(tag){
            case EstanciasFragment.STATE_SEGUN_PERIODO:
                layoutBuscarEstanciaPeriodo.setVisibility(View.VISIBLE);
                break;
            case EstanciasFragment.STATE_SEGUN_CLIENTE:
                layoutBuscarEstanciaCliente.setVisibility(View.VISIBLE);
                break;
            case EstanciasFragment.STATE_SEGUN_HAB:
                layoutBuscarEstanciaHabitacion.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void hideAllLayouts(){
        desde.setText(getResources().getString(R.string.desde));
        hasta.setText(getResources().getString(R.string.hasta));
        actvClientes.setText("");
        etNoHab.setText("");
        layoutBuscarEstanciaPeriodo.setVisibility(View.GONE);
        layoutBuscarEstanciaCliente.setVisibility(View.GONE);
        layoutBuscarEstanciaHabitacion.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu!=null){
            menu.clear();
        }
        inflater.inflate(R.menu.menu_estancias,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_nuevaEstancia:
                myCallBack.setUpNewEstanciaFragment();
                break;
            case R.id.menu_item_en_casa:
                myCallBack.udActivity(EstanciasFragment.STATE_EN_CASA);
                showLayout(EstanciasFragment.STATE_EN_CASA);
                getEstanciasEnCasa();
                showEstanciasIfExist();
                break;
            case R.id.menu_item_segun_periodo:
                myCallBack.udActivity(EstanciasFragment.STATE_SEGUN_PERIODO);
                showLayout(EstanciasFragment.STATE_SEGUN_PERIODO);
                getEstanciasFromDB(desde.getText().toString(),hasta.getText().toString());
                showEstanciasIfExist();
                break;
            case R.id.menu_item_segun_cliente:
                myCallBack.udActivity(EstanciasFragment.STATE_SEGUN_CLIENTE);
                showLayout(EstanciasFragment.STATE_SEGUN_CLIENTE);
                emptyListEstancias();
                showEstanciasIfExist();
                break;
            case R.id.menu_item_segun_hab:
                myCallBack.udActivity(EstanciasFragment.STATE_SEGUN_HAB);
                showLayout(EstanciasFragment.STATE_SEGUN_HAB);
                getEstanciasSegunHab();
                showEstanciasIfExist();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void emptyListEstancias(){
        if(listEstancias==null){listEstancias = new ArrayList<>();}
        if(listEstancias.size()!=0){listEstancias.clear();}
    }

    public interface CallBack{
        void setUpNewEstanciaFragment();
        void setUpEstanciaFragment(int id);
        void udActivity(String tag);
    }
}
