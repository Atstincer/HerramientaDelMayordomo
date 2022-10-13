package com.example.usuario.herramientadelmayordomoii.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomoii.Entities.Estancia;
import com.example.usuario.herramientadelmayordomoii.R;
//import com.example.usuario.herramientadelmayordomoii.UI.EstanciaActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 7/10/2021.
 */

public class EstanciasRVAdapter extends RecyclerView.Adapter<EstanciasRVAdapter.ViewHolder> {

    private List<Estancia> listEstancias = new ArrayList<>();
    private Context context;
    private CallBack cb;

    public EstanciasRVAdapter(Context context, CallBack cb) {
        this.context = context;
        this.cb = cb;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_estancia,parent,false),cb);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.bindComponents(position);
    }

    @Override
    public int getItemCount() {
        return listEstancias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tv_hab,tv_fechas,tv_family_name;
        private CardView cv;
        private CallBack cb;

        public ViewHolder(View view, final CallBack cb){
            super(view);
            this.cb = cb;
            tv_hab = (TextView)view.findViewById(R.id.no_hab);
            tv_fechas = (TextView)view.findViewById(R.id.fechas);
            tv_family_name = (TextView)view.findViewById(R.id.pax_family_name);
            cv = (CardView)view.findViewById(R.id.estancias_cardView);
            cv.setOnClickListener(this);
        }

        public void bindComponents(int position){
            tv_hab.setText(listEstancias.get(position).getNo_hab());
            tv_fechas.setText(getStrFechas(listEstancias.get(position).getDesde(),listEstancias.get(position).getHasta()));
            tv_family_name.setText(listEstancias.get(position).getFamilyName());
        }

        private String getStrFechas(String desde,String hasta){
            String fechas = "";
            if(desde.substring(3,5).equals(hasta.substring(3,5))){
                fechas = desde.substring(0,2) + " "+ context.getResources().getString(R.string.al) + " " + hasta;
            }else {
                if(desde.substring(6).equals(hasta.substring(6))){
                    fechas = desde.substring(0,5) + " " + context.getResources().getString(R.string.al) + " " + hasta;
                } else {
                    fechas = desde + " " + context.getResources().getString(R.string.al) + " " + hasta;
                }
            }
            return fechas;
        }

        @Override
        public void onClick(View view) {
            cb.itemClicked(getAdapterPosition());
        }
    }

    public void setListEstancias(List<Estancia> listEstancias) {
        this.listEstancias = listEstancias;
        notifyDataSetChanged();
    }

    public interface CallBack{
        void itemClicked(int position);
    }
}
