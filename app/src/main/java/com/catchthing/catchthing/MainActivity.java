package com.catchthing.catchthing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.catchsquare.catchsquare.R;

import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private TextView record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
    }

    private void Init() {
        record = (TextView) findViewById(R.id.textViewRecord);
    }

    public void goGame(View view) {
        Intent intent = new Intent(this, Game.class);
        startActivity(intent);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        readScore();
    }

    private void readScore() {
        try {
            FileInputStream fin = openFileInput("score.cc");
            record.setText(String.valueOf(fin.read()));
            fin.close();
        } catch (IOException e) {
            record.setText(String.valueOf(0));
            e.printStackTrace();
        }
    }
}
