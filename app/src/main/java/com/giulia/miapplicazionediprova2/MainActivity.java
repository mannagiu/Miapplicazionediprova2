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

    public MainActivity () {

        // #########################################################################
        // creazione elemento root
        // #########################################################################
        this.myAdapter = new Myadapter(MainActivity.this, new ArrayList<Integer>(), new ArrayList<String>());

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

        ImageView aggiungiElemento = (ImageView) findViewById(R.id.aggiungiElemento);
        aggiungiElemento.setImageResource(R.drawable.addfolder);
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

        // creazione elemento su click aggiunta
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
    private TreeNode<JSONObject> creaElemento(String testo, TreeNode<JSONObject> currentNode) {

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

        salva(encode(root));


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

    private void salva(JSONObject data) {

        String root = Environment.getExternalStorageDirectory().toString();

        try {
            // Create a new FileWriter object
            FileWriter fileWriter = new FileWriter(root + "/strutturaDirectory.json");

            System.out.println("filewriter: " + fileWriter);
            // Writting the jsonObject into sample.json
            fileWriter.write(data.toString());
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
                this.decode(this.root);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            fileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * serializza la struttura in un formato json
     * @param node
     * @return
     */
    private JSONObject encode(TreeNode<JSONObject> node) {

        JSONObject data = new JSONObject();

        if (node.getChildren().size() == 0) {
            return null;
        }

        try {

            data = node.data;

            ArrayList<JSONObject> children = new ArrayList<>();
            for (TreeNode<JSONObject> child: node.getChildren()) {
                children.add(child.data);
                // richiamo ricorsivamente sul nodo figlio
                encode(child);
            }

            data.put("children" , children);

        } catch (JSONException e) {
            e.printStackTrace();                                                                                                                                                                                                                                              ;
        }

        return data;
    }

    /**
     * deserializza il contenuto della stringa in input e costruisce la struttura
     */
    private JSONObject decode(TreeNode<JSONObject> node) {

        System.out.print("decode jsonSavedOnDisk : " + this.jsonSavedOnDisk);

        try {

            JSONArray children = new JSONArray(this.jsonSavedOnDisk.get("children").toString());
            if (children.length() == 0) {
                return null;
            }

            if (node.data.get("type").toString().equals("none")) {
                this.currentNode = node;
                this.creaElemento(currentNode.data.get("name").toString(), currentNode);
                for (int i=0; i<children.length();i++) {
                    decode(new TreeNode<JSONObject>((JSONObject)children.get(i)));
                }
            }
            this.myAdapter.setData(currentNode);

        } catch (JSONException exception) {

            System.out.println("Eccezione decode " + exception);
        }

        return null;
    }


}




