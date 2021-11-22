package com.example.usuario.herramientadelmayordomoii.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.usuario.herramientadelmayordomoii.Entities.Estancia;
import com.example.usuario.herramientadelmayordomoii.R;
//import com.example.usuario.herramientadelmayordomoii.UI.EstanciaActivity;

import java.util.ArrayList;

/**
 * Created by usuario on 7/10/2021.
 */

public class EstanciasRVAdapter extends RecyclerView.Adapter<EstanciasRVAdapter.ViewHolder> {

    private ArrayList<Estancia> estancias = new ArrayList<>();
    private Context context;
    private String[] familyNames = {"Diana Luanda x 2","Eric Torres x 2","Belkis Nelida x 1","Manolo Alego x 2","Perico Perez x 2"};


    public EstanciasRVAdapter(Context context) {
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_estancia,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.tv_hab.setText(estancias.get(position).getNo_hab());
        String desde = estancias.get(position).getDesde();
        String hasta = estancias.get(position).getHasta();
        String fechas = "";

        if(desde.substring(3,5).equals(hasta.substring(3,5))){
            fechas = desde.substring(0,2) + " al " + hasta;
        }else {
            if(desde.substring(6).equals(hasta.substring(6))){
                fechas = desde.substring(0,5) + " al " + hasta;
            } else {
                fechas = desde + " al " + hasta;
            }
        }

        holder.tv_fechas.setText(fechas);

        System.out.println(estancias.get(position));

        /*
        if(estancias.get(position).getFamilyName_id()==0 || estancias.get(position).getFamilyName_id()>5){
            holder.tv_family_name.setText("-----");
        }else{
            holder.tv_family_name.setText(familyNames[(estancias.get(position).getFamilyName_id())-1]);
        }*/

        holder.tv_family_name.setText(familyNames[(estancias.get(position).getFamilyName_id())-1]);

        holder.cv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
/*                Intent intent = new Intent(context,EstanciaActivity.class);
                intent.putExtra("no_hab",estancias.get(position).getNo_hab());
                intent.putExtra("desde",estancias.get(position).getDesde());
                intent.putExtra("hasta",estancias.get(position).getHasta());
                intent.putExtra("family_name",familyNames[estancias.get(position).getFamilyName_id()-1]);
                intent.putExtra("noches",estancias.get(position).getNo_noches());
                context.startActivity(intent);*/
                //Toast.makeText(context, estancias.get(position).getFamilyName_id() + " selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return estancias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_hab,tv_fechas,tv_family_name;
        private CardView cv;

        public ViewHolder(View view){
            super(view);

            tv_hab = (TextView)view.findViewById(R.id.no_hab);
            tv_fechas = (TextView)view.findViewById(R.id.fechas);
            tv_family_name = (TextView)view.findViewById(R.id.pax_family_name);
            cv = (CardView)view.findViewById(R.id.estancias_cardView);
        }
    }

    public void setEstancias(ArrayList<Estancia> estancias) {
        this.estancias = estancias;
        notifyDataSetChanged();
    }
}
