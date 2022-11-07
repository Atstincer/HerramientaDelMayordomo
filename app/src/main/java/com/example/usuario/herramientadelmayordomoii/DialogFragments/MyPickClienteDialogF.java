package com.example.usuario.herramientadelmayordomoii.DialogFragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomoii.Adapters.ClientesRVAdapter;
import com.example.usuario.herramientadelmayordomoii.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomoii.Entities.Cliente;
import com.example.usuario.herramientadelmayordomoii.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by usuario on 4/12/2021.
 */

public class MyPickClienteDialogF extends DialogFragment {

    public static final String TAG = "MyPickClienteDialogF";

    private Callback myCallback;

    private List<Cliente> listClientes;
    private List<Cliente> fullListClientes;
    private ClientesRVAdapter adapter;

    private RecyclerView rv_clientesRelacionados;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialogfragment_client_picker,container,false);
        bindComponents(v);
        listClientes = myCallback.getClientesRelacionados();
        if(myCallback.searchForAllClientsInBD()) {
            getClientesFromDB();
            udClientesRelacionados();
        }else{
            fullListClientes = new ArrayList<>(myCallback.getClientesRelacionados());
        }
        setUpAdapter();
        setUpRecyclerView();
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            myCallback = (Callback)getTargetFragment();
        }catch (ClassCastException e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void bindComponents(View v){
        SearchView sv = (SearchView)v.findViewById(R.id.sv_client_pick_dialog);
        rv_clientesRelacionados = (RecyclerView)v.findViewById(R.id.rv_clientes);
        Button btn_ok = (Button)v.findViewById(R.id.btn_ok_cliente_pick_dialog);
        Button btn_cancel = (Button)v.findViewById(R.id.btn_cancel_cliente_pick_dialog);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                List<Cliente> clientesRelacionados = new ArrayList<>();
                for(Cliente c: fullListClientes){
                    if(c.isChecked()){clientesRelacionados.add(c);}
                }
                myCallback.udClientesRelacionados(clientesRelacionados);
                dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void getClientesFromDB(){
        if(fullListClientes==null){fullListClientes = new ArrayList<>();}else{fullListClientes.clear();}
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase BD = admin.getReadableDatabase();
        Cursor cursor = BD.rawQuery("SELECT * FROM "+Cliente.TABLE_NAME,null);
        if(cursor.moveToFirst()){
            do{
                Cliente c = new Cliente();
                c.setId(cursor.getInt(0));
                c.setName(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_NAME)));
                c.setPass(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_PASS)));
                c.setOrigenPais(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_ORIGEN_PAIS)));
                c.setOrigenCiudad(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_ORIGEN_CIUDAD)));
                c.setDob(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_DOB)));
                c.setFoto(cursor.getBlob(cursor.getColumnIndex(Cliente.CAMPO_FOTO)));
                fullListClientes.add(c);
            }while(cursor.moveToNext());
        }
        cursor.close();
        Collections.sort(fullListClientes,Cliente.nameAscending);
    }

    private void udClientesRelacionados(){
        if(listClientes==null||fullListClientes==null){return;}
        for(Cliente c1: listClientes){
            //c1.setChecked(true);
            for(Cliente c2: fullListClientes){
                if(c1.getId()==c2.getId()){c2.setChecked(true);}
            }
        }
    }

    private void setUpAdapter(){
        adapter = new ClientesRVAdapter(fullListClientes,new ClientesRVAdapter.Callback(){
            @Override
            public void onItemClicked(int position) {
                //fullListClientes.get(position).checkUncheck();
                //fullListClientes.get(fullListClientes.indexOf(listClientes.get(position))).checkUncheck();
            }
        },true,false); //estaba true
    }

    private void setUpRecyclerView(){
        rv_clientesRelacionados.setAdapter(adapter);
        rv_clientesRelacionados.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public interface Callback{
        List<Cliente> getClientesRelacionados();
        void udClientesRelacionados(List<Cliente> listClientesRelacionados);
        boolean searchForAllClientsInBD();
    }
}
