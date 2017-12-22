package com.catchthing.catchthing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.catchthing.catchthing.connect.HttpRequestTask;
import com.catchthing.catchthing.games.GameLeft;
import com.catchthing.catchthing.games.GameRight;
import com.catchthing.catchthing.status.StateMain;

import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    private static final StringBuilder URL = new StringBuilder()
            .append("http://192.168.1.7:8080");
    private static Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.gifview);
        Init();
    }

    private void Init() {
        @SuppressLint("HardwareIds")
        String deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        System.out.println("DEVICE ID =" + deviceId);

        StringBuilder url = new StringBuilder()
                .append(URL)
                .append("/auth")
                .append("/user")
                .append("?deviceId=")
                .append(deviceId);

        System.out.println(url);

        StateMain stateMain = getDatas(url);

        if (stateMain != null) {
            userId = stateMain.getUserId();
        } else {
            userId = 0L;
        }

        System.out.println(userId);
    }

    private StateMain getDatas(StringBuilder url) {
        StateMain stateMain = null;
        try {
             stateMain = new HttpRequestTask(url.toString()).execute().get();
            System.out.println(stateMain);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return stateMain;
    }

    public void goGameLeft(View view) {
        StringBuilder url = new StringBuilder()
                .append(URL)
                .append("/game")
                .append("/getGameLeft")
                .append("?userId=")
                .append(userId);

        StateMain stateMain = getDatas(url);

        System.out.println("STATE MAIN LEFT : " + stateMain);

        Intent intent = new Intent(this, GameLeft.class);
        intent.putExtra("userId", userId);
        if (stateMain != null) {
            intent.putExtra("record", stateMain.getGameLeftRecord());
        }

        startActivity(intent);
    }

    public void goGameRight(View view) {
        StringBuilder url = new StringBuilder()
                .append(URL)
                .append("/game")
                .append("/getGameRight")
                .append("?userId=")
                .append(userId);

        StateMain stateMain = getDatas(url);

        Intent intent = new Intent(this, GameRight.class);
        intent.putExtra("userId", userId);
        if (stateMain != null) {
            intent.putExtra("record", stateMain.getGameRightRecord());
        }

        startActivity(intent);
    }
}
