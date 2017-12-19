package com.catchthing.catchthing.games;

import android.app.AlertDialog;
import android.content.Context;
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


public class GameRight extends AppCompatActivity {
    private final GameRight gameRight = this;
    private CountDownTimer countDownTimer;
    private Random random;
    private int count = 0;
    private int sizeCircles = 70;
    private RelativeLayout relativeLayout;
    private TextView textViewScore;
    private Button buttonGame;
    private Button startGameButton;
    private ArrayList<RelativeLayout.LayoutParams> layoutParamsArrayList;


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
        textViewScore = findViewById(R.id.textViewScoreRight);
        layoutParamsArrayList = new ArrayList<>();

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
    }

    protected void start() throws InterruptedException {
        System.out.println("for green");
        layoutParamsArrayList.add(getRandomParams());
        buttonGame.setLayoutParams(checkLayoutParams
                (layoutParamsArrayList.get(layoutParamsArrayList.size() - 1),
                        layoutParamsArrayList.size() - 1));
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
        start();
    }

    private void sleep() {
        countDownTimer = new CountDownTimer(1000, 1000) {
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
        countDownTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                showAlertDialog();
            }
        }.start();
    }

    protected void showAlertDialog() {
        if (countDownTimer != null)
            countDownTimer.cancel();
        saveScore();
        count = 0;
        String title = "Проигрыш: Вы не успели нажать на кнопку";
        Context context = GameRight.this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage("Ваш счет: " + textViewScore.getText())
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
    }

    private void closeGame() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void saveScore() {
        String FILENAME = "score_right.cc";

        try {
            InputStream inputStream = openFileInput(FILENAME);

            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String line;
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }

                if (Integer.parseInt(String.valueOf(builder)) < Integer.parseInt(String.valueOf(textViewScore.getText()))) {
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
            Toast.makeText(GameRight.this,
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private RelativeLayout.LayoutParams checkLayoutParams
            (RelativeLayout.LayoutParams layoutParams, int number) {
        if (layoutParamsArrayList.size() != 0) {

        }
        return layoutParams;
    }
}
