package com.giulia.miapplicazionediprova2;

import android.content.DialogInterface;
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

public class MainActivity extends AppCompatActivity {

    /**
     * albero di oggetti json
     * for example
     * { type: "iso", name: "windows-x86.iso" }
     * { type: "img", name: "foo.img" }
     */
    private TreeNode<JSONObject> root;
    private TreeNode<JSONObject> currentNode;
    private ArrayList<String> currentPath = new ArrayList<>();
    private TextView textview;
    private ListView listView;

    public MainActivity () {

        // #########################################################################
        // creazione elemento root
        // #########################################################################
        JSONObject objRoot = new JSONObject();
        try {
            objRoot.put("type", "none");
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

        ImageView aggiungiElemento = (ImageView) findViewById(R.id.aggiungiElemento);
        aggiungiElemento.setImageResource(R.drawable.addfolder);
        listView = (ListView) findViewById(R.id.listv);

        // adattatore per scambiare dati con la listView
        final Myadapter myAdapter = new Myadapter(MainActivity.this, new ArrayList<Integer>(), new ArrayList<String>());
        listView.setAdapter(myAdapter);
        textview = (TextView) findViewById(R.id.view);

        /**
         *  click su un elemento della lista
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id) {

                String nomeCartella = (String) listView.getItemAtPosition(pos);
                String displayPath = "";

                // se l'elemento è il padre vado al parent e rimuovo elemento da currentPath
                // TODO: sistemare codice

                if (nomeCartella == "..") {
                    currentNode = currentNode.parent;
                    currentPath.remove(currentPath.size() -1);
                } else {
                    currentPath.add(nomeCartella);
                    currentNode = currentNode.getChildren().get(pos);
                }
                // monto il path corrente
                for (String elem: currentPath) {
                    displayPath += "/" + elem;
                }

                TextView uiCurrentPath = (TextView) findViewById(R.id.currentPath);
                uiCurrentPath.setText(displayPath);
                myAdapter.setData(currentNode);
            }

        });

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        /**
         *  creazione elemento su click aggiunta
         */
        aggiungiElemento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.dialog_newfolder, null);
                final EditText nomeCartella = (EditText) mView.findViewById(R.id.nome_cartella);

                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        creaElemento(nomeCartella.getText().toString(), currentNode);
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
    private void creaElemento(String testo, TreeNode<JSONObject> currentNode) {

        JSONObject obj = new JSONObject();
        try {
            // creo riferimento al padre quando clicco su una cartella
            System.out.println("Current node parent : " + currentNode.parent);
            obj.put("type", "folder");
            obj.put("name", testo);
            TreeNode<JSONObject> child = currentNode.addChild(obj);

            if (!testo.equals("..")) {
                // creo elemento che fa riferimento al padre
                creaElemento("..", child);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}




