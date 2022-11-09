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
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.example.usuario.herramientadelmayordomoii.Adapters.FamilyNamesRVAdapter;
import com.example.usuario.herramientadelmayordomoii.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomoii.Entities.FamilyName;
import com.example.usuario.herramientadelmayordomoii.Entities.FamilyNames_Clientes;
import com.example.usuario.herramientadelmayordomoii.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by usuario on 29/11/2021.
 */

public class FamilyNamesFragment extends Fragment implements FamilyNamesRVAdapter.Callback {

    public static final String TAG = "FamilyNamesFragment";

    private RecyclerView rv_familyNames;
    private TextView tvNoFamilyNamesRegistrados;
    private FloatingActionButton fab;

    private List<FamilyName> listFamilyNames;
    private FamilyNamesRVAdapter adapter;
    private Callback myCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_family_names, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv_familyNames = (RecyclerView) view.findViewById(R.id.rv_family_names);
        tvNoFamilyNamesRegistrados = (TextView) view.findViewById(R.id.tv_no_family_names_registrados);
        fab = (FloatingActionButton) view.findViewById(R.id.fab_family_names);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                myCallback.setUpNewFamilyNameFragment();
            }
        });
        myCallback.udActivity(FamilyNamesFragment.TAG);
        udListFamilyNames();
        setUpRecyclerView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myCallback = (Callback) context;
    }

    private void udListFamilyNames() {
        if (listFamilyNames == null) {
            listFamilyNames = new ArrayList<>();
        } else {
            listFamilyNames.clear();
        }
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor cursorFamilyNames = bd.rawQuery("SELECT * FROM " + FamilyName.TABLE_NAME, null);

        if (cursorFamilyNames.moveToFirst()) {
            do {
                FamilyName familyName = new FamilyName();
                familyName.setId(cursorFamilyNames.getInt(0));
                familyName.setFamilyName(cursorFamilyNames.getString(cursorFamilyNames.getColumnIndex(FamilyName.CAMPO_FAMILY_NAME)));

                Cursor cursorRelacionCliente = bd.rawQuery("SELECT * FROM " + FamilyNames_Clientes.TABLE_NAME + " WHERE " + FamilyNames_Clientes.CAMPO_FAMILY_NAME_ID + " = '"
                        + familyName.getId() + "'", null);
                if (cursorRelacionCliente.getCount() > 1) {
                    listFamilyNames.add(familyName);
                }
                cursorRelacionCliente.close();
            } while (cursorFamilyNames.moveToNext());
        }

        cursorFamilyNames.close();

        if (!listFamilyNames.isEmpty()) {
            Collections.sort(listFamilyNames, FamilyName.nameAscending);
        }
    }

    private void setUpRecyclerView() {
        if(listFamilyNames!=null && listFamilyNames.size()>0) {
            showMsgNoFamilyNamesRegistrados(false);
            adapter = new FamilyNamesRVAdapter(listFamilyNames, this);
            rv_familyNames.setAdapter(adapter);
            rv_familyNames.setLayoutManager(new LinearLayoutManager(getActivity()));
        }else{
            showMsgNoFamilyNamesRegistrados(true);
        }
    }

    @Override
    public void onItemClicked(int position) {
        myCallback.setUpFamilyNameFragment(listFamilyNames.get(position).getId());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu!=null){menu.clear();}
        inflater.inflate(R.menu.menu_family_names_fragment,menu);
        inflater.inflate(R.menu.menu_main,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_nuevo_nombre_de_familia:
                myCallback.setUpNewFamilyNameFragment();
                break;
            case R.id.menu_item_buscar_family_names:
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

    private void showMsgNoFamilyNamesRegistrados(boolean choise){
        if(choise){
            tvNoFamilyNamesRegistrados.setVisibility(View.VISIBLE);
            rv_familyNames.setVisibility(View.GONE);
        }else{
            tvNoFamilyNamesRegistrados.setVisibility(View.GONE);
            rv_familyNames.setVisibility(View.VISIBLE);
        }
    }

    public interface Callback{
        void udActivity(String tag);
        void setUpFamilyNameFragment(int id);
        void setUpNewFamilyNameFragment();
    }

}
