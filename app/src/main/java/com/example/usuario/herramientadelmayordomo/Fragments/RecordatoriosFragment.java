package com.example.usuario.herramientadelmayordomo.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomo.Adapters.RecordatoriosRVAdaptar;
import com.example.usuario.herramientadelmayordomo.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.herramientadelmayordomo.Entities.Recordatorio;
import com.example.usuario.herramientadelmayordomo.R;
import com.example.usuario.herramientadelmayordomo.Util.DateHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by usuario on 24/11/2022.
 */

public class RecordatoriosFragment extends Fragment implements RecordatoriosRVAdaptar.CallBack {

    public static final String TAG = "RecordatoriosFragment";

    private TextView tvFecha, tvNoRecordatoriosRegistrados;
    private EditText etMsjNotificacion;
    private RecyclerView rvRecordatorios;


    private List<Recordatorio> listRecordatorios = new ArrayList<>();

    private RecordatoriosRVAdaptar adapter;
    private CallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recordatorios, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindComponents(view);
        myCallBack.udActivity(TAG);
        udInfoFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myCallBack = (CallBack) context;
    }

    private void bindComponents(View view) {
        tvFecha = (TextView) view.findViewById(R.id.tv_fecha_nuevo_recordatorio);
        etMsjNotificacion = (EditText) view.findViewById(R.id.et_msj_recordatorio);
        TextView btnAgregarRecordatorio = (TextView) view.findViewById(R.id.btn_agregar_recordatorio);
        rvRecordatorios = (RecyclerView) view.findViewById(R.id.rv_recordatorios);
        tvNoRecordatoriosRegistrados = (TextView) view.findViewById(R.id.tv_no_recordatorios_registrados);

        tvFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDatePickerDialog();
            }
        });

        btnAgregarRecordatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validarRecordatorio()) {
                    return;
                }
                Recordatorio recordatorio = new Recordatorio(0,tvFecha.getText().toString(),
                        getResources().getString(R.string.recordatorio_general),etMsjNotificacion.getText().toString(),0);
                if(Recordatorio.setAlarm(getContext(),recordatorio,true)){
                    resetNuevoRecordatorio();
                    udInfoFragment();
                    Toast.makeText(getContext(),getResources().getString(R.string.recordatorio_agregado),Toast.LENGTH_SHORT).show();
                }
                /*if(Recordatorio.agregarRecordatorio(getContext(),tvFecha.getText().toString(),getResources().getString(R.string.recordatorio_general)
                        ,etMsjNotificacion.getText().toString())){
                    resetNuevoRecordatorio();
                    udInfoFragment();
                    Toast.makeText(getContext(),getResources().getString(R.string.recordatorio_agregado),Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        adapter = new RecordatoriosRVAdaptar(getContext(), this);
        rvRecordatorios.setAdapter(adapter);
        rvRecordatorios.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private boolean validarRecordatorio() {
        if (tvFecha.getText().toString().equals(getResources().getString(R.string.seleccionar_fecha))) {
            Toast.makeText(getContext(), getResources().getString(R.string.debe_seleccionar_una_fecha), Toast.LENGTH_SHORT).show();
            return false;
        } else if (etMsjNotificacion.getText().toString().equals("")) {
            Toast.makeText(getContext(), getResources().getString(R.string.debe_agregar_msj), Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Recordatorio.validarTimeStamp(tvFecha.getText().toString())){
            Toast.makeText(getContext(),getString(R.string.la_fecha_no_es_correcta),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void resetNuevoRecordatorio() {
        tvFecha.setText(getResources().getString(R.string.seleccionar_fecha));
        etMsjNotificacion.setText("");
    }

    private void udInfoFragment() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getRecordatoriosFromDB();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showRecordatoriosIfExist();
                    }
                });
            }
        }).start();
    }

    private void getRecordatoriosFromDB() {
        listRecordatorios.clear();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        String today = DateHandler.getToday(DateHandler.FECHA_FORMATO_BD);

        Cursor cursor = db.rawQuery("SELECT * FROM " + Recordatorio.TABLA_NAME + " WHERE '"+today+"'<="+Recordatorio.CAMPO_FECHA, null);
        if (cursor.moveToFirst()) {
            do {
                Recordatorio recordatorio = Recordatorio.getRecordatorio(cursor);
                /*recordatorio.setId(cursor.getLong(0));
                recordatorio.setFecha(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(Recordatorio.CAMPO_FECHA))));
                recordatorio.setTitle(cursor.getString(cursor.getColumnIndex(Recordatorio.CAMPO_TITULO)));
                recordatorio.setMensaje(cursor.getString(cursor.getColumnIndex(Recordatorio.CAMPO_MENSAJE)));*/
                listRecordatorios.add(recordatorio);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void showRecordatoriosIfExist() {
        if (listRecordatorios.size() > 0) {
            Collections.sort(listRecordatorios, Recordatorio.dateAscending);
            adapter.setListRecordatorios(listRecordatorios);
            rvRecordatorios.setVisibility(View.VISIBLE);
            tvNoRecordatoriosRegistrados.setVisibility(View.GONE);
        } else {
            rvRecordatorios.setVisibility(View.GONE);
            tvNoRecordatoriosRegistrados.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void requestEliminarRecordatorio(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.confirmar_eliminar_recordatorio));
        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        builder.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                eliminarRecordatorio(listRecordatorios.get(position));
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void eliminarRecordatorio(final Recordatorio recordatorio) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Recordatorio.cancelarAlarma(getContext(),recordatorio);
                AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
                SQLiteDatabase db = admin.getWritableDatabase();
                db.delete(Recordatorio.TABLA_NAME, "id=?", new String[]{String.valueOf(recordatorio.getId())});
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), getString(R.string.registro_eliminado_correctamente), Toast.LENGTH_SHORT).show();
                        udInfoFragment();
                    }
                });
            }
        }).start();
    }

    private void handleDatePickerDialog() {

        String tv_str = tvFecha.getText().toString();
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
                tvFecha.setText(DateHandler.formatDateToShow(day, month + 1, year));
            }
        }, year, month, day).show();
    }

    public interface CallBack {
        void udActivity(String fragmentTag);
    }
}
