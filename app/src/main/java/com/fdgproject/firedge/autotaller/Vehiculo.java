package com.fdgproject.firedge.autotaller;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Firedge on 17/11/2014.
 */
public class Vehiculo implements Parcelable, Comparable<Vehiculo>{
    //Datos del vehiculo
    private String matricula, tipo, averia;
    //Fecha de admisión
    private int dia, mes, anio;
    //Presupuesto sobre la avería
    private double presupuesto;
    //Si esta reparado o no y la urgencia.
    private int reparado, urgente;
    static String[] meses;

    public Vehiculo(String matricula, String tipo, String averia, int dia, int mes, int anio, double presupuesto) {
        this.matricula = matricula;
        this.tipo = tipo;
        this.averia = averia;
        this.dia = dia;
        this.mes = mes;
        this.anio = anio;
        this.presupuesto = presupuesto;
        this.reparado = 0;
        this.urgente = 0;
    }

    public Vehiculo(String matricula, String tipo, String averia, int dia, int mes, int anio, double presupuesto, int reparado, int urgente) {
        this.matricula = matricula;
        this.tipo = tipo;
        this.averia = averia;
        this.dia = dia;
        this.mes = mes;
        this.anio = anio;
        this.presupuesto = presupuesto;
        this.reparado = reparado;
        this.urgente = urgente;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAveria() {
        return averia;
    }

    public void setAveria(String averia) {
        this.averia = averia;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(double presupuesto) {
        this.presupuesto = presupuesto;
    }

    public int getReparado() {
        return reparado;
    }

    public void setReparado(int reparado) {
        this.reparado = reparado;
    }

    public int getUrgente() {
        return urgente;
    }

    public void setUrgente(int urgente) {
        this.urgente = urgente;
    }

    public String getMesNombre(){
        return meses[this.mes];
    }

    public static void setMeses(String [] meses){
        Vehiculo.meses = meses;
    }

    public String getFecha(){
        return dia+" - "+getMesNombre()+" - "+anio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vehiculo vehiculo = (Vehiculo) o;

        if (matricula != null ? !matricula.equals(vehiculo.matricula) : vehiculo.matricula != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return matricula != null ? matricula.hashCode() : 0;
    }

    @Override
    public int compareTo(Vehiculo vehiculo) {
        if (this.urgente == 1 && vehiculo.getUrgente() == 0)
            return -1;
        else if(this.urgente == vehiculo.getUrgente()){
            if (this.anio < vehiculo.getAnio())
            return -1;
            else if (this.anio == vehiculo.getAnio()) {
                if (this.mes < vehiculo.getMes())
                    return -1;
                else if (this.mes == vehiculo.getMes()) {
                    if (this.dia < vehiculo.getDia())
                        return -1;
                    else
                        return 0;
                }
                return 1;
            }
            return 1;
        }
        return 1;
    }

    /**************************** Para hacerlo Parcelable ************************************/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //El orden es muy importante, porque a la hora de coger los elementos, se cogen por orden.
        parcel.writeString(this.matricula);
        parcel.writeString(this.tipo);
        parcel.writeString(this.averia);
        parcel.writeInt(this.dia);
        parcel.writeInt(this.mes);
        parcel.writeInt(this.anio);
        parcel.writeDouble(this.presupuesto);
        parcel.writeInt(this.reparado);
        parcel.writeInt(this.urgente);
    }

    public Vehiculo(Parcel p){
        this(p.readString(),p.readString(),p.readString(),p.readInt(),p.readInt(),p.readInt(),p.readDouble());
        this.reparado = p.readInt();
        this.urgente = p.readInt();
    }

    public static final Parcelable.Creator<Vehiculo> CREATOR =
            new Parcelable.Creator<Vehiculo>(){

                @Override
                public Vehiculo createFromParcel(Parcel parcel) {
                    return new Vehiculo(parcel);
                }

                @Override
                public Vehiculo[] newArray(int i) {
                    return new Vehiculo[i];
                }
            };
}
