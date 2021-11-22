package com.example.usuario.herramientadelmayordomoii.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.usuario.herramientadelmayordomoii.R;

/**
 * Created by usuario on 6/11/2021.
 */

public class EstanciaFragment extends Fragment {

    public static final String TAG = "EstanciaFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_estancia,container,false);
    }
}
