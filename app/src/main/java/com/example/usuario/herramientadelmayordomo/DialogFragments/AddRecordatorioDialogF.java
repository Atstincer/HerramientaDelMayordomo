package com.example.usuario.herramientadelmayordomo.DialogFragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomo.Almacenamiento.MySharedPreferences;
import com.example.usuario.herramientadelmayordomo.Entities.Estancia;
import com.example.usuario.herramientadelmayordomo.Entities.Recordatorio;
import com.example.usuario.herramientadelmayordomo.R;
import com.example.usuario.herramientadelmayordomo.Util.DateHandler;

import java.util.Calendar;

/**
 * Created by usuario on 14/12/2022.
 */

public class AddRecordatorioDialogF extends DialogFragment {

    public static final String TAG = "AddRecordatorioDialogF";

    private TextView tvFecha;
    private Button btnCancelar, btnAgregar;

    private CallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_agregar_recordatorio,container,false);
        bindComponents(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            myCallBack = (CallBack)getTargetFragment();
        }catch (ClassCastException e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void bindComponents(View view){
        tvFecha = (TextView)view.findViewById(R.id.tv_fecha_recordatodio_dialogfragment);
        btnCancelar = (Button)view.findViewById(R.id.btn_cancel_recordatorio_dialogfragment);
        btnAgregar = (Button)view.findViewById(R.id.btn_ok_recordatorio_dialogfragment);

        tvFecha.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                handleDatePickerDialog();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnAgregar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                agregarRecordatorio();
            }
        });
    }

    private void handleDatePickerDialog() {

        final String tvFecha_str = tvFecha.getText().toString();
        int year = 0;
        int month = 0;
        int day = 0;

        if (tvFecha_str.equalsIgnoreCase(getResources().getString(R.string.seleccionar_fecha))) {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            day = Integer.parseInt(tvFecha_str.substring(0, 2));
            month = Integer.parseInt(tvFecha_str.substring(3, 5)) - 1;
            year = Integer.parseInt(tvFecha_str.substring(6));
        }

        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                tvFecha.setText(DateHandler.formatDateToShow(day, month + 1, year));
            }
        }, year, month, day).show();
    }

    private void agregarRecordatorio(){
        if(!validarFecha()){return;}
        Estancia estancia = Estancia.getEstanciaFromDB(getContext(),myCallBack.getSelectedEstanciaId());

        Recordatorio.setAlarm(getContext(),
                new Recordatorio(0,tvFecha.getText().toString(),estancia.getTitleRecordatorio(),estancia.getMensajeRecordatodio(getContext()),estancia.getId()),
                true);
        Toast.makeText(getContext(),getString(R.string.recordatorio_agregado),Toast.LENGTH_SHORT).show();
        dismiss();
    }

    private boolean validarFecha(){
        if(tvFecha.getText().toString().equals(getString(R.string.seleccionar_fecha))){
            Toast.makeText(getContext(),getString(R.string.debe_seleccionar_una_fecha),Toast.LENGTH_SHORT).show();
            return false;
        } else if(!Recordatorio.validarTimeStamp(tvFecha.getText().toString())){
            Toast.makeText(getContext(),getString(R.string.la_fecha_no_es_correcta),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public interface CallBack{
        long getSelectedEstanciaId();
    }
}
