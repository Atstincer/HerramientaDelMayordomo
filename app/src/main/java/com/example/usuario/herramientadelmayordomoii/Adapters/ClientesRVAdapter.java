package com.example.usuario.herramientadelmayordomoii.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.usuario.herramientadelmayordomoii.Entities.Cliente;
import com.example.usuario.herramientadelmayordomoii.R;
import com.example.usuario.herramientadelmayordomoii.Util.MyBitmapFactory;

import java.util.List;


/**
 * Created by usuario on 10/11/2021.
 */

public class ClientesRVAdapter extends RecyclerView.Adapter<ClientesRVAdapter.ViewHolder> {

    private List<Cliente> listClientes;
    private Callback mycallback;

    public ClientesRVAdapter(List<Cliente> listClientes,Callback callback) {
        this.listClientes = listClientes;
        this.mycallback = callback;
    }

    public void setListClientes(List<Cliente> listClientes) {
        this.listClientes = listClientes;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_cliente,parent,false);
        return new ViewHolder(view,mycallback);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindViews(position);
    }


    @Override
    public int getItemCount() {
        return listClientes.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView foto;
        TextView tv_nombre, tv_pais;
        Callback myCallback;

        public ViewHolder(View view, Callback callback) {
            super(view);
            this.myCallback = callback;
            foto = (ImageView)view.findViewById(R.id.iv_foto_cliente);
            tv_nombre = (TextView)view.findViewById(R.id.tv_nombre_cliente);
            tv_pais = (TextView)view.findViewById(R.id.tv_paisOrigen);
            view.setOnClickListener(this);
        }

        private void bindViews(int position){
            if(listClientes.get(position).getFoto()!=null) {
                foto.setImageBitmap(MyBitmapFactory.getScaledBitmap(listClientes.get(position).getFoto(), foto));
            }else{
                foto.setImageResource(R.drawable.ic_cliente);
            }
            tv_nombre.setText(listClientes.get(position).getName());
            tv_pais.setText(listClientes.get(position).getOrigenPais());
        }

        @Override
        public void onClick(View view) {
            myCallback.onItemClicked(getAdapterPosition());
        }
    }

    public interface Callback{
        void onItemClicked(int position);
    }
}
