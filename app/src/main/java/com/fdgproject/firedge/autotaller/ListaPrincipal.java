package com.fdgproject.firedge.autotaller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;


public class ListaPrincipal extends Activity {

    private ArrayList<Vehiculo> vehiculos = new ArrayList<Vehiculo>();
    private Adaptador adp;
    private final int FORMULARIO = 1;

    /********************************************************************************************/
    /*                                                                                          */
    /*                                   Metodos on...                                          */
    /*                                                                                          */
    /********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_principal);
        if(savedInstanceState!= null) {
            vehiculos = (ArrayList<Vehiculo>)savedInstanceState.getSerializable("objeto");
        } else {
            cargarXML();
        }
        initcomponents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        guardarXML();
    }

    @Override
    protected void onSaveInstanceState(Bundle savingInstanceState) {
        super.onSaveInstanceState(savingInstanceState);
        savingInstanceState.putSerializable("objeto", vehiculos);
    }

    //Recoger el resultado del Intent del Formulario
    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK && requestCode == FORMULARIO) {
            vehiculos = data.getParcelableArrayListExtra("resul");
            lanzaLista();
            tostada(getResources().getString(R.string.msg_editar));
        }
    }

    /******************************  Menu principal  *********************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_nuevo) {
            nuevo();
            return true;
        } else if(id == R.id.action_borrar_todo){
            borrarTodo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /******************************  Menu contextual  *********************************/

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contextual, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = item.getItemId();
        int index = info.position;

