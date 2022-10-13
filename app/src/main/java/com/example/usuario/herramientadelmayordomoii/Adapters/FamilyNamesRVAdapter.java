package com.example.usuario.herramientadelmayordomoii.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.example.usuario.herramientadelmayordomoii.Entities.FamilyName;
import com.example.usuario.herramientadelmayordomoii.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 29/11/2021.
 */

public class FamilyNamesRVAdapter extends RecyclerView.Adapter<FamilyNamesRVAdapter.ViewHolder> {

    private List<FamilyName> listFamilyNames;
    private List<FamilyName> fullListFamilyNames;
    private Callback myCallback;

    public FamilyNamesRVAdapter(List<FamilyName> listFamilyNames, Callback callback) {
        this.listFamilyNames = listFamilyNames;
        fullListFamilyNames = new ArrayList<>(listFamilyNames);
        this.myCallback = callback;
    }

    public void setListFamilyNames(List<FamilyName> listFamilyNames) {
        this.listFamilyNames = listFamilyNames;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_family_name,parent,false);
        return new ViewHolder(view,myCallback);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindView(position);
    }


    @Override
    public int getItemCount() {
        return listFamilyNames.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tv_familyName;
        private Callback vhCallback;

        public ViewHolder(View view, Callback cb) {
            super(view);
            tv_familyName = (TextView)view.findViewById(R.id.tv_family_name);
            vhCallback = cb;
            view.setOnClickListener(this);
            //tv_familyName.setOnClickListener(this);
        }

        public void bindView(int position){
            tv_familyName.setText(listFamilyNames.get(position).getFamilyName());
        }

        @Override
        public void onClick(View view) {
            vhCallback.onItemClicked(getAdapterPosition());
        }
    }

    public Filter getFilter(){return exampleFilter;}

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<FamilyName> filteredList = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0){
                filteredList = fullListFamilyNames;
            } else {
                String strPattern = charSequence.toString().toLowerCase().trim();

                for(FamilyName f: fullListFamilyNames){
                    if(f.getFamilyName().toLowerCase().trim().contains(strPattern)){
                        filteredList.add(f);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listFamilyNames.clear();
            listFamilyNames.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };


    public interface Callback{
        void onItemClicked(int position);
    }
}
