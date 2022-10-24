package com.example.usuario.herramientadelmayordomoii.Fragments;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomoii.Adapters.ClientesRVAdapter;
import com.example.usuario.herramientadelmayordomoii.BD_conexion.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomoii.DialogFragments.MyPickClienteDialogF;
import com.example.usuario.herramientadelmayordomoii.Entities.Cliente;
import com.example.usuario.herramientadelmayordomoii.Entities.Estancia;
import com.example.usuario.herramientadelmayordomoii.Entities.Estancias_Clientes;
import com.example.usuario.herramientadelmayordomoii.Entities.FamilyName;
import com.example.usuario.herramientadelmayordomoii.Entities.FamilyNames_Clientes;
import com.example.usuario.herramientadelmayordomoii.Interfaces.IMyFragments;
import com.example.usuario.herramientadelmayordomoii.R;
import com.example.usuario.herramientadelmayordomoii.Util.DateHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by usuario on 6/11/2021.
 */

public class EstanciaFragment extends Fragment implements MyPickClienteDialogF.Callback, IMyFragments {

    public static final String TAG = "EstanciaFragment";
    public static final String STATE_REGULAR_MODE = "STATE_ESTANCIA_REGULAR_MODE";
    public static final String STATE_ESTANCIA_UD_MODE = "STATE_ESTANCIA_UD_MODE";
    public static final String STATE_NEW_ESTANCIA_MODE = "STATE_NEW_ESTANCIA_MODE";

    private EditText etNoHab,etAdultos,etMenores,etInfantes;
    private TextView tvNoNoches,tvDesde,tvHasta,tvNoClientesAsociados,tvReportes,tvCortesias,tvRecordatorios,tvReservaRestaurantes,tvObs,tvNoExistenReportes;
    private AutoCompleteTextView actvFamilyName;
    private RecyclerView rvClientesAsociados;
    private Button btnMain;
    private RecyclerView rvReportes;

    private List<FamilyName> listFamilyNamesBD;
    private List<Cliente> listClientesRelacionados;
    private Estancia selectedEstancia;

