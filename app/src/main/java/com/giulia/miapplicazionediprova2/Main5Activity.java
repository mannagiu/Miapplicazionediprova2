package com.giulia.miapplicazionediprova2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Main5Activity extends AppCompatActivity {
    Bundle datopassato;
    String dato5;
    JSONArray datop;
    ListView lv5;
    TextView tv5;
    ArrayList<Integer> listi5;
    ArrayList<String> listp5;
    Myadapter Myadapter5;
    String key;
    JSONObject obj;
    int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        lv5=(ListView) findViewById(R.id.listv5);
        tv5=(TextView)findViewById(R.id.tv5);
        listi5=new ArrayList<>();
        listp5=new ArrayList<>();
       Myadapter5 = new Myadapter(Main5Activity.this, listi5, listp5);
        lv5.setAdapter(Myadapter5);
        obj=new JSONObject();
        // inietto i dati
        //mylist.setAdapter(myAdapter);

        datopassato=getIntent().getExtras();
        dato5= datopassato.getString("array");
        try {
            datop=new JSONArray(dato5);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        for (i = 0; i < datop.length(); i++) {
            try {
                // con questo dovrei ottenere lil valore

                obj=datop.getJSONObject(i);
                Iterator iterator = obj.keys();
                while (iterator.hasNext()) {
                    key = (String) iterator.next();
                    listp5.add(key);
                    listi5.add(R.drawable.folder);
                    }



            } catch (JSONException e) {
                e.printStackTrace();
            }
    }
}}
