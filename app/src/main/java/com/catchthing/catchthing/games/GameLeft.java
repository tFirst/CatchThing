package com.catchthing.catchthing.games;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.catchthing.catchthing.MainActivity;
import com.catchthing.catchthing.R;
import com.catchthing.catchthing.status.StateMain;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Random;

public class GameLeft extends AppCompatActivity {
    private CountDownTimer countDownTimer;
    private int DIFFICULTY_LEVEL;
    private int MAX_DIFFICULTY_LEVEL = 20;
    private Random random;
    private int count = 0;
    private int sizeCircles = 70;
    private RelativeLayout relativeLayout;
    private TextView textViewScore;
    private TextView textViewRecord;
    private Button buttonGame;
    private Button buttonNoClick;
    private Button startGameButton;
    private ArrayList<Button> buttonNoClickList;
    private View.OnClickListener onClickListener;
    private ArrayList<RelativeLayout.LayoutParams> layoutParamsArrayList;
    private static Long userId;
    private static Long record;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_left);
        Init();
    }

    private void Init() {
        random = new Random();
        relativeLayout = findViewById(R.id.relativeLayoutGameLeft);
        relativeLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.background));
        startGameButton = findViewById(R.id.startGameButtonLeft);
        buttonNoClick = findViewById(R.id.buttonNoClick);
        buttonGame = findViewById(R.id.buttonGameLeft);
        textViewScore = findViewById(R.id.textViewScoreLeft);
        textViewScore.setText("0");
        textViewRecord = findViewById(R.id.textViewRecord);
        buttonNoClickList = new ArrayList<>();
        layoutParamsArrayList = new ArrayList<>();

        userId = getIntent().getLongExtra("userId", 0);

        getStats();

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog("miss");
            }
        };
    }

    private void forBegin() {
        Intent intent = new Intent(this, GameLeft.class);
        startActivity(intent);
    }

    private void getStats() {
        String url = "http://192.168.1.7:8080/game/getGameLeft?userId=" + userId;
        new HttpRequestTask(url).execute();
        textViewRecord.setText(String.valueOf(record));
    }

    protected void start() {
        for (int i = 0; i < (DIFFICULTY_LEVEL + 1); i++) {
            Button button = new Button(this);
            button.setId(i);
            System.out.println("but " + i);
            layoutParamsArrayList.add(getRandomParams());
            button.setLayoutParams(layoutParamsArrayList.get(i));
            button.setOnClickListener(onClickListener);
            button.setBackground(buttonNoClick.getBackground());
            button.setVisibility(View.VISIBLE);
            button.setText(String.valueOf(i));
            buttonNoClickList.add(button);
            relativeLayout.addView(button);
        }
        System.out.println("for green");
        layoutParamsArrayList.add(getRandomParams());
        buttonGame.setLayoutParams((layoutParamsArrayList.get(layoutParamsArrayList.size() - 1)));
        buttonGame.setVisibility(View.VISIBLE);
        startTimer();
    }

    private RelativeLayout.LayoutParams getRandomParams() {
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(sizeCircles, sizeCircles);
        int left, top;

        System.out.println("Relative height " + relativeLayout.getHeight() +
                " width " + relativeLayout.getWidth());

        left = random.nextInt(relativeLayout.getWidth());
        if (left > sizeCircles)
            if (left < relativeLayout.getWidth() - sizeCircles) {
                System.out.println("norm left");
                layoutParams.leftMargin = left;
            } else {
                System.out.println("left " + left + " > l gr");
                layoutParams.leftMargin = left - (sizeCircles - (relativeLayout.getWidth() - left - 1));
            }
        else {
            System.out.println("l " + left + " < size");
            layoutParams.leftMargin += (sizeCircles + 1);
        }

        top = random.nextInt(relativeLayout.getHeight());
        if (top > sizeCircles)
            if (top < relativeLayout.getHeight() - sizeCircles) {
                System.out.println("norm top");
                layoutParams.topMargin = top;
            } else {
                System.out.println("t " + top + " > t gr");
                layoutParams.topMargin = top - (sizeCircles - (relativeLayout.getHeight() - top - 1));
            }
        else {
            System.out.println("t " + top + " < size");
            layoutParams.topMargin += (sizeCircles + 1);
        }
        System.out.println("rand l " + left + " rand t " + top);
        System.out.println("lp l " + layoutParams.leftMargin + " lp t " + layoutParams.topMargin);
        return layoutParams;
    }

    public void clickToButton(View view) {
        count++;
        layoutParamsArrayList.clear();
        for (int i = 0; i < buttonNoClickList.size(); i++) {
            buttonNoClickList.get(i).setVisibility(View.GONE);
        }
        if (count % 3 == 0)
            if (DIFFICULTY_LEVEL < MAX_DIFFICULTY_LEVEL)
                DIFFICULTY_LEVEL++;
            else
                DIFFICULTY_LEVEL = MAX_DIFFICULTY_LEVEL;
        if (countDownTimer != null)
            countDownTimer.cancel();
        textViewScore.setText(String.valueOf(count));
        start();
    }

    public void startGame(View view) {
        DIFFICULTY_LEVEL = 0;
        startGameButton.setVisibility(View.GONE);
        start();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                showAlertDialog("time");
            }
        }.start();
    }

    protected void showAlertDialog(String msg) {
        if (countDownTimer != null)
            countDownTimer.cancel();
        saveScore();
        String title;
        if (msg.equals("time")) {
            title = "Проигрыш: Вы не успели нажать на кнопку";
        } else {
            title = "Проигрыш: Вы промахнулись";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(GameLeft.this);
        builder.setTitle(title)
                .setMessage("Ваш счет: " + count)
                .setCancelable(false)
                .setPositiveButton("Начать заново",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                forBegin();
                            }
                        })
                .setNegativeButton("Выход",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                closeGame();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
        count = 0;
    }

    private void closeGame() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void saveScore() {
        String url = "http://192.168.1.7:8080/game/saveGameLeft?userId=" + userId + "&record=" + textViewScore.getText();

        new HttpRequestTask(url).execute();
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
        protected void onPostExecute(StateMain stateMain) {
            if (stateMain.getGameLeftRecord() != null) {
                record = stateMain.getGameLeftRecord();
            } else {
                record = 0L;
            }
        }
    }
}
