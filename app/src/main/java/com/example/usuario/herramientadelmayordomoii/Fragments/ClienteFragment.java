package com.example.usuario.herramientadelmayordomoii.Fragments;

import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomoii.BD_conexion.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomoii.Entities.Cliente;
import com.example.usuario.herramientadelmayordomoii.Interfaces.IClienteFragment;
import com.example.usuario.herramientadelmayordomoii.R;

import java.util.Calendar;

/**
 * Created by usuario on 6/11/2021.
 */


public class ClienteFragment extends Fragment implements IClienteFragment {

    public static final String TAG = "ClienteFragment";
    public static final String STATE_CLIENTE_MODE = "STATE_CLIENTE_MODE";
    public static final String STATE_CLIENTE_UD_MODE = "STATE_CLIENTE_UD_MODE";
    public static final String STATE_NEW_CLIENTE_MODE = "STATE_NEW_CLIENTE_MODE";

    //private String currentState;

    private Callback myCallback;
    private EditText nombre, pass, pais, ciudad, dob, preferencias, limitaciones, obs;
    private Button btn;
    private Menu myMenu;

    //private int idSelectedClient;
    private Cliente selectedClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_cliente, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindComponents(view);
        setUpNewState(myCallback.getCurrentStateClienteFragment());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myCallback = (Callback) context;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {
            menu.clear();
        }
        if (myCallback.getCurrentStateClienteFragment().equals(ClienteFragment.STATE_CLIENTE_MODE)) {
            inflater.inflate(R.menu.menu_cliente_mode, menu);
        }
        inflater.inflate(R.menu.menu_main, menu);
        myMenu = menu;
        //super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_editar:
                myCallback.setNewCurrentStateClienteFragment(ClienteFragment.STATE_CLIENTE_UD_MODE);
                setUpNewState(ClienteFragment.STATE_CLIENTE_UD_MODE);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUpNewState(String state) {
        switch (state) {
            case STATE_CLIENTE_MODE:
                setUpClienteMode(true);
                break;
            case STATE_CLIENTE_UD_MODE:
                setUpUpdateMode();
                break;
            case STATE_NEW_CLIENTE_MODE:
                setUpNewClienteMode();
                break;
            default:
                break;
        }
    }

    @Override
    public String getCurrentState() {
        return null;
    }

    private void setUpClienteMode(boolean checkForArguments) {
        if(checkForArguments){
            if (getArguments() != null) {
                Bundle bundle = getArguments();
                udClientFromDB(bundle.getInt("id"));
            }
        }
        setViewEditable(false);
        showViews(true);
        btn.setVisibility(View.GONE);
        getActivity().invalidateOptionsMenu();
        showInfoClient();
    }

    private void setUpUpdateMode() {
        setViewEditable(true);
        showViews(false);
        btn.setVisibility(View.VISIBLE);
        btn.setText(getResources().getString(R.string.actualizar));
        getActivity().invalidateOptionsMenu();
    }

    private void setUpNewClienteMode() {
        setViewEditable(true);
        showViews(false);
        btn.setVisibility(View.VISIBLE);
        btn.setText(getResources().getString(R.string.registrar));
    }

    private void showViews(boolean choise) {
        if (choise) {
            preferencias.setVisibility(View.VISIBLE);
            limitaciones.setVisibility(View.VISIBLE);
            obs.setVisibility(View.VISIBLE);
        } else {
            preferencias.setVisibility(View.GONE);
            limitaciones.setVisibility(View.GONE);
            obs.setVisibility(View.GONE);
        }
    }

    private void setViewEditable(boolean choise) {
        nombre.setEnabled(choise);
        pass.setEnabled(choise);
        pais.setEnabled(choise);
        ciudad.setEnabled(choise);
        dob.setEnabled(choise);
    }

