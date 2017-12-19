package com.catchthing.catchthing.games;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.catchthing.catchthing.MainActivity;
import com.catchthing.catchthing.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
    private Button buttonGame;
    private Button buttonNoClick;
    private Button startGameButton;
    private ArrayList<Button> buttonNoClickList;
    private View.OnClickListener onClickListener;
    private ArrayList<RelativeLayout.LayoutParams> layoutParamsArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_left);
        Init();
    }

    private void Init() {
        random = new Random();
        relativeLayout = findViewById(R.id.relativeLayoutGameLeft);
        startGameButton = findViewById(R.id.startGameButtonLeft);
        buttonNoClick = findViewById(R.id.buttonNoClick);
        buttonGame = findViewById(R.id.buttonGameLeft);
        textViewScore = findViewById(R.id.textViewScoreLeft);
        textViewScore.setText("0");
        buttonNoClickList = new ArrayList<>();
        layoutParamsArrayList = new ArrayList<>();

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
                                onCreate(new Bundle());
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
        String FILENAME = "score_left.cc";

        try {
            InputStream inputStream = openFileInput(FILENAME);

            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String line;
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                if (Integer.parseInt(String.valueOf(builder)) < count) {
                    saveFile(FILENAME);
                }

                inputStream.close();
            }
        } catch (Throwable t) {
            saveFile(FILENAME);
        }
    }

    private void saveFile(String fileName) {
        try {
            OutputStream outputStream = openFileOutput(fileName, 0);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            osw.write(count);
            osw.close();
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
