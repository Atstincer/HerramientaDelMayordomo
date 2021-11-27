package com.example.usuario.herramientadelmayordomoii.Fragments;

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
import android.view.inputmethod.EditorInfo;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomoii.Adapters.ClientesRVAdapter;
import com.example.usuario.herramientadelmayordomoii.BD_conexion.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomoii.Entities.Cliente;
import com.example.usuario.herramientadelmayordomoii.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by usuario on 17/11/2021.
 */

public class ClientesFragment extends Fragment implements ClientesRVAdapter.Callback{

    public static final String TAG = "ClientesFragment";

    private List<Cliente> listaClientes;
    private ClientesRVAdapter adapter;

    private RecyclerView recyclerView;
    private Callback mycallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_clientes, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_clientes);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_clientes);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mycallback.setUpNewClientFragment();
            }
        });
        udListaClientes();
        setUpRecyclerView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mycallback = (Callback) context;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {
            menu.clear();
        }
        inflater.inflate(R.menu.menu_clientes_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selected = item.getItemId();

        switch (selected) {
            case R.id.menu_item_nuevo_cliente:
                mycallback.setUpNewClientFragment();
                break;
            case R.id.menu_item_buscar:
                Toast.makeText(getActivity(), "Buscar clicked.", Toast.LENGTH_SHORT).show();
                SearchView sv = (SearchView) item.getActionView();

                sv.setImeOptions(EditorInfo.IME_ACTION_DONE);

                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.getFilter().filter(newText);
                        return false;
                    }
                });
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(int position) {
        //makeToast("Cliente "+listaClientes.get(position).getName()+" clicked..");
        mycallback.setUpClienteFragment(listaClientes.get(position).getId());
    }

    private void udListaClientes() {
        if (listaClientes == null) {
            listaClientes = new ArrayList<>();
        } else {
            listaClientes.clear();
        }
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getActivity(), AdminSQLiteOpenHelper.BD_NAME, null, 1);
        SQLiteDatabase BD = admin.getWritableDatabase();
        Cursor cursor = BD.rawQuery("SELECT * FROM " + Cliente.TABLE_NAME, null);

        while (cursor.moveToNext()) {
            Cliente c = new Cliente();
            c.setId(cursor.getInt(0));
            c.setName(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_NAME)));
            c.setPass(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_PASS)));
            c.setDob(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_DOB)));
            c.setOrigenCiudad(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_ORIGEN_CIUDAD)));
            c.setOrigenPais(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_ORIGEN_PAIS)));
            c.setFoto(cursor.getBlob(cursor.getColumnIndex(Cliente.CAMPO_FOTO)));
            listaClientes.add(c);
        }
        cursor.close();
        Collections.sort(listaClientes,Cliente.nameAscending);
    }

    private void setUpRecyclerView() {
        adapter = new ClientesRVAdapter(listaClientes,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void makeToast(String s){
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    public interface Callback {
        void setUpNewClientFragment();
        void setUpClienteFragment(int id);
    }
}
