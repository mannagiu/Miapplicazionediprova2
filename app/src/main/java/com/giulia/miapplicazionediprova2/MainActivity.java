package com.giulia.miapplicazionediprova2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * albero di oggetti json
     * for example
     * { type: "iso", name: "windows-x86.iso" }
     * { type: "img", name: "foo.img" }
     */
    private TreeNode<JSONObject> root;
    private TreeNode<JSONObject> currentNode;
    private String currentPath = "/";
    private TextView textview;
    private ListView listView;

    public MainActivity () {

        // #########################################################################
        // creazione elemento root
        // #########################################################################
        JSONObject objRoot = new JSONObject();
        try {
            objRoot.put("type", "folder");
            objRoot.put("name", "root");
            root = new TreeNode<JSONObject>(objRoot);
            currentNode = root;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * metodo per creare l'applicazione android
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView im = (ImageView) findViewById(R.id.ima);
        im.setImageResource(R.drawable.addfolder);
        listView = (ListView) findViewById(R.id.listv);

        // adattatore per scambiare dati con la listView
        final Myadapter myAdapter = new Myadapter(MainActivity.this, new ArrayList<Integer>(), new ArrayList<String>());
        listView.setAdapter(myAdapter);
        textview=(TextView) findViewById(R.id.view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id) {

            String nomeCartella = (String) listView.getItemAtPosition(pos);
            /**
             * aggiungo la cartella selezionata al percorso corrente
             */
            currentPath +=  nomeCartella + "/" ;

            TextView uiCurrentPath = (TextView)findViewById(R.id.currentPath);
            uiCurrentPath.setText(currentPath);

            myAdapter.setData(currentNode);

            }

        });

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        im.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.dialog_newfolder, null);
                final EditText nomeCartella = (EditText) mView.findViewById(R.id.nome_cartella);

                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        creaElemento(nomeCartella, currentNode);
                        myAdapter.setData(currentNode);
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

    } // end onCreate

    /**
     * crea figli per il nodo corrente
     * @param testo testo dell'elemento da inserire
     * @param currentNode nodo a cui aggiugere l'elemento
     */
    private void creaElemento(EditText testo, TreeNode<JSONObject> currentNode) {

        JSONObject obj = new JSONObject();
        try {

            obj.put("type", "folder");
            obj.put("name", testo.getText().toString());
            currentNode.addChild(obj);
            System.out.println("json object created : " + obj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}




