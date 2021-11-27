package com.example.usuario.herramientadelmayordomoii.Util;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by usuario on 25/11/2021.
 */

public class MyBitmapFactory {

    public static Bitmap getScaledBitmap(Bitmap bitmap, ImageView iv){
        Bitmap scaledBitmap = null;

        /*System.out.println("********************************************");
        System.out.println("Bitmap width = "+bitmap.getWidth()+" -- height = "+bitmap.getHeight());
        System.out.println("ImageView width = "+iv.getWidth()+" -- height = "+iv.getHeight());
        System.out.println("********************************************");*/

        if(iv.getWidth()==0 || iv.getHeight()==0){
            scaledBitmap = bitmap;
        }else if(bitmap.getWidth()>bitmap.getHeight()){
            //según ancho
            if(bitmap.getWidth()>iv.getWidth()){
                float scala = (float)iv.getWidth()/(float)bitmap.getWidth();
                scaledBitmap = Bitmap.createScaledBitmap(bitmap,iv.getWidth(),(int)(bitmap.getHeight()*scala),true);
            }else{
                scaledBitmap = bitmap;
            }
        }else{
            //según alto
            if(bitmap.getHeight()>iv.getHeight()){
                float scala = (float)iv.getHeight()/(float)bitmap.getHeight();
                scaledBitmap = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*scala),iv.getHeight(),true);
            }else{
                scaledBitmap = bitmap;
            }
        }
        return scaledBitmap;
    }
}