    private void bindComponents(View view) {
        nombre = (EditText) view.findViewById(R.id.et_nombre);
        pass = (EditText) view.findViewById(R.id.et_pass);
        pais = (EditText) view.findViewById(R.id.et_origenPais);
        ciudad = (EditText) view.findViewById(R.id.et_origenCiudad);
        dob = (EditText) view.findViewById(R.id.et_dob);
        preferencias = (EditText) view.findViewById(R.id.et_preferencias);
        limitaciones = (EditText) view.findViewById(R.id.et_limitaciones);
        obs = (EditText) view.findViewById(R.id.et_obs_cliente);
        btn = (Button) view.findViewById(R.id.btn_cliente);

        preferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Preferencias clicked", Toast.LENGTH_SHORT).show();
            }
        });

        limitaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Limitaciones clicked", Toast.LENGTH_SHORT).show();
            }
        });

        obs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Observaciones clicked", Toast.LENGTH_SHORT).show();
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDates();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentState = myCallback.getCurrentStateClienteFragment();
                switch (currentState) {
                    case STATE_NEW_CLIENTE_MODE:
                        registrar();
                        break;
                    case STATE_CLIENTE_UD_MODE:
                        actualizar();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void handleDates() {
        String currentDate = dob.getText().toString();
        int year = 0;
        int month = 0;
        int day = 0;
        if (currentDate.equals("") || currentDate.equalsIgnoreCase("Fecha de nacimiento")) {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            day = Integer.parseInt(currentDate.substring(0, 2));
            month = Integer.parseInt(currentDate.substring(3, 5)) - 1;
            year = Integer.parseInt(currentDate.substring(6));
        }

        new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                String month_str = toDosLugares(m + 1);
                String day_str = toDosLugares(d);

                String full_date = day_str + "/" + month_str + "/" + y;
                dob.setText(full_date);
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

    private boolean registrar() {
        Cliente newCliente = getNewCliente();
        if (!validarNewCliente(newCliente)) {
            return false;
        }

        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getActivity(), AdminSQLiteOpenHelper.BD_NAME, null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Cliente.CAMPO_NAME, newCliente.getName());
        values.put(Cliente.CAMPO_PASS, newCliente.getPass());
        values.put(Cliente.CAMPO_ORIGEN_PAIS, newCliente.getOrigenPais());
        values.put(Cliente.CAMPO_ORIGEN_CIUDAD, newCliente.getOrigenCiudad());
        values.put(Cliente.CAMPO_DOB, newCliente.getDob());

        bd.insert(Cliente.TABLE_NAME, null, values);

        Toast.makeText(getActivity(), "Cliente registrado correctamente.", Toast.LENGTH_SHORT).show();
        cleanFragment();
        return true;
    }

    private boolean actualizar() {
        Cliente clienteNewInfo = getNewCliente();
        if(!validarNewCliente(clienteNewInfo)){
            return false;
        }
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,1);
        SQLiteDatabase BD = admin.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Cliente.CAMPO_NAME,clienteNewInfo.getName());
        values.put(Cliente.CAMPO_PASS,clienteNewInfo.getPass());
        values.put(Cliente.CAMPO_ORIGEN_PAIS,clienteNewInfo.getOrigenPais());
        values.put(Cliente.CAMPO_ORIGEN_CIUDAD,clienteNewInfo.getOrigenCiudad());
        values.put(Cliente.CAMPO_DOB,clienteNewInfo.getDob());
        BD.update(Cliente.TABLE_NAME,values,"id=?",new String[]{String.valueOf(selectedClient.getId())});
        selectedClient = clienteNewInfo;
        makeToast("Cliente actualizado correctamente");
        myCallback.setNewCurrentStateClienteFragment(ClienteFragment.STATE_CLIENTE_MODE);
        setUpClienteMode(false);
        return true;
    }

    private void cleanFragment() {
        nombre.setText("");
        pass.setText("");
        pais.setText("");
        ciudad.setText("");
        dob.setText(getActivity().getResources().getString(R.string.dob));
    }

    private boolean validarNewCliente(Cliente newCliente) {
        if (newCliente.getName().equals("")) {
            Toast.makeText(getActivity(), "Debe ingresar al menos un nombre.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(myCallback.getCurrentStateClienteFragment().equals(ClienteFragment.STATE_CLIENTE_UD_MODE)&&selectedClient==null){
            makeToast("No se puede actualizar...cliente no seleccionado.");
            return false;
        }
        return true;
    }

    private Cliente getNewCliente() {
        Cliente newCliente = new Cliente();
        newCliente.setName(nombre.getText().toString());
        newCliente.setPass(pass.getText().toString());
        newCliente.setOrigenPais(pais.getText().toString());
        newCliente.setOrigenCiudad(ciudad.getText().toString());
        newCliente.setDob(dob.getText().toString());
        return newCliente;
    }

    private void makeToast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void udClientFromDB(int id){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor cursor = bd.rawQuery("SELECT * FROM "+Cliente.TABLE_NAME+" WHERE id="+id,null);

        if(selectedClient==null){selectedClient=new Cliente();}
        while(cursor.moveToNext()){
            selectedClient.setId(id);
            selectedClient.setName(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_NAME)));
            selectedClient.setPass(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_PASS)));
            selectedClient.setOrigenPais(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_ORIGEN_PAIS)));
            selectedClient.setOrigenCiudad(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_ORIGEN_CIUDAD)));
            selectedClient.setDob(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_DOB)));
        }
    }

    private void showInfoClient(){
        if(selectedClient!=null){
            nombre.setText(selectedClient.getName());
            pass.setText(selectedClient.getPass());
            pais.setText(selectedClient.getOrigenPais());
            ciudad.setText(selectedClient.getOrigenCiudad());
            dob.setText(selectedClient.getDob());
        }
    }

    public interface Callback {
        void setNewCurrentStateClienteFragment(String newCurrentStateClienteFragment);
        String getCurrentStateClienteFragment();
    }
}
