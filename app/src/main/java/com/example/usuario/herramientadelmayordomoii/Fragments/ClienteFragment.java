package com.example.usuario.herramientadelmayordomoii.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomoii.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomoii.Entities.Cliente;
import com.example.usuario.herramientadelmayordomoii.Entities.FamilyName;
import com.example.usuario.herramientadelmayordomoii.Entities.FamilyNames_Clientes;
import com.example.usuario.herramientadelmayordomoii.Interfaces.IMyFragments;
import com.example.usuario.herramientadelmayordomoii.MainActivity;
import com.example.usuario.herramientadelmayordomoii.R;
import com.example.usuario.herramientadelmayordomoii.Util.MyApp;
import com.example.usuario.herramientadelmayordomoii.Util.MyBitmapFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by usuario on 6/11/2021.
 */


public class ClienteFragment extends Fragment implements IMyFragments {

    public static final String TAG = "ClienteFragment";
    /*
    public static final String STATE_CLIENTE_MODE = "STATE_CLIENTE_MODE";
    public static final String STATE_CLIENTE_UD_MODE = "STATE_CLIENTE_UD_MODE";
    public static final String STATE_NEW_CLIENTE_MODE = "STATE_NEW_CLIENTE_MODE";*/

    private Callback myCallback;
    private ImageView foto;
    private EditText nombre, pass, pais, ciudad, dob, preferencias, limitaciones, obs;
//    private TextView preferencias, limitaciones, obs;
    private Button btn;

    private Cliente selectedClient;

