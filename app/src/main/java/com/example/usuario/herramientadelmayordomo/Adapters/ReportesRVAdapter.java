package com.example.usuario.herramientadelmayordomo.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.usuario.herramientadelmayordomo.Entities.Reporte;
import com.example.usuario.herramientadelmayordomo.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by usuario on 24/10/2022.
 */

public class ReportesRVAdapter extends RecyclerView.Adapter<ReportesRVAdapter.ViewHolder> {

    private CallBack myCallBack;
    private List<Reporte> listReportes;
    private Context context;

    public ReportesRVAdapter(Context context,CallBack callBack){
        this.context = context;
        this.myCallBack = callBack;
        this.listReportes = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_reporte,parent,false),myCallBack);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindComponents(position);
    }

    @Override
    public int getItemCount() {
        return listReportes.size();
    }

    public void setListReportes(List<Reporte> newListReportes){
        listReportes = newListReportes;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView fecha,mañana,tarde,noche;
        private CardView cardView;
        private CallBack cb;

        public ViewHolder(View view,CallBack callBack) {
            super(view);
            fecha = (TextView)view.findViewById(R.id.tv_fecha_reporte_card_view);
            mañana = (TextView)view.findViewById(R.id.tv_mañana_reportecardview);
            tarde = (TextView)view.findViewById(R.id.tv_tarde_reportecardview);
            noche = (TextView)view.findViewById(R.id.tv_noche_reportecardview);
            cardView = (CardView)view.findViewById(R.id.reporte_cardView);
            cardView.setOnClickListener(this);
            cb = callBack;

        }

        public void bindComponents(int position){
            fecha.setText(listReportes.get(position).getFecha());
            if(!listReportes.get(position).getReporteMañana().equals("")){
                mañana.setText(getLabel(R.string.reporte_mañana,true));
                mañana.setTextColor(ContextCompat.getColor(context,R.color.color_font_blanco));
            }else {
                mañana.setText(getLabel(R.string.reporte_mañana,false));
                mañana.setTextColor(ContextCompat.getColor(context,R.color.link));
            }
            if(!listReportes.get(position).getReporteTarde().equals("")){
                tarde.setText(getLabel(R.string.reporte_tarde,true));
                tarde.setTextColor(ContextCompat.getColor(context,R.color.color_font_blanco));
            }else {
                tarde.setText(getLabel(R.string.reporte_tarde,false));
                tarde.setTextColor(ContextCompat.getColor(context,R.color.link));
            }
            if(!listReportes.get(position).getReporteNoche().equals("")){
                noche.setText(getLabel(R.string.reporte_noche,true));
                noche.setTextColor(ContextCompat.getColor(context,R.color.color_font_blanco));
            }else {
                noche.setText(getLabel(R.string.reporte_noche,false));
                noche.setTextColor(ContextCompat.getColor(context,R.color.link));
            }
        }

        private String getLabel(int text,boolean choise){
            if(choise) {
                return context.getResources().getString(text) + ": " + context.getResources().getString(R.string.si_minuscula);
            }else {
                return context.getResources().getString(text) + ": " + context.getResources().getString(R.string.no_minuscula);
            }
        }

        @Override
        public void onClick(View view) {
            cb.itemClicked(getAdapterPosition());
        }
    }

    public interface CallBack{
        void itemClicked(int position);
    }
}
