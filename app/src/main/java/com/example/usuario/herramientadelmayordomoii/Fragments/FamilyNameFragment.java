package com.example.usuario.herramientadelmayordomoii.Fragments;

import android.content.ContentValues;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomoii.Adapters.ClientesRVAdapter;
import com.example.usuario.herramientadelmayordomoii.BD_conexion.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomoii.DialogFragments.MyPickClienteDialogF;
import com.example.usuario.herramientadelmayordomoii.Entities.Cliente;
import com.example.usuario.herramientadelmayordomoii.Entities.FamilyName;
import com.example.usuario.herramientadelmayordomoii.Entities.FamilyNames_Clientes;
import com.example.usuario.herramientadelmayordomoii.Interfaces.IMyFragments;
import com.example.usuario.herramientadelmayordomoii.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 1/12/2021.
 */

public class FamilyNameFragment extends Fragment implements MyPickClienteDialogF.Callback, IMyFragments {

    public static final String TAG = "FamilyNameFragment";

    public static final String STATE_REGULAR_MODE = "STATE_FAMILY_NAME_REGULAR_MODE";
    public static final String STATE_FAMILY_NAME_UD_MODE = "STATE_FAMILY_NAME_UD_MODE";
    public static final String STATE_NEW_FAMILY_NAME_MODE = "STATE_NEW_FAMILY_NAME_MODE";

    private FamilyName selectedFamilyName;
    private List<Cliente> listClientesRelacionados;

    private EditText etFamilyName;
    private TextView tvNoClientesRelacionados;
    private RecyclerView rvClientesRelacionados;
    private Button btnMain, btnAdministrarClientesRelacionados;

    private ClientesRVAdapter adapter;

    private CallBack myCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_family_name, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindComponents(view);
        if (myCallback.getCurrentStateFamilyNameFragment().equals(STATE_REGULAR_MODE) && getArguments() != null) {
            Bundle bundle = getArguments();
            udSelectedFamilyNameFromDB(bundle.getInt("id"));
            setUpRegularMode();//just shows info of a family name
        }else if(myCallback.getCurrentStateFamilyNameFragment().equals(STATE_NEW_FAMILY_NAME_MODE)){
            setUpNewFamilyNameMode();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myCallback = (CallBack) context;
    }

