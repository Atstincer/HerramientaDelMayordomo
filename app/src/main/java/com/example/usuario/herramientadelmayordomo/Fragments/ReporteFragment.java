package com.example.usuario.herramientadelmayordomo.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomo.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomo.Almacenamiento.MySharedPreferences;
import com.example.usuario.herramientadelmayordomo.Entities.Estancia;
import com.example.usuario.herramientadelmayordomo.Entities.MyEmail;
import com.example.usuario.herramientadelmayordomo.Entities.Reporte;
import com.example.usuario.herramientadelmayordomo.MainActivity;
import com.example.usuario.herramientadelmayordomo.R;
import com.example.usuario.herramientadelmayordomo.Util.DateHandler;
import com.example.usuario.herramientadelmayordomo.Util.MyApp;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by usuario on 17/10/2022.
 */

public class ReporteFragment extends Fragment {

    public static final String TAG = "ReporteFragment";

    //Flags
    private final int ASUNTO_MAIL = 0;
    private final int CUERPO_MSG = 1;
    private final int NAME_FILE = 2;
    private final int FECHA_NAME_DIRECTORIO = 3;

    /*
    public static final String STATE_REGULAR_MODE = "STATE_REPORTE_REGULAR_MODE";
    public static final String STATE_NEW_REPORTE_MODE = "STATE_NEW_REPORTE_MODE";*/

    private EditText etReporteMañana, etReporteTarde, etReporteNoche;
    private TextView tvDate, tvNoHab;
    private Button btn;