    private int TOMAR_FOTO = 100;
    private int SELEC_IMAGEN = 200;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
        if (myCallback.getCurrentStateClienteFragment()== MyApp.STATE_REGULAR) {
            inflater.inflate(R.menu.menu_cliente_mode, menu);
        } else if (myCallback.getCurrentStateClienteFragment()==MyApp.STATE_UPDATE) {
            inflater.inflate(R.menu.menu_cliente_update_mode, menu);
        }
        inflater.inflate(R.menu.menu_main, menu);
        //super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_editar:
                myCallback.setNewCurrentStateClienteFragment(MyApp.STATE_UPDATE);
                setUpNewState(MyApp.STATE_UPDATE);
                break;
            case R.id.menu_item_eliminar_cliente:
                confirmarEliminarCliente();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindComponents(View view) {
        foto = (ImageView) view.findViewById(R.id.iv_foto_cliente);
        nombre = (EditText) view.findViewById(R.id.et_nombre);
        pass = (EditText) view.findViewById(R.id.et_pass);
        pais = (EditText) view.findViewById(R.id.et_origenPais);
        ciudad = (EditText) view.findViewById(R.id.et_origenCiudad);
        dob = (EditText) view.findViewById(R.id.et_dob);
        preferencias = (EditText) view.findViewById(R.id.et_preferencias);
        limitaciones = (EditText) view.findViewById(R.id.et_limitaciones);
        obs = (EditText) view.findViewById(R.id.et_obs_cliente);
        btn = (Button) view.findViewById(R.id.btn_cliente);

        foto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    makeToast(getResources().getString(R.string.no_camara_disponible));
                    return false;
                }
                /*if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
                }*/
                PopupMenu menu = new PopupMenu(getContext(), view);
                try {
                    ((BitmapDrawable) foto.getDrawable()).getBitmap();
                    menu.getMenuInflater().inflate(R.menu.menu_picture_deselect, menu.getMenu());
                } catch (Exception e) {
                    //do nothing
                }
                menu.getMenuInflater().inflate(R.menu.menu_picture_clicked, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_item_tomar_foto:
                                tomarFoto();
                                break;
                            case R.id.menu_item_seleccionar_foto:
                                selectImagen();
                                break;
                            case R.id.menu_item_eliminar_foto:
                                foto.setImageResource(R.drawable.ic_cliente);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                menu.show();
                return false;
            }
        });

        /*
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
        });*/


        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDates();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (myCallback.getCurrentStateClienteFragment()) {
                    case MyApp.STATE_NEW:
                        registrarCliente();
                        break;
                    case MyApp.STATE_UPDATE:
                        actualizarCliente();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void setUpNewState(int state) {
        switch (state) {
            case MyApp.STATE_REGULAR:
                setUpRegularMode(true);
                break;
            case MyApp.STATE_UPDATE:
                setUpUpdateMode();
                break;
            case MyApp.STATE_NEW:
                setUpNewClienteMode();
                break;
            default:
                break;
        }
    }


    private void setUpRegularMode(boolean checkForArguments) {
        if (checkForArguments) {
            if (getArguments() != null) {
                Bundle bundle = getArguments();
                udClientFromDB(bundle.getInt("id"));
            }
        } else {
            if (selectedClient != null) {
                udClientFromDB(selectedClient.getId());
            }
        }
        setViewEditable(false);
//        showViews(true);
        btn.setVisibility(View.GONE);
        getActivity().invalidateOptionsMenu();
        showInfoClient();
        myCallback.udActivity(ClienteFragment.TAG);
    }

    private void setUpUpdateMode() {
        setViewEditable(true);
//        showViews(false);
        btn.setVisibility(View.VISIBLE);
        btn.setText(getResources().getString(R.string.actualizar));
        getActivity().invalidateOptionsMenu();
        myCallback.udActivity(ClienteFragment.TAG);
    }

    private void setUpNewClienteMode() {
        setViewEditable(true);
//        showViews(false);
        btn.setVisibility(View.VISIBLE);
        btn.setText(getResources().getString(R.string.registrar));
        myCallback.udActivity(ClienteFragment.TAG);
    }

    /*
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
    }*/

    private void setViewEditable(boolean choise) {
        foto.setEnabled(choise);
        nombre.setEnabled(choise);
        pass.setEnabled(choise);
        pais.setEnabled(choise);
        ciudad.setEnabled(choise);
        dob.setEnabled(choise);
        preferencias.setEnabled(choise);
        limitaciones.setEnabled(choise);
        obs.setEnabled(choise);
    }

    private void handleDates() {
        String currentDate = dob.getText().toString();
        int year = 0;
        int month = 0;
        int day = 0;
        if (currentDate.equals("") || currentDate.equals(getResources().getString(R.string.dob))) {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            day = Integer.parseInt(currentDate.substring(0,2));
            month = Integer.parseInt(currentDate.substring(3,5))-1;
            year = Integer.parseInt(currentDate.substring(6));
        }

        new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                String month_str = toDosLugares(m+1);
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

    private void cleanFragment() {
        foto.setImageResource(R.drawable.ic_cliente);
        nombre.setText("");
        pass.setText("");
        pais.setText("");
        ciudad.setText("");
        dob.setText("");
        preferencias.setText("");
        limitaciones.setText("");
        obs.setText("");
    }

    private boolean validarNewCliente(Cliente newCliente) {
        if (newCliente.getName().equals("")) {
            Toast.makeText(getActivity(), "Debe ingresar al menos un nombre.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (myCallback.getCurrentStateClienteFragment()==MyApp.STATE_UPDATE && selectedClient == null) {
            makeToast("No se puede actualizar cliente...cliente no seleccionado.");
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
        try {
            newCliente.setFoto(((BitmapDrawable) foto.getDrawable()).getBitmap());
        } catch (Exception e) {
            //do nothing
        }
        newCliente.setPreferencias(preferencias.getText().toString());
        newCliente.setLimitaciones(limitaciones.getText().toString());
        newCliente.setObservaciones(obs.getText().toString());
        return newCliente;
    }

    private void makeToast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void udClientFromDB(int id) {
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor cursor = bd.rawQuery("SELECT * FROM " + Cliente.TABLE_NAME + " WHERE id=" + id, null);

        if (selectedClient == null) {
            selectedClient = new Cliente();
        }
        if (cursor.moveToFirst()) {
            selectedClient.setId(id);
            selectedClient.setName(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_NAME)));
            selectedClient.setPass(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_PASS)));
            selectedClient.setOrigenPais(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_ORIGEN_PAIS)));
            selectedClient.setOrigenCiudad(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_ORIGEN_CIUDAD)));
            selectedClient.setDob(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_DOB)));
            selectedClient.setFoto(cursor.getBlob(cursor.getColumnIndex(Cliente.CAMPO_FOTO)));
            selectedClient.setPreferencias(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_PREFERENCIAS)));
            selectedClient.setLimitaciones(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_LIMITACIONES)));
            selectedClient.setObservaciones(cursor.getString(cursor.getColumnIndex(Cliente.CAMPO_OBSERVACIONES)));
        }
        cursor.close();
    }

    private void showInfoClient() {
        if (selectedClient != null) {
            if (selectedClient.getFoto() != null) {
                foto.setImageBitmap(MyBitmapFactory.getScaledBitmap(selectedClient.getFoto(), foto));
            } else if (selectedClient.getFoto() == null) {
                foto.setImageResource(R.drawable.ic_cliente);
            }
            nombre.setText(selectedClient.getName());
            pass.setText(selectedClient.getPass());
            pais.setText(selectedClient.getOrigenPais());
            ciudad.setText(selectedClient.getOrigenCiudad());
            dob.setText(selectedClient.getDob());
            preferencias.setText(selectedClient.getPreferencias());
            limitaciones.setText(selectedClient.getLimitaciones());
            obs.setText(selectedClient.getObservaciones());
        }
    }

    private void tomarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, TOMAR_FOTO);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
            // TODO: 25/11/2021 handle the exception 
        }
    }

    private void selectImagen() {
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galeria, SELEC_IMAGEN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == MainActivity.RESULT_OK && requestCode == SELEC_IMAGEN) {
            Uri imagenUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imagenUri);
                foto.setImageBitmap(MyBitmapFactory.getScaledBitmap(bitmap, foto));
            } catch (IOException e) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == MainActivity.RESULT_OK && requestCode == TOMAR_FOTO) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            foto.setImageBitmap(MyBitmapFactory.getScaledBitmap(imageBitmap, foto));
        }
    }

    private void confirmarEliminarCliente() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getResources().getString(R.string.confirmar_eliminar_registro));
        builder.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                eliminarCliente();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        AlertDialog confirmationDialog = builder.create();
        confirmationDialog.show();
    }

    private boolean registrarCliente() {
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
        values.put(Cliente.CAMPO_FOTO, newCliente.getFotoInBytes());
        values.put(Cliente.CAMPO_PREFERENCIAS, newCliente.getPreferencias());
        values.put(Cliente.CAMPO_LIMITACIONES,newCliente.getLimitaciones());
        values.put(Cliente.CAMPO_OBSERVACIONES,newCliente.getObservaciones());

        long id_newClient = bd.insert(Cliente.TABLE_NAME, null, values);

        /*
        if (id_newClient > 0) {
            values.clear();
            values.put(FamilyName.CAMPO_FAMILY_NAME, newCliente.getName() + " x 1");
            long id_newFamilyName = bd.insert(FamilyName.TABLE_NAME, null, values);

            if (id_newFamilyName > 0) {
                values.clear();
                values.put(FamilyNames_Clientes.CAMPO_CLIENTE_ID, id_newClient);
                values.put(FamilyNames_Clientes.CAMPO_FAMILY_NAME_ID, id_newFamilyName);
                long conf = bd.insert(FamilyNames_Clientes.TABLE_NAME, null, values);
                if (conf > 0) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.registro_correcto), Toast.LENGTH_SHORT).show();
                    cleanFragment();
                }
            }
        }*/

        if (id_newClient > 0){
            Toast.makeText(getActivity(), getResources().getString(R.string.registro_correcto), Toast.LENGTH_SHORT).show();
            cleanFragment();
        } else {
            return false;
        }
        return true;
    }

    private boolean actualizarCliente() {
        Cliente clienteNewInfo = getNewCliente();
        if (!validarNewCliente(clienteNewInfo)) {
            return false;
        }
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, 1);
        SQLiteDatabase BD = admin.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Cliente.CAMPO_NAME, clienteNewInfo.getName());
        values.put(Cliente.CAMPO_PASS, clienteNewInfo.getPass());
        values.put(Cliente.CAMPO_ORIGEN_PAIS, clienteNewInfo.getOrigenPais());
        values.put(Cliente.CAMPO_ORIGEN_CIUDAD, clienteNewInfo.getOrigenCiudad());
        values.put(Cliente.CAMPO_DOB, clienteNewInfo.getDob());
        values.put(Cliente.CAMPO_FOTO, clienteNewInfo.getFotoInBytes());
        values.put(Cliente.CAMPO_PREFERENCIAS, clienteNewInfo.getPreferencias());
        values.put(Cliente.CAMPO_LIMITACIONES,clienteNewInfo.getLimitaciones());
        values.put(Cliente.CAMPO_OBSERVACIONES,clienteNewInfo.getObservaciones());

        BD.update(Cliente.TABLE_NAME, values, "id=?", new String[]{String.valueOf(selectedClient.getId())});

        /*
        if (!selectedClient.getName().equals(clienteNewInfo.getName())) {
            values.clear();
            values.put(FamilyName.CAMPO_FAMILY_NAME, clienteNewInfo.getName() + " x 1");
            BD.update(FamilyName.TABLE_NAME, values, FamilyName.CAMPO_FAMILY_NAME + "=?", new String[]{selectedClient.getName() + " x 1"});
        }*/

        selectedClient = clienteNewInfo;
        makeToast(getResources().getString(R.string.actualizacion_correcta));
        myCallback.setNewCurrentStateClienteFragment(MyApp.STATE_REGULAR);
        setUpRegularMode(false);
        return true;
    }

    private void eliminarCliente() {
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();

        List<Integer> familyNamesIdsToDelete = new ArrayList<>();

        Cursor cursor = bd.rawQuery("SELECT " + FamilyNames_Clientes.CAMPO_FAMILY_NAME_ID +
                " FROM " + FamilyNames_Clientes.TABLE_NAME + " WHERE " + FamilyNames_Clientes.CAMPO_CLIENTE_ID + "="+selectedClient.getId(),null);

        if(cursor.moveToFirst()){
            do {
                int idFamilyName = cursor.getInt(0);
                Cursor cursorII = bd.rawQuery("SELECT * FROM " + FamilyNames_Clientes.TABLE_NAME + " WHERE " + FamilyNames_Clientes.CAMPO_FAMILY_NAME_ID + "=" +
                        idFamilyName,null);
                if(cursorII.moveToFirst()){
                    if(cursorII.getCount()==2){
                        familyNamesIdsToDelete.add(idFamilyName);
                    }
                }
                cursorII.close();
            }while(cursor.moveToNext());
        }
        cursor.close();

        bd.execSQL("DELETE FROM " + Cliente.TABLE_NAME + " WHERE id=" + selectedClient.getId());
        bd.execSQL("DELETE FROM " + FamilyNames_Clientes.TABLE_NAME + " WHERE " + FamilyNames_Clientes.CAMPO_CLIENTE_ID + "=" + selectedClient.getId());

        //bd.execSQL("DELETE FROM " + FamilyName.TABLE_NAME + " WHERE " + FamilyName.CAMPO_FAMILY_NAME + "='" + selectedClient.getName() + " x 1'");

        if(familyNamesIdsToDelete.size()>0){
            for(int i=0; i<familyNamesIdsToDelete.size(); i++){
                bd.execSQL("DELETE FROM " + FamilyName.TABLE_NAME + " WHERE id = " + familyNamesIdsToDelete.get(i));
            }
        }

        makeToast(getResources().getString(R.string.registro_eliminado_correctamente));
        selectedClient = null;
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public interface Callback {
        void setNewCurrentStateClienteFragment(int newCurrentStateClienteFragment);
        int getCurrentStateClienteFragment();
        void setUpClientesFragment();
        void udActivity(String tag);
    }
}
