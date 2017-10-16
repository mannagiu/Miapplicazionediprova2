package com.giulia.miapplicazionediprova2;

import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by maurovitale on 16/10/17.
 */

public class DbConnector {

    public void salva(JSONObject data) {

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

    /**
     * serializza la struttura in un formato json
     * @param node
     * @return
     */
    public JSONObject encode(TreeNode<JSONObject> node) {

        /**
         * {
         *     type: "none",
         *     children : [
         *          {
         *              type : "folder",
         *              data : "01/01/2017",
         *              children: [
         *                  {children : []},
         *                  {..},
         *                  {..}
         *              ]
         *           }
         *     ]
         * }
         */

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


}
