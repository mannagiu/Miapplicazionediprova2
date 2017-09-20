package com.giulia.miapplicazionediprova2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main3Activity extends AppCompatActivity {
    Bundle dati;
    String uno;
    String due;
    File filedirectory;
    FileInputStream stream = null;
    String temp;
    TextView tv3;
    Button b3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        tv3=(TextView) findViewById(R.id.tv3);
        b3=(Button) findViewById(R.id.b3);

        filedirectory = new File(Environment.getExternalStorageDirectory(), "Documents/giulia5.json");
        try {
            stream = new FileInputStream(filedirectory);

            try {
                temp="";
                int c;
                while((c=stream.read())!=-1){
                    temp=temp+ Character.toString((char)c);
                }

                tv3.setText(temp);

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

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pagina4=new Intent(Main3Activity.this, Main4Activity.class);
                pagina4.putExtra("dato",temp);
                startActivity(pagina4);


            }
        });


    }
}
