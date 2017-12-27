package com.catchthing.catchthing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.catchthing.catchthing.games.GameLeft;
import com.catchthing.catchthing.games.GameRight;


public class MainActivity extends AppCompatActivity {

    private static Long userId;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            userId = getIntent().getLongExtra("userId", 0);
            deviceId = getIntent().getStringExtra("deviceId");
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        deviceId = savedInstanceState.getString("deviceId");
        userId = savedInstanceState.getLong("userId");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("deviceId", deviceId);
        savedInstanceState.putLong("userId", userId);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void goGameLeft(View view) {
        Intent intent = new Intent(this, GameLeft.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    public void goGameRight(View view) {
        Intent intent = new Intent(this, GameRight.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
}
