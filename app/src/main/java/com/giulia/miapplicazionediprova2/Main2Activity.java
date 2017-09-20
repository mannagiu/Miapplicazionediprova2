package com.giulia.miapplicazionediprova2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Main2Activity extends AppCompatActivity {

    TextView tv2;
    ArrayList<String> listp2;
    //ARRAY DI IMMAGINI
    ArrayList<Integer> list_i2;
    ListView mylist2;
    FileInputStream stream = null;
    FileOutputStream fOut = null;
    File filedirectory = null;
    String temp;
    JSONArray parse= null;

    JSONObject semplice;
    int i;
    String s2;
    JSONArray arrayinterno;
    Bundle datipassati;
    String dato1;
    String ciao;
    JSONObject obj;
    String key;
    Button b2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);
        tv2=(TextView)findViewById(R.id.tv2) ;
        ImageView im = (ImageView) findViewById(R.id.ima);
        im.setImageResource(R.drawable.addfolder);
        listp2=new ArrayList<>();
        list_i2=new ArrayList<>();
        final Myadapter myAdapter1= new Myadapter(Main2Activity.this,list_i2,listp2);
        mylist2 = (ListView) findViewById(R.id.listv2);
        mylist2.setAdapter(myAdapter1);
        filedirectory = new File(Environment.getExternalStorageDirectory(), "Documents/giulia5.json");
        obj= new JSONObject();
        arrayinterno=new JSONArray();
        b2=(Button) findViewById(R.id.b2);


        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final AlertDialog.Builder mBuilder2= new AlertDialog.Builder(Main2Activity.this);
                final View mView2=getLayoutInflater().inflate(R.layout.dialog_newfolder,null);
                final EditText crea_cartella = (EditText) mView2.findViewById(R.id.nome_cartella);

                datipassati = getIntent().getExtras();
                dato1 = datipassati.getString("dato");


                mBuilder2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!crea_cartella.getText().toString().isEmpty()) {
                            Toast.makeText(Main2Activity.this, "Cartella aggiunta", Toast.LENGTH_SHORT).show();

                            s2 = crea_cartella.getText().toString();

                            listp2.add(s2);
                            list_i2.add(R.drawable.folder);

                            //CREO ADESSO L'OGGETTO JSON DA INSERIRE ALL' INTERNO DEL FILE JSON

                            try {
                                semplice=new JSONObject();
                                semplice.put(s2,"dir");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                stream = new FileInputStream(filedirectory);

                                try {
                                    temp = "";
                                    int c;
                                    while ((c = stream.read()) != -1) {
                                        temp = temp + Character.toString((char) c);
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    stream.close();

                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            try {
                                parse = new JSONArray(temp);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            int flag=0;
                            for (i = 0; i < parse.length()&&flag==0; i++) {
                                try {
                                    obj=parse.getJSONObject(i);
                                    // con questo dovrei ottenere lil valore
                                    Iterator iterator = obj.keys();
                                    while (iterator.hasNext() && flag == 0) {
                                        key = (String) iterator.next();
                                        if (key.equals(dato1)) {
                                            flag = 1;
                                            arrayinterno = obj.getJSONArray(dato1);
                                            arrayinterno.put(semplice);
                                        }

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            ciao=parse.toString();
                            tv2.setText(ciao);
                            try {
                                fOut = new FileOutputStream(filedirectory);
                                fOut.write(ciao.getBytes());
                                } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    fOut.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }



                            //apro il file giulia4.json
                            //cerco il valore della chiave s2 e metto s2 nell'array json corrispondente

                                               //APRO IL FILE JSON E AGGIUNGO L'OGGETTO JSON IN CORRISPONDENZA DELLA CARTELLA SU CUI HO SCHIACCIATO "CREA CARTELLA"


                        }




                    }


                });
                mBuilder2.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });


                mBuilder2.setView(mView2);
                AlertDialog dialog = mBuilder2.create();
                dialog.show();


                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pagina3 = new Intent(Main2Activity.this, Main3Activity.class);
                        startActivity(pagina3);
                    }
                });









                //scrivifile(r1);
                //tv2.setText(r1);








                }

    });

}



}






//Myobject o=new Myobject(s,"dir",false);
//arrayobj.add(o);
// listp.add(s);
//  list_i.add(R.drawable.folder);

                          /*  JSONObject newone = new JSONObject();
                            JSONArray arrayinterno = new JSONArray();
                            try {
                                newone.put(s, arrayinterno);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //IN QUESTO CASO MI TROVO NELLA SECONDA ACTIVITY, A SECONDA DELLA CARTELLA SU CUI CLICCO
                            //NELLA PRIMA ACTIVITY , SALVO IL NOME DELL'ELEMENTO SU CUI CLICCO IN QUALCHE MODO E LO PASSO
                            //NELLA SECONDA ACTIVITY
                            //aggiungo all' array json la cartella
                            parse.put(newone);
                            //aggiorno il file
                            String ciao;
                            ciao = parse.toString();
                            try {

                                fOut = new FileOutputStream(filedirectory);

                                fOut.write(ciao.getBytes());

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    fOut.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            //Rileggo tutto
                            try {
                                stream = new FileInputStream(filedirectory);

                                try {
                                    temp = "";
                                    int c;
                                    while ((c = stream.read()) != -1) {
                                        temp = temp + Character.toString((char) c);
                                    }

                                    tv.setText(temp);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    stream.close();
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //Quello che leggo lo metto in un array json


                            try {
                                parse = new JSONArray(temp);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JSONObject obj = new JSONObject();

                            for (i = 0; i < parse.length(); i++) {


                                try {
                                    //Estraggo l' oggetto json dall'array, in particolare l'i-esimo oggetto json
                                    obj = parse.getJSONObject(i);
                                    //ottengo le chiavi che ci sono nel mio array json
                                    Iterator iterator = obj.keys();
                                    while (iterator.hasNext()) {
                                        String key = (String) iterator.next();
                                        //uso le chiavi per aggiungerle alla lista che mi serve poi per rappresentare il tutto
                                        listp.add(key);
                                        list_i.add(R.drawable.folder);

                                    }
                                    //inietto i dati nell' Adapter
                                    //  mylist.setAdapter(myAdapter);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                    }
                });

                mBuilder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });


                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();

            }
        });
*/








