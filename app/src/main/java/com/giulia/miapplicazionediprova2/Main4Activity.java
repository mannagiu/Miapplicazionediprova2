package com.giulia.miapplicazionediprova2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Main4Activity extends AppCompatActivity {

    String temp;
    JSONArray parse;
    ArrayList<Integer> list_i4;
    ArrayList<String> listp4;
    ListView mylist5;
    int i;
    Bundle dato;
    String dato1;
    JSONArray arrayinterno;
    JSONObject obj;
    Iterator iterator;
    Toast toast;
    TextView tv4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        parse=new JSONArray();
        list_i4=new ArrayList<>();
        listp4=new ArrayList<>();
        mylist5=(ListView)findViewById(R.id.listv);
        final Myadapter myAdapter = new Myadapter(Main4Activity.this, list_i4, listp4);
        mylist5.setAdapter(myAdapter);
        arrayinterno=new JSONArray();
        dato = getIntent().getExtras();
        dato1 = dato.getString("dato");
        obj=new JSONObject();
        tv4=(TextView)findViewById(R.id.tv4);


        try {
            parse = new JSONArray(dato1);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        for (i = 0; i < parse.length(); i++) {


            try {
                //Estraggo l' oggetto json dall'array, in particolare l'i-esimo oggetto json
                obj = parse.getJSONObject(i);
                //ottengo le chiavi che ci sono nel mio array json
                iterator = obj.keys();
                while(iterator.hasNext()) {
                    String key = (String) iterator.next();
                    //uso le chiavi per aggiungerle alla lista che mi serve poi per rappresentare il tutto
                    listp4.add(key);
                    list_i4.add(R.drawable.folder);
                                }



            } catch (JSONException e) {
                e.printStackTrace();
            }}

            mylist5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        obj=parse.getJSONObject(position);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String cartella = (String) mylist5.getItemAtPosition(position);
                    try {
                        arrayinterno=obj.getJSONArray(cartella);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                            Intent pagina5 = new Intent(getApplicationContext(), Main5Activity.class);
                            pagina5.putExtra("array", arrayinterno.toString());
                            startActivity(pagina5);

                    }
            });

                        /*mylist5.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (arrayinterno.length() != 0) {
                                    Intent pagina5 = new Intent(getApplicationContext(), Main5Activity.class);
                                    pagina5.putExtra("array", arrayinterno.toString());
                                    startActivity(pagina5);

                                } else
                                    Toast.makeText(getApplicationContext(), "Cartella vuota", Toast.LENGTH_LONG);
                            }});*/











    }
}
