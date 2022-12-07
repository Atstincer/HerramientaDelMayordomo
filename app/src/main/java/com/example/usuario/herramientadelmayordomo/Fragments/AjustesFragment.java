package com.example.usuario.herramientadelmayordomo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomo.Almacenamiento.MySharedPreferences;
import com.example.usuario.herramientadelmayordomo.R;
import com.example.usuario.herramientadelmayordomo.Util.MyApp;


/**
 * Created by usuario on 5/11/2021.
 */

public class AjustesFragment extends Fragment {

    public static final String TAG = "AjustesFragment";

    private EditText etDiasDeAntelacion,etMailAddress;
    private ImageView ivRecordatorioCheck, ivDefaultMailLocked;
    private boolean recordatorioEstanciaisChecked,defaultMailLocked;

    private MyCallBack myCallBack;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_ajustes,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindComponents(view);
        setUpPreferences();
        myCallBack.udActivity(AjustesFragment.TAG);
        //requestCode = 0;
    }

    @Override
    public void onPause() {
        storePreferences();
        super.onPause();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myCallBack = (MyCallBack)context;
    }

    @Override
    public void onResume() {
        super.onResume();
        myCallBack.udActivity(AjustesFragment.TAG);
    }

    private void bindComponents(View view){
        etDiasDeAntelacion = (EditText)view.findViewById(R.id.et_dias_antelacion);
        ivRecordatorioCheck = (ImageView)view.findViewById(R.id.iv_check_recordatorios);
        etMailAddress = (EditText)view.findViewById(R.id.et_mail);
        ivDefaultMailLocked = (ImageView)view.findViewById(R.id.iv_lock_mail);
        RelativeLayout layoutActivarRecordatorios = (RelativeLayout)view.findViewById(R.id.layout_activar_recordatorio);
        RelativeLayout layoutBloquearMailPorDefecto = (RelativeLayout)view.findViewById(R.id.layout_enviar_por_defecto_mail);

        etDiasDeAntelacion.clearFocus();

        layoutActivarRecordatorios.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(recordatorioEstanciaisChecked){
                    setRecordatorioEstanciaActivado(false);}
                else {
                    setRecordatorioEstanciaActivado(true);}
            }
        });

        layoutBloquearMailPorDefecto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(defaultMailLocked){
                    lockDefaultMail(false);}
                else {
                    lockDefaultMail(true);}
            }
        });



    }

    private void setUpPreferences(){
        if(MySharedPreferences.getRecordatorioEstanciasIsActivado(getContext())){
            setRecordatorioEstanciaActivado(true);
            etDiasDeAntelacion.setText(MySharedPreferences.getDiasDeAntelacion(getContext()));
        }else {
            setRecordatorioEstanciaActivado(false);
        }
        etMailAddress.setText(MySharedPreferences.getDefaultMail(getContext()));
        lockDefaultMail(MySharedPreferences.getDefaultMailLocked(getContext()));
    }

    private void storePreferences() {
        if(recordatorioEstanciaisChecked && etDiasDeAntelacion.getText().toString().equals("") || etDiasDeAntelacion.getText().toString().equals("0")){
            setRecordatorioEstanciaActivado(false);}
        if(recordatorioEstanciaisChecked && !MyApp.isInt(etDiasDeAntelacion.getText().toString())){
            Toast.makeText(getContext(),getString(R.string.numero_dias_no_valido),Toast.LENGTH_SHORT).show();
        }
        MySharedPreferences.storePreferencesRecordatorio(getContext(), recordatorioEstanciaisChecked,etDiasDeAntelacion.getText().toString());
        MySharedPreferences.storeDefaultMail(getContext(),etMailAddress.getText().toString(),defaultMailLocked);
    }

    private void setRecordatorioEstanciaActivado(boolean choise){
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

    private void lockDefaultMail(boolean choise){
        if(choise){
            ivDefaultMailLocked.setImageResource(R.drawable.ic_locked);
            defaultMailLocked =true;etMailAddress.setEnabled(false);}
        else {
            ivDefaultMailLocked.setImageResource(R.drawable.ic_unlocked);
            defaultMailLocked =false;etMailAddress.setEnabled(true);}
    }

    public interface MyCallBack{
        void udActivity(String Tag);
    }

}
