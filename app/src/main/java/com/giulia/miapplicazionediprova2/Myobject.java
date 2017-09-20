package com.giulia.miapplicazionediprova2;

/**
 * Created by giulia on 23/05/17.
 */
import android.support.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class Myobject implements Comparable<Myobject>{

    String nome;
    String tipo;
    boolean check;
    int anno;
    int mese;
    int giorno;
    int ore ;
    int min ;
    int sec ;




    public Myobject(String nome1, String tipo1, boolean check1){

        this.nome=nome1;
        this.tipo=tipo1;
        this.anno = 0;
        this.mese = 0;
        this.giorno = 0;
        this.ore = 0;
        this.min = 0;
        this.sec = 0;
        this.check=check1;

    }

    public Myobject() {

    }

    public void data(){
        GregorianCalendar gc = new GregorianCalendar();
        this.anno = gc.get(Calendar.YEAR);
        this.mese = gc.get(Calendar.MONTH) + 1;
        this.giorno = gc.get(Calendar.DATE);
        this.ore = gc.get(Calendar.HOUR);
        this.min = gc.get(Calendar.MINUTE);
        this.sec = gc.get(Calendar.SECOND);

    }
    public String getTipo(){
        return (this.tipo);
    }

    public String getNome(){
        return (this.nome);
    }

    public boolean getCheck(){ return (this.check);}
    public void setCheck(boolean c){
        this.check=c;

    }

    /*public void dim_file(File f){
        this.dim_file=f.length(); //dimensione in bytes

    }*/

    /* DEVO PASSARE IL FILE CHE M'INTERESSA
     * BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    File f = new File(in.readLine());

    if (f.exists()) {

    long file_size = f.length(); //dimensione in bytes
    }


    */
    public void array() throws IOException{
        //Stampa su un file i blocchi di memoria che vengono assegnati
        FileWriter w;
        w=new FileWriter("prova.txt");

        BufferedWriter b;
        b=new BufferedWriter (w);
        b.write("");//blocchi di memoria
        b.close();

    }

    public void scrivi_tutto() throws IOException{
        FileWriter w1;
        w1=new FileWriter("prova2.txt");
        BufferedWriter b1;
        b1=new BufferedWriter(w1);
        b1.write("");



    }

    @Override
    public int compareTo(@NonNull Myobject o) {
        return 0;
    }

  /*  public abstract boolean add(Myobject value);
    public abstract boolean contain(Myobject value);
    public abstract void clear();
    public abstract Myobject remove(Myobject value);
    public abstract int size();

    public abstract boolean validate();*/
}

