package com.catchthing.catchthing.games;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.catchthing.catchthing.MainActivity;
import com.catchthing.catchthing.R;
import com.catchthing.catchthing.connect.HttpRequestTask;
import com.catchthing.catchthing.status.StateMain;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;


public class GameRight extends AppCompatActivity {
    private final GameRight gameRight = this;
    private CountDownTimer countDownTimer;
    private Random random;
    private int count = 0;
    private RelativeLayout relativeLayout;
    private TextView textViewScore;
    private TextView textViewRecordRight;
    private TextView textViewDesc;
    private Button buttonGame;
    private Button startGameButton;
    private ArrayList<RelativeLayout.LayoutParams> layoutParamsArrayList;
    private static Long userId;
    private Boolean isAlertDialog = false;

    private static final String URL = "https://catching.herokuapp.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_right);
        Init();
    }

    private void Init() {
        random = new Random();
        relativeLayout = findViewById(R.id.relativeLayoutGameRight);
        startGameButton = findViewById(R.id.startGameButtonRight);
        buttonGame = findViewById(R.id.buttonGameRight);
        textViewDesc = findViewById(R.id.textViewDesc);
        textViewScore = findViewById(R.id.textViewScoreRight);
        textViewScore.setText("0");
        textViewRecordRight = findViewById(R.id.textViewRecord2);
        layoutParamsArrayList = new ArrayList<>();

        userId = getIntent().getLongExtra("userId", 0);

        getStats();

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
    }

    private void forBegin() {
        Intent intent = new Intent(this, GameRight.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    @SuppressLint("SetTextI18n")
    private void getStats() {
        StringBuilder url = new StringBuilder()
                .append(URL)
                .append("/game")
                .append("/getGameRight")
                .append("?userId=")
                .append(userId);

        StateMain stateMain = null;
        try {
            stateMain = new com.catchthing.catchthing.connect.HttpRequestTask(url.toString()).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (stateMain != null) {
            userId = stateMain.getUserId();
            textViewRecordRight.setText(stateMain.getGameRightRecord().toString());
        }
    }

    protected void start() throws InterruptedException {
        System.out.println("for green");
        layoutParamsArrayList.add(getRandomParams());
        buttonGame.setLayoutParams(layoutParamsArrayList.get(layoutParamsArrayList.size() - 1));
        buttonGame.setVisibility(View.VISIBLE);
        startTimer();
    }

    private RelativeLayout.LayoutParams getRandomParams() {
        int sizeCircles = 75;
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(sizeCircles, sizeCircles);
        int left, top;

        System.out.println("Relative height " + relativeLayout.getHeight() +
                " width " + relativeLayout.getWidth());

        left = random.nextInt(relativeLayout.getWidth());
        if (left > sizeCircles)
            if (left < relativeLayout.getWidth() - sizeCircles) {
                System.out.println("norm game_left");
                layoutParams.leftMargin = left;
            } else {
                System.out.println("game_left " + left + " > l gr");
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

    public void clickToButtonRight(View view) throws InterruptedException {
        buttonGame.setVisibility(View.INVISIBLE);
        count++;
        layoutParamsArrayList.clear();
        if (countDownTimer != null)
            countDownTimer.cancel();
        textViewScore.setText(String.valueOf(count));
        sleep();
    }

    public void startGame(View view) throws InterruptedException {
        startGameButton.setVisibility(View.GONE);
        textViewDesc.setVisibility(View.GONE);
        sleep();
    }

    private void sleep() {
        countDownTimer = new CountDownTimer(random.nextInt(3500), 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                try {
                    gameRight.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(700, 700) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                showAlertDialog();
            }
        }.start();
    }

    protected synchronized void showAlertDialog() {
        if (!isAlertDialog) {
            isAlertDialog = true;
            if (countDownTimer != null)
                countDownTimer.cancel();
            if (Long.parseLong(textViewScore.getText().toString()) > Long.parseLong(textViewRecordRight.getText().toString())) {
                saveScore();
            }
            String title = "Проигрыш: Вы не успели нажать на кнопку";
            AlertDialog.Builder builder = new AlertDialog.Builder(GameRight.this);
            builder.setTitle(title)
                    .setMessage("Ваш счет: " + count)
                    .setCancelable(false)
                    .setPositiveButton("Начать заново",
                            (dialog, id) -> {
                                dialog.cancel();
                                forBegin();
                            })
                    .setNegativeButton("Выход",
                            (dialog, id) -> {
                                dialog.cancel();
                                closeGame();
                            });
            AlertDialog alert = builder.create();
            alert.show();
            count = 0;
        }
    }

    private void closeGame() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveScore() {
        String url = URL +
                "/game" +
                "/saveGameRight" +
                "?userId=" +
                userId +
                "&record=" +
                textViewScore.getText();

        new HttpRequestTask(url).execute();
    }

    @Override
    public void onBackPressed() {
        if (Long.parseLong(textViewScore.getText().toString()) > Long.parseLong(textViewRecordRight.getText().toString())) {
            saveScore();
        }
        closeGame();
    }
}