        if(id == R.id.action_editar){
            editar(index);
            return true;
        } else if(id == R.id.action_borrar){
            borrar(index);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    /********************************************************************************************/
    /*                                                                                          */
    /*                              Metodos auxiliares...                                       */
    /*                                                                                          */
    /********************************************************************************************/

    //Metodo para cargar la aplicación
    private void initcomponents(){
        String [] meses = getResources().getStringArray(R.array.meses);
        Vehiculo.setMeses(meses);
        lanzaLista();
    }

    //Metodo que lanza crea el ListView
    private void lanzaLista(){
        Collections.sort(vehiculos);
        calculaTotal();
        adp = new Adaptador(this, R.layout.list_element_opc, vehiculos);
        final ListView lv = (ListView)findViewById(R.id.lv_principal);
        lv.setAdapter(adp);
        lv.setClickable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(vehiculos.get(i).getReparado()==1){
                    vehiculos.get(i).setReparado(0);
                } else {
                    vehiculos.get(i).setReparado(1);
                }
                actualizar();
            }
        });

        registerForContextMenu(lv);
    }

    //Metodo que ordena los elementos de la lista y realiza los cambios necesarios en el ListView
    private void actualizar(){
        Collections.sort(vehiculos);
        adp.notifyDataSetChanged();
        calculaTotal();
    }

    public void tostada(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    /********************************************************************************************/
    /*                                                                                          */
    /*                                Metodos onClick...                                        */
    /*                                                                                          */
    /********************************************************************************************/

    //Metodo que llama al formulario para crear un Vehiculo nuevo
    private void nuevo(){
        Intent i = new Intent(this, FormularioVehiculo.class);
        Bundle b = new Bundle();
        b.putParcelableArrayList("list", vehiculos);
        b.putInt("indice", -1);
        i.putExtras(b);
        startActivityForResult(i, FORMULARIO);
    }

    //Metodo que llama al formulario para editar un Vehiculo existente
    private void editar(int indice){
        Intent i = new Intent(this, FormularioVehiculo.class);
        Bundle b = new Bundle();
        b.putParcelableArrayList("list", vehiculos);
        b.putInt("indice", indice);
        i.putExtras(b);
        startActivityForResult(i, FORMULARIO);
    }

    //Metodo que muestra un AlertDialog para eliminar un Vehiculo
    private void borrar(final int index){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.dg_borrar));
        alert.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        vehiculos.remove(index);
                        actualizar();
                        tostada(getResources().getString(R.string.borrado));
                    }
                });
        alert.setNegativeButton(android.R.string.cancel,null);
        alert.show();
    }

    /********************************************************************************************/
    /*                                                                                          */
    /*                               Metodos logisticos...                                      */
    /*                                                                                          */
    /********************************************************************************************/

    //Metodo que elimina todos los vehiculos con reparado=1
    private void borrarTodo(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.dg_borrar_todo));
        alert.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        for(int i=0; i<vehiculos.size(); i++){
                            if(vehiculos.get(i).getReparado()==1) {
                                vehiculos.remove(i);
                                i--;
                            }
                        }
                        actualizar();
                        tostada(getResources().getString(R.string.borrado_todo));
                    }
                });
        alert.setNegativeButton(android.R.string.cancel,null);
        alert.show();
    }

    //Metodo para calcular el total de la factura y mostrarlo
    private void calculaTotal(){
        double dinero = 0;
        for(Vehiculo veh:vehiculos){
            dinero += veh.getPresupuesto();
        }
        TextView tv_total = (TextView)findViewById(R.id.tv_total);
        tv_total.setText(Double.toString(dinero));
    }

    /********************************************************************************************/
    /*                                                                                          */
    /*                                Metodos ficheros...                                       */
    /*                                                                                          */
    /********************************************************************************************/

    //Para guardar el ArrayList de vehiculos en un fichero XML
    private void guardarXML(){
        try {
            FileOutputStream fosxml = new FileOutputStream(new File(getExternalFilesDir(null), "vehiculos.xml"));
            XmlSerializer docxml= Xml.newSerializer();
            docxml.setOutput(fosxml, "UTF-8");
            docxml.startDocument(null, Boolean.valueOf(true));
            docxml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            docxml.startTag(null, "taller");
            for(Vehiculo veh:vehiculos) {
                docxml.startTag(null, "vehiculo");
                docxml.attribute(null, "matricula", veh.getMatricula());
                docxml.attribute(null, "tipo", veh.getTipo());
                docxml.attribute(null, "averia", veh.getAveria());
                docxml.attribute(null, "dia", Integer.toString(veh.getDia()));
                docxml.attribute(null, "mes", Integer.toString(veh.getMes()));
                docxml.attribute(null, "anio", Integer.toString(veh.getAnio()));
                docxml.attribute(null, "presupuesto", Double.toString(veh.getPresupuesto()));
                docxml.attribute(null, "reparado", Integer.toString(veh.getReparado()));
                docxml.attribute(null, "urgente", Integer.toString(veh.getUrgente()));
                docxml.endTag(null, "vehiculo");
            }
            docxml.endTag(null, "taller");
            docxml.endDocument();
            docxml.flush();
            fosxml.close();

        }catch(Exception ex){
            Toast.makeText(this, getResources().getString(R.string.msg_error_guardar), Toast.LENGTH_LONG).show();
        }
    }

    //Para leer el fichero XML y cargar los datos en el ArrayList
    private void cargarXML(){
        String matricula="", tipo="", averia="";
        int dia=0, mes=0, anio=0;
        double presupuesto=0;
        int reparado=0, urgente=0;
        try {
            XmlPullParser lectorxml = Xml.newPullParser();
            lectorxml.setInput(new FileInputStream(new File(getExternalFilesDir(null), "vehiculos.xml")), "utf-8");
            int evento = lectorxml.getEventType();
            while(evento != XmlPullParser.END_DOCUMENT){
                if(evento == XmlPullParser.START_TAG){
                    String etiqueta = lectorxml.getName();
                    if(etiqueta.compareTo("vehiculo")==0){
                        matricula = lectorxml.getAttributeValue(null, "matricula");
                        tipo = lectorxml.getAttributeValue(null, "tipo");
                        averia = lectorxml.getAttributeValue(null, "averia");
                        dia = Integer.parseInt(lectorxml.getAttributeValue(null, "dia"));
                        mes = Integer.parseInt(lectorxml.getAttributeValue(null, "mes"));
                        anio = Integer.parseInt(lectorxml.getAttributeValue(null, "anio"));
                        presupuesto = Double.parseDouble(lectorxml.getAttributeValue(null, "presupuesto"));
                        reparado = Integer.parseInt(lectorxml.getAttributeValue(null, "reparado"));
                        urgente = Integer.parseInt(lectorxml.getAttributeValue(null, "urgente"));
                        vehiculos.add(new Vehiculo(matricula, tipo, averia, dia, mes, anio, presupuesto, reparado, urgente));
                    }
                }
                evento = lectorxml.next();
            }
        }catch (Exception ex){
            Toast.makeText(this, getResources().getString(R.string.msg_error_cargar), Toast.LENGTH_LONG).show();
        }
    }

}
