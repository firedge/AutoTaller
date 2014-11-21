package com.fdgproject.firedge.autotaller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class FormularioVehiculo extends Activity {

    private DatePicker dpFecha;
    private CheckBox cbUrgente;
    private EditText etMatricula, etAveria, etPresupuesto;

    private String tipo = "";
    private int dia, mes, anio;
    private String matricula, averia;
    private double presupuesto;

    private ArrayList<Vehiculo> vehiculos;
    private int indice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_vehiculo);

        Bundle b = getIntent().getExtras();

        vehiculos = b.getParcelableArrayList("list");
        indice = b.getInt("indice");

        final String [] tipos = getResources().getStringArray(R.array.tipo_vehiculo);

        Spinner spTipo= (Spinner)findViewById(R.id.spTipo);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tipo_vehiculo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter);
        spTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tipo = tipos[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                tipo = tipos[0];
            }
        });

        etMatricula = (EditText)findViewById(R.id.etMatricula);
        etAveria = (EditText)findViewById(R.id.etAveria);
        etPresupuesto = (EditText)findViewById(R.id.etPresupuesto);
        dpFecha = (DatePicker)findViewById(R.id.dpFecha);
        cbUrgente = (CheckBox)findViewById(R.id.cbUrgente);

        if(indice != -1){
            etMatricula.setText(vehiculos.get(indice).getMatricula());
            etAveria.setText(vehiculos.get(indice).getAveria());
            etPresupuesto.setText(Double.toString(vehiculos.get(indice).getPresupuesto()));
            if(vehiculos.get(indice).getUrgente()==1)
                cbUrgente.setChecked(true);
            int i = 0;
            while(!tipos[i].equals(vehiculos.get(indice).getTipo()))
                i++;
            spTipo.setSelection(i);
        }
    }

    public void aceptar_bt(View v){
        matricula = etMatricula.getText().toString();
        averia = etAveria.getText().toString();
        dia = dpFecha.getDayOfMonth();
        mes = dpFecha.getMonth();
        anio = dpFecha.getYear();
        try {
            presupuesto = Double.parseDouble(etPresupuesto.getText().toString());
        }catch (Exception ex){
            presupuesto = 0;
        }
        Vehiculo veh = new Vehiculo(matricula, tipo, averia, dia, mes, anio, presupuesto);
        if (cbUrgente.isChecked())
            veh.setUrgente(1);
        if(!vehiculos.contains(veh) || (indice!=-1 && vehiculos.get(indice).equals(veh))) {
            if (indice == -1) {
                vehiculos.add(veh);
                Intent i = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("resul", vehiculos);
                i.putExtras(bundle);
                setResult(Activity.RESULT_OK, i);
                finish();
            } else {
                vehiculos.remove(indice);
                vehiculos.add(veh);
                Intent i = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("resul", vehiculos);
                i.putExtras(bundle);
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        } else {
            String s = getResources().getString(R.string.msg_aviso);
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelar_bt(View v){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
