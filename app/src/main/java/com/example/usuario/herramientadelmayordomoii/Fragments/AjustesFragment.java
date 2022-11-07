package com.example.usuario.herramientadelmayordomoii.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomoii.Entities.Recordatorio;
import com.example.usuario.herramientadelmayordomoii.Almacenamiento.MySharedPreferences;
import com.example.usuario.herramientadelmayordomoii.R;
import com.example.usuario.herramientadelmayordomoii.Util.DateHandler;

import java.util.Calendar;


/**
 * Created by usuario on 5/11/2021.
 */

public class AjustesFragment extends Fragment {

    private TextView tvFechas;
    private EditText etDiasDeAntelacion,etMsjNotificacion,etMailAddress;
    private ImageView ivRecordatorioCheck, ivDefaultMailCheck;
    private boolean recordatorioEstanciaisChecked,defaultMailChecked;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ajustes,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindComponents(view);
        setUpPreferences();
        //requestCode = 0;
    }

    @Override
    public void onPause() {
        storePreferences();
        super.onPause();
    }

    private void bindComponents(View view){
        tvFechas = (TextView)view.findViewById(R.id.tv_fecha_nuevo_recordatorio);
        TextView btnAgregarRecordatorio = (TextView)view.findViewById(R.id.btn_agregar_recordatorio);
        etDiasDeAntelacion = (EditText)view.findViewById(R.id.et_dias_antelacion);
        etMsjNotificacion = (EditText)view.findViewById(R.id.et_msj_recordatorio);
        ivRecordatorioCheck = (ImageView)view.findViewById(R.id.iv_check_recordatorios);
        etMailAddress = (EditText)view.findViewById(R.id.et_mail);
        ivDefaultMailCheck = (ImageView)view.findViewById(R.id.iv_check_mail);
        RelativeLayout layoutActivarRecordatorios = (RelativeLayout)view.findViewById(R.id.layout_activar_recordatorio);
        RelativeLayout layoutActivarMailPorDefecto = (RelativeLayout)view.findViewById(R.id.layout_enviar_por_defecto_mail);

        tvFechas.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                handleDatePickerDialog();
            }
        });

        layoutActivarRecordatorios.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(recordatorioEstanciaisChecked){setRecordatorioEstenciaActivado(false);}
                else {setRecordatorioEstenciaActivado(true);}
            }
        });

        layoutActivarMailPorDefecto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(defaultMailChecked){
                    setDefaultMailActivado(false);}
                else {
                    setDefaultMailActivado(true);}
            }
        });


        btnAgregarRecordatorio.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!validarRecordatorio()){return;}
                //agregarRecordatorio();

                if(Recordatorio.agregarRecordatorio(getContext(),tvFechas.getText().toString(),getResources().getString(R.string.recordatorio_general)
                        ,etMsjNotificacion.getText().toString())){
                    resetNuevoRecordatorio();
                    Toast.makeText(getContext(),getResources().getString(R.string.recordatorio_agregado),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUpPreferences(){
        if(MySharedPreferences.getRecordatorioEstanciasIsActivado(getContext())){
            setRecordatorioEstenciaActivado(true);
            etDiasDeAntelacion.setText(MySharedPreferences.getDiasDeAntelacion(getContext()));
        }else {
            setRecordatorioEstenciaActivado(false);
        }
        if(!MySharedPreferences.getDefaultMail(getContext()).equals("")){
            setDefaultMailActivado(true);
            etMailAddress.setText(MySharedPreferences.getDefaultMail(getContext()));
        }else {
            setDefaultMailActivado(false);
        }
    }

    private boolean validarRecordatorio(){
        if (tvFechas.getText().toString().equals(getResources().getString(R.string.seleccionar_fecha))){
            Toast.makeText(getContext(),getResources().getString(R.string.debe_seleccionar_una_fecha),Toast.LENGTH_SHORT).show();
            return false;
        }else if(etMsjNotificacion.getText().toString().equals("")){
            Toast.makeText(getContext(),getResources().getString(R.string.debe_agregar_msj),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void storePreferences() {
        if(recordatorioEstanciaisChecked && etDiasDeAntelacion.getText().toString().equals("") || etDiasDeAntelacion.getText().toString().equals("0")){setRecordatorioEstenciaActivado(false);}
        MySharedPreferences.storePreferencesRecordatorio(getContext(), recordatorioEstanciaisChecked,etDiasDeAntelacion.getText().toString());
        MySharedPreferences.storeDefaultMail(getContext(),etMailAddress.getText().toString());
    }

    private void setRecordatorioEstenciaActivado(boolean choise){
        if(choise){
            ivRecordatorioCheck.setVisibility(View.VISIBLE);
            recordatorioEstanciaisChecked = true;
            etDiasDeAntelacion.setEnabled(true);
        } else {
            ivRecordatorioCheck.setVisibility(View.GONE);
            recordatorioEstanciaisChecked = false;
            etDiasDeAntelacion.setText("");
            etDiasDeAntelacion.setEnabled(false);
        }
    }

    private void setDefaultMailActivado(boolean choise){
        if(choise){
            ivDefaultMailCheck.setVisibility(View.VISIBLE);
            defaultMailChecked =true;etMailAddress.setEnabled(true);}
        else {
            ivDefaultMailCheck.setVisibility(View.GONE);
            defaultMailChecked =false;etMailAddress.setEnabled(false);etMailAddress.setText("");}
    }

    private void resetNuevoRecordatorio(){
        tvFechas.setText(getResources().getString(R.string.seleccionar_fecha));
        etMsjNotificacion.setText("");
    }

    private void handleDatePickerDialog(){

        String tv_str = tvFechas.getText().toString();
        int year = 0;
        int month = 0;
        int day = 0;

        if (tv_str.equalsIgnoreCase(getResources().getString(R.string.seleccionar_fecha))){
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            day = Integer.parseInt(tv_str.substring(0,2));
            month = Integer.parseInt(tv_str.substring(3,5)) - 1;
            year = Integer.parseInt(tv_str.substring(6));
        }

        new DatePickerDialog(getContext(),new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                tvFechas.setText(DateHandler.formatDateToShow(day,month+1,year));
            }
        },year,month,day).show();
    }


}
