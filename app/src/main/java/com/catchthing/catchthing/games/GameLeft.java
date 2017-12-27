package com.catchthing.catchthing.games;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

// игра слева
public class GameLeft extends AppCompatActivity {

	private static final String URL = "https://catching.herokuapp.com"; // URL сервера
	private int DIFFICULTY_LEVEL; // уровень сложности (max = 20)
	private int count = 0;
	private Long userId;
	private Long record;

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
    private ArrayList<RelativeLayout.LayoutParams> layoutParamsArrayList;
    private Boolean isAlertDialog = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
			Init();
		}
    }

    // инициализация компонентов активности
    private void Init() {
    	// userId
		userId = getIntent().getLongExtra("userId", 0);

    	// получение данных с сервера
		getStats();

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
        textViewRecord.setText(String.valueOf(record)); // заполнение поля с рекордом
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
        Intent intent = new Intent(this, GameLeft.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    // получаем данные с сервера
    @SuppressLint("SetTextI18n")
    private void getStats() {
        StringBuilder url = new StringBuilder()
                .append(URL)
                .append("/game")
                .append("/getGameLeft")
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
            record = stateMain.getGameLeftRecord();
        }
    }

    // начало игры
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

    // определение координат для кнопок на relativeLayout
    private RelativeLayout.LayoutParams getRandomParams() {
        int sizeCircles = 75; // размер кнопок
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
        return checkParams(layoutParams);
    }

    // проверка наслаивания кнопок друг на друга
    private RelativeLayout.LayoutParams checkParams(RelativeLayout.LayoutParams layoutParams) {
    	for (RelativeLayout.LayoutParams params : layoutParamsArrayList) {
			Point pointOne = new Point(params.leftMargin, params.topMargin);
			Point pointTwo = new Point(layoutParams.leftMargin, layoutParams.topMargin);

			double diff = pointOne.getDistance(pointTwo);
			if (diff < 30) {
				layoutParams = getRandomParams();
			}
		}

		return layoutParams;
	}

	// обработка нажатия на зелено-белую кнопку
    public void clickToButton(View view) {
        count++;
        layoutParamsArrayList.clear();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			buttonNoClickList.forEach((b) -> b.setVisibility(View.GONE));
		} else {
			for (int i = 0; i < buttonNoClickList.size(); i++) {
				buttonNoClickList.get(i).setVisibility(View.GONE);
			}
		}
        int MAX_DIFFICULTY_LEVEL = 20;
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

    // обработка нажатия на кнопку GO
    public void startGame(View view) {
        DIFFICULTY_LEVEL = 0;
        startGameButton.setVisibility(View.GONE);
        textViewDesc.setVisibility(View.GONE);
        start();
    }

    // запуск таймера на нажатие кнопки
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

    // вывод диалога с результатами игры
    protected synchronized void showAlertDialog(String msg) {
    	if (!isAlertDialog) {
			isAlertDialog = true;
			if (countDownTimer != null) {
				countDownTimer.cancel();
			}
			if (Long.parseLong(textViewScore.getText().toString()) > Long.parseLong(textViewRecord.getText().toString())) {
				saveScore();
			}
			String title;
			if (msg.equals("time")) {
				title = "Вы не успели нажать на кнопку";
			} else {
				title = "Вы промахнулись";
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(GameLeft.this);
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

    // закрываем активность
    private void closeGame() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    // отправка рекорда на сервер
    private void saveScore() {
        String url = URL +
                "/game" +
                "/saveGameLeft" +
                "?userId=" +
                userId +
                "&record=" +
                textViewScore.getText();

        new HttpRequestTask(url).execute();
    }

    // обработка нажатия кнопки "Назад"
	@Override
	public void onBackPressed() {
    	Long currentScore = Long.parseLong(textViewScore.getText().toString());
		if (currentScore > record) {
			saveScore();
		}
		closeGame();
	}
}
