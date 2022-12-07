package com.example.usuario.herramientadelmayordomo.Fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.usuario.herramientadelmayordomo.Adapters.ClientesRVAdapter;
import com.example.usuario.herramientadelmayordomo.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomo.DialogFragments.MyPickClienteDialogF;
import com.example.usuario.herramientadelmayordomo.Entities.Cliente;
import com.example.usuario.herramientadelmayordomo.Entities.FamilyName;
import com.example.usuario.herramientadelmayordomo.Entities.FamilyNames_Clientes;
import com.example.usuario.herramientadelmayordomo.Interfaces.IMyFragments;
import com.example.usuario.herramientadelmayordomo.R;
import com.example.usuario.herramientadelmayordomo.Util.MyApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 1/12/2021.
 */

public class FamilyNameFragment extends Fragment implements MyPickClienteDialogF.Callback, IMyFragments {

    public static final String TAG = "FamilyNameFragment";
    /*
    public static final String STATE_REGULAR_MODE = "STATE_FAMILY_NAME_REGULAR_MODE";
    public static final String STATE_FAMILY_NAME_UD_MODE = "STATE_FAMILY_NAME_UD_MODE";
    public static final String STATE_NEW_FAMILY_NAME_MODE = "STATE_NEW_FAMILY_NAME_MODE";*/

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
        if (myCallback.getCurrentStateFamilyNameFragment()==MyApp.STATE_REGULAR || myCallback.getCurrentStateFamilyNameFragment()==MyApp.STATE_UPDATE) {
            if(getArguments() != null){
                Bundle bundle = getArguments();
                udSelectedFamilyNameFromDB(bundle.getInt("id"));
            }else if(savedInstanceState!=null){
                for(String key:savedInstanceState.keySet()){
                    switch (key){
                        case "id":
                            udSelectedFamilyNameFromDB(savedInstanceState.getInt("id"));
                    }
                }
            }
            if(myCallback.getCurrentStateFamilyNameFragment()==MyApp.STATE_REGULAR){
                setUpRegularMode();//just shows info of a family name
            }else if(myCallback.getCurrentStateFamilyNameFragment()==MyApp.STATE_UPDATE){
                setUpUDMode();
            }
        }else if(myCallback.getCurrentStateFamilyNameFragment()==MyApp.STATE_NEW){
            setUpNewFamilyNameMode();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myCallback = (CallBack) context;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(myCallback.getCurrentStateFamilyNameFragment()==MyApp.STATE_REGULAR || myCallback.getCurrentStateFamilyNameFragment()==MyApp.STATE_UPDATE){
            outState.putInt("id",selectedFamilyName.getId());
        }
        super.onSaveInstanceState(outState);
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
                if(myCallback.getCurrentStateFamilyNameFragment()==MyApp.STATE_UPDATE||
                        myCallback.getCurrentStateFamilyNameFragment()==MyApp.STATE_NEW){
                    showDialogPicker();
                }
            }
        });

        btnMain.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(myCallback.getCurrentStateFamilyNameFragment()==MyApp.STATE_NEW){
                    registrarNewFamilyName();
                }else if(myCallback.getCurrentStateFamilyNameFragment()==MyApp.STATE_UPDATE){
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
        rvClientesRelacionados.setAdapter(adapter);
        rvClientesRelacionados.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setUpRegularMode() {
        myCallback.udActivity(FamilyNameFragment.TAG);
        getActivity().invalidateOptionsMenu();
        showInfoSelectedFamilyName();
        setViewEditable(false);
        showBtns(false);
    }

    private void setUpNewFamilyNameMode() {
        myCallback.udActivity(FamilyNameFragment.TAG);
        selectedFamilyName = null;
        if(listClientesRelacionados!=null){listClientesRelacionados.clear();}
        etFamilyName.setText("");
        setViewEditable(true);
        showBtns(true);
        showMsgNoClienteRelacionado(true);
    }

    private void setUpUDMode(){
        myCallback.udActivity(FamilyNameFragment.TAG);
        getActivity().invalidateOptionsMenu();
        showInfoSelectedFamilyName();
        setViewEditable(true);
        showBtns(true);
        selectedFamilyName.setClientesMiembros(listClientesRelacionados);
    }

    @Override
    public void setUpNewState(int state) {
        switch (state){
            case MyApp.STATE_UPDATE:
                setUpUDMode();
                break;
            case MyApp.STATE_NEW:
                setUpNewFamilyNameMode();
                break;
            case MyApp.STATE_REGULAR:
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
            //setUpRVClientesRelacionados();
            adapter.setListClientes(listClientesRelacionados);
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
            if(myCallback.getCurrentStateFamilyNameFragment()==MyApp.STATE_NEW){
                btnMain.setText(getResources().getString(R.string.registrar));
            }else if (myCallback.getCurrentStateFamilyNameFragment()==MyApp.STATE_UPDATE){
                btnMain.setText(getResources().getString(R.string.actualizar));
            }
        }else{
            btnMain.setVisibility(View.GONE);
            btnAdministrarClientesRelacionados.setVisibility(View.GONE);
        }
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

    private void confirmarEliminarFamilyName(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.confirmar_eliminar_nombre_familias));
        builder.setPositiveButton(getString(R.string.btn_ok),new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteFamilyName();
            }
        });
        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteFamilyName(){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        db.delete(FamilyName.TABLE_NAME,"id=?",new String[]{String.valueOf(selectedFamilyName.getId())});
        Toast.makeText(getContext(),getString(R.string.registro_eliminado_correctamente),Toast.LENGTH_SHORT).show();
        //getFragmentManager().popBackStack();
        getActivity().onBackPressed();
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
        }
    }

    private void showDialogPicker(){
        MyPickClienteDialogF dialog = new MyPickClienteDialogF();
        dialog.setTargetFragment(FamilyNameFragment.this,1);
        dialog.show(getChildFragmentManager(),MyPickClienteDialogF.TAG);
    }

    private void makeToast(String msg){Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu!=null){menu.clear();}
        if(myCallback.getCurrentStateFamilyNameFragment()==MyApp.STATE_REGULAR){
            inflater.inflate(R.menu.menu_family_name_fragment_regular_mode,menu);
        }
        inflater.inflate(R.menu.menu_main,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_editar_family_name:
                myCallback.setNewCurrentStateFamilyNameFragment(MyApp.STATE_UPDATE);
                setUpUDMode();
                break;
            case R.id.menu_item_eliminar_family_name:
                confirmarEliminarFamilyName();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface CallBack {
        void udActivity(String tag);
        int getCurrentStateFamilyNameFragment();
        void setNewCurrentStateFamilyNameFragment(int newState);
    }
}
