package com.catchthing.catchthing;


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

import com.catchsquare.catchsquare.R;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

public class Game extends Activity {
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
        setContentView(R.layout.game_layout);
        Init();
    }

    private void Init() {
        density = getApplicationContext().getResources().getDisplayMetrics().density;
        random = new Random();
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutGame);
        startGameButton = (Button) findViewById(R.id.startGameButton);
        buttonNoClick = (Button) findViewById(R.id.buttonNoClick);
        buttonGame = (Button) findViewById(R.id.buttonGame);
        textViewScore = (TextView) findViewById(R.id.textViewScore);
        buttonNoClickList = new ArrayList<>();
        layoutParamsArrayList = new ArrayList<>();

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        };
    }

    protected void start() {
        for (int i = 0; i < (DIFFICULTY_LEVEL + 1); i++) {
            Button button = new Button(this);
            button.setId(i);
            System.out.println("but " + i);
            layoutParamsArrayList.add(getRandomParams(button));
            button.setLayoutParams
                    (checkLayoutParams(layoutParamsArrayList.get(i)));
            //button.setLayoutParams(layoutParamsArrayList.get(i));
            button.setOnClickListener(onClickListener);
            button.setBackground(buttonNoClick.getBackground());
            button.setVisibility(View.VISIBLE);
            button.setText("" + i);
            buttonNoClickList.add(button);
            relativeLayout.addView(button);
        }
        System.out.println("for green");
        layoutParamsArrayList.add(getRandomParams(buttonGame));
        buttonGame.setLayoutParams(checkLayoutParams
                (layoutParamsArrayList.get(layoutParamsArrayList.size()-1)));
        //buttonGame.setLayoutParams(layoutParamsArrayList.get(layoutParamsArrayList.size()-1));
        buttonGame.setVisibility(View.VISIBLE);
        //startTimer();
    }

    private RelativeLayout.LayoutParams getRandomParams (Button buttonGame) {
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(sizeCircles, sizeCircles);
        int left, top;

        System.out.println("Relative height " + relativeLayout.getHeight() +
                " width " + relativeLayout.getWidth());

        left = random.nextInt(relativeLayout.getWidth());
        if (left > sizeCircles)
            if (left < relativeLayout.getWidth()-sizeCircles) {
                System.out.println("norm left");
                layoutParams.leftMargin = left;
                //buttonGame.setLeft(left);
            }
            else {
                System.out.println("left " + left + " > l gr");
                layoutParams.leftMargin = left - (sizeCircles - (relativeLayout.getWidth() - left - 1));
                //buttonGame.setLeft(layoutParams.leftMargin - relativeLayout.getWidth() - left - 1);
            }
        else {
            System.out.println("l " + left + " < size");
            layoutParams.leftMargin += (sizeCircles + 1);
            //buttonGame.setLeft(layoutParams.leftMargin + sizeCircles + 1);
        }

        top = random.nextInt(relativeLayout.getHeight());
        if (top > sizeCircles)
            if (top < relativeLayout.getHeight()-sizeCircles) {
                System.out.println("norm top");
                layoutParams.topMargin = top;
                //buttonGame.setTop(top);
            }
            else {
                System.out.println("t " + top + " > t gr");
                layoutParams.topMargin = top - (sizeCircles - (relativeLayout.getHeight() - top - 1));
                //buttonGame.setTop(layoutParams.topMargin - relativeLayout.getHeight() - top - 1);
            }
        else {
            System.out.println("t " + top + " < size");
            layoutParams.topMargin += (sizeCircles + 1);
            //buttonGame.setTop(layoutParams.topMargin + sizeCircles + 1);
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
        if (count%3 == 0)
            if (DIFFICULTY_LEVEL < MAX_DIFFICULTY_LEVEL)
                DIFFICULTY_LEVEL++;
            else
                DIFFICULTY_LEVEL = MAX_DIFFICULTY_LEVEL;
        if (countDownTimer != null)
            countDownTimer.cancel();
        textViewScore.setText("  " + count);
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
                showAlertDialog();
            }
        }.start();
    }

    protected void showAlertDialog() {
        if (countDownTimer != null)
            countDownTimer.cancel();
        saveScore();
        count = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(Game.this);
        builder.setTitle("Вы проиграли")
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
            FileInputStream fin = openFileInput("score.cc");
            if (fin.read() < count) {
                OutputStreamWriter osw = new OutputStreamWriter(openFileOutput("score.cc", MODE_PRIVATE));
                osw.write(count);
                osw.close();
            }
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RelativeLayout.LayoutParams checkLayoutParams
            (RelativeLayout.LayoutParams layoutParams) {
        int topMargin, leftMargin;
        if (layoutParamsArrayList.size() != 0) {
            for (int i = 0; i < layoutParamsArrayList.size()-1; i++) {
                topMargin = layoutParamsArrayList.get(i).topMargin;
                leftMargin = layoutParamsArrayList.get(i).leftMargin;
                switch (checkMargins(topMargin, leftMargin,
                        layoutParams.topMargin, layoutParams.leftMargin)) {
                    case 1:
                        System.out.println("Текущая точка (" + layoutParams.topMargin + ":" +
                                layoutParams.leftMargin + ") выше и правее, чем (" +
                                topMargin + ":" + leftMargin + ")");
                        layoutParams.topMargin -= sizeCircles - Math.abs(topMargin - layoutParams.topMargin) + 1;
                        layoutParams.leftMargin += sizeCircles - Math.abs(leftMargin - layoutParams.leftMargin) + 1;
                        System.out.println("Координаты изменились на (" + layoutParams.topMargin +
                                ":" + layoutParams.leftMargin + ")");
                        break;
                    case 2:
                        System.out.println("Текущая точка (" + layoutParams.topMargin + ":" +
                                layoutParams.leftMargin + ") выше и левее, чем (" +
                                topMargin + ":" + leftMargin + ")");
                        layoutParams.topMargin -= sizeCircles - Math.abs(topMargin - layoutParams.topMargin) + 1;
                        layoutParams.leftMargin -= sizeCircles - Math.abs(leftMargin - layoutParams.leftMargin) + 1;
                        System.out.println("Координаты изменились на (" + layoutParams.topMargin +
                                ":" + layoutParams.leftMargin + ")");
                        break;
                    case 3:
                        System.out.println("Текущая точка (" + layoutParams.topMargin + ":" +
                                layoutParams.leftMargin + ") ниже и правее, чем (" +
                                topMargin + ":" + leftMargin + ")");
                        layoutParams.topMargin += sizeCircles - Math.abs(topMargin - layoutParams.topMargin) + 1;
                        layoutParams.leftMargin += sizeCircles - Math.abs(leftMargin - layoutParams.leftMargin) + 1;
                        System.out.println("Координаты изменились на (" + layoutParams.topMargin +
                                ":" + layoutParams.leftMargin + ")");
                        break;
                    case 4:
                        System.out.println("Текущая точка (" + layoutParams.topMargin + ":" +
                                layoutParams.leftMargin + ") ниже и левее, чем (" +
                                topMargin + ":" + leftMargin + ")");
                        layoutParams.topMargin += sizeCircles - Math.abs(topMargin - layoutParams.topMargin) + 1;
                        layoutParams.leftMargin -= sizeCircles - Math.abs(leftMargin - layoutParams.leftMargin) + 1;
                        System.out.println("Координаты изменились на (" + layoutParams.topMargin +
                                ":" + layoutParams.leftMargin + ")");
                        break;
                    case 0:
                        System.out.println("Точки (" + layoutParams.topMargin + ":" +
                                layoutParams.leftMargin + ") и (" +
                                topMargin + ":" + leftMargin + ") не пересекаются");
                        break;
                }
            }
        }
        return layoutParams;
    }

    private int checkMargins(int topArray, int leftArray, int baseTop, int baseLeft) {
        if (checkIntersection(topArray, leftArray, baseTop, baseLeft))
            if (checkTopSign(topArray, baseTop))
                if (checkLeftSign(leftArray, baseLeft))
                    return 3;
                else
                    return 4;
            else
                if (checkLeftSign(leftArray, baseLeft))
                    return 1;
                else
                    return 2;
        else
            return 0;
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
        return Math.abs(top - relativeLayout.getHeight()) <= 0;
    }

    private  boolean checkBorderTopSign(int top) {
        return relativeLayout.getHeight()
                <= top;
    }

    private boolean checkBorderLeftInter(int left) {
        return Math.abs(left - relativeLayout.getWidth()) <= 0;
    }

    private boolean checkBorderLeftSign(int left) {
        return relativeLayout.getWidth()
                <= left;
    }
}
