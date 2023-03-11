package com.example.usuario.herramientadelmayordomo.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.usuario.herramientadelmayordomo.Entities.Cliente;
import com.example.usuario.herramientadelmayordomo.R;
import com.example.usuario.herramientadelmayordomo.Util.MyBitmapFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by usuario on 10/11/2021.
 */

public class ClientesRVAdapter extends RecyclerView.Adapter<ClientesRVAdapter.ViewHolder> implements Filterable {

    private List<Cliente> listClientes, fullListClientes;
    private Callback mycallback;

    private boolean selectModeOn, showOnlySelected;

    public ClientesRVAdapter(List<Cliente> listClientes,Callback callback, boolean selectMode, boolean showOnlySelected) {
        this.listClientes = listClientes;
        fullListClientes = new ArrayList<>(listClientes);
        this.mycallback = callback;
        this.selectModeOn = selectMode;
        this.showOnlySelected = showOnlySelected;

        if(this.showOnlySelected){
            this.listClientes.clear();
            for(Cliente c: fullListClientes){
                if(c.isChecked()){
                    this.listClientes.add(c);
                }
            }
            notifyDataSetChanged();
        }
        //setListClientes(listClientes);
    }

    public void setListClientes(List<Cliente> listClientes) {
        this.listClientes = listClientes;
        //fullListClientes = new ArrayList<>(listClientes);
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

        ImageView foto, check;
        TextView tv_nombre, tv_pais;
        Callback myCallback;

        private ViewHolder(View view, Callback callback) {
            super(view);
            this.myCallback = callback;
            foto = (ImageView)view.findViewById(R.id.iv_foto_cliente);
            check = (ImageView)view.findViewById(R.id.iv_check);
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
            if(selectModeOn){
                if(listClientes.get(position).isChecked()){
                    check.setVisibility(View.VISIBLE);
                }else{
                    check.setVisibility(View.INVISIBLE);
                }
            }
            tv_nombre.setText(listClientes.get(position).getName());
            tv_pais.setText(listClientes.get(position).getOrigenPais());
        }

        @Override
        public void onClick(View view) {
            if(selectModeOn){
                if(listClientes.get(getAdapterPosition()).isChecked()){
                    check.setVisibility(View.INVISIBLE);
                }else{
                    check.setVisibility(View.VISIBLE);
                }
                fullListClientes.get(fullListClientes.indexOf(listClientes.get(getAdapterPosition()))).checkUncheck();
            }
            if(showOnlySelected){
                listClientes.clear();
                for(Cliente c:fullListClientes){
                    if(c.isChecked()){listClientes.add(c);}
                }
                notifyDataSetChanged();
            }
            myCallback.onItemClicked(getAdapterPosition());
        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Cliente> filteredList = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0){
                if(showOnlySelected){
                    for(Cliente c:fullListClientes){
                        if(c.isChecked()){filteredList.add(c);}
                    }
                }else {
                    filteredList.addAll(fullListClientes);
                }
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(Cliente c: fullListClientes){
                    if(c.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(c);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if (listClientes!=null && filterResults.values!=null){
                listClientes.clear();
                listClientes.addAll((List)filterResults.values);
            }
            if(selectModeOn){
                udSelectedItemsListClientes();
            }
            notifyDataSetChanged();
        }
    };

    private void udSelectedItemsListClientes(){
        if(listClientes==null || listClientes.size()<=0){return;}
        for(Cliente c: listClientes){
            c.setChecked(fullListClientes.get(fullListClientes.indexOf(c)).isChecked());
        }
    }

    public interface Callback{
        void onItemClicked(int position);
    }
}
