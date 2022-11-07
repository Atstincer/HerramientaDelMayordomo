package com.example.usuario.herramientadelmayordomoii.Almacenamiento;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomoii.R;

import java.util.regex.Pattern;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by usuario on 1/11/2022.
 */

public class MySharedPreferences {

    private static final String NAME_ARCHIVO = "MyPreferences";
    private static final String KEY_RECORDATORIO_ACTIVADO = "recordatorio_activado";
    private static final String KEY_DEFAULT_MAIL = "default_mail";
    private static final String KEY_DIAS_ANTELACION = "dias_antelacion";
    private static final String KEY_NOTIFICATION_ID = "id_notificacion";


    public static String getDefaultMail(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_APPEND);
        return preferences.getString(KEY_DEFAULT_MAIL,"");
    }

    public static void storeDefaultMail(Context ctx,String mail){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_DEFAULT_MAIL,mail);
        editor.apply();
        if(!mail.equals("")){if(!isMailValid(mail)){Toast.makeText(ctx,ctx.getString(R.string.mail_no_valido),Toast.LENGTH_SHORT).show();}}
    }

    public static boolean getRecordatorioEstanciasIsActivado(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences(MySharedPreferences.NAME_ARCHIVO,MODE_APPEND);
        return preferences.getBoolean(MySharedPreferences.KEY_RECORDATORIO_ACTIVADO,false);
    }

    public static String getDiasDeAntelacion(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences(MySharedPreferences.NAME_ARCHIVO,MODE_APPEND);
        return preferences.getString(MySharedPreferences.KEY_DIAS_ANTELACION,"");
    }

    public static int getNotificationId(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences(MySharedPreferences.NAME_ARCHIVO,MODE_APPEND);
        if(preferences.getInt(MySharedPreferences.KEY_NOTIFICATION_ID,0)==50){return 0;}
        return preferences.getInt(MySharedPreferences.KEY_NOTIFICATION_ID,0);
    }

    public static void storeNotificationId(Context ctx, int id){
        SharedPreferences prf = ctx.getSharedPreferences(MySharedPreferences.NAME_ARCHIVO,MODE_PRIVATE);
        SharedPreferences.Editor edit = prf.edit();
        edit.putInt(MySharedPreferences.KEY_NOTIFICATION_ID,id);
        edit.apply();
    }

    public static void storePreferencesRecordatorio(Context ctx, boolean activo, String dias){
        SharedPreferences prf = ctx.getSharedPreferences(MySharedPreferences.NAME_ARCHIVO,MODE_PRIVATE);
        SharedPreferences.Editor edit = prf.edit();
        edit.putBoolean(MySharedPreferences.KEY_RECORDATORIO_ACTIVADO,activo);
        edit.putString(MySharedPreferences.KEY_DIAS_ANTELACION,dias);
        edit.apply();
    }

    private static boolean isMailValid(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null || email.equals(""))
            return false;
        return pat.matcher(email).matches();
    }

}
