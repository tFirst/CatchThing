package com.catchthing.catchthing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import com.catchthing.catchthing.connect.HttpRequestTask;
import com.catchthing.catchthing.status.StateMain;

import org.springframework.http.HttpStatus;

import java.util.concurrent.ExecutionException;

// загрузочный экран приложения
public class SplashActivity extends AppCompatActivity {

    private String deviceId;
    private Long userId;
    private Boolean isOnline = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Init();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("isOnline", isOnline);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        deviceId = savedInstanceState.getString("deviceId");
        userId = savedInstanceState.getLong("userId");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("deviceId", deviceId);
        savedInstanceState.putLong("userId", userId);
    }

    @SuppressLint("HardwareIds")
    private void Init() {

        deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        StringBuilder url = new StringBuilder()
                .append(getString(R.string.url))
                .append("/auth")
                .append("/user")
                .append("?deviceId=")
                .append(deviceId);

        StateMain stateMain = getDatas(url);

        if (stateMain != null && stateMain.getStatus() != HttpStatus.GATEWAY_TIMEOUT) {
            userId = stateMain.getUserId();
        } else {
            isOnline = false;
        }
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
}