    private long idEstanciaRelacionada;
    private long idReporteRelacionado;
    private CallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_reporte, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myCallBack = (CallBack) context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindComponents(view);
        if (getArguments() != null) {
            idEstanciaRelacionada = (long) getArguments().get("estanciaId");
            idReporteRelacionado = (long) getArguments().get("reporteId");
            /*if (idReporteRelacionado > 0) {
                showInfoFragment(idReporteRelacionado);
                return;
            }*/
            showInfoFragment();
        }
        if (myCallBack.getCurrentStateReporteFragment() == MyApp.STATE_REGULAR) {
            setUpRegularMode();
        } else if (myCallBack.getCurrentStateReporteFragment() == MyApp.STATE_NEW) {
            setCurrentDate();
        }
    }

    private void bindComponents(View v) {
        tvNoHab = (TextView)v.findViewById(R.id.tv_habitacion_reporte_fragment);
        etReporteMañana = (EditText) v.findViewById(R.id.et_reporte_mañana);
        etReporteTarde = (EditText) v.findViewById(R.id.et_reporte_tarde);
        etReporteNoche = (EditText) v.findViewById(R.id.et_reporte_noche);
        tvDate = (TextView) v.findViewById(R.id.tv_date);
        btn = (Button) v.findViewById(R.id.btn_reporte_fragment);

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myCallBack.getCurrentStateReporteFragment() == MyApp.STATE_NEW) {
                    registrar();
                } else if (myCallBack.getCurrentStateReporteFragment() == MyApp.STATE_REGULAR) {
                    actualizar();
                }
            }
        });
    }

    private void setUpRegularMode() {
        if (myCallBack.getCurrentStateReporteFragment() != MyApp.STATE_REGULAR) {
            myCallBack.setCurrentStateReporteFragment(MyApp.STATE_REGULAR);
        }
        btn.setText(getResources().getString(R.string.actualizar));
        getActivity().invalidateOptionsMenu();
    }

    private void setUpNewReportMode() {
        if (myCallBack.getCurrentStateReporteFragment() != MyApp.STATE_NEW) {
            myCallBack.setCurrentStateReporteFragment(MyApp.STATE_NEW);
        }
        resetFragment();
        btn.setText(getResources().getString(R.string.registrar));
        getActivity().invalidateOptionsMenu();
    }

    private void registrar() {
        if (!validarReporte()) {
            return;
        }
        Reporte newReporte = getNewReporte();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues info = new ContentValues();
        info.put(Reporte.CAMPO_ESTANCIA_ID, newReporte.getEstanciaId());
        info.put(Reporte.CAMPO_FECHA, DateHandler.formatDateToStoreInDB(newReporte.getFecha()));
        info.put(Reporte.CAMPO_REPORTE_MAÑANA, newReporte.getReporteMañana());
        info.put(Reporte.CAMPO_REPORTE_TARDE, newReporte.getReporteTarde());
        info.put(Reporte.CAMPO_REPORTE_NOCHE, newReporte.getReporteNoche());
        long id = db.insert(Reporte.TABLE_NAME, null, info);
        if (id != 0) {
            Toast.makeText(getContext(), getResources().getString(R.string.registro_correcto), Toast.LENGTH_SHORT).show();
            idReporteRelacionado = id;
            setUpRegularMode();
        }
    }

    private void actualizar() {
        if (!validarReporte() || idReporteRelacionado == 0) {
            return;
        }
        Reporte reporte = getNewReporte();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues values = Reporte.getContentValues(reporte);
        db.update(Reporte.TABLE_NAME, values, "id=?", new String[]{String.valueOf(idReporteRelacionado)});
        Toast.makeText(getContext(), getResources().getString(R.string.actualizacion_correcta), Toast.LENGTH_SHORT).show();
    }

    private void eliminarReporte() {
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        db.delete(Reporte.TABLE_NAME, "id=?", new String[]{String.valueOf(idReporteRelacionado)});
        Toast.makeText(getContext(), getResources().getString(R.string.registro_eliminado_correctamente), Toast.LENGTH_SHORT).show();
        setUpNewReportMode();
    }

    private void confirmarEliminarReporte() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getResources().getString(R.string.confirmar_eliminar_reporte));
        builder.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                eliminarReporte();
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

    private boolean validarReporte() {
        if (etReporteMañana.getText().toString().equals("") && etReporteTarde.getText().toString().equals("") && etReporteNoche.getText().toString().equals("")) {
            Toast.makeText(getContext(), getResources().getString(R.string.reporte_no_valido), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private Reporte getNewReporte() {
        Reporte reporte = new Reporte();
        reporte.setEstanciaId(idEstanciaRelacionada);
        reporte.setFecha(tvDate.getText().toString());
        reporte.setReporteMañana(etReporteMañana.getText().toString());
        reporte.setReporteTarde(etReporteTarde.getText().toString());
        reporte.setReporteNoche(etReporteNoche.getText().toString());
        return reporte;
    }

    private void showDatePicker() {
        String tv_str = tvDate.getText().toString();
        int year = Integer.parseInt(tv_str.substring(6));
        int month = Integer.parseInt(tv_str.substring(3, 5)) - 1;
        int day = Integer.parseInt(tv_str.substring(0, 2));

        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //tvDate.setText(DateHandler.formatDateToShow(day,month+1,year));
                showDateIfValid(DateHandler.formatDateToShow(day, month + 1, year));
                checkReportsForNewDate();
            }
        }, year, month, day).show();
    }

    private void checkReportsForNewDate() {
        String fechaBD = DateHandler.formatDateToStoreInDB(tvDate.getText().toString());
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Reporte.TABLE_NAME + " WHERE " + Reporte.CAMPO_FECHA + " = '" + fechaBD + "' AND " + Reporte.CAMPO_ESTANCIA_ID + " = " +
                "" + idEstanciaRelacionada, null);
        List<Reporte> listReportes = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Reporte reporte = new Reporte();
                reporte.setId(cursor.getInt(0));
                reporte.setEstanciaId(cursor.getInt(cursor.getColumnIndex(Reporte.CAMPO_ESTANCIA_ID)));
                reporte.setFecha(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_FECHA))));
                reporte.setReporteMañana(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_MAÑANA)));
                reporte.setReporteTarde(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_TARDE)));
                reporte.setReporteNoche(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_NOCHE)));
                listReportes.add(reporte);
            } while (cursor.moveToNext());
        }
        cursor.close();
        if (!listReportes.isEmpty()) {
            if (listReportes.size() > 1) {
                Toast.makeText(getContext(), getResources().getString(R.string.varios_reportes_misma_fecha), Toast.LENGTH_SHORT).show();
            }
            showInfoFragment(listReportes.get(0));
            myCallBack.setCurrentStateReporteFragment(MyApp.STATE_REGULAR);
            setUpRegularMode();
        } else {
            myCallBack.setCurrentStateReporteFragment(MyApp.STATE_NEW);
            setUpNewReportMode();
        }
    }

    private void showInfoFragment(){
        if(idEstanciaRelacionada>0){
            tvNoHab.setText(Estancia.getEstanciaFromDB(getContext(),idEstanciaRelacionada).getNo_hab());
        }
        if(idReporteRelacionado>0){
            showInfoFragment(idReporteRelacionado);
        }
    }

    private void showInfoFragment(long reporteId) {
        showInfoFragment(getReporteFromDB(reporteId));
        setUpRegularMode();
    }

    private void showInfoFragment(Reporte reporte) {
        if (reporte == null) {
            return;
        }
        idReporteRelacionado = reporte.getId();
        if (idEstanciaRelacionada != reporte.getEstanciaId()) {
            idEstanciaRelacionada = reporte.getEstanciaId();
        }
        if (!tvDate.getText().toString().equals(reporte.getFecha())) {
            tvDate.setText(reporte.getFecha());
        }
        cleanET();
        etReporteMañana.setText(reporte.getReporteMañana());
        etReporteTarde.setText(reporte.getReporteTarde());
        etReporteNoche.setText(reporte.getReporteNoche());
    }

    private Reporte getReporteFromDB(long reporteId) {
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Reporte.TABLE_NAME + " WHERE id=? AND " + Reporte.CAMPO_ESTANCIA_ID + "=?",
                new String[]{String.valueOf(reporteId), String.valueOf(idEstanciaRelacionada)});
        Reporte reporte = new Reporte();
        if (cursor.moveToFirst()) {
            reporte.setId(cursor.getLong(0));
            reporte.setEstanciaId(cursor.getInt(cursor.getColumnIndex(Reporte.CAMPO_ESTANCIA_ID)));
            reporte.setFecha(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_FECHA))));
            reporte.setReporteMañana(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_MAÑANA)));
            reporte.setReporteTarde(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_TARDE)));
            reporte.setReporteNoche(cursor.getString(cursor.getColumnIndex(Reporte.CAMPO_REPORTE_NOCHE)));
        }
        cursor.close();
        return reporte;
    }

    private void setCurrentDate() {
        Calendar today = Calendar.getInstance();
        int day = today.get(Calendar.DAY_OF_MONTH);
        int month = today.get(Calendar.MONTH);
        int year = today.get(Calendar.YEAR);
        System.out.println("Today: "+DateHandler.formatDateToShow(day, month + 1, year));
        showDateIfValid(DateHandler.formatDateToShow(day, month + 1, year));
        //tvDate.setText(DateHandler.formatDateToShow(day,month+1,year));
        checkReportsForNewDate();
    }

    private void sendMail() {
        MyEmail email = new MyEmail();
        email.setPara(new String[]{MySharedPreferences.getDefaultMail(getContext())});
        email.setAsunto(getFormatMSG(ASUNTO_MAIL));
        email.setCuerpo(getFormatMSG(CUERPO_MSG));
        MyEmail.setUpEmail(getContext(),email);

        /*Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL,new String[]{MySharedPreferences.getDefaultMail(getContext())});
        intent.putExtra(Intent.EXTRA_SUBJECT, getFormatMSG(ASUNTO_MAIL));
        intent.putExtra(Intent.EXTRA_TEXT, getFormatMSG(CUERPO_MSG));
        //intent.setType("message/rfc822");
        intent.setData(Uri.parse("mailto:"));
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), getString(R.string.no_aplicacion_disponible), Toast.LENGTH_SHORT).show();
        }*/
    }

    private String getFormatMSG(int info) {
        Estancia estancia = Estancia.getEstanciaFromDB(getContext(),idEstanciaRelacionada);
        String noHab = "-";
        if (!estancia.getNo_hab().equals("")) {
            noHab = estancia.getNo_hab();
        }
        String msg = "";
        if (info == ASUNTO_MAIL) {
            msg = estancia.getFamilyName() + " " + getString(R.string.Hab) + ":" + noHab + " " + getString(R.string.reportes) + " " + tvDate.getText().toString();
        } else if (info == NAME_FILE) {
            String fecha = tvDate.getText().toString();
            String fechaTitle = fecha.substring(0, 2) + fecha.substring(3, 5) + fecha.substring(8);
            msg = noHab + " " + fechaTitle + "_" + estancia.getFamilyName();
        } else if (info == CUERPO_MSG) {
            Reporte reporte = getReporteFromDB(idReporteRelacionado);
            msg = getString(R.string.reportes)+" "+tvDate.getText().toString() + "\t\t\t" + getString(R.string.Hab) + ":" + noHab + "\n" +
                    "" + estancia.getFamilyName() + "\n" +
                    "" + getString(R.string.estancia) + ": " + Estancia.formatPeriodoToShow(getContext(), estancia.getDesde(), estancia.getHasta()) + "\n\n\n" +
                    "" + getString(R.string.reporte_mañana) + "\n" +
                    "" + reporte.getReporteMañana() + "\n\n" +
                    "" + getString(R.string.reporte_tarde) + "\n" +
                    "" + reporte.getReporteTarde() + "\n\n" +
                    "" + getString(R.string.reporte_noche) + "\n" +
                    "" + reporte.getReporteNoche();
        }else if(info == FECHA_NAME_DIRECTORIO){
            String fecha = tvDate.getText().toString();
            msg = fecha.substring(0, 2) + fecha.substring(3, 5) + fecha.substring(8);
        }
        return msg;
    }


    private void generateTXT() {

        boolean sdDisponible = false;
        boolean sdAccesoEscritura = false;

        //Comprobamos el estado de la memoria externa (tarjeta SD)
        switch (Environment.getExternalStorageState()) {
            case Environment.MEDIA_MOUNTED:
                sdDisponible = true;
                sdAccesoEscritura = true;
                break;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                sdDisponible = true;
                sdAccesoEscritura = false;
                break;
            default:
                sdDisponible = false;
                sdAccesoEscritura = false;
                break;
        }

        if (!sdDisponible) {
            Toast.makeText(getContext(), getString(R.string.tarjetasd_no_disponible), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!sdAccesoEscritura) {
            Toast.makeText(getContext(), getString(R.string.tarjetasd_no_writable), Toast.LENGTH_SHORT).show();
            return;
        }


        try {
            //File rutaSD = Environment.getExternalStorageDirectory();
            File rutaSD = new File(Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name));
            if(!rutaSD.exists()){rutaSD.mkdir();}
            rutaSD = new File(rutaSD.getAbsolutePath()+"/"+getString(R.string.reportes));
            if(!rutaSD.exists()){rutaSD.mkdir();}
            rutaSD = new File(rutaSD.getAbsolutePath()+"/"+getFormatMSG(FECHA_NAME_DIRECTORIO));
            if(!rutaSD.exists()){rutaSD.mkdir();}
            //File rutaSD = Environment.getExternalFilesDir(null);
            File file = new File(rutaSD.getAbsolutePath(), getFormatMSG(NAME_FILE) + ".txt");
            OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(file));
            fout.write(getFormatMSG(CUERPO_MSG));
            fout.flush();
            fout.close();
            Toast.makeText(getContext(),getString(R.string.archivo_creado),Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //System.out.println("Mensaje error: " + e.getMessage());
            Toast.makeText(getContext(), "Mensaje error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //lo crea en almacenamiento interno MODO Privado...no se ve desde otras apps
        /*try{
            OutputStreamWriter archivo = new OutputStreamWriter(getContext().openFileOutput(getFormatMSG(NAME_FILE)+".txt", Activity.MODE_PRIVATE));
            archivo.write(getFormatMSG(CUERPO_MSG));
            archivo.flush();
            archivo.close();
            Toast.makeText(getContext(),getString(R.string.archivo_creado),Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }*/
    }

    private void checkForPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_EXTORAGE);
        } else {
            // Permission is already granted, call the function that does what you need
            generateTXT();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        System.out.println("onRequestPermissionResult....");
        System.out.println("requestCode: "+requestCode);

        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MainActivity.REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_EXTORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("granResults.length: "+grantResults.length);

                    // permission was granted, yay!....call the function that does what you need
                    generateTXT();
                } else {
                    Log.e(TAG, "Write permissions has to be granted tp ATMS, otherwise it cannot operate properly.\n Exiting the program...\n");
                }
                break;
            }

            // other 'case' lines to check for other permissions this app might request.
        }
    }

    private void showDateIfValid(String fecha) {
        String fechaAnterior = tvDate.getText().toString();
        Estancia estancia = Estancia.getEstanciaFromDB(getContext(),idEstanciaRelacionada);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date desdeDate = sdf.parse(estancia.getDesde());
            Date hastaDate = sdf.parse(estancia.getHasta());
            Date newDate = sdf.parse(fecha);
            if (newDate.after(desdeDate) && newDate.before(hastaDate) || newDate.equals(desdeDate) || newDate.equals(hastaDate)) {
                tvDate.setText(fecha);
                return;
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(getContext(), R.string.la_fecha_no_es_correcta, Toast.LENGTH_SHORT).show();

        if (!fechaAnterior.equals("") && !fechaAnterior.equals("Fecha")) {
            tvDate.setText(fechaAnterior);
        } else {
            tvDate.setText(estancia.getDesde());
        }
    }

    /*private Estancia getEstanciaRelacionada() {
        Estancia estancia = new Estancia();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Estancia.TABLE_NAME + " WHERE id = " + idEstanciaRelacionada, null);
        if (cursor.moveToFirst()) {
            estancia.setId(idEstanciaRelacionada);
            estancia.setDesde(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_DESDE))));
            estancia.setHasta(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_HASTA))));
            estancia.setNo_hab(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_NO_HAB)));
            estancia.setFamilyName(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_FAMILY_NAME)));
            estancia.setAdultos(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_ADULTOS)));
            estancia.setObservaciones(cursor.getString(cursor.getColumnIndex(Estancia.CAMPO_OBSERVACIONES)));
            estancia.setMenores(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_MENORES)));
            estancia.setInfantes(cursor.getInt(cursor.getColumnIndex(Estancia.CAMPO_INFANTES)));
        }
        cursor.close();
        return estancia;
    }*/

    private void resetFragment() {
        idReporteRelacionado = 0;
        cleanET();
    }

    private void cleanET() {
        etReporteMañana.setText("");
        etReporteTarde.setText("");
        etReporteNoche.setText("");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {
            menu.clear();
        }
        switch (myCallBack.getCurrentStateReporteFragment()) {
            case MyApp.STATE_REGULAR:
                inflater.inflate(R.menu.menu_reporte_fragment_regular, menu);
                break;
        }
        inflater.inflate(R.menu.menu_main,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_eliminar_reporte:
                confirmarEliminarReporte();
                break;
            case R.id.menu_item_enviar_por_mail:
                sendMail();
                break;
            case R.id.menu_item_guardar_txt:
                checkForPermissions();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface CallBack {
        int getCurrentStateReporteFragment();

        void setCurrentStateReporteFragment(int state);
    }
}
