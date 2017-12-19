package com.catchthing.catchthing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.catchthing.catchthing.games.GameLeft;
import com.catchthing.catchthing.games.GameRight;


public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        findViewById(R.id.gifview);
    }

    public void goGameLeft(View view) {
        Intent intent = new Intent(this, GameLeft.class);
        startActivity(intent);
    }

    public void goGameRight(View view) {
        Intent intent = new Intent(this, GameRight.class);
        startActivity(intent);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}
