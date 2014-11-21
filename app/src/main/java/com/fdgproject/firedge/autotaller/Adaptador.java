package com.fdgproject.firedge.autotaller;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Firedge on 29/09/2014.
 */
public class Adaptador extends ArrayAdapter<Vehiculo> {

    private Context cnt;
    private ArrayList<Vehiculo> vehiculos;
    private int rec;
    private static LayoutInflater lin;

    public static class ViewHolder{
        public TextView tv_dia, tv_mes, tv_matricula, tv_presupuesto;
        public ImageView iv_tipo, iv_reparado;
        public RelativeLayout layprin;
    }

    public Adaptador(Context context, int resource, ArrayList<Vehiculo> objects) {
        super(context, resource, objects);
        this.cnt = context;
        this.vehiculos = objects;
        this.rec = resource;
        this.lin = (LayoutInflater)cnt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView == null) {
            convertView = lin.inflate(rec, null);
            vh =new ViewHolder();
            vh.tv_dia = (TextView)convertView.findViewById(R.id.tv_day);
            vh.tv_mes = (TextView)convertView.findViewById(R.id.tv_month);
            vh.tv_matricula = (TextView)convertView.findViewById(R.id.tv_matricula);
            vh.tv_presupuesto = (TextView)convertView.findViewById(R.id.tv_presupuesto);
            vh.iv_reparado = (ImageView)convertView.findViewById(R.id.ivCalendar);
            vh.iv_tipo = (ImageView)convertView.findViewById(R.id.ivTipo);
            vh.layprin = (RelativeLayout)convertView.findViewById(R.id.list_layout);
            vh.tv_matricula.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int elemento = -1;
                    elemento = (Integer) view.getTag();
                    AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                    LayoutInflater inflater = LayoutInflater.from(view.getContext());
                    View vista = inflater.inflate(R.layout.vista_detalle, null);
                    alert.setView(vista);
                    TextView tv_fecha = (TextView) vista.findViewById(R.id.tv_fecha);
                    tv_fecha.setText(vehiculos.get(elemento).getFecha());
                    TextView tv_averia = (TextView) vista.findViewById(R.id.tv_averia);
                    tv_averia.setText(vehiculos.get(elemento).getAveria());
                    alert.setNegativeButton(android.R.string.ok, null);
                    alert.show();
                }
            });
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder)convertView.getTag();
        }
        vh.tv_dia.setText(Integer.toString(vehiculos.get(position).getDia()));
        vh.tv_mes.setText(vehiculos.get(position).getMesNombre());
        vh.tv_matricula.setText(vehiculos.get(position).getMatricula());
        vh.tv_matricula.setTag(position);
        vh.tv_presupuesto.setText(Double.toString(vehiculos.get(position).getPresupuesto()));
        String [] tipos = cnt.getResources().getStringArray(R.array.tipo_vehiculo);
        if(vehiculos.get(position).getTipo().equals(tipos[0])){
            vh.iv_tipo.setImageResource(R.drawable.coche);
        } else if(vehiculos.get(position).getTipo().equals(tipos[1])){
            vh.iv_tipo.setImageResource(R.drawable.moto);
        } else if(vehiculos.get(position).getTipo().equals(tipos[2])) {
            vh.iv_tipo.setImageResource(R.drawable.furgoneta);
        }
        if(vehiculos.get(position).getReparado()==1) {
            //vh.layprin.setBackgroundColor(R.style.Theme_Pink_default_Widget);
            vh.layprin.setBackgroundColor(R.style.bgGris);
            vh.iv_reparado.setBackgroundResource(R.drawable.pag_calendar_grey);
        } else {
            if(vehiculos.get(position).getUrgente()==1){
                vh.layprin.setBackgroundColor(Color.RED);
            } else {
                vh.layprin.setBackgroundColor(1);
            }
            vh.iv_reparado.setBackgroundResource(R.drawable.pag_calendar);
        }
        return convertView;
    }
}