    private void bindComponents(View view){
        etFamilyName = (EditText) view.findViewById(R.id.et_family_name);
        tvNoClientesRelacionados = (TextView) view.findViewById(R.id.tv_no_clientes_asosiados);
        rvClientesRelacionados = (RecyclerView) view.findViewById(R.id.rv_clientes);
        btnMain = (Button) view.findViewById(R.id.btn_family_name);
        btnAdministrarClientesRelacionados = (Button) view.findViewById(R.id.btn_admin_clientes_relacionados);

        tvNoClientesRelacionados.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(myCallback.getCurrentStateFamilyNameFragment().equals(STATE_FAMILY_NAME_UD_MODE)||
                        myCallback.getCurrentStateFamilyNameFragment().equals(STATE_NEW_FAMILY_NAME_MODE)){
                    showDialogPicker();
                }
            }
        });

        btnMain.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(myCallback.getCurrentStateFamilyNameFragment().equals(STATE_NEW_FAMILY_NAME_MODE)){
                    registrarNewFamilyName();
                }else if(myCallback.getCurrentStateFamilyNameFragment().equals(STATE_FAMILY_NAME_UD_MODE)){
                    actualizarFamilyName();
                }
            }
        });

        btnAdministrarClientesRelacionados.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showDialogPicker();
            }
        });

        listClientesRelacionados = new ArrayList<>();
        adapter = new ClientesRVAdapter(listClientesRelacionados, new ClientesRVAdapter.Callback() {
            @Override
            public void onItemClicked(int position) {
                //do nothing
            }
        },false,false);
    }

    private void setUpRegularMode() {
        myCallback.udActivity(FamilyNameFragment.STATE_REGULAR_MODE);
        showInfoSelectedFamilyName();
        setViewEditable(false);
        showBtns(false);
    }

    private void setUpNewFamilyNameMode() {
        myCallback.udActivity(FamilyNameFragment.STATE_NEW_FAMILY_NAME_MODE);
        selectedFamilyName = null;
        if(listClientesRelacionados!=null){listClientesRelacionados.clear();}
        etFamilyName.setText("");
        setViewEditable(true);
        showBtns(true);
        showMsgNoClienteRelacionado(true);
    }

    private void setUpUDMode(){
        myCallback.udActivity(FamilyNameFragment.STATE_FAMILY_NAME_UD_MODE);
        getActivity().invalidateOptionsMenu();
        setViewEditable(true);
        showBtns(true);
        selectedFamilyName.setClientesMiembros(listClientesRelacionados);
    }

    @Override
    public void setUpNewState(String state) {
        switch (state){
            case STATE_FAMILY_NAME_UD_MODE:
                setUpUDMode();
                break;
            case STATE_NEW_FAMILY_NAME_MODE:
                setUpNewFamilyNameMode();
                break;
            case STATE_REGULAR_MODE:
                setUpRegularMode();
                break;
            default:
                break;
        }
    }


    private void registrarNewFamilyName(){
        if(!validarInfo()){return;}
        FamilyName newfamilyName = getNewInfoFamilyName();

        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase BD = admin.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FamilyName.CAMPO_FAMILY_NAME,newfamilyName.getFamilyName());
        long idNewFamilyName = BD.insert(FamilyName.TABLE_NAME,null,values);
        if(idNewFamilyName<0){
            makeToast(getResources().getString(R.string.no_se_pudo));
            return;
        }

        for(Cliente c:newfamilyName.getClientesMiembros()) {
            values.clear();
            values.put(FamilyNames_Clientes.CAMPO_FAMILY_NAME_ID,idNewFamilyName);
            values.put(FamilyNames_Clientes.CAMPO_CLIENTE_ID,c.getId());
            BD.insert(FamilyNames_Clientes.TABLE_NAME,null,values);
        }
        makeToast(getResources().getString(R.string.registro_correcto));
        clearUI();
    }

    private void actualizarFamilyName(){
        if(!validarInfo()){return;}
        FamilyName newFamilyName = getNewInfoFamilyName();

        //chequear si selectedFamilyName = newFamilyName...o sea, si no hay modificaciones
        if(!hasBeenAChange(newFamilyName)){return;}

        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase BD = admin.getWritableDatabase();

        BD.execSQL("DELETE FROM " + FamilyNames_Clientes.TABLE_NAME + " WHERE " + FamilyNames_Clientes.CAMPO_FAMILY_NAME_ID + " = " +
                "" + selectedFamilyName.getId());

        ContentValues values = new ContentValues();

        for(Cliente c: newFamilyName.getClientesMiembros()){
            values.put(FamilyNames_Clientes.CAMPO_FAMILY_NAME_ID,selectedFamilyName.getId());
            values.put(FamilyNames_Clientes.CAMPO_CLIENTE_ID,c.getId());
            BD.insert(FamilyNames_Clientes.TABLE_NAME,null,values);
            values.clear();
        }
        selectedFamilyName.setClientesMiembros(listClientesRelacionados);

        if(!selectedFamilyName.getFamilyName().equalsIgnoreCase(newFamilyName.getFamilyName())){
            //makeToast("Modificando name....");
            values.clear();
            values.put(FamilyName.CAMPO_FAMILY_NAME,newFamilyName.getFamilyName());
            BD.update(FamilyName.TABLE_NAME,values,"id=?",new String[]{String.valueOf(selectedFamilyName.getId())});
            selectedFamilyName.setFamilyName(newFamilyName.getFamilyName());
        }

        makeToast(getResources().getString(R.string.actualizacion_correcta));
    }


    private boolean hasBeenAChange(FamilyName newFN){ // solo para comparar la nueva info con la que ya existia para saber si es necesario actualizar
        if(selectedFamilyName.getClientesMiembros().size() != newFN.getClientesMiembros().size()){return true;}
        if(!selectedFamilyName.getFamilyName().equalsIgnoreCase(newFN.getFamilyName())){return true;}
        for(Cliente c1: selectedFamilyName.getClientesMiembros()){
            boolean estaPresente = false;
            for(Cliente c2: newFN.getClientesMiembros()){
                if(c1.getId()==c2.getId()){estaPresente = true;}
            }
            if(!estaPresente){return true;}
        }
        return false;
    }

    private FamilyName getNewInfoFamilyName(){
        FamilyName familyName = new FamilyName();
        familyName.setFamilyName(etFamilyName.getText().toString());
        familyName.setClientesMiembros(listClientesRelacionados);
        return familyName;
    }

    private boolean validarInfo(){
        if(etFamilyName.getText().toString().equals("")){
            makeToast(getResources().getString(R.string.debe_introducir_un_nombre_de_familia));
            return false;
        }else if(listClientesRelacionados.size()<2){
            makeToast(getResources().getString(R.string.debe_seleccionar_al_menos_dos_clientes));
            return false;
        }
        return true;
    }

    private void clearUI(){
        etFamilyName.setText("");
        showMsgNoClienteRelacionado(true);
        listClientesRelacionados.clear();
    }

    private void showInfoSelectedFamilyName() {
        if (selectedFamilyName == null) {
            return;
        }
        etFamilyName.setText(selectedFamilyName.getFamilyName());
        if (hayClientesRelacionados()) {
            showMsgNoClienteRelacionado(false);
            setUpRVClientesRelacionados();
        }else{
            showMsgNoClienteRelacionado(true);
        }
    }

    private void showMsgNoClienteRelacionado(boolean show){
        if(show){
            rvClientesRelacionados.setVisibility(View.GONE);
            tvNoClientesRelacionados.setVisibility(View.VISIBLE);
        }else{
            rvClientesRelacionados.setVisibility(View.VISIBLE);
            tvNoClientesRelacionados.setVisibility(View.GONE);
        }
    }

    private void setViewEditable(boolean choise) {
        etFamilyName.setEnabled(choise);
    }

    private void showBtns(boolean show){
        if(show){
            btnMain.setVisibility(View.VISIBLE);
            btnAdministrarClientesRelacionados.setVisibility(View.VISIBLE);
            if(myCallback.getCurrentStateFamilyNameFragment().equals(STATE_NEW_FAMILY_NAME_MODE)){
                btnMain.setText(getResources().getString(R.string.registrar));
            }else if (myCallback.getCurrentStateFamilyNameFragment().equals(STATE_FAMILY_NAME_UD_MODE)){
                btnMain.setText(getResources().getString(R.string.actualizar));
            }
        }else{
            btnMain.setVisibility(View.GONE);
            btnAdministrarClientesRelacionados.setVisibility(View.GONE);
        }
    }

    private void setUpRVClientesRelacionados() {
        rvClientesRelacionados.setAdapter(adapter);
        rvClientesRelacionados.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private boolean hayClientesRelacionados() {
        if(listClientesRelacionados!=null && listClientesRelacionados.size()>0){listClientesRelacionados.clear();}
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase BD = admin.getReadableDatabase();
        Cursor cursor = BD.rawQuery("SELECT " + FamilyNames_Clientes.CAMPO_CLIENTE_ID + " FROM " + FamilyNames_Clientes.TABLE_NAME + " WHERE " +
                FamilyNames_Clientes.CAMPO_FAMILY_NAME_ID + "=" + selectedFamilyName.getId(), null);
        if (cursor.moveToFirst()) {
            do {
                addClienteRelacionado(cursor.getInt(0));
            } while (cursor.moveToNext());
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    private void addClienteRelacionado(int id) {
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase BD = admin.getReadableDatabase();
        Cursor cursor = BD.rawQuery("SELECT * FROM " + Cliente.TABLE_NAME + " WHERE id=" + id, null);
        if (cursor.moveToFirst()) {
            do {
                Cliente c = new Cliente();
                c.setId(id);
                c.setName(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_NAME)));
                c.setPass(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_PASS)));
                c.setOrigenPais(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_ORIGEN_PAIS)));
                c.setOrigenCiudad(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_ORIGEN_CIUDAD)));
                c.setDob(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_DOB)));
                c.setFoto(cursor.getBlob(cursor.getColumnIndex(Cliente.CAMPO_FOTO)));
                if (listClientesRelacionados == null) {
                    listClientesRelacionados = new ArrayList<>();
                }
                listClientesRelacionados.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void udSelectedFamilyNameFromDB(int id) {
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase BD = admin.getReadableDatabase();
        Cursor cursor = BD.rawQuery("SELECT " + FamilyName.CAMPO_FAMILY_NAME + " FROM " + FamilyName.TABLE_NAME + " WHERE id=" + id, null);
        if (cursor.moveToFirst()) {
            if (selectedFamilyName == null) {
                selectedFamilyName = new FamilyName();
            }
            selectedFamilyName.setId(id);
            selectedFamilyName.setFamilyName(cursor.getString(cursor.getColumnIndex(FamilyName.CAMPO_FAMILY_NAME)));
        }
        cursor.close();
    }

    private void deleteFamilyName(){
        makeToast("Deleting family name...");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu!=null){menu.clear();}
        if(myCallback.getCurrentStateFamilyNameFragment().equals(STATE_REGULAR_MODE)){
            inflater.inflate(R.menu.menu_family_name_fragment_regular_mode,menu);
        }else if(myCallback.getCurrentStateFamilyNameFragment().equals(STATE_FAMILY_NAME_UD_MODE)){
            inflater.inflate(R.menu.menu_family_name_fragment_ud_mode,menu);
        }
        //super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_editar_family_name:
                myCallback.setNewCurrentStateFamilyNameFragment(FamilyNameFragment.STATE_FAMILY_NAME_UD_MODE);
                setUpUDMode();
                break;
            case R.id.menu_item_eliminar_family_name:
                deleteFamilyName();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public List<Cliente> getClientesRelacionados() {
        return listClientesRelacionados;
    }

    @Override
    public boolean searchForAllClientsInBD() {
        return true;
    }

    @Override
    public void udClientesRelacionados(List<Cliente> listClientesRelacionados) {
        if(this.listClientesRelacionados!=null){this.listClientesRelacionados.clear();}
        if(listClientesRelacionados!=null && listClientesRelacionados.size()>0) {
            showMsgNoClienteRelacionado(false);
            this.listClientesRelacionados = listClientesRelacionados;
            adapter.setListClientes(this.listClientesRelacionados);
            if(rvClientesRelacionados.getAdapter()==null){
                setUpRVClientesRelacionados();
            }
        }
    }

    private void showDialogPicker(){
        MyPickClienteDialogF dialog = new MyPickClienteDialogF();
        dialog.setTargetFragment(FamilyNameFragment.this,1);
        dialog.show(getChildFragmentManager(),MyPickClienteDialogF.TAG);
    }

    private void makeToast(String msg){Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();}

    public interface CallBack {
        void udActivity(String tag);
        String getCurrentStateFamilyNameFragment();
        void setNewCurrentStateFamilyNameFragment(String newState);
    }
}
