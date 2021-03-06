package com.giulia.miapplicazionediprova2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giulia on 26/06/17.
 */

public class Myadapter extends BaseAdapter {

    private Context context;
    private ArrayList<Integer> listid;
    private ArrayList<String> nameList;

    public Myadapter(Context context, ArrayList<Integer> listid, ArrayList<String> nameList) {
        super();
        this.context = context;
        this.listid = listid;
        this.nameList = nameList;
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int position) {
        return nameList.get( position );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate( context, R.layout.items_list, null );


        }
        ImageView images = (ImageView) convertView.findViewById( R.id.imageView );
        TextView text = (TextView) convertView.findViewById( R.id.textView );
        // images.setImageResource( listid.get( position ) );
        text.setText( nameList.get( position ) );
        return convertView;
    }


    public void setData(TreeNode<JSONObject> currentNode) {

        ArrayList<String> temp = new ArrayList<>();

        for (TreeNode<JSONObject> child :currentNode.getChildren()) {
            try {
                temp.add(child.data.get("name").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        this.nameList = temp;
        notifyDataSetChanged();
    }


}



