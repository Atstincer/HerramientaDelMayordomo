package com.example.usuario.herramientadelmayordomo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.usuario.herramientadelmayordomo.R;

/**
 * Created by usuario on 13/12/2021.
 */

public class FrontFragment extends Fragment {

    public static final String TAG = "FrontFragment";

    private Callback myCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_front,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myCallback.udActivity(FrontFragment.TAG);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myCallback = (Callback)context;
    }

    public interface Callback{
        void udActivity(String tag);
    }
}
