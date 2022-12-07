package com.example.usuario.herramientadelmayordomo.Fragments;

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
import android.support.v4.content.ContextCompat;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomo.Adapters.ClientesRVAdapter;
import com.example.usuario.herramientadelmayordomo.Adapters.ReportesRVAdapter;
import com.example.usuario.herramientadelmayordomo.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomo.DialogFragments.MyPickClienteDialogF;
import com.example.usuario.herramientadelmayordomo.Entities.Cliente;
import com.example.usuario.herramientadelmayordomo.Entities.Estancia;
import com.example.usuario.herramientadelmayordomo.Entities.Estancias_Clientes;
import com.example.usuario.herramientadelmayordomo.Entities.FamilyName;
import com.example.usuario.herramientadelmayordomo.Entities.FamilyNames_Clientes;
import com.example.usuario.herramientadelmayordomo.Entities.MyEmail;
import com.example.usuario.herramientadelmayordomo.Entities.Recordatorio;
import com.example.usuario.herramientadelmayordomo.Almacenamiento.MySharedPreferences;
import com.example.usuario.herramientadelmayordomo.Entities.Reporte;
import com.example.usuario.herramientadelmayordomo.Interfaces.IMyFragments;
import com.example.usuario.herramientadelmayordomo.R;
import com.example.usuario.herramientadelmayordomo.Util.DateHandler;
import com.example.usuario.herramientadelmayordomo.Util.MyApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by usuario on 6/11/2021.
 */

public class EstanciaFragment extends Fragment implements MyPickClienteDialogF.Callback, IMyFragments {

    public static final String TAG = "EstanciaFragment";
    /*
    public static final String STATE_REGULAR_MODE = "STATE_REGULAR_MODE";
    public static final String STATE_UPDATE_MODE = "STATE_UPDATE_MODE";
    public static final String STATE_NEW_ESTANCIA_MODE = "STATE_NEW_ESTANCIA_MODE";*/

    private EditText etNoHab, etAdultos, etMenores, etInfantes, etObservaciones;
    private TextView tvNoNoches,tvDesde, tvHasta, tvNoClientesAsociados, tvReportes, tvAddReporte;
    private AutoCompleteTextView actvFamilyName;
    private RecyclerView rvClientesAsociados;
    private Button btnMain;
    private RecyclerView rvReportes;
    private RelativeLayout layoutReportes;

    private List<FamilyName> listFamilyNamesBD;
    private List<Cliente> listClientesRelacionados;
    private List<Reporte> listReportesRelacionados;
    private Estancia selectedEstancia;

