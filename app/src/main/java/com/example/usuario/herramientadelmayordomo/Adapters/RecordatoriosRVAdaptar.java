package com.example.usuario.herramientadelmayordomo.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.usuario.herramientadelmayordomo.Entities.Recordatorio;
import com.example.usuario.herramientadelmayordomo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 25/11/2022.
 */

public class RecordatoriosRVAdaptar extends RecyclerView.Adapter<RecordatoriosRVAdaptar.ViewHolder> {

    private List<Recordatorio> listRecordatorios = new ArrayList<>();

    private Context ctx;
    private CallBack myCallBack;

    public RecordatoriosRVAdaptar(Context ctx, CallBack callBack){
        this.ctx = ctx;
        myCallBack = callBack;
    }

    public void setListRecordatorios(List<Recordatorio> listRecordatorios){
        this.listRecordatorios = listRecordatorios;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_recordatorio,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindViews(position);
    }

    @Override
    public int getItemCount() {
        return listRecordatorios.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        CardView cardView;
        TextView fecha, titulo, mensaje;

        private ViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.recordatorio_cardView);
            fecha = (TextView)view.findViewById(R.id.tv_fecha_recordatorio_cardview);
            titulo = (TextView)view.findViewById(R.id.tv_titulo_recordatorio_cardview);
            mensaje = (TextView)view.findViewById(R.id.tv_mensaje_recordatorio_cardview);
            cardView.setOnLongClickListener(this);
        }

        private void bindViews(int position){
            fecha.setText(listRecordatorios.get(position).getFecha());
            titulo.setText(listRecordatorios.get(position).getTitle());
            mensaje.setText(listRecordatorios.get(position).getMensaje());
        }

        @Override
        public boolean onLongClick(View view) {
            PopupMenu menu = new PopupMenu(ctx,view);
            menu.getMenuInflater().inflate(R.menu.menu_recordatorio_clicked,menu.getMenu());
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.menu_item_eliminar_recordatorio:
                            myCallBack.requestEliminarRecordatorio(getAdapterPosition());
                            break;
                    }
                    return false;
                }
            });
            menu.show();
            return false;
        }
    }

    public interface CallBack{
        void requestEliminarRecordatorio(int position);
    }
}
