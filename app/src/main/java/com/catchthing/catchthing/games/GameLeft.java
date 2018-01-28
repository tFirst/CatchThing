package com.catchthing.catchthing.games;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import com.catchthing.catchthing.calculate.Point;
import com.catchthing.catchthing.connect.HttpRequestTask;
import com.catchthing.catchthing.status.StateMain;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ишра слева
 */
@TargetApi(Build.VERSION_CODES.N)// TODO: проверить как рабит
public class GameLeft extends AppCompatActivity {

    private static final int START_DIFFICULTY = 0;
    private static final int MAX_DIFFICULTY_LEVEL = 20;
    private static final double RADIUS = 30;

    private final GameService gameService;

    private int DIFFICULTY_LEVEL;
    private int count = 0;
    private Long userId;
    private Long record;
    private Boolean isOnline;

    private CountDownTimer countDownTimer;
    private Random random;
    private RelativeLayout relativeLayout;
    private TextView textViewScore;
    private TextView textViewRecord;
    private TextView textViewDesc;
    private Button buttonGame;
    private Button buttonNoClick;
    private Button startGameButton;
    private ArrayList<Button> buttonNoClickList;
    private View.OnClickListener onClickListener;
    private List<RelativeLayout.LayoutParams> layoutParamsArrayList;
    private Boolean isAlertDialog = false;