    private Callback myCallBack;
    ArrayAdapter<FamilyName> actvAdapter;
    ReportesRVAdapter reportesRVAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_estancia, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myCallBack = (Callback) context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindComponents(view);
        switch (myCallBack.getCurrentStateEstanciaFragment()) {
            case MyApp.STATE_REGULAR:
                setUpRegularMode();
                break;
            case MyApp.STATE_NEW:
                setUpNewEstanciaMode();
                break;
            case MyApp.STATE_UPDATE:
                setUpUDMode();
                break;
            default:
                break;
        }
    }

    private void bindComponents(View v) {
        etNoHab = (EditText) v.findViewById(R.id.et_no_hab);
        etAdultos = (EditText) v.findViewById(R.id.et_adultos);
        etMenores = (EditText) v.findViewById(R.id.et_menores);
        etInfantes = (EditText) v.findViewById(R.id.et_infantes);
        tvNoNoches = (TextView) v.findViewById(R.id.tv_no_noches);
        tvDesde = (TextView) v.findViewById(R.id.tv_desde);
        tvHasta = (TextView) v.findViewById(R.id.tv_hasta);
        tvNoClientesAsociados = (TextView) v.findViewById(R.id.tv_no_clientes_asosiados);
        tvReportes = (TextView) v.findViewById(R.id.tv_reportes);
        tvAddReporte = (TextView)v.findViewById(R.id.tv_add_reporte);
        actvFamilyName = (AutoCompleteTextView) v.findViewById(R.id.actv_family_name);
        rvClientesAsociados = (RecyclerView) v.findViewById(R.id.rv_clientes);
        btnMain = (Button) v.findViewById(R.id.btn_estancia);
        rvReportes = (RecyclerView) v.findViewById(R.id.rv_reportes);
        layoutReportes = (RelativeLayout) v.findViewById(R.id.layout_reportes);
        etObservaciones = (EditText) v.findViewById(R.id.et_obs_estancia);

        tvReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutReportes.getVisibility() == View.VISIBLE) {
                    layoutReportes.setVisibility(View.GONE);
                } else {
                    layoutReportes.setVisibility(View.VISIBLE);
                }
                if (listReportesRelacionados == null || listReportesRelacionados.size() <= 0) {
                    Toast.makeText(getContext(), R.string.no_existen_reportes, Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvAddReporte.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (myCallBack.getCurrentStateEstanciaFragment() != MyApp.STATE_NEW) {
                    myCallBack.setUpReportFragment(selectedEstancia.getId(), 0);
                }
            }
        });

        tvDesde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDatePickerDialog(tvDesde);
            }
        });

        tvHasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDatePickerDialog(tvHasta);
            }
        });

        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (myCallBack.getCurrentStateEstanciaFragment()) {
                    case MyApp.STATE_NEW:
                        registrarEstancia();
                        break;
                    case MyApp.STATE_UPDATE:
                        actualizarEstancia();
                        break;
                    default:
                        break;
                }
            }
        });

        actvFamilyName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (myCallBack.getCurrentStateEstanciaFragment() == MyApp.STATE_REGULAR) {
                    return;
                }
                FamilyName familyName = (FamilyName) adapterView.getItemAtPosition(position);
                if (familyName.getId() == 0) {
                    for (Cliente c : familyName.getClientesMiembros()) {
                        c.setChecked(true);
                        setUpClearListClientesRelacionados();
                        listClientesRelacionados.add(c);
                    }
                } else {
                    getRelatedClientsFromDB(familyName.getId(), FamilyNames_Clientes.TABLE_NAME);
                }
                showRelatedClientsIfExist();
            }
        });

        reportesRVAdapter = new ReportesRVAdapter(getContext(), new ReportesRVAdapter.CallBack() {
            @Override
            public void itemClicked(int position) {
                //System.out.println("Item clicked: "+position);
                myCallBack.setUpReportFragment(selectedEstancia.getId(), listReportesRelacionados.get(position).getId());
            }
        });
        rvReportes.setAdapter(reportesRVAdapter);
        rvReportes.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setUpRegularMode() {
        myCallBack.udActivity(EstanciaFragment.TAG);
        if (getArguments() != null) {
            getSelectedEstanciaFromDB((long) getArguments().get("id"));
        }
        if (selectedEstancia != null && selectedEstancia.getId() > 0) {
            getRelatedClientsFromDB(selectedEstancia.getId(), Estancias_Clientes.TABLE_NAME);
        }
        getRelatedReportsFromDB();
        showSelectedEstancia();
        //showRelatedClientsIfExist();
        getActivity().invalidateOptionsMenu();
        showLayoutReportes(true);
        setViewsEditable(false);
        showBtns(false);
        actvFamilyName.dismissDropDown();
    }

    private void setUpNewEstanciaMode() {
        setViewsEditable(true);
        showLayoutReportes(false);
        showHowManyNights();
        showBtns(true);
        setUpACTV();
        showMsgNoClientesAsociados(true);
        myCallBack.udActivity(EstanciaFragment.TAG);
    }

    private void setUpUDMode() {
        myCallBack.setNewCurrentStateEstanciaFragment(MyApp.STATE_UPDATE);
        myCallBack.udActivity(EstanciaFragment.TAG);
        getActivity().invalidateOptionsMenu();
        setUpACTV();
        showLayoutReportes(false);
        setViewsEditable(true);
        showBtns(true);
    }

    @Override
    public void setUpNewState(int state) {
        switch (state) {
            case MyApp.STATE_UPDATE:
                setUpUDMode();
                break;
            case MyApp.STATE_NEW:
                setUpNewEstanciaMode();
                break;
            case MyApp.STATE_REGULAR:
                setUpRegularMode();
                break;
        }
    }

    private void getSelectedEstanciaFromDB(long id) {
        if (selectedEstancia == null) {
            selectedEstancia = new Estancia();
        }
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor cursor = bd.rawQuery("SELECT * FROM " + Estancia.TABLE_NAME + " WHERE id = " + id, null);
        if (cursor.moveToFirst()) {
            selectedEstancia.setId(id);
            selectedEstancia.setFamilyName(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_FAMILY_NAME)));
            selectedEstancia.setNo_hab(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_NO_HAB)));
            selectedEstancia.setDesde(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_DESDE))));
            selectedEstancia.setHasta(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_HASTA))));
            selectedEstancia.setAdultos(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_ADULTOS)));
            selectedEstancia.setMenores(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_MENORES)));
            selectedEstancia.setInfantes(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_INFANTES)));
            selectedEstancia.setObservaciones(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_OBSERVACIONES)));
        }
        cursor.close();
    }

    private void registrarEstancia() {
        if (!validarInfo()) {
            return;
        }
        Estancia newEstancia = getInfoEstancia();

        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Estancia.CAMPO_NO_HAB, newEstancia.getNo_hab());
        values.put(Estancia.CAMPO_DESDE, DateHandler.formatDateToStoreInDB(newEstancia.getDesde()));
        values.put(Estancia.CAMPO_HASTA, DateHandler.formatDateToStoreInDB(newEstancia.getHasta()));
        values.put(Estancia.CAMPO_FAMILY_NAME, newEstancia.getFamilyName());
        values.put(Estancia.CAMPO_ADULTOS, newEstancia.getAdultos());
        values.put(Estancia.CAMPO_MENORES, newEstancia.getMenores());
        values.put(Estancia.CAMPO_INFANTES, newEstancia.getInfantes());
        values.put(Estancia.CAMPO_OBSERVACIONES, newEstancia.getObservaciones());

        long newEstanciaId = bd.insert(Estancia.TABLE_NAME, null, values);
        newEstancia.setId(newEstanciaId);

        if (newEstanciaId != 0 && listClientesRelacionados != null && listClientesRelacionados.size() != 0) {
            for (Cliente c : listClientesRelacionados) {
                bd.execSQL("INSERT INTO " + Estancias_Clientes.TABLE_NAME + "(" + Estancias_Clientes.CAMPO_ESTANCIA_ID + "," + Estancias_Clientes.CAMPO_CLIENTE_ID +
                        ") VALUES(" + newEstanciaId + "," + c.getId() + ")");
            }
        }

        makeToast(getResources().getString(R.string.registro_correcto));
        clear();
        setUpRecordatorio(newEstancia);
    }

    private void actualizarEstancia() {
        if (!validarInfo()) {
            return;
        }
        Estancia estanciaNewInfo = getInfoEstancia();

        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Estancia.CAMPO_NO_HAB, estanciaNewInfo.getNo_hab());
        values.put(Estancia.CAMPO_DESDE, DateHandler.formatDateToStoreInDB(estanciaNewInfo.getDesde()));
        values.put(Estancia.CAMPO_HASTA, DateHandler.formatDateToStoreInDB(estanciaNewInfo.getHasta()));
        values.put(Estancia.CAMPO_FAMILY_NAME, estanciaNewInfo.getFamilyName());
        values.put(Estancia.CAMPO_ADULTOS, estanciaNewInfo.getAdultos());
        values.put(Estancia.CAMPO_MENORES, estanciaNewInfo.getMenores());
        values.put(Estancia.CAMPO_INFANTES, estanciaNewInfo.getInfantes());
        values.put(Estancia.CAMPO_OBSERVACIONES, estanciaNewInfo.getObservaciones());

        bd.update(Estancia.TABLE_NAME, values, "id=?", new String[]{String.valueOf(selectedEstancia.getId())});
        Toast.makeText(getContext(), getResources().getString(R.string.actualizacion_correcta), Toast.LENGTH_SHORT).show();

        bd.execSQL("DELETE FROM " + Estancias_Clientes.TABLE_NAME + " WHERE " + Estancias_Clientes.CAMPO_ESTANCIA_ID + " = " + selectedEstancia.getId());

        if (listClientesRelacionados == null || listClientesRelacionados.isEmpty()) {
            return;
        }
        for (Cliente c : listClientesRelacionados) {
            bd.execSQL("INSERT INTO " + Estancias_Clientes.TABLE_NAME + "(" + Estancias_Clientes.CAMPO_ESTANCIA_ID + ", " + Estancias_Clientes.CAMPO_CLIENTE_ID +
                    ") VALUES (" + selectedEstancia.getId() + ", " + c.getId() + ")");
        }
    }

    private void eliminarEstancia() {
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase BD = admin.getWritableDatabase();
        BD.execSQL("DELETE FROM " + Estancia.TABLE_NAME + " WHERE id = " + selectedEstancia.getId());
        //BD.execSQL("DELETE FROM " + Estancias_Clientes.TABLE_NAME + " WHERE " + Estancias_Clientes.CAMPO_ESTANCIA_ID + " = " + selectedEstancia.getId());
        makeToast(getResources().getString(R.string.registro_eliminado_correctamente));
        getActivity().onBackPressed();
    }

    private void confirmarEliminarEstancia(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.confirmar_eliminar_estancia));
        builder.setPositiveButton(getString(R.string.btn_ok),new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                eliminarEstancia();
            }
        });
        builder.setNegativeButton(getString(R.string.btn_cancel),new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean validarInfo() {
        /*if(etNoHab.getText().toString().equals("")){
            makeToast(getResources().getString(R.string.debe_introducir_numero_de_hab));
            return false;
        }*/
        if (!isHastaAfterDesde(tvDesde, false)) {
            makeToast(getResources().getString(R.string.la_fecha_no_es_correcta));
            return false;
        }
        if (actvFamilyName.getText().toString().equals("")) {
            makeToast(getResources().getString(R.string.debe_introducir_a_nombre_de_quien));
            return false;
        }
        if (etAdultos.getText().toString().equals("")) {
            makeToast(getResources().getString(R.string.debe_introducir_numero_de_adultos));
            return false;
        }
        return true;
    }

    private Estancia getInfoEstancia() {
        Estancia newEstancia = new Estancia();
        newEstancia.setNo_hab(etNoHab.getText().toString());
        newEstancia.setDesde(tvDesde.getText().toString());
        newEstancia.setHasta(tvHasta.getText().toString());
        newEstancia.setFamilyName(actvFamilyName.getText().toString());
        newEstancia.setAdultos(Integer.parseInt(etAdultos.getText().toString()));
        if (!etMenores.getText().toString().equals("")) {
            newEstancia.setMenores(Integer.parseInt(etMenores.getText().toString()));
        }
        if (!etInfantes.getText().toString().equals("")) {
            newEstancia.setInfantes(Integer.parseInt(etInfantes.getText().toString()));
        }
        newEstancia.setObservaciones(etObservaciones.getText().toString());
        return newEstancia;
    }

    private void clear() {
        etNoHab.setText("");
        tvDesde.setText(getResources().getString(R.string.seleccionar_fecha));
        tvDesde.setTextColor(ContextCompat.getColor(getContext(), R.color.link));
        tvHasta.setText(getResources().getString(R.string.seleccionar_fecha));
        tvHasta.setTextColor(ContextCompat.getColor(getContext(), R.color.link));
        actvFamilyName.setText("");
        etAdultos.setText("");
        etMenores.setText("");
        etInfantes.setText("");
        etObservaciones.setText("");
        if(listClientesRelacionados!=null){listClientesRelacionados.clear();}
        tvNoNoches.setText(getString(R.string.noches));
        tvNoNoches.setVisibility(View.GONE);
        showRelatedClientsIfExist();
    }

    private void setUpACTV() {
        if (listFamilyNamesBD == null) {
            listFamilyNamesBD = new ArrayList<>();
        } else {
            listFamilyNamesBD.clear();
        }
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor cursor = bd.rawQuery("SELECT * FROM " + FamilyName.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                FamilyName familyName = new FamilyName();
                familyName.setId(cursor.getInt(0));
                familyName.setFamilyName(cursor.getString(cursor.getColumnIndex(FamilyName.CAMPO_FAMILY_NAME)));
                listFamilyNamesBD.add(familyName);
            } while (cursor.moveToNext());
        }

        cursor.close();

        Cursor cursorII = bd.rawQuery("SELECT * FROM " + Cliente.TABLE_NAME, null);

        if (cursorII.moveToFirst()) {
            do {
                Cliente cliente = new Cliente();
                cliente.setId(cursorII.getInt(0));
                cliente.setName(cursorII.getString(cursorII.getColumnIndex(Cliente.CAMPO_NAME)));
                cliente.setPass(cursorII.getString(cursorII.getColumnIndex(Cliente.CAMPO_PASS)));
                cliente.setOrigenPais(cursorII.getString(cursorII.getColumnIndex(Cliente.CAMPO_ORIGEN_PAIS)));
                cliente.setOrigenCiudad(cursorII.getString(cursorII.getColumnIndex(Cliente.CAMPO_ORIGEN_CIUDAD)));
                cliente.setDob(cursorII.getString(cursorII.getColumnIndex(Cliente.CAMPO_DOB)));
                cliente.setFoto(cursorII.getBlob(cursorII.getColumnIndex(Cliente.CAMPO_FOTO)));
                cliente.setPreferencias(cursorII.getString(cursorII.getColumnIndex(Cliente.CAMPO_PREFERENCIAS)));
                cliente.setLimitaciones(cursorII.getString(cursorII.getColumnIndex(Cliente.CAMPO_LIMITACIONES)));
                cliente.setObservaciones(cursorII.getString(cursorII.getColumnIndex(Cliente.CAMPO_OBSERVACIONES)));

                FamilyName familyName = new FamilyName();
                familyName.setFamilyName(cliente.getName() + " x 1");
                familyName.addClienteMiembro(cliente);
                listFamilyNamesBD.add(familyName);
            } while (cursorII.moveToNext());
        }
        cursorII.close();

        actvAdapter = new ArrayAdapter<FamilyName>(getContext(), R.layout.my_simple_dropdown_item_1line, listFamilyNamesBD);
        actvFamilyName.setAdapter(actvAdapter);
    }

    private void getRelatedClientsFromDB(long id, String tabla) {
        setUpClearListClientesRelacionados();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        String query = "";
        switch (tabla) {
            case FamilyNames_Clientes.TABLE_NAME:
                query = "SELECT " + FamilyNames_Clientes.CAMPO_CLIENTE_ID + " FROM " + FamilyNames_Clientes.TABLE_NAME + " WHERE " +
                        FamilyNames_Clientes.CAMPO_FAMILY_NAME_ID + " = " + id;
                break;
            case Estancias_Clientes.TABLE_NAME:
                query = "SELECT " + Estancias_Clientes.CAMPO_CLIENTE_ID + " FROM " + Estancias_Clientes.TABLE_NAME + " WHERE " +
                        Estancias_Clientes.CAMPO_ESTANCIA_ID + " = " + id;
        }
        Cursor cursor = bd.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int clientId = cursor.getInt(0);
                Cursor cursorII = bd.rawQuery("SELECT * FROM " + Cliente.TABLE_NAME + " WHERE id=" + clientId, null);
                if (cursorII.moveToFirst()) {
                    do {
                        Cliente c = new Cliente();
                        c.setId(cursorII.getInt(0));
                        c.setName(cursorII.getString(cursorII.getColumnIndex(Cliente.CAMPO_NAME)));
                        c.setPass(cursorII.getString(cursorII.getColumnIndex(Cliente.CAMPO_PASS)));
                        c.setOrigenPais(cursorII.getString(cursorII.getColumnIndex(Cliente.CAMPO_ORIGEN_PAIS)));
                        c.setOrigenCiudad(cursorII.getString(cursorII.getColumnIndex(Cliente.CAMPO_ORIGEN_CIUDAD)));
                        c.setDob(cursorII.getString(cursorII.getColumnIndex(Cliente.CAMPO_DOB)));
                        c.setFoto(cursorII.getBlob(cursorII.getColumnIndex(Cliente.CAMPO_FOTO)));
                        c.setPreferencias(cursorII.getString(cursorII.getColumnIndex(Cliente.CAMPO_PREFERENCIAS)));
                        c.setLimitaciones(cursorII.getString(cursorII.getColumnIndex(Cliente.CAMPO_LIMITACIONES)));
                        c.setObservaciones(cursorII.getString(cursorII.getColumnIndex(Cliente.CAMPO_OBSERVACIONES)));
                        c.setChecked(true);
                        listClientesRelacionados.add(c);
                    } while (cursorII.moveToNext());
                }
                cursorII.close();
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void getRelatedReportsFromDB() {
        if (selectedEstancia == null || selectedEstancia.getId() <= 0) {
            return;
        }
        getRelatedReportsFromDB(selectedEstancia.getId());
    }

    private void getRelatedReportsFromDB(long estanciaId) {
        if (listReportesRelacionados == null) {
            listReportesRelacionados = new ArrayList<>();
        } else {
            listReportesRelacionados.clear();
        }
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Reporte.TABLE_NAME + " WHERE " + Reporte.CAMPO_ESTANCIA_ID + " = " + estanciaId, null);
        if (cursor.moveToFirst()) {
            do {
                Reporte reporte = new Reporte();
                reporte.setId(cursor.getInt(0));
                reporte.setEstanciaId(cursor.getInt(cursor.getColumnIndex(Reporte.CAMPO_ESTANCIA_ID)));
                reporte.setFecha(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_FECHA))));
                reporte.setReporteMañana(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_MAÑANA)));
                reporte.setReporteTarde(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_TARDE)));
                reporte.setReporteNoche(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_NOCHE)));
                listReportesRelacionados.add(reporte);
            } while (cursor.moveToNext());
        }
        cursor.close();
        if (listReportesRelacionados.size() > 0) {
            Collections.sort(listReportesRelacionados, Reporte.dateAscending);
        }
    }

    private void showRelatedClientsIfExist() {
        if (listClientesRelacionados == null || listClientesRelacionados.size() == 0) {
            showMsgNoClientesAsociados(true);
            return;
        }
        showMsgNoClientesAsociados(false);
        List<Cliente> selectedClients = new ArrayList<>();
        for (Cliente c : listClientesRelacionados) {
            if (c.isChecked()) {
                selectedClients.add(c);
            }
        }
        ClientesRVAdapter adapter = new ClientesRVAdapter(selectedClients, new ClientesRVAdapter.Callback() {
            @Override
            public void onItemClicked(int position) {
                if (myCallBack.getCurrentStateEstanciaFragment() == MyApp.STATE_REGULAR) {
                    return;
                }
                showDialogPicker();
            }
        }, false, false);
        rvClientesAsociados.setAdapter(adapter);
        rvClientesAsociados.setLayoutManager(new LinearLayoutManager(getContext()));
        if (myCallBack.getCurrentStateEstanciaFragment() == MyApp.STATE_REGULAR) {
            return;
        }
        upDateClientsNumber(listClientesRelacionados.size());
    }

    private void showRelatedReportsIfExist() {
        if (listReportesRelacionados == null || listReportesRelacionados.size() == 0) {
            return;
        }
        reportesRVAdapter.setListReportes(listReportesRelacionados);
        showRVReportes();
    }

    private void upDateClientsNumber(int cant) {
        etAdultos.setText(String.valueOf(cant));
    }

    private void showSelectedEstancia() {
        if (selectedEstancia == null) {
            return;
        }
        etNoHab.setText(selectedEstancia.getNo_hab());
        tvDesde.setText(selectedEstancia.getDesde());
        tvHasta.setText(selectedEstancia.getHasta());
        tvDesde.setTextColor(ContextCompat.getColor(getContext(), R.color.color_font_blanco));
        tvHasta.setTextColor(ContextCompat.getColor(getContext(), R.color.color_font_blanco));
        actvFamilyName.setText(selectedEstancia.getFamilyName());
        etAdultos.setText(String.valueOf(selectedEstancia.getAdultos()));
        etMenores.setText(String.valueOf(selectedEstancia.getMenores()));
        etInfantes.setText(String.valueOf(selectedEstancia.getInfantes()));
        showHowManyNights();
        etObservaciones.setText(selectedEstancia.getObservaciones());
        showRelatedClientsIfExist();
        showRelatedReportsIfExist();
    }

    private void handleDatePickerDialog(final TextView tv) {

        String tv_str = tv.getText().toString();
        int year = 0;
        int month = 0;
        int day = 0;

        if (tv_str.equalsIgnoreCase(getResources().getString(R.string.seleccionar_fecha))) {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            day = Integer.parseInt(tv_str.substring(0, 2));
            month = Integer.parseInt(tv_str.substring(3, 5)) - 1;
            year = Integer.parseInt(tv_str.substring(6));
        }

        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                tv.setText(DateHandler.formatDateToShow(day, month + 1, year));
                if (!tvDesde.getText().toString().equalsIgnoreCase(getResources().getString(R.string.seleccionar_fecha)) &&
                        !tvHasta.getText().toString().equalsIgnoreCase(getResources().getString(R.string.seleccionar_fecha))) {
                    if (!isHastaAfterDesde(tv, true)) {
                        Toast.makeText(getContext(), getResources().getString(R.string.la_fecha_no_es_correcta), Toast.LENGTH_SHORT).show();
                    }
                    showHowManyNights();
                } else {
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_font_blanco));
                }
            }
        }, year, month, day).show();
    }

    private void showHowManyNights() {
        if (tvDesde.getText().toString().equalsIgnoreCase(getResources().getString(R.string.seleccionar_fecha)) ||
                tvHasta.getText().toString().equalsIgnoreCase(getResources().getString(R.string.seleccionar_fecha))) {
            tvNoNoches.setText(getString(R.string.noches));
            tvNoNoches.setVisibility(View.GONE);
            return;
        }
        if (getHowManyNights() > 0) {
            tvNoNoches.setVisibility(View.VISIBLE);
            tvNoNoches.setText(getResources().getString(R.string.noches) + " " + getHowManyNights());
        } else {
            tvNoNoches.setText(getString(R.string.noches));
            tvNoNoches.setVisibility(View.GONE);
        }
    }

    private int getHowManyNights() {
        Date dateDesde = DateHandler.getDate(tvDesde.getText().toString(), DateHandler.FECHA_FORMATO_MOSTRAR);
        Date dateHasta = DateHandler.getDate(tvHasta.getText().toString(), DateHandler.FECHA_FORMATO_MOSTRAR);
        return DateHandler.getDiasEntreFechas(dateDesde, dateHasta);
    }

    private void setUpRecordatorio(final Estancia estancia){
        new Thread(new Runnable() {
            @Override
            public void run(){
                if (!MySharedPreferences.getRecordatorioEstanciasIsActivado(getContext()) || MySharedPreferences.getDiasDeAntelacion(getContext()).equals("0") ||
                        MySharedPreferences.getDiasDeAntelacion(getContext()).equals("") || !MyApp.isInt(MySharedPreferences.getDiasDeAntelacion(getContext()))) {
                    return;
                }

                long diasAntelacion = Integer.parseInt(MySharedPreferences.getDiasDeAntelacion(getContext())) * 24 * 60 * 60 * 1000;
                long desde = DateHandler.configurarHoraRecordatorio(DateHandler.getCalendar(estancia.getDesde(), DateHandler.FECHA_FORMATO_MOSTRAR)).getTimeInMillis();
                long timeStamp = desde - diasAntelacion;

                //if(!Recordatorio.validarTimeStamp(timeStamp)){return;}

                String title = estancia.getFamilyName();
                String msg = getResources().getString(R.string.llegando_el)+" "+estancia.getDesde();

                if(Recordatorio.setAlarm(getActivity().getApplicationContext(),new Recordatorio(0,desde,title,msg,estancia.getId()),true)){
                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        System.out.println(e.getMessage());
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),getResources().getString(R.string.recordatorio_agregado),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),getResources().getString(R.string.recordatorio_agregado),Toast.LENGTH_SHORT).show();
            }
        },3000);*/
    }

    private boolean isHastaAfterDesde(TextView tv, boolean showByColor) {

        String desde_str = tvDesde.getText().toString();
        String hasta_str = tvHasta.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date desde_date = sdf.parse(desde_str);
            Date hasta_date = sdf.parse(hasta_str);

            if (hasta_date.before(desde_date)) {
                if (showByColor) {
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.link));
                }
                return false;
            } else {
                if (showByColor) {
                    tvDesde.setTextColor(ContextCompat.getColor(getContext(), R.color.color_font_blanco));
                    tvHasta.setTextColor(ContextCompat.getColor(getContext(), R.color.color_font_blanco));
                }
                return true;
            }
        } catch (ParseException e) {
            //Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            if (showByColor) {
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.link));
            }
            return false;
        }
    }

    private void setUpClearListClientesRelacionados() {
        if (listClientesRelacionados == null) {
            listClientesRelacionados = new ArrayList<Cliente>();
        } else {
            listClientesRelacionados.clear();
        }
    }

    @Override
    public List<Cliente> getClientesRelacionados() {
        return listClientesRelacionados;
    }

    @Override
    public void udClientesRelacionados(List<Cliente> listClientesRelacionados) {
        this.listClientesRelacionados = listClientesRelacionados;
        showRelatedClientsIfExist();
    }

    @Override
    public boolean searchForAllClientsInBD() {
        return true;
    }

    private void shareByMail(){
        MyEmail.setUpEmail(getContext(),
                new MyEmail(new String[]{MySharedPreferences.getDefaultMail(getContext())},
                        selectedEstancia.getEmailAsunto(),
                        selectedEstancia.getEmailBody(getContext())));
    }

    private void showDialogPicker() {
        MyPickClienteDialogF dialog = new MyPickClienteDialogF();
        dialog.setTargetFragment(EstanciaFragment.this, 1);
        dialog.show(getChildFragmentManager(), MyPickClienteDialogF.TAG);
    }

    private void setViewsEditable(boolean choise) {
        etNoHab.setEnabled(choise);
        etAdultos.setEnabled(choise);
        etMenores.setEnabled(choise);
        etInfantes.setEnabled(choise);
        tvDesde.setEnabled(choise);
        tvHasta.setEnabled(choise);
        actvFamilyName.setEnabled(choise);
        etObservaciones.setEnabled(choise);
    }

    private void showLayoutReportes(boolean choise) {
        if (choise) {
            tvReportes.setVisibility(View.VISIBLE);
            tvAddReporte.setVisibility(View.VISIBLE);
            showRVReportes();
        } else {
            tvReportes.setVisibility(View.GONE);
            tvAddReporte.setVisibility(View.GONE);
            layoutReportes.setVisibility(View.GONE);
        }
    }

    private void showMsgNoClientesAsociados(boolean choise) {
        if (choise) {
            tvNoClientesAsociados.setVisibility(View.VISIBLE);
            rvClientesAsociados.setVisibility(View.GONE);
        } else {
            tvNoClientesAsociados.setVisibility(View.GONE);
            rvClientesAsociados.setVisibility(View.VISIBLE);
        }
    }

    private void showRVReportes() {
        if (listReportesRelacionados != null && listReportesRelacionados.size() > 0) {
            rvReportes.setVisibility(View.VISIBLE);
        } else {
            rvReportes.setVisibility(View.GONE);
            //Toast.makeText(getContext(),R.string.no_existen_reportes,Toast.LENGTH_SHORT).show();
        }
    }

    private void showBtns(boolean choise) {
        if (choise) {
            btnMain.setVisibility(View.VISIBLE);
            if (myCallBack.getCurrentStateEstanciaFragment() == MyApp.STATE_NEW) {
                btnMain.setText(getResources().getString(R.string.registrar));
            } else if (myCallBack.getCurrentStateEstanciaFragment() == MyApp.STATE_UPDATE) {
                btnMain.setText(getResources().getString(R.string.actualizar));
            }
        } else {
            btnMain.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {
            menu.clear();
        }
        switch (myCallBack.getCurrentStateEstanciaFragment()) {
            case MyApp.STATE_REGULAR:
                inflater.inflate(R.menu.menu_estancia_fragment, menu);
                break;
        }
        inflater.inflate(R.menu.menu_main,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_editar_estancia_fragment:
                setUpUDMode();
                break;
            case R.id.menu_item_borrar_estancia_fragment:
                //makeToast("Borrando estancia...");
                //eliminarEstancia();
                confirmarEliminarEstancia();
                break;
            case R.id.menu_item_agregar_reporte:
                if (myCallBack.getCurrentStateEstanciaFragment() != MyApp.STATE_NEW) {
                    myCallBack.setUpReportFragment(selectedEstancia.getId(), 0);
                }
                break;
            case R.id.menu_item_enviar_por_mail:
                shareByMail();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public interface Callback {
        void udActivity(String tag);

        int getCurrentStateEstanciaFragment();

        void setNewCurrentStateEstanciaFragment(int newState);

        void setUpReportFragment(long estanciaId, long reporteId);
    }
}
