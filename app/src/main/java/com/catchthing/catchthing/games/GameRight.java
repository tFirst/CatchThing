package com.catchthing.catchthing.games;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.catchthing.catchthing.MainActivity;
import com.catchthing.catchthing.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;


public class GameRight extends Activity {
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
    private float density;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_right);
        Init();
    }

    private void Init() {
        density = getApplicationContext().getResources().getDisplayMetrics().density;
        random = new Random();
        relativeLayout = findViewById(R.id.relativeLayoutGame);
        startGameButton = findViewById(R.id.startGameButton);
        buttonNoClick = findViewById(R.id.buttonNoClick);
        buttonGame = findViewById(R.id.buttonGame);
        textViewScore = findViewById(R.id.textViewScore);
        //buttonNoClickList = new ArrayList<>();
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

    protected void start() throws InterruptedException {
        Random random = new Random();
        Thread.sleep(random.nextInt(3000));
        System.out.println("for green");
        layoutParamsArrayList.add(getRandomParams(buttonGame));
        buttonGame.setLayoutParams(checkLayoutParams
                (layoutParamsArrayList.get(layoutParamsArrayList.size() - 1),
                        layoutParamsArrayList.size() - 1));
        buttonGame.setVisibility(View.VISIBLE);
        startTimer();
    }

    private RelativeLayout.LayoutParams getRandomParams(Button buttonGame) {
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
//        for (int i = 0; i < buttonNoClickList.size(); i++) {
//            buttonNoClickList.get(i).setVisibility(View.GONE);
//        }
//        if (count % 3 == 0)
//            if (DIFFICULTY_LEVEL < MAX_DIFFICULTY_LEVEL)
//                DIFFICULTY_LEVEL++;
//            else
//                DIFFICULTY_LEVEL = MAX_DIFFICULTY_LEVEL;
        if (countDownTimer != null)
            countDownTimer.cancel();
        textViewScore.setText(String.valueOf(count));
        start();
    }

    public void startGame(View view) throws InterruptedException {
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
        count = 0;
        String title;
        if (msg.equals("time")) {
            title = "Проигрыш: вы не успели нажать на кнопку";
        } else {
            title = "Проигрыш: вы промахнулись";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(GameRight.this);
        builder.setTitle(title)
                .setMessage("Ваш счет: \n" + textViewScore.getText())
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
        try {
            FileInputStream fin = openFileInput("score_game_left.cc");
            if (fin.read() < count) {
                OutputStreamWriter osw = new OutputStreamWriter(openFileOutput("score_game_left.cc", MODE_PRIVATE));
                osw.write(count);
                osw.close();
                System.out.println("Write the score " + count + " in file " + fin);
            }
            fin.close();
        } catch (IOException e) {
            try {
                FileOutputStream fout = openFileOutput("score_game_left.cc", 0);
                OutputStreamWriter osw = new OutputStreamWriter(fout);
                osw.write(count);
                osw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private RelativeLayout.LayoutParams checkLayoutParams
            (RelativeLayout.LayoutParams layoutParams, int number) {
        if (layoutParamsArrayList.size() != 0) {

        }
        return layoutParams;
    }

    private boolean checkIntersection(int top1, int left1, int top2, int left2) {
        return checkTopInter(top1, top2) &&
                checkLeftInter(left1, left2);
    }

    private boolean checkTopInter(int arg1, int arg2) {
        return Math.abs(arg1 - arg2) <= sizeCircles;
    }

    private boolean checkTopSign(int arg1, int arg2) {
        return arg1 <= arg2;
    }

    private boolean checkLeftInter(int arg1, int arg2) {
        return Math.abs(arg1 - arg2) <= sizeCircles;
    }

    private boolean checkLeftSign(int arg1, int arg2) {
        return arg1 <= arg2;
    }

    private boolean checkBorderTopInter(int top) {
        return Math.abs(top - relativeLayout.getHeight()) <= sizeCircles;
    }

    private boolean checkBorderTopSign(int top) {
        return relativeLayout.getHeight()
                <= top;
    }

    private boolean checkBorderLeftInter(int left) {
        return Math.abs(left - relativeLayout.getWidth()) <= sizeCircles;
    }

    private boolean checkBorderLeftSign(int left) {
        return relativeLayout.getWidth()
                <= left;
    }
}
