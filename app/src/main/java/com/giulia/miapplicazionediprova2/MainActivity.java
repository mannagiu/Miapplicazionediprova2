package com.giulia.miapplicazionediprova2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {

    /**
     * albero di oggetti json
     * for example
     * { type: "iso", name: "windows-x86.iso" }
     * { type: "img", name: "foo.img" }
     */
    private TreeNode<JSONObject> root;
    /**
     * definisce il puntatore al nodo corrente
     */
    private TreeNode<JSONObject> currentNode;
    private ArrayList<String> currentPath = new ArrayList<>();
    private TextView textview;
    private ListView listView;
    private JSONObject jsonSavedOnDisk;
    private Myadapter myAdapter;
    private DbConnector dbConnector;
    private FloatingActionsMenu addElement;

    public MainActivity () {

        // #########################################################################
        // creazione elemento root
        // #########################################################################
        this.myAdapter = new Myadapter(MainActivity.this, new ArrayList<Integer>(), new ArrayList<String>());
        this.dbConnector = new DbConnector();

        JSONObject objRoot = new JSONObject();
        try {
            objRoot.put("type", "none");
            objRoot.put("name", "root");
            root = new TreeNode<JSONObject>(objRoot);
            currentNode = root;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // lettura file json
        this.leggi();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.i("TAG","Permission Granted");
                }else{
                    Log.i("TAG","Permission Denied");
                }
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

        addElement = (FloatingActionsMenu)findViewById(R.id.addElement);
        listView = (ListView) findViewById(R.id.listv);

        // adattatore per scambiare dati con la listView
        listView.setAdapter(this.myAdapter);
        textview = (TextView) findViewById(R.id.view);

        // click su elemento della lista
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
        FloatingActionButton floatingAddFolder  = (FloatingActionButton)findViewById(R.id.addfolder);
        FloatingActionButton floatingAddFile    = (FloatingActionButton)findViewById(R.id.addfile);

        floatingAddFolder.setOnClickListener(this.onClickFloatingButton("folder"));
        floatingAddFile.setOnClickListener(this.onClickFloatingButton("file"));

    } // end onCreate

    /**
     * crea figli per il nodo corrente
     * @param testo testo dell'elemento da inserire
     * @param currentNode nodo a cui aggiugere l'elemento
     */
    private TreeNode<JSONObject> creaElemento(String testo, TreeNode<JSONObject> currentNode, String type) {

        JSONObject obj = new JSONObject();
        try {
            // creo riferimento al padre quando clicco su una cartella
            System.out.println("Current node parent : " + currentNode.parent);
            obj.put("type", type);
            obj.put("name", testo);
            TreeNode<JSONObject> child = currentNode.addChild(obj);

            if (type == "folder" && !testo.equals("..")) {
                // creo elemento che fa riferimento al padre
                creaElemento("..", child, "folder");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.dbConnector.salva(this.dbConnector.encode(root));

        return currentNode;

        // da Android 23 occorre richiedere i permessi di scrittura runtime
        // non è piu sufficiente il manifest

        /*
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else{
            salva(encode(root));
        }
        */

    }

    private void leggi() {

        String root = Environment.getExternalStorageDirectory().toString();
        System.out.println("leggi file ");

        try {

            FileReader fileReader = new FileReader(root + "/strutturaDirectory.json");

            BufferedReader br = new BufferedReader(fileReader);
            String s;
            String data = "";
            while((s = br.readLine()) != null) {
                System.out.println(s);
                data = s;
            }

            System.out.println("leggi json");

            try {
                System.out.println(data);
                this.jsonSavedOnDisk = new JSONObject(data);

                this.root = new TreeNode<>(this.jsonSavedOnDisk);
                this.decode(this.root, new JSONArray(this.jsonSavedOnDisk.get("children").toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            fileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * deserializza il contenuto della stringa in input e costruisce la struttura
     */
    private JSONObject decode(TreeNode<JSONObject> node, JSONArray children) {

        System.out.print("decode jsonSavedOnDisk : " + this.jsonSavedOnDisk);

        try {

            // JSONArray children = new JSONArray(this.jsonSavedOnDisk.get("children").toString());
            if (children.length() == 0) {
                return null;
            }

            if (!node.data.get("type").toString().equals("none")) {
                this.currentNode = node;
                this.creaElemento(currentNode.data.get("name").toString(), currentNode, "folder");
                for (int i=0; i< children.length();i++) {
                    JSONObject obj = (JSONObject)children.get(i);
                    decode(new TreeNode<JSONObject>(obj), new JSONArray(obj.get("children").toString()));
                }
            }
            // aggiorno i dati nelle vista
            this.myAdapter.setData(currentNode);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    private View.OnClickListener onClickFloatingButton(final String type) {

        return new View.OnClickListener() {

            @Override
            public void onClick(final View v) {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                final View mView;
                mView = getLayoutInflater().inflate(R.layout.dialog_newfile, null);
                final TextView createElementTitle = (TextView) mView.findViewById(R.id.createElementTitle);

                System.out.println("createElementTitle " + createElementTitle);

                if (type == "folder") {
                    createElementTitle.setText(R.string.nomecartella);
                } else {
                    createElementTitle.setText("Crea nuovo file");
                }

                final EditText nomeCartella = (EditText) mView.findViewById(R.id.nomefile);

                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        creaElemento(nomeCartella.getText().toString(), currentNode, "folder");
                        myAdapter.setData(currentNode);
                        addElement.collapse();
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

            } // end onClick

        };

    }

}




