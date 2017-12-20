package com.catchthing.catchthing;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.catchthing.catchthing.games.GameLeft;
import com.catchthing.catchthing.games.GameRight;
import com.catchthing.catchthing.status.StateMain;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;


public class MainActivity extends AppCompatActivity {

    private static Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        System.out.println("DEVICE ID =" + deviceId);

        String url = "http://192.168.1.7:8080/auth/user?deviceId=" + deviceId;

        new HttpRequestTask(url).execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.gifview);
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

    private static class HttpRequestTask extends AsyncTask<Void, Void, StateMain> {

        private String url;

        HttpRequestTask(String url) {
            this.url = url;
        }

        @Override
        protected StateMain doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                return restTemplate.getForObject(url, StateMain.class);
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        public void onPostExecute(StateMain stateMain) {
            System.out.println("USER ID = " + stateMain.getUserId());
            if (stateMain.getUserId() != null) {
                userId = stateMain.getUserId();
            } else {
                userId = 0L;
            }
        }
    }
}
