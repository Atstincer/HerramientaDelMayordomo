package com.example.usuario.herramientadelmayordomoii.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.usuario.herramientadelmayordomoii.R;

/**
 * Created by usuario on 5/11/2021.
 */

public class EstanciasFragment extends Fragment {

    public static final String TAG = "EstanciasFragment";

    private CallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_estancias,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab_estancias);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                myCallBack.setUpNewEstanciaFragment();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myCallBack = (CallBack)context;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu!=null){
            menu.clear();
        }
        inflater.inflate(R.menu.menu_estancias,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_nuevaEstancia:
                //getFragmentManager().beginTransaction().replace(R.id.fragment_container,new EstanciaFragment()).commit();
                if(myCallBack!=null){
                    myCallBack.setUpNewEstanciaFragment();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public interface CallBack{
        void setUpNewEstanciaFragment();
    }
}