    private Callback myCallBack;
    ArrayAdapter<FamilyName> actvAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_estancia,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myCallBack = (Callback)context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindComponents(view);
        switch (myCallBack.getCurrentStateEstanciaFragment()){
            case EstanciaFragment.STATE_REGULAR_MODE:
                setUpRegularMode();
                break;
            case EstanciaFragment.STATE_NEW_ESTANCIA_MODE:
                setUpNewEstanciaMode();
                break;
            case EstanciaFragment.STATE_ESTANCIA_UD_MODE:
                setUpUDMode();
                break;
            default:
                break;
        }
    }

    private void bindComponents(View v){
        etNoHab = (EditText)v.findViewById(R.id.et_no_hab);
        etAdultos = (EditText)v.findViewById(R.id.et_adultos);
        etMenores = (EditText)v.findViewById(R.id.et_menores);
        etInfantes = (EditText)v.findViewById(R.id.et_infantes);
        tvNoNoches = (TextView)v.findViewById(R.id.tv_no_noches);
        tvDesde = (TextView)v.findViewById(R.id.tv_desde);
        tvHasta = (TextView)v.findViewById(R.id.tv_hasta);
        tvNoClientesAsociados = (TextView)v.findViewById(R.id.tv_no_clientes_asosiados);
        tvReportes = (TextView)v.findViewById(R.id.tv_reportes);
        tvCortesias = (TextView)v.findViewById(R.id.tv_cortesias);
        tvRecordatorios = (TextView)v.findViewById(R.id.tv_recordatorios);
        tvReservaRestaurantes = (TextView)v.findViewById(R.id.tv_reservas_resto);
        tvObs = (TextView)v.findViewById(R.id.tv_obs_estancia);
        actvFamilyName = (AutoCompleteTextView)v.findViewById(R.id.actv_family_name);
        rvClientesAsociados = (RecyclerView)v.findViewById(R.id.rv_clientes);
        btnMain = (Button)v.findViewById(R.id.btn_estancia);
        tvNoExistenReportes = (TextView)v.findViewById(R.id.tv_no_existen_reportes);
        rvReportes = (RecyclerView)v.findViewById(R.id.rv_reportes);

        tvDesde.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                handleDatePickerDialog(tvDesde);
            }
        });

        tvHasta.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                handleDatePickerDialog(tvHasta);
            }
        });

        btnMain.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                switch (myCallBack.getCurrentStateEstanciaFragment()){
                    case EstanciaFragment.STATE_NEW_ESTANCIA_MODE:
                        registrarEstancia();
                        break;
                    case EstanciaFragment.STATE_ESTANCIA_UD_MODE:
                        actualizarEstancia();
                        break;
                    default:
                        break;
                }
            }
        });

        actvFamilyName.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(myCallBack.getCurrentStateEstanciaFragment().equals(EstanciaFragment.STATE_REGULAR_MODE)){return;}
                FamilyName familyName = (FamilyName)adapterView.getItemAtPosition(position);
                if(familyName.getId()==0){
                    for(Cliente c:familyName.getClientesMiembros()){
                        c.setChecked(true);
                        setUpClearListClientesRelacionados();
                        listClientesRelacionados.add(c);
                    }
                }else {
                    getRelatedClientsFromDB(familyName.getId(), FamilyNames_Clientes.TABLE_NAME);
                }
                showRelatedClientsIfExist();
            }
        });
    }

    private void setUpRegularMode(){
        myCallBack.udActivity(EstanciaFragment.STATE_REGULAR_MODE);
        if(getArguments()!=null){
            getSelectedEstanciaFromDB((int)getArguments().get("id"));
        }
        if(selectedEstancia!=null && selectedEstancia.getId()>0){
            getRelatedClientsFromDB(selectedEstancia.getId(),Estancias_Clientes.TABLE_NAME);
        }
        showSelectedEstancia();
        showRelatedClientsIfExist();
        getActivity().invalidateOptionsMenu();
        showViews(true);
        setViewsEditable(false);
        showBtns(false);
        actvFamilyName.dismissDropDown();
    }

    private void setUpNewEstanciaMode(){
        setViewsEditable(true);
        showViews(false);
        showBtns(true);
        setUpACTV();
        showMsgNoClientesAsociados(true);
        myCallBack.udActivity(EstanciaFragment.STATE_NEW_ESTANCIA_MODE);
    }

    private void setUpUDMode(){
        myCallBack.setNewCurrentStateEstanciaFragment(EstanciaFragment.STATE_ESTANCIA_UD_MODE);
        myCallBack.udActivity(EstanciaFragment.STATE_ESTANCIA_UD_MODE);
        getActivity().invalidateOptionsMenu();
        setUpACTV();
        showViews(false);
        setViewsEditable(true);
        showBtns(true);
    }

    @Override
    public void setUpNewState(String state) {
        switch (state){
            case EstanciaFragment.STATE_ESTANCIA_UD_MODE:
                setUpUDMode();
                break;
            case EstanciaFragment.STATE_NEW_ESTANCIA_MODE:
                setUpNewEstanciaMode();
                break;
            case EstanciaFragment.STATE_REGULAR_MODE:
                setUpRegularMode();
                break;
        }
    }

    private void getSelectedEstanciaFromDB(int id){
        if(selectedEstancia == null){selectedEstancia = new Estancia();}
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor cursor = bd.rawQuery("SELECT * FROM " + Estancia.TABLE_NAME + " WHERE id = " + id,null);
        if(cursor.moveToFirst()){
            selectedEstancia.setId(id);
            selectedEstancia.setFamilyName(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_FAMILY_NAME)));
            selectedEstancia.setNo_hab(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_NO_HAB)));
            selectedEstancia.setDesde(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_DESDE))));
            selectedEstancia.setHasta(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_HASTA))));
            selectedEstancia.setAdultos(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_ADULTOS)));
            selectedEstancia.setMenores(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_MENORES)));
            selectedEstancia.setInfantes(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_INFANTES)));
        }
        cursor.close();
    }

    private void registrarEstancia(){
        if(!validarInfo()){return;}
        Estancia newEstancia = getInfoEstancia();

        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Estancia.CAMPO_NO_HAB,newEstancia.getNo_hab());
        values.put(Estancia.CAMPO_DESDE,DateHandler.formatDateToStoreInDB(newEstancia.getDesde()));
        values.put(Estancia.CAMPO_HASTA,DateHandler.formatDateToStoreInDB(newEstancia.getHasta()));
        values.put(Estancia.CAMPO_FAMILY_NAME,newEstancia.getFamilyName());
        values.put(Estancia.CAMPO_ADULTOS,newEstancia.getAdultos());
        values.put(Estancia.CAMPO_MENORES,newEstancia.getMenores());
        values.put(Estancia.CAMPO_INFANTES,newEstancia.getInfantes());

        long newEstanciaId = bd.insert(Estancia.TABLE_NAME,null,values);

        if(newEstanciaId!=0 && listClientesRelacionados!=null && listClientesRelacionados.size()!=0){
            for(Cliente c: listClientesRelacionados){
                bd.execSQL("INSERT INTO "+ Estancias_Clientes.TABLE_NAME+"("+ Estancias_Clientes.CAMPO_ESTANCIA_ID+","+Estancias_Clientes.CAMPO_CLIENTE_ID+
                ") VALUES("+newEstanciaId+","+c.getId()+")");
            }
        }

        makeToast(getResources().getString(R.string.registro_correcto));
        clear();
    }

    private void actualizarEstancia(){
        if(!validarInfo()){return;}
        Estancia estanciaNewInfo = getInfoEstancia();

        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Estancia.CAMPO_NO_HAB,estanciaNewInfo.getNo_hab());
        values.put(Estancia.CAMPO_DESDE,DateHandler.formatDateToStoreInDB(estanciaNewInfo.getDesde()));
        values.put(Estancia.CAMPO_HASTA,DateHandler.formatDateToStoreInDB(estanciaNewInfo.getHasta()));
        values.put(Estancia.CAMPO_FAMILY_NAME,estanciaNewInfo.getFamilyName());
        values.put(Estancia.CAMPO_ADULTOS,estanciaNewInfo.getAdultos());
        values.put(Estancia.CAMPO_MENORES,estanciaNewInfo.getMenores());
        values.put(Estancia.CAMPO_INFANTES,estanciaNewInfo.getInfantes());

        bd.update(Estancia.TABLE_NAME,values,"id=?",new String[]{String.valueOf(selectedEstancia.getId())});
        Toast.makeText(getContext(),getResources().getString(R.string.actualizacion_correcta),Toast.LENGTH_SHORT).show();

        bd.execSQL("DELETE FROM " + Estancias_Clientes.TABLE_NAME + " WHERE " + Estancias_Clientes.CAMPO_ESTANCIA_ID + " = " + selectedEstancia.getId());

        if(listClientesRelacionados==null||listClientesRelacionados.isEmpty()){return;}
        for(Cliente c:listClientesRelacionados){
            bd.execSQL("INSERT INTO " + Estancias_Clientes.TABLE_NAME + "(" + Estancias_Clientes.CAMPO_ESTANCIA_ID + ", " + Estancias_Clientes.CAMPO_CLIENTE_ID +
            ") VALUES (" + selectedEstancia.getId() + ", " + c.getId() + ")");
        }
    }

    private void borrarEstancia(){
        //System.out.println("*********************************");
        //System.out.println(selectedEstancia.getFamilyName() + "//n" + selectedEstancia.getDesde() + " al " + selectedEstancia.getHasta());
        //System.out.println("*********************************");
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase BD = admin.getWritableDatabase();
        BD.execSQL("DELETE FROM " + Estancia.TABLE_NAME + " WHERE id = " + selectedEstancia.getId());
        BD.execSQL("DELETE FROM " + Estancias_Clientes.TABLE_NAME + " WHERE " + Estancias_Clientes.CAMPO_ESTANCIA_ID + " = " + selectedEstancia.getId());
        makeToast(getResources().getString(R.string.registro_eliminado_correctamente));
    }

    private boolean validarInfo(){
        /*if(etNoHab.getText().toString().equals("")){
            makeToast(getResources().getString(R.string.debe_introducir_numero_de_hab));
            return false;
        }*/
        if(!isHastaAfterDesde(tvDesde,false)){
            makeToast(getResources().getString(R.string.las_fechas_no_son_correctas));
            return false;
        }
        if(actvFamilyName.getText().toString().equals("")){
            makeToast(getResources().getString(R.string.debe_introducir_a_nombre_de_quien));
            return false;
        }
        if(etAdultos.getText().toString().equals("")){
            makeToast(getResources().getString(R.string.debe_introducir_numero_de_adultos));
            return false;
        }
        return true;
    }

    private Estancia getInfoEstancia(){
        Estancia newEstancia = new Estancia();
        newEstancia.setNo_hab(etNoHab.getText().toString());
        newEstancia.setDesde(tvDesde.getText().toString());
        newEstancia.setHasta(tvHasta.getText().toString());
        newEstancia.setFamilyName(actvFamilyName.getText().toString());
        newEstancia.setAdultos(Integer.parseInt(etAdultos.getText().toString()));
        if(!etMenores.getText().toString().equals("")){newEstancia.setMenores(Integer.parseInt(etMenores.getText().toString()));}
        if(!etInfantes.getText().toString().equals("")){newEstancia.setInfantes(Integer.parseInt(etInfantes.getText().toString()));}
        return newEstancia;
    }
    
    private void clear(){
        etNoHab.setText("");
        tvDesde.setText(getResources().getString(R.string.seleccionar_fecha));
        tvDesde.setTextColor(ContextCompat.getColor(getContext(),R.color.link));
        tvHasta.setText(getResources().getString(R.string.seleccionar_fecha));
        tvHasta.setTextColor(ContextCompat.getColor(getContext(),R.color.link));
        actvFamilyName.setText("");
        etAdultos.setText("");
        etMenores.setText("");
        etInfantes.setText("");
        listClientesRelacionados.clear();
        showRelatedClientsIfExist();
    }

    private void setUpACTV(){
        if(listFamilyNamesBD==null){listFamilyNamesBD = new ArrayList<>();}else {listFamilyNamesBD.clear();}
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor cursor = bd.rawQuery("SELECT * FROM "+FamilyName.TABLE_NAME,null);

        if(cursor.moveToFirst()){
            do{
                FamilyName familyName = new FamilyName();
                familyName.setId(cursor.getInt(0));
                familyName.setFamilyName(cursor.getString(cursor.getColumnIndex(FamilyName.CAMPO_FAMILY_NAME)));
                listFamilyNamesBD.add(familyName);
            }while (cursor.moveToNext());
        }

        cursor.close();

        Cursor cursorII = bd.rawQuery("SELECT * FROM " + Cliente.TABLE_NAME,null);

        if(cursorII.moveToFirst()){
            do{
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
                familyName.setFamilyName(cliente.getName()+" x 1");
                familyName.addClienteMiembro(cliente);
                listFamilyNamesBD.add(familyName);
            }while (cursorII.moveToNext());
        }
        cursorII.close();

        actvAdapter = new ArrayAdapter<FamilyName>(getContext(),R.layout.my_simple_dropdown_item_1line,listFamilyNamesBD);
        actvFamilyName.setAdapter(actvAdapter);
    }

    private void getRelatedClientsFromDB(int id, String tabla){
        setUpClearListClientesRelacionados();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        String query = "";
        switch (tabla){
            case FamilyNames_Clientes.TABLE_NAME:
                query = "SELECT " + FamilyNames_Clientes.CAMPO_CLIENTE_ID + " FROM " + FamilyNames_Clientes.TABLE_NAME + " WHERE " +
                        FamilyNames_Clientes.CAMPO_FAMILY_NAME_ID + " = " + id;
                break;
            case Estancias_Clientes.TABLE_NAME:
                query = "SELECT " + Estancias_Clientes.CAMPO_CLIENTE_ID + " FROM " + Estancias_Clientes.TABLE_NAME + " WHERE " +
                        Estancias_Clientes.CAMPO_ESTANCIA_ID + " = " + id;
        }
        Cursor cursor = bd.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do{
                int clientId = cursor.getInt(0);
                Cursor cursorII = bd.rawQuery("SELECT * FROM " + Cliente.TABLE_NAME + " WHERE id=" + clientId,null);
                if(cursorII.moveToFirst()){
                    do{
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
                    }while (cursorII.moveToNext());
                }
                cursorII.close();
            }while(cursor.moveToNext());
        }
        cursor.close();
    }

    private void showRelatedClientsIfExist(){
        if(listClientesRelacionados==null || listClientesRelacionados.size()==0){showMsgNoClientesAsociados(true);return;}
        showMsgNoClientesAsociados(false);
        List<Cliente> selectedClients = new ArrayList<>();
        for(Cliente c: listClientesRelacionados){if(c.isChecked()){selectedClients.add(c);}}
        ClientesRVAdapter adapter = new ClientesRVAdapter(selectedClients,new ClientesRVAdapter.Callback(){
            @Override
            public void onItemClicked(int position) {
                if(myCallBack.getCurrentStateEstanciaFragment().equals(EstanciaFragment.STATE_REGULAR_MODE)){return;}
                showDialogPicker();
            }
        },false,false);
        rvClientesAsociados.setAdapter(adapter);
        rvClientesAsociados.setLayoutManager(new LinearLayoutManager(getContext()));
        if(myCallBack.getCurrentStateEstanciaFragment().equals(EstanciaFragment.STATE_REGULAR_MODE)){return;}
        upDateClientsNumber(listClientesRelacionados.size());
    }

    private void upDateClientsNumber(int cant){
        etAdultos.setText(String.valueOf(cant));
    }

    private void showSelectedEstancia(){
        if(selectedEstancia==null){
            return;
        }
        etNoHab.setText(selectedEstancia.getNo_hab());
        tvDesde.setText(selectedEstancia.getDesde());
        tvHasta.setText(selectedEstancia.getHasta());
        tvDesde.setTextColor(ContextCompat.getColor(getContext(),R.color.color_font_blanco));
        tvHasta.setTextColor(ContextCompat.getColor(getContext(),R.color.color_font_blanco));
        actvFamilyName.setText(selectedEstancia.getFamilyName());
        etAdultos.setText(String.valueOf(selectedEstancia.getAdultos()));
        etMenores.setText(String.valueOf(selectedEstancia.getMenores()));
        etInfantes.setText(String.valueOf(selectedEstancia.getInfantes()));
    }

    private void handleDatePickerDialog(final TextView tv){

        String tv_str = tv.getText().toString();
        int year = 0;
        int month = 0;
        int day = 0;

        if (tv_str.equalsIgnoreCase(getResources().getString(R.string.seleccionar_fecha))){
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            day = Integer.parseInt(tv_str.substring(0,2));
            month = Integer.parseInt(tv_str.substring(3,5)) - 1;
            year = Integer.parseInt(tv_str.substring(6));
        }

        new DatePickerDialog(getContext(),new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                /*
                String month_str = DateHandler.toDosLugares(month + 1);
                String day_str = DateHandler.toDosLugares(day);

                String full_date = day_str + "/" + month_str + "/" + year;

                tv.setText(full_date);
                */

                tv.setText(DateHandler.formatDateToShow(day,month+1,year));

                if (!tvDesde.getText().toString().equalsIgnoreCase(getResources().getString(R.string.seleccionar_fecha)) &&
                        !tvHasta.getText().toString().equalsIgnoreCase(getResources().getString(R.string.seleccionar_fecha))){
                    if (!isHastaAfterDesde(tv,true)){
                        Toast.makeText(getContext(), getResources().getString(R.string.las_fechas_no_son_correctas), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    tv.setTextColor(ContextCompat.getColor(getContext(),R.color.color_font_blanco));
                }
            }
        },year,month,day).show();
    }

    private boolean isHastaAfterDesde(TextView tv,boolean showByColor){

        String desde_str = tvDesde.getText().toString();
        String hasta_str = tvHasta.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());

        try{
            Date desde_date = sdf.parse(desde_str);
            Date hasta_date = sdf.parse(hasta_str);

            if (hasta_date.before(desde_date)){
                if(showByColor){tv.setTextColor(ContextCompat.getColor(getContext(),R.color.link));}
                return false;
            } else {
                if(showByColor) {
                    tvDesde.setTextColor(ContextCompat.getColor(getContext(), R.color.color_font_blanco));
                    tvHasta.setTextColor(ContextCompat.getColor(getContext(), R.color.color_font_blanco));
                }
                return true;
            }
        }catch(ParseException e){
            //Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            if(showByColor){tv.setTextColor(ContextCompat.getColor(getContext(),R.color.link));}
            return false;
        }
    }

    private void setUpClearListClientesRelacionados(){
        if(listClientesRelacionados==null){
            listClientesRelacionados = new ArrayList<Cliente>();
        }else{
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

    private void showDialogPicker(){
        MyPickClienteDialogF dialog = new MyPickClienteDialogF();
        dialog.setTargetFragment(EstanciaFragment.this,1);
        dialog.show(getChildFragmentManager(),MyPickClienteDialogF.TAG);
    }

    private void setViewsEditable(boolean choise){
        etNoHab.setEnabled(choise);
        etAdultos.setEnabled(choise);
        etMenores.setEnabled(choise);
        etInfantes.setEnabled(choise);
        tvDesde.setEnabled(choise);
        tvHasta.setEnabled(choise);
        actvFamilyName.setEnabled(choise);
    }

    private void showViews(boolean choise){
        if(choise){
            tvReportes.setVisibility(View.VISIBLE);
            tvCortesias.setVisibility(View.VISIBLE);
            tvRecordatorios.setVisibility(View.VISIBLE);
            tvReservaRestaurantes.setVisibility(View.VISIBLE);
            tvObs.setVisibility(View.VISIBLE);
        }else{
            tvReportes.setVisibility(View.GONE);
            tvCortesias.setVisibility(View.GONE);
            tvRecordatorios.setVisibility(View.GONE);
            tvReservaRestaurantes.setVisibility(View.GONE);
            tvObs.setVisibility(View.GONE);
        }
    }

    private void showMsgNoClientesAsociados(boolean choise){
        if(choise){
            tvNoClientesAsociados.setVisibility(View.VISIBLE);
            rvClientesAsociados.setVisibility(View.GONE);
        }else{
            tvNoClientesAsociados.setVisibility(View.GONE);
            rvClientesAsociados.setVisibility(View.VISIBLE);
        }
    }

    private void showBtns(boolean choise){
        if(choise){
            btnMain.setVisibility(View.VISIBLE);
            if(myCallBack.getCurrentStateEstanciaFragment().equals(EstanciaFragment.STATE_NEW_ESTANCIA_MODE)){
                btnMain.setText(getResources().getString(R.string.registrar));
            }else if(myCallBack.getCurrentStateEstanciaFragment().equals(EstanciaFragment.STATE_ESTANCIA_UD_MODE)){
                btnMain.setText(getResources().getString(R.string.actualizar));
            }
        }else{
            btnMain.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu!=null){menu.clear();}
        switch(myCallBack.getCurrentStateEstanciaFragment()){
            case EstanciaFragment.STATE_REGULAR_MODE:
                inflater.inflate(R.menu.menu_estancia,menu);
                break;
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_editar_estancia_fragment:
                setUpUDMode();
                break;
            case R.id.menu_item_borrar_estancia_fragment:
                //makeToast("Borrando estancia...");
                borrarEstancia();
                break;
            case R.id.menu_item_agregar_reporte:
                if(!myCallBack.getCurrentStateEstanciaFragment().equals(EstanciaFragment.STATE_NEW_ESTANCIA_MODE)){
                    myCallBack.setUpNewReporteFragment(selectedEstancia.getId());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public interface Callback{
        void udActivity(String tag);
        String getCurrentStateEstanciaFragment();
        void setNewCurrentStateEstanciaFragment(String newState);
        void setUpNewReporteFragment(int estanciaId);
    }
}
