package com.example.usuario.herramientadelmayordomoii.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.usuario.herramientadelmayordomoii.AlarmManagement.AlarmReceiver;
import com.example.usuario.herramientadelmayordomoii.R;

/**
 * Created by usuario on 5/11/2021.
 */

public class RecordatoriosFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recordatorios,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setAlarm();
    }

    /*
    private void setAlarm(int i, long timestamp, Context ctx){
        AlarmManager alarmManager = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(ctx, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,i,alarmIntent,PendingIntent.FLAG_ONE_SHOT);
        alarmIntent.setData(Uri.parse("custom://"+ System.currentTimeMillis()));
        alarmManager.set(AlarmManager.RTC_WAKEUP,timestamp,pendingIntent);
    }*/
}
