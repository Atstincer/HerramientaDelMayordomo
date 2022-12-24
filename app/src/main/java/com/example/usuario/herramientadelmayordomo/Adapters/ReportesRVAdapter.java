package com.example.usuario.herramientadelmayordomo.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.usuario.herramientadelmayordomo.Entities.Estancia;
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

    private int type;

    public ReportesRVAdapter(Context context,CallBack callBack,int type){
        this.context = context;
        this.myCallBack = callBack;
        this.listReportes = new ArrayList<>();
        this.type = type;
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

        private TextView tvIdentificador, tvReporteMañana, tvReporteTarde, tvReporteNoche, tvFamilyName, tvPeriodo;
        private CardView cardView;
        private CallBack cb;

        public ViewHolder(View view,CallBack callBack) {
            super(view);
            tvIdentificador = (TextView)view.findViewById(R.id.tv_identifier_reporte_card_view);
            tvFamilyName = (TextView)view.findViewById(R.id.tv_family_name_reporte_card_view);
            tvPeriodo = (TextView)view.findViewById(R.id.tv_periodo_reporte_card_view);
            tvReporteMañana = (TextView)view.findViewById(R.id.tv_mañana_reportecardview);
            tvReporteTarde = (TextView)view.findViewById(R.id.tv_tarde_reportecardview);
            tvReporteNoche = (TextView)view.findViewById(R.id.tv_noche_reportecardview);
            cardView = (CardView)view.findViewById(R.id.reporte_cardView);
            cardView.setOnClickListener(this);
            cb = callBack;

        }

        public void bindComponents(int position){
            if(type==Reporte.LAYOUT_EN_ESTANCIA) {//identificador refleja la fecha del reporte en el fragment Estancia
                tvIdentificador.setText(listReportes.get(position).getFecha());
                tvFamilyName.setText("");
                tvPeriodo.setText("");
                tvFamilyName.setVisibility(View.GONE);
                tvPeriodo.setVisibility(View.GONE);
            }else if (type==Reporte.LAYOUT_EN_REPORTES){//identificador refleja la número de hab del reporte en el fragment Reportes
                Estancia estancia = Estancia.getEstanciaFromDB(context,listReportes.get(position).getEstanciaId());
                tvIdentificador.setText(estancia.getNo_hab());
                tvFamilyName.setText(estancia.getFamilyName());
                tvPeriodo.setText(Estancia.formatPeriodoToShow(context,estancia.getDesde(),estancia.getHasta()));
                tvFamilyName.setVisibility(View.VISIBLE);
                tvPeriodo.setVisibility(View.VISIBLE);
            }
            if(!listReportes.get(position).getReporteMañana().equals("")){
                tvReporteMañana.setText(getLabel(R.string.reporte_mañana,true));
                tvReporteMañana.setTextColor(ContextCompat.getColor(context,R.color.color_font_blanco));
            }else {
                tvReporteMañana.setText(getLabel(R.string.reporte_mañana,false));
                tvReporteMañana.setTextColor(ContextCompat.getColor(context,R.color.link));
            }
            if(!listReportes.get(position).getReporteTarde().equals("")){
                tvReporteTarde.setText(getLabel(R.string.reporte_tarde,true));
                tvReporteTarde.setTextColor(ContextCompat.getColor(context,R.color.color_font_blanco));
            }else {
                tvReporteTarde.setText(getLabel(R.string.reporte_tarde,false));
                tvReporteTarde.setTextColor(ContextCompat.getColor(context,R.color.link));
            }
            if(!listReportes.get(position).getReporteNoche().equals("")){
                tvReporteNoche.setText(getLabel(R.string.reporte_noche,true));
                tvReporteNoche.setTextColor(ContextCompat.getColor(context,R.color.color_font_blanco));
            }else {
                tvReporteNoche.setText(getLabel(R.string.reporte_noche,false));
                tvReporteNoche.setTextColor(ContextCompat.getColor(context,R.color.link));
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
