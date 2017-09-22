package com.giulia.miapplicazionediprova2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
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
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements android.widget.CompoundButton.OnCheckedChangeListener {

    /**
     * array di immagini utilizzate nell'applicazione
     */
    ArrayList<Integer> array_immagini;

    /**
     * rappresenta il nome degli elementi (file, cartelle)
     */
    ArrayList<String> array_nomielementi;

    /**
     * Gli array sopra dichiarati mi servono per riempire la listview
     * Struttura dati b-albero per tenere in memoria le cartelle ecc
     */
    ArrayList<String> array_elementiselezionati;

    /**
     * Stringa dove viene memorizzata il percorso corrente
     */
    String currentPath = "/";

    /**
     *  Struttura utilizzata per memorizzare la gerarchia di file e cartelle
     *  i.e.
     *  {
     *      { type: "jpeg", name: "animal.jpeg"},
     *      "Documents": [
     *          { type: "txt", name: "file.txt" }
     *      ],
     *      "Applicazioni": [
     *          { type: "exe", name: "TelematicoSuite.exe" }
     *          { type: "iso", name: "windows-x86.iso" }
     *      ]
     *
     *  }
     */
    JSONObject strutturaDirectory = new JSONObject();

    JSONArray arrayjsonesterno;
    TextView textview;

    FileInputStream fIn = null;
    FileOutputStream fOut = null;
    File filedirectory = null;

    String lettura;
    String key;
    int l=0;
    ListView listView;
    JSONObject newone;
    JSONArray arrayinterno;
    String strparse,str;
    String nome_cartella;
    int count=0;
    int j=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView im = (ImageView) findViewById(R.id.ima);
        im.setImageResource(R.drawable.addfolder);
        listView = (ListView) findViewById(R.id.listv);
        array_immagini = new ArrayList<>();
        array_nomielementi = new ArrayList<String>();
        array_elementiselezionati= new ArrayList<>();
        // arrayobj = new ArrayList<>();
        arrayjsonesterno=new JSONArray();

        final Myadapter myAdapter = new Myadapter(MainActivity.this, array_immagini ,array_nomielementi);
        listView.setAdapter(myAdapter);
        textview=(TextView) findViewById(R.id.view);

        filedirectory = new File(Environment.getExternalStorageDirectory(), "Documents/mauro.json");

        lettura=leggifile(filedirectory);
        //Quello che leggo lo metto in un array json
        System.out.println(lettura);
        arrayjsonesterno= parseJSON(lettura, array_immagini, array_nomielementi);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id) {

                nome_cartella = (String) listView.getItemAtPosition(pos);
                /**v
                 * aggiungo la cartella selezionata al percorso corrente
                 */
                currentPath +=  nome_cartella + "/" ;

                TextView uiCurrentPath = (TextView)findViewById(R.id.currentPath);
                uiCurrentPath.setText(currentPath);

                try {

                    JSONObject obj = arrayjsonesterno.getJSONObject(pos);
                    JSONArray jArray = obj.getJSONArray(nome_cartella);
                    myAdapter.setData(jArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {


            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {

                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.my_context_menu, menu);

                return true;

            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {


                switch (item.getItemId()) {
                    case R.id.delete_id:
                        lettura=leggifile(filedirectory);
                        arrayjsonesterno=Elimina_cartella(array_elementiselezionati,array_immagini,array_nomielementi,filedirectory,lettura);
                        Toast.makeText(getBaseContext(), count + "elementi eliminati", Toast.LENGTH_SHORT).show();
                        textview.setText(arrayjsonesterno.toString());
                        count = 0;
                        mode.finish();
                        return true;
                    case R.id.rinomina:
                        Rinomina(array_elementiselezionati,array_nomielementi,filedirectory);
                        mode.finish();
                        return true;


                    default:
                        return false;
                }

            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {

            }

            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {
               count =  count+1;
                mode.setTitle(count + "elementi selezionati");
                array_elementiselezionati.add(array_nomielementi.get(position));
                

            }

        });

        im.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {

                System.out.println("onClick");

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.dialog_newfolder, null);
                final EditText crea_cartella = (EditText) mView.findViewById(R.id.nome_cartella);

                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    str=Crea_cartella(crea_cartella,arrayjsonesterno,array_immagini,array_nomielementi);
                    textview.setText(str);
                    scrivifile(str,filedirectory);
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

    }





private String Crea_cartella(EditText crea, JSONArray directory, ArrayList<Integer> immagini, ArrayList<String> nomi) {

    JSONObject obj_nuovo;
    JSONArray array_nuovo;
    String risultato;

    if (!crea.getText().toString().isEmpty()) {

        String nomeElemento = crea.getText().toString();
        nomi.add(nomeElemento);
        immagini.add(R.drawable.folder);

        obj_nuovo = new JSONObject();
        array_nuovo = new JSONArray();

        try {
            obj_nuovo.put(nomeElemento, array_nuovo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //aggiungo all' array json la cartella

        directory.put(obj_nuovo);
        /**
         * controllo il risultato dell'inserimento della cartella
         */
        risultato = parse.toString();

        Toast.makeText(MainActivity.this, "Cartella aggiunta", Toast.LENGTH_SHORT).show();

        return risultato;
    }
    return null;
}

/**
 *
 * @param testo testo dell'elemento da inserire
 * @param directory
 * @param immagini
 * @param nomi
 * @param type
 */
private void creaElemento(EditText testo, JSONArray directory,  ArrayList<Integer> immagini, ArrayList<String> nomi, String type) {


}

private void Rinomina(final ArrayList<String> selezionati, final ArrayList<String> nomi, final File f) {

    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
    final View mView = getLayoutInflater().inflate(R.layout.dialog_rename, null);
    final EditText rinomina = (EditText) mView.findViewById(R.id.rinomina_cartella);

    mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
        int flag = 0;
        int i=0;
        JSONArray parse=new JSONArray();
        String temp;
        JSONArray elem1=new JSONArray(  );

        if (!rinomina.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this, "Elemento rinominato", Toast.LENGTH_SHORT).show();
            String s;
            s = rinomina.getText().toString();
            String elem=selezionati.get(0);
            int k=nomi.indexOf(elem);
           nomi.set(k,s);
            count=0;
            selezionati.clear();
            temp=leggifile(f);

            try {
                parse = new JSONArray(temp);
                JSONObject obj = new JSONObject();
                for (i = 0; i < parse.length() && flag == 0; i++) {
                    //Estraggo l' oggetto json dall'array, in particolare l'i-esimo oggetto json
                    obj = parse.getJSONObject(i);
                    //ottengo le chiavi che ci sono nel mio array json
                    Iterator iterator = obj.keys();
                    while (iterator.hasNext() && flag == 0) {
                        String key = (String) iterator.next();
                        //uso le chiavi per aggiungerle alla lista che mi serve poi per rappresentare il tutto
                        if (elem.equals(key)) {
                            flag = 1;
                            //obj.remove(elem);
                            //Voglio cambiare il valore della chiave
                        }

                    }
                } scrivifile(parse.toString(),f);


        } catch (Exception e) {
                e.printStackTrace();
            }}
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

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
private JSONArray Elimina_cartella(ArrayList<String> daEliminare, ArrayList<Integer> immagini , ArrayList<String> nomi, File f, String temp){
    JSONArray parse=new JSONArray();
    int i=0;


    for (String msg : daEliminare) {
        j = nomi.indexOf(msg);
       immagini.remove(j);
        nomi.remove(msg);
        //DEVO MODIFICARE QUI IL FILE
        //modificafile();
        leggifile(f);
        int flag = 0;

        try {
            parse = new JSONArray(temp);
            JSONObject obj = new JSONObject();
            for (i = 0; i < parse.length() && flag == 0; i++) {
                //Estraggo l' oggetto json dall'array, in particolare l'i-esimo oggetto json
             obj = parse.getJSONObject(i);
                //ottengo le chiavi che ci sono nel mio array json
                Iterator iterator = obj.keys();
                while (iterator.hasNext() && flag == 0) {
                    String key = (String) iterator.next();
                    //uso le chiavi per aggiungerle alla lista che mi serve poi per rappresentare il tutto
                    if (msg.equals(key)) {
                        flag = 1;
                        obj.remove(key);
                        parse.remove(i);

                    }

                }
            } scrivifile(parse.toString(),f);

        } catch (Exception e) {
            e.printStackTrace();
        }}


    daEliminare.clear();
        return parse;
    }


    private String leggifile(File f){
        FileInputStream stream;
        String temp="";
        int c;
            try {
            stream = new FileInputStream(f);
                while((c=stream.read())!=-1){
                    temp=temp+ Character.toString((char)c);
                }

            stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        return temp;
    }


    public void scrivifile(String scrivi, File f){
        FileOutputStream fOut1;
        try {

           fOut1 = new FileOutputStream(f);
           fOut1.write(scrivi.getBytes());
           fOut1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONArray parseJSON(String risultato_lettura, ArrayList<Integer> immagini, ArrayList <String> nomi) {
        JSONArray parse=new JSONArray();
        JSONObject obj =new JSONObject();
        int i=0;
        try{
            parse = new JSONArray(risultato_lettura);
                for (i = 0; i < parse.length(); i++) {
                //Estraggo l' oggetto json dall'array, in particolare l'i-esimo oggetto json
                obj = parse.getJSONObject(i);
                //ottengo le chiavi che ci sono nel mio array json
                Iterator iterator = obj.keys();
                while(iterator.hasNext()) {
                    String key = (String) iterator.next();
                    //uso le chiavi per aggiungerle alla lista che mi serve poi per rappresentare il tutto
                    nomi.add(key);
                    immagini.add(R.drawable.folder);
                }}
        } catch (JSONException e) {
        e.printStackTrace();
    }return parse;

}

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pos=listView.getPositionForView(buttonView);
        if(pos!=listView.INVALID_POSITION);
        String n=array_nomielementi.get(pos);
        buttonView.setSelected(isChecked);
    }

}




