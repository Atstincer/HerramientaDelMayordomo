package com.example.usuario.herramientadelmayordomoii.Util;

/**
 * Created by usuario on 24/10/2022.
 */

public class MyApp {

    public static final int STATE_REGULAR = 0;
    public static final int STATE_NEW = 1;
    public static final int STATE_UPDATE = 2;

    public static boolean isInt(String cad){
        try{
            int numero = Integer.parseInt(cad);
            return true;
        }catch (Exception e){
            return false;
        }
    }

}
