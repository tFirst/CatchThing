package com.catchthing.catchthing.games;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.concurrent.ExecutionException;

// игра справа
public class GameRight extends AppCompatActivity {

    private final GameService gameService;

    private GameRight gameRight = this;

    private String URL = null;
    private String filename = null;
    private static Long userId;
    private int count = 0;
    private Long record;
    private Boolean isOnline;

    private CountDownTimer countDownTimer;
    private Random random;
    private RelativeLayout relativeLayout;
    private TextView textViewScore;
    private TextView textViewRecordRight;
    private TextView textViewDesc;
    private Button buttonGame;
    private Button startGameButton;
    private Boolean isAlertDialog = false;

    public GameRight(GameService gameService) {
        this.gameService = gameService;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Init();
        }
    }

    // инициализация компонентов активности
    private void Init() {
        URL = getString(R.string.url);
        filename = getString(R.string.fileNameRight);
        // userId
        userId = getIntent().getLongExtra("userId", 0);

        isOnline = getIntent().getBooleanExtra("isOnline", false);

        if (!isOnline) {
            record = readOffline(0L);
        } else {
            // получение данных с сервера
            getStats();
        }

        setContentView(R.layout.game_right); // определение самой активности

        random = new Random(); // random
        relativeLayout = findViewById(R.id.relativeLayoutGameRight); // layout, на котором происходят все действия
        startGameButton = findViewById(R.id.startGameButtonRight); // кнопка GO начала игры
        buttonGame = findViewById(R.id.buttonGameRight); // зелено-белая кнопка
        textViewDesc = findViewById(R.id.textViewDesc); // правила игры
        textViewScore = findViewById(R.id.textViewScoreRight); // поле для вывода текущих очков
        textViewScore.setText(String.valueOf(0)); // заполняем нулем перед игрой
        textViewRecordRight = findViewById(R.id.textViewRecordRight); // поле для вывода рекорда игры
        textViewRecordRight.setText(String.valueOf(record == null ? 0 : record)); // заполнение поля с рекордом

        // определение параметров дисплея телефона
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("userId", userId);
        savedInstanceState.putLong("record", record);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        userId = savedInstanceState.getLong("userId");
        record = savedInstanceState.getLong("record");
    }

    // начинаем игру заново
    private void forBegin() {
        Intent intent = new Intent(this, GameRight.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    // получение данных с сервера
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
            stateMain = new HttpRequestTask(url.toString()).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (stateMain != null) {
            userId = stateMain.getUserId();
            record = stateMain.getGameRightRecord();
        }
    }

    // начало игры
    protected void start() throws InterruptedException {
        System.out.println("for green");
        buttonGame.setLayoutParams(getRandomParams());
        buttonGame.setVisibility(View.VISIBLE);
        buttonGame.setEnabled(true);
        startTimer();
    }

    // определяем координаты для кнопки
    private RelativeLayout.LayoutParams getRandomParams() {
        return gameService.calculateRandomParams(relativeLayout, random);
    }

    // обработчик нажатия на зелено-белую кнопку
    public void clickToButtonRight(View view) throws InterruptedException {
        buttonGame.setVisibility(View.GONE);
        count++;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        textViewScore.setText(String.valueOf(count));
        sleep();
    }

    // обработка нажатия на кнопку GO
    public void startGame(View view) throws InterruptedException {
        startGameButton.setVisibility(View.GONE);
        textViewDesc.setVisibility(View.GONE);
        sleep();
    }

    // определяем, через какое время кнопка появится снова
    private void sleep() {
        countDownTimer = new CountDownTimer(random.nextInt(3500), 3500) {

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

    // запуск таймера на нажатие кнопки
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

    // вывод диалога о результатах игры
    protected synchronized void showAlertDialog() {
        if (!isAlertDialog) {

            buttonGame.setEnabled(false);
            isAlertDialog = true;

            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            if (Long.parseLong(textViewScore.getText().toString()) >
                    Long.parseLong(textViewRecordRight.getText().toString())) {
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

    // выход в главное меню
    private void closeGame() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    // передача рекорда на сервер
    private void saveScore() {
        if (isOnline) {
            String url = URL +
                    "/game" +
                    "/saveGameRight" +
                    "?userId=" +
                    userId +
                    "&record=" +
                    readOffline(Long.parseLong(String.valueOf(textViewScore.getText())));

            new HttpRequestTask(url).execute();
        } else {
            saveOffline();
        }
    }

    private void saveOffline() {
        FileOutputStream outputStream;
        Long currCount = Long.parseLong(String.valueOf(textViewScore.getText()));

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            osw.write(currCount > record ? currCount.toString() : record.toString());
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Long readOffline(Long num) {
        Long fromFile = 0L;
        try {
            FileInputStream fileInputStream = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fileInputStream);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                fromFile = Long.parseLong(line);
            }
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (fromFile > num) ? fromFile : num;
    }

    // обработка нажатия на кнопку "Назад" телефона
    @Override
    public void onBackPressed() {
        Long currentScore = Long.parseLong(textViewScore.getText().toString());
        if (isOnline && currentScore > record) {
            saveScore();
        } else if (!isOnline) {
            saveOffline();
        }
        closeGame();
    }
}