    public GameLeft(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Init();
        }
    }

    /**
     * инициализация компонентов активности
     */
    private void Init() {
        userId = getIntent().getLongExtra("userId", 0);
        isOnline = getIntent().getBooleanExtra("isOnline", false);

        if (!isOnline) {
            record = readOffline(0L);
        } else {
            getStats();
        }

        setContentView(R.layout.game_left); // определение самой активности

        random = new Random(); // random
        relativeLayout = findViewById(R.id.relativeLayoutGameLeft); // layout, на котором происходят основные действия
        startGameButton = findViewById(R.id.startGameButtonLeft); // кнопка GO
        buttonNoClick = findViewById(R.id.buttonNoClick); // красно-белые кнопки
        buttonGame = findViewById(R.id.buttonGameLeft); // зелено-бела кнопка
        textViewScore = findViewById(R.id.textViewScoreLeft); // поле для вывода текущих очков
        textViewScore.setText(String.valueOf(0)); // заполняется нулем перед началом игры
        textViewDesc = findViewById(R.id.textViewDesc); // правила игры
        textViewRecord = findViewById(R.id.textViewRecord); // поле для вывода рекорда
        textViewRecord.setText(String.valueOf(record == null ? 0 : record)); // заполнение поля с рекордом
        buttonNoClickList = new ArrayList<>(); // список красно-белых кнопок
        layoutParamsArrayList = new ArrayList<>(); // список координат кнопок на relativeLayout

        // определение параметров экрана телефона
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        // создаем слушателя для красно-белых кнопок
        onClickListener = v -> {
            if (!isAlertDialog) {
                showAlertDialog("miss");
            }
        };
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (isOnline) {
            savedInstanceState.putLong("userId", userId);
        }
        savedInstanceState.putLong("record", record == null ? 0 : record);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        userId = savedInstanceState.getLong("userId");
        record = savedInstanceState.getLong("record");
    }

    /**
     * начинаем игру заново
     */
    private void forBegin() {
        try {
            Intent intent = new Intent(this, GameLeft.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        } catch (Exception e) {
            e.fillInStackTrace();
        } finally {
            finish();
        }

    }

    /**
     * получаем данные с сервера
     */
    @SuppressLint("SetTextI18n")
    private void getStats() {
        StringBuilder url = new StringBuilder()
                .append(getString(R.string.url))
                .append("/game")
                .append("/getGameLeft")
                .append("?userId=")
                .append(userId);
        try {
            StateMain stateMain = new HttpRequestTask(url.toString()).execute().get();
            if (stateMain != null) {
                userId = stateMain.getUserId();
                record = stateMain.getGameLeftRecord();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * начало игры
     */
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
            buttonNoClickList.add(button);
            relativeLayout.addView(button);
        }
        System.out.println("for green");
        layoutParamsArrayList.add(getRandomParams());
        buttonGame.setLayoutParams((layoutParamsArrayList.get(layoutParamsArrayList.size() - 1)));
        buttonGame.setVisibility(View.VISIBLE);
        startTimer();
    }

    /**
     * определение координат для кнопок на relativeLayout
     */
    private RelativeLayout.LayoutParams getRandomParams() {
        return checkParams(gameService.calculateRandomParams(relativeLayout, random));
    }

    /**
     * проверка наслаивания кнопок друг на друга
     */
    private RelativeLayout.LayoutParams checkParams(RelativeLayout.LayoutParams layoutParams) {
        for (RelativeLayout.LayoutParams params : layoutParamsArrayList) {
            Point pointOne = new Point(params.leftMargin, params.topMargin);
            Point pointTwo = new Point(layoutParams.leftMargin, layoutParams.topMargin);

            if (pointOne.getDistance(pointTwo) < RADIUS) {
                layoutParams = getRandomParams();
            }
        }

        return layoutParams;
    }

    /**
     * обработка нажатия на зелено-белую кнопку
     */
    public void clickToButton(View view) {
        count++;
        layoutParamsArrayList.clear();
        buttonNoClickList.forEach((b) -> b.setVisibility(View.GONE));

        if (count % 3 == 0 && DIFFICULTY_LEVEL < MAX_DIFFICULTY_LEVEL) {
            DIFFICULTY_LEVEL++;
        } else {
            DIFFICULTY_LEVEL = MAX_DIFFICULTY_LEVEL;
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        textViewScore.setText(String.valueOf(count));
        start();
    }

    /**
     * обработка нажатия на кнопку GO
     */
    public void startGame(View view) {
        DIFFICULTY_LEVEL = START_DIFFICULTY;
        startGameButton.setVisibility(View.GONE);
        textViewDesc.setVisibility(View.GONE);
        start();
    }

    /**
     * запуск таймера на нажатие кнопки
     */
    private void startTimer() {
        countDownTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (!isAlertDialog) {
                    showAlertDialog("time");
                }
            }
        }.start();
    }

    /**
     * вывод диалога с результатами игры
     */
    protected synchronized void showAlertDialog(String msg) {
        if (!isAlertDialog) {
            isAlertDialog = true;
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            if (Long.parseLong(textViewScore.getText().toString()) > Long.parseLong(textViewRecord.getText().toString())) {
                saveScore();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(GameLeft.this);
            builder.setTitle(msg.equals("time") ? "Вы не успели нажать на кнопку" : "Вы промахнулись")
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

    /**
     * закрываем активность
     */
    private void closeGame() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    /**
     * отправка рекорда на сервер
     */
    private void saveScore() {
        if (isOnline) {
            String url = getString(R.string.url) +
                    "/game" +
                    "/saveGameLeft" +
                    "?userId=" +
                    userId +
                    "&record=" +
                    readOffline(Long.parseLong(String.valueOf(textViewScore.getText())));

            new HttpRequestTask(url).execute();
        } else {
            saveOffline();
        }
    }

    /**
     * сохранение результата оффлайн
     */
    private void saveOffline() {
        try {
            Long currCount = Long.parseLong(String.valueOf(textViewScore.getText()));
            FileOutputStream outputStream = openFileOutput(getString(R.string.fileNameLeft), Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            osw.write(currCount.compareTo(record) > 0 ? currCount.toString() : record.toString());
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * чтение локального файла с результом
     */
    private Long readOffline(Long num) {
        Long fromFile = 0L;
        try {
            FileInputStream fileInputStream = openFileInput(getString(R.string.fileNameLeft));
            InputStreamReader isr = new InputStreamReader(fileInputStream);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                fromFile = Long.parseLong(line);
            }
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (fromFile.compareTo(num) > 0) ? fromFile : num;
    }

    /**
     * обработка нажатия кнопки "Назад"
     */
    @Override
    public void onBackPressed() {
        Long currentScore = Long.parseLong(textViewScore.getText().toString());
        if (isOnline && currentScore.compareTo(record) > 0) {
            saveScore();
        } else if (!isOnline) {
            saveOffline();
        }
        closeGame();
    }
}
