package com.example.breakout_marko_guastella;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class Game_Activity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {
    private ScalingManager displayScaleManager;
    private FrameLayout gameLayout;
    private boolean hasAlreadyBeenCalled = false;
    private BallView ballToRemove, currentBall, currentItemBox, itemBoxToRemove;
    private PlayerView playerObject;
    private ImageButton pauseButton, restartButton, homeButton, resumeButton;
    private GameObjectView heartView_1, heartView_2, heartView_3, darkBorderView, blurBackgroundView, timerBackgroundView;
    private Drawable playerDrawable, ballDrawable, obstacleDrawable1, obstacleDrawable2, pauseButtonDrawable, resumeButtonDrawable, homeButtonDrawable, restartButtonDrawable, obstacleDrawable3, heartDrawable, darkBorderDrawable, timerBackgroundDrawable, itemBoxBallDrawable, itemBoxHeartDrawable;
    private HashSet obstaclesHashSet, activeBallsHashSet, bonusItemBoxHashSet;
    private Runnable gameLoop, timeRunnable, playerMovementRunnable;
    private Handler handler, timeHandler, playerMovementHandler;
    private ColllisionDetection checkCollisionObject;
    private SoundPool soundPool;
    private SharedPreferences highscoreData;
    private Intent homeIntent;
    private TextView scoreText, timeText, scoreAnnotation, highscoreText, highscoreAnnotation;
    private String gameState = "ready", lastGameState;
    private SensorManager sm;
    private Sensor accelerometer;
    private float sensorState = 0;
    private int sound_brick_1, sound_healthRemove, sound_playerHit, sound_powerUp, sound_wallHit, score, time_minutes, time_seconds, hitBrickX, hitBrickY, highscore;
    private Random rng = new Random();
    private boolean spawnItemFlag = false;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createObjects();
        initSensor();
        setupActivityScreen();
        loadAssets();
        setupGameObjects();
        setupGameLoop();
        setupUserInterface();

        playerMovementRunnable = new Runnable() {
            @Override
            //Thread für die Spielerbewegung.
            public void run() {
                if (getGameState() == "running" || getGameState() == "ready") {
                    //Bewegung des Spielers
                    if (playerObject.objectXPosition + sensorState * Constants.PLAYER_SPEED_MULTIPILIER > 0 && playerObject.objectWidthPositionX + sensorState * Constants.PLAYER_SPEED_MULTIPILIER < displayScaleManager.getRightDisplayBorder()) {
                        playerObject.movePlayer(sensorState);
                        updateViewPosition(playerObject);
                    }
                }
                playerMovementHandler.postDelayed(this, displayScaleManager.calculateFramesPerSecond(Constants.PLAYERMOVEMENT_REFRESH_RATE));
            }
        };
        playerMovementHandler.postDelayed(playerMovementRunnable, displayScaleManager.calculateFramesPerSecond(Constants.PLAYERMOVEMENT_REFRESH_RATE));

        time_minutes = Constants.START_TIME;
        timeRunnable = new Runnable() {
            @Override
            //Thread für die Zeit-Steuerug.
            public void run() {
                switch (getGameState()) {
                    case "running":
                        updateTime();
                        break;
                }

                timeHandler.postDelayed(this, 1000);
            }
        };
        timeHandler.postDelayed(timeRunnable, 1000);

        gameLoop = new Runnable() {
            @Override
            //Thread für Kollisionsüberprüfung und Bewegung der Spielobjekte.
            public void run() {
                //Item wird vor dem Iterator gespawnt, da ein HashSet nicht während des Iterierens verändert werden darf.
                if (spawnItemFlag) {
                    spawnItemFlag = false;
                    spawnBonusItem();
                }

                GameObjectView currentGameObject;
                //Hier wird jeder einzelne Block auf Kollisionen überprüft.
                //Findet eine Kollision statt, wird das Objekt aus dem FrameLayout und dem HashSet entfernt.
                Iterator<BallView> ballViewIterator = activeBallsHashSet.iterator();
                while (ballViewIterator.hasNext()) {

                    currentBall = ballViewIterator.next();
                    Iterator<GameObjectView> brickViewIterator = obstaclesHashSet.iterator();
                    while (brickViewIterator.hasNext()) {

                        currentGameObject = brickViewIterator.next();
                        if (checkCollisionObject.checkCollision(currentBall, currentGameObject)) {
                            addScore(1);
                            hitBrickX = currentGameObject.getObjectXPosition();
                            hitBrickY = currentGameObject.getObjectYPosition();
                            if (obstaclesHashSet.size() > 1) {
                                spawnItemFlag = true;
                            }
                            playSoundFile("brick");
                            gameLayout.removeView(currentGameObject);
                            brickViewIterator.remove();
                        }
                    }

                    //Prüfe Ball Kollision mit den Wänden
                    if (checkCollisionObject.checkWallCollision(currentBall, displayScaleManager)) {
                        playSoundFile("wall");
                    }

                    //Prüfe Ball Kollision mit Schläger
                    if (checkCollisionObject.checkPlayerCollision(currentBall, playerObject, displayScaleManager)) {
                        currentBall.setPositionY(playerObject.objectYPosition - Constants.BALL_HEIGHT);

                    }

                    //Beende Spiel wenn alle Bricks zerstört wurden.
                    if (obstaclesHashSet.isEmpty()) {
                        endGame();
                    }

                    //Bewege den Ball nur wenn das Spiel läuft.
                    if (getGameState() == "running") {
                        currentBall.move();
                    }

                    //Prüfe ob der Ball den Boden berührt.
                    if (checkCollisionObject.checkBottomCollision(currentBall, displayScaleManager)) {
                        if (activeBallsHashSet.size() > 1) {
                            ballToRemove = currentBall;
                            gameLayout.removeView(currentBall);
                        } else {
                            playerHit();
                        }
                    }

                    //Aktualisieren der Positionen des Spielers und Ball.
                    if (getGameState() == "running" || getGameState() == "ready") {
                        if (currentBall != null) {
                            updateViewPosition(playerObject);
                            updateViewPosition(currentBall);
                        }

                        //Fixiere den Ball auf dem Spieler solange das Spiel nicht läuft.
                        if (getGameState() == "ready") {
                            currentBall.setObjectXPosition(displayScaleManager.getBallSpawnX(playerObject));
                            currentBall.setObjectYPosition(displayScaleManager.getBallSpawnY(playerObject));
                        }
                    }
                }

                //Itembox Iteration, Markierung zur Löschung und Aktivierung.
                if (getGameState() == "running") {
                    Iterator<BallView> itemBoxIterator = bonusItemBoxHashSet.iterator();
                    while (itemBoxIterator.hasNext()) {
                        currentItemBox = itemBoxIterator.next();
                        if (checkCollisionObject.checkPlayerCollision(currentItemBox, playerObject, displayScaleManager)) {
                            itemBoxToRemove = currentItemBox;
                            activateItem(currentItemBox.getItemType());
                        } else {
                            currentItemBox.move();
                            updateViewPosition(currentItemBox);
                        }

                        if (checkCollisionObject.checkBottomCollision(currentItemBox, displayScaleManager)) {
                            if (bonusItemBoxHashSet.size() > 0) {
                                itemBoxToRemove = currentItemBox;
                                gameLayout.removeView(currentItemBox);
                            }
                        }
                    }
                }

                //Lösche die Itembox, welche den Boden berührt hat (Muss außerhalb des Iterators sein!).
                if (itemBoxToRemove != null) {
                    bonusItemBoxHashSet.remove(itemBoxToRemove);
                    gameLayout.removeView(itemBoxToRemove);
                    itemBoxToRemove = null;
                }

                //Lösche den Ball, welcher den Boden berührt hat (Muss außerhalb des Iterators sein!).
                if (ballToRemove != null) {
                    activeBallsHashSet.remove(ballToRemove);
                    ballToRemove = null;
                }

                //Stoppe den Loop sobald der Spieler tot ist.
                if (playerObject.getHealth() > 0)
                    handler.postDelayed(this, displayScaleManager.calculateFramesPerSecond(Constants.FPS));
            }
        };
        //Starte den GameLoop
        handler.post(gameLoop);
    }

    /**
     * Konfiguriert den Handler für die Spielerbewegung, Zeit und der Aktualisierung der andern Spielobjekte.
     */
    private void setupGameLoop() {
        handler = new Handler(Looper.myLooper());
        timeHandler = new Handler(Looper.myLooper());
        playerMovementHandler = new Handler(Looper.myLooper());
    }

    /**
     * Erzeugt alle benötgten Objekte.
     */
    private void createObjects() {
        displayScaleManager = new ScalingManager(this.getWindowManager());
        checkCollisionObject = new ColllisionDetection();
        obstaclesHashSet = new HashSet<>();
        activeBallsHashSet = new HashSet<>();
        bonusItemBoxHashSet = new HashSet<>();
        playerObject = new PlayerView(this, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT, displayScaleManager.getPlayerSpawnX(), displayScaleManager.getPlayerSpawnY(), Constants.PLAYER_HEALTH);
        pauseButton = new ImageButton(this);
        homeButton = new ImageButton(this);
        restartButton = new ImageButton(this);
        resumeButton = new ImageButton(this);
        homeIntent = new Intent(this, MainActivity.class);
        highscoreData = getSharedPreferences("PREFS", 0);
    }

    /**
     * Aktualisiert die Zeit-Anzeige am oberem Bildschirmrand.
     */
    private void updateTime() {
        if (time_seconds < 1 && time_minutes < 1) {
            endGame();
        }

        if (time_minutes != 0 && time_seconds == 0) {

            if (time_seconds == 0) {
                time_minutes--;
                time_seconds = 60;
            }
        }

        if (time_seconds != 0) {
            time_seconds--;
        }

        if (time_seconds < 10) {
            timeText.setText(time_minutes + ":" + "0" + time_seconds);
        } else {
            timeText.setText(time_minutes + ":" + time_seconds);
        }
    }

    /**
     * Lädt den jetzigen Highscore.
     */
    private void loadHighscore() {
        highscore = highscoreData.getInt("highscore", 0);
    }

    /**
     * Speichert den aktuellen Score als Highscore wenn dieser größer ist.
     */
    private void saveHigscore() {
        SharedPreferences.Editor editor = highscoreData.edit();
        if (score > highscore) {
            editor.putInt("highscore", score);
        }
        editor.apply();
    }

    /**
     * Erzeugt die Blöcke und konfiguriert den Spieler.
     */
    private void setupGameObjects() {
        loadHighscore();

        displayScaleManager.calculateObstacleSize();

        //Erzeuge Blöcke
        int obstacleSpawnX = 0;
        int obstacleSpawnY = 0;
        GameObjectView temporaryGameObject;

        //Diese Verschachtelte Schleife sorgt für die Erzeugung der Blöcke. Die Anzahl der Spalten und Reihen
        //können in der Klasse Constants angepasst werden.
        //Die Äussere Schleife kontrolliert die Reihen, die innere ist für die Spalten zuständig.
        //Am ende jedes Schleifendurchlaufs, wird das erzeugte Objekt in ein HashSet geladen.
        int colorCounter = 0;
        for (int i = 0; i < Constants.OBSTACLE_ROWS; i++) {
            for (int k = 0; k < Constants.OBSTACLE_COLUMNS; k++) {
                if (colorCounter == 3) colorCounter = 0;
                temporaryGameObject = new GameObjectView(this, Constants.OBSTACLE_WIDTH, Constants.OBSTACLE_HEIGHT, obstacleSpawnX, obstacleSpawnY, 1);

                //Prüft in welcher Reihe der Block erstellt wird und weißt diesem in folge dessen die richtige Farbe zu.
                switch (colorCounter) {
                    case 0:
                        temporaryGameObject.setImageDrawable(obstacleDrawable1);
                        break;
                    case 1:
                        temporaryGameObject.setImageDrawable(obstacleDrawable2);
                        break;
                    case 2:
                        temporaryGameObject.setImageDrawable(obstacleDrawable3);
                        break;
                    default:
                        temporaryGameObject.setImageDrawable(obstacleDrawable1);
                        break;
                }
                setupLayout(temporaryGameObject);
                temporaryGameObject.setScaleType(ImageView.ScaleType.FIT_XY);
                gameLayout.addView(temporaryGameObject);
                obstaclesHashSet.add(temporaryGameObject);

                obstacleSpawnX += Constants.OBSTACLE_WIDTH;
            }
            obstacleSpawnX = 0;
            obstacleSpawnY += Constants.OBSTACLE_HEIGHT;

            colorCounter++;
        }

        //Initialisiere Spieler
        playerObject.setImageDrawable(playerDrawable);
        setupLayout(playerObject);
        playerObject.setScaleType(ImageView.ScaleType.FIT_XY);
        gameLayout.addView(playerObject);

        //Initialisiere Ball
        spawnBall(false);
    }

    /**
     * Setzt die Größe und Position für das jeweilige Layout.
     *
     * @param v Layout welches konfiguriert werden soll.
     */
    private void setupLayout(GameObjectView v) {
        v.setLayoutParams(new FrameLayout.LayoutParams(v.objectWidth, v.objectHeigth));
        v.setX(v.getObjectXPosition());
        v.setY(v.getObjectYPosition());
    }

    /**
     * Aktualisiert die Position des Layouts.
     *
     * @param v Layout welches aktualisiert werden soll.
     */
    private void updateViewPosition(GameObjectView v) {
        v.setX(v.getObjectXPosition());
        v.setY(v.getObjectYPosition());
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    /**
     * Setzt die Activity auf Vollbild und konfiguriert das Layout.
     */
    private void setupActivityScreen() {
        //Setze benötigte Flags für den Fullscreen & Notch Support
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_game_);

        //Verweis des FrameLayouts
        gameLayout = findViewById(R.id.gameLayout);
        gameLayout.setOnClickListener(this);
    }

    /**
     * Erzeugt das eingesammelte Item der Item-Box.
     */
    private void spawnBonusItem() {
        int generatedRandom = rng.nextInt(99);
        if (generatedRandom < 24) {
            spawnItemBox("ball");
        }
        if (playerObject.getHealth() < 3) {
            if (generatedRandom > 74) {
                spawnItemBox("heart");
            }
        }
    }

    /**
     * Erzeugt eine Item-Box welch vom Spieler eingesammelt werden kann.
     *
     * @param item Steuert welche Art von Item-Box erzeugt werden soll.
     */
    private void spawnItemBox(String item) {
        switch (item) {
            case "ball":
                BallView ballItemBoxObject = new BallView(this, Constants.ITEMBOX_WIDTH, Constants.ITEMBOX_HEIGHT, (hitBrickX + Constants.OBSTACLE_WIDTH / 2) - Constants.ITEMBOX_WIDTH / 2, (hitBrickY + Constants.OBSTACLE_HEIGHT / 2) - Constants.ITEMBOX_HEIGHT / 2, 0, "ball");
                ballItemBoxObject.setImageDrawable(itemBoxBallDrawable);
                setupLayout(ballItemBoxObject);
                gameLayout.addView(ballItemBoxObject);
                startBallMovement(ballItemBoxObject, true);
                bonusItemBoxHashSet.add(ballItemBoxObject);
                break;

            case "heart":
                BallView ballItemBoxHeartObject = new BallView(this, Constants.ITEMBOX_WIDTH, Constants.ITEMBOX_HEIGHT, (hitBrickX + Constants.OBSTACLE_WIDTH / 2) - Constants.ITEMBOX_WIDTH / 2, (hitBrickY + Constants.OBSTACLE_HEIGHT / 2) - Constants.ITEMBOX_HEIGHT / 2, 0, "heart");
                ballItemBoxHeartObject.setImageDrawable(itemBoxHeartDrawable);
                setupLayout(ballItemBoxHeartObject);
                gameLayout.addView(ballItemBoxHeartObject);
                startBallMovement(ballItemBoxHeartObject, true);
                bonusItemBoxHashSet.add(ballItemBoxHeartObject);
                break;
        }
    }

    /**
     * Aktiviert ein Bonus-Item.
     *
     * @param item Name des zu aktivierenden Items.
     */
    private void activateItem(String item) {
        switch (item) {
            case ("ball"):
                playSoundFile("powerUp");
                spawnBall(true);
                break;
            case ("heart"):
                playSoundFile("powerUp");
                switch (playerObject.getHealth()) {
                    case 1:
                        heartView_2.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        heartView_3.setVisibility(View.VISIBLE);
                        break;
                }
                playerObject.addHealth();
                break;
        }
    }

    /**
     * Erzeugt einen Ball.
     *
     * @param isBonus Entscheidet darüber ob der Ball als "Bonus-Ball" behandelt werden soll.
     */
    private void spawnBall(boolean isBonus) {
        BallView ballObject = new BallView(this, Constants.BALL_WIDTH, Constants.BALL_HEIGHT, displayScaleManager.getBallSpawnX(playerObject), displayScaleManager.getBallSpawnY(playerObject), 0, "none");
        ballObject.setImageDrawable(ballDrawable);
        setupLayout(ballObject);
        gameLayout.addView(ballObject);
        if (isBonus) startBallMovement(ballObject, false);
        activeBallsHashSet.add(ballObject);
    }

    /**
     * Erzeugt und initialisiert alle für die UI relevanten Objekte.
     */
    private void setupUserInterface() {
        heartView_1 = new GameObjectView(this, Constants.HEART_IMG_SIZE, Constants.HEART_IMG_SIZE, displayScaleManager.getHeartSpawnX(), displayScaleManager.getHeartSpawnY(), 0);
        heartView_1.setImageDrawable(heartDrawable);
        setupLayout(heartView_1);
        heartView_1.setScaleType(ImageView.ScaleType.FIT_XY);
        heartView_1.setAlpha(150);
        gameLayout.addView(heartView_1);

        heartView_2 = new GameObjectView(this, Constants.HEART_IMG_SIZE, Constants.HEART_IMG_SIZE, (int) (displayScaleManager.getHeartSpawnX() - Constants.HEART_IMG_SIZE * 1.1), displayScaleManager.getHeartSpawnY(), 0);
        heartView_2.setImageDrawable(heartDrawable);
        setupLayout(heartView_2);
        heartView_2.setScaleType(ImageView.ScaleType.FIT_XY);
        heartView_2.setAlpha(150);
        gameLayout.addView(heartView_2);

        heartView_3 = new GameObjectView(this, Constants.HEART_IMG_SIZE, Constants.HEART_IMG_SIZE, (int) (displayScaleManager.getHeartSpawnX() - Constants.HEART_IMG_SIZE * 2.2), displayScaleManager.getHeartSpawnY(), 0);
        heartView_3.setImageDrawable(heartDrawable);
        setupLayout(heartView_3);
        heartView_3.setScaleType(ImageView.ScaleType.FIT_XY);
        heartView_3.setAlpha(150);
        gameLayout.addView(heartView_3);

        darkBorderView = new GameObjectView(this, displayScaleManager.getHighscoreBorderWidth(), displayScaleManager.getHighscoreBorderHeight(), displayScaleManager.getHighscoreBorderX(), displayScaleManager.getHighscoreBorderY(), 0);
        darkBorderView.setImageDrawable(darkBorderDrawable);
        setupLayout(darkBorderView);
        darkBorderView.setScaleType(ImageView.ScaleType.FIT_XY);
        darkBorderView.setAlpha(180);
        gameLayout.addView(darkBorderView);
        darkBorderView.setVisibility(View.INVISIBLE);

        timeText = new TextView(this);
        timeText.setText(Constants.START_TIME + ":00");
        timeText.setTextSize(24);
        timeText.measure(0, 0);
        timeText.setX((displayScaleManager.getRightDisplayBorder() / 2) - timeText.getMeasuredWidth() / 2);
        timeText.setY(0);
        timeText.setTextColor(Color.WHITE);

        highscoreText = new TextView(this);
        highscoreText.setText(String.valueOf(highscore));
        highscoreText.setTextSize(26);
        highscoreText.setTextColor(Color.WHITE);
        highscoreText.setVisibility(View.INVISIBLE);
        gameLayout.addView(highscoreText);

        highscoreAnnotation = new TextView(this);
        highscoreAnnotation.setText(String.valueOf(score));
        highscoreAnnotation.setTextSize(30);
        highscoreAnnotation.setTextColor(Color.WHITE);
        highscoreAnnotation.setVisibility(View.INVISIBLE);
        gameLayout.addView(highscoreAnnotation);

        timerBackgroundView = new GameObjectView(this, (int) (timeText.getMeasuredWidth() * 2), (int) (timeText.getMeasuredHeight() * 1.1), (((displayScaleManager.getRightDisplayBorder() / 2) - (timeText.getMeasuredWidth() * 2) / 2)), 0, 0);
        timerBackgroundView.setImageDrawable(timerBackgroundDrawable);
        setupLayout(timerBackgroundView);
        timerBackgroundView.setScaleType(ImageView.ScaleType.FIT_XY);
        timerBackgroundView.setAlpha(200);
        gameLayout.addView(timerBackgroundView);
        gameLayout.addView(timeText);

        pauseButton.setImageDrawable(pauseButtonDrawable);
        pauseButton.setBackground(null);
        pauseButton.setPadding(0, 0, 0, 0);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(displayScaleManager.getPauseButtonDimensions(), displayScaleManager.getPauseButtonDimensions());
        pauseButton.setLayoutParams(params);
        pauseButton.setX(displayScaleManager.getRightDisplayBorder() - params.width);
        pauseButton.setY(0);
        pauseButton.setScaleType(ImageView.ScaleType.FIT_XY);
        pauseButton.setOnClickListener(this);
        gameLayout.addView(pauseButton);

        restartButton.setImageDrawable(restartButtonDrawable);
        restartButton.setBackground(null);
        restartButton.setPadding(0, 0, 0, 0);
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(displayScaleManager.getButtonDimensions(), displayScaleManager.getButtonDimensions());
        restartButton.setLayoutParams(params1);
        restartButton.setX((displayScaleManager.getRightDisplayBorder() / 2) - displayScaleManager.getButtonDimensions() / 2);
        restartButton.setY((float) (darkBorderView.getObjectHeightY() - params1.height * 1.3));
        restartButton.setScaleType(ImageView.ScaleType.FIT_XY);
        restartButton.setOnClickListener(this);
        restartButton.setVisibility(View.INVISIBLE);
        gameLayout.addView(restartButton);

        homeButton.setImageDrawable(homeButtonDrawable);
        homeButton.setBackground(null);
        homeButton.setPadding(0, 0, 0, 0);
        homeButton.setLayoutParams(params1);
        homeButton.setX((float) (((displayScaleManager.getRightDisplayBorder() / 2) - params1.width / 2) - params.width * Constants.BUTTON_SPACING_PAUSE));
        homeButton.setY((float) (darkBorderView.getObjectHeightY() - params1.height * 1.3));
        homeButton.setScaleType(ImageView.ScaleType.FIT_XY);
        homeButton.setOnClickListener(this);
        homeButton.setVisibility(View.INVISIBLE);
        gameLayout.addView(homeButton);

        resumeButton.setImageDrawable(resumeButtonDrawable);
        resumeButton.setBackground(null);
        resumeButton.setPadding(0, 0, 0, 0);
        resumeButton.setLayoutParams(params1);
        resumeButton.setX((float) ((displayScaleManager.getRightDisplayBorder() / 2) - params1.width / 2) + params.width * Constants.BUTTON_SPACING_PAUSE);
        resumeButton.setY((float) (darkBorderView.getObjectHeightY() - params1.height * 1.3));
        resumeButton.setScaleType(ImageView.ScaleType.FIT_XY);
        resumeButton.setOnClickListener(this);
        resumeButton.setVisibility(View.INVISIBLE);
        gameLayout.addView(resumeButton);

        scoreText = new TextView(this);
        scoreText.setText(String.valueOf(score));
        scoreText.setTextSize(20);
        scoreText.setTextColor(Color.WHITE);
        scoreText.setVisibility(View.INVISIBLE);
        gameLayout.addView(scoreText);

        scoreAnnotation = new TextView(this);
        scoreAnnotation.setTextSize(26);
        scoreAnnotation.setText("");
        scoreAnnotation.setTextColor(Color.WHITE);
        scoreAnnotation.setVisibility(View.INVISIBLE);
        gameLayout.addView(scoreAnnotation);
    }

    /**
     * Lädt Grafiken, Sounddateien und initialisiert den SoundPool.
     */
    private void loadAssets() {
        //Laden der Drawables
        playerDrawable = ResourcesCompat.getDrawable(getApplication().getResources(), R.drawable.img_player, null);
        ballDrawable = ResourcesCompat.getDrawable(getApplication().getResources(), R.drawable.img_ball, null);
        obstacleDrawable1 = ResourcesCompat.getDrawable(getApplication().getResources(), R.drawable.img_obstacle_2, null);
        obstacleDrawable2 = ResourcesCompat.getDrawable(getApplication().getResources(), R.drawable.img_obstacle_3, null);
        obstacleDrawable3 = ResourcesCompat.getDrawable(getApplication().getResources(), R.drawable.img_obstacle_1, null);
        heartDrawable = ResourcesCompat.getDrawable(getApplication().getResources(), R.drawable.img_heart, null);
        heartDrawable = ResourcesCompat.getDrawable(getApplication().getResources(), R.drawable.img_heart, null);
        darkBorderDrawable = ResourcesCompat.getDrawable(getApplication().getResources(), R.drawable.img_black_background, null);
        timerBackgroundDrawable = ResourcesCompat.getDrawable(getApplication().getResources(), R.drawable.img_timer_background, null);
        itemBoxBallDrawable = ResourcesCompat.getDrawable(getApplication().getResources(), R.drawable.img_itembox_ball, null);
        pauseButtonDrawable = ResourcesCompat.getDrawable(getApplication().getResources(), R.drawable.img_button_pause, null);
        resumeButtonDrawable = ResourcesCompat.getDrawable(getApplication().getResources(), R.drawable.img_button_start, null);
        homeButtonDrawable = ResourcesCompat.getDrawable(getApplication().getResources(), R.drawable.img_button_exit, null);
        restartButtonDrawable = ResourcesCompat.getDrawable(getApplication().getResources(), R.drawable.img_button_restart, null);
        itemBoxHeartDrawable = ResourcesCompat.getDrawable(getApplication().getResources(), R.drawable.img_itembox_heart, null);

        //Initialisiere den SoundPool
        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(attrs)
                .build();

        sound_brick_1 = soundPool.load(this, R.raw.sound_brick_1, 1);
        sound_healthRemove = soundPool.load(this, R.raw.sound_health_remove, 3);
        sound_playerHit = soundPool.load(this, R.raw.sound_player_hit, 4);
        sound_powerUp = soundPool.load(this, R.raw.sound_power_up, 5);
        sound_wallHit = soundPool.load(this, R.raw.sound_wall_hit, 6);
    }

    /**
     * Spielt einen Soundeffekt ab.
     *
     * @param soundName Welcher Soundeffekt abgespielt werden soll.
     */
    public void playSoundFile(String soundName) {
        if (getGameState() != "running") return;
        switch (soundName) {
            case "bottom":
                soundPool.play(sound_healthRemove, 1, 1, 1, 0, 1.0F);
                break;
            case "wall":
                soundPool.play(sound_wallHit, 1, 1, 3, 0, 1.0F);
                break;
            case "player":
                soundPool.play(sound_playerHit, 1, 1, 1, 0, 1.0F);
                break;
            case "powerUp":
                soundPool.play(sound_powerUp, 1, 1, 1, 0, 1.0F);
                break;
            case "brick":
                soundPool.play(sound_brick_1, 1, 1, 1, 0, 1.0F);
                break;
        }
    }

    /**
     * Speichert den Highscore und setzt den Text für die diversen Textobjekte.
     *
     * @param title Text-Überschrift
     */
    private void showScoreText(String title) {
        blurBackground();
        saveHigscore();
        score += time_minutes * 60 + time_seconds + playerObject.getHealth();
        scoreText.setText(score + "");
        highscoreText.setText(highscore + "");
        highscoreAnnotation.setText("- HIGHSCORE -");

        scoreAnnotation.setText(title);
        scoreAnnotation.measure(0, 0);
        scoreAnnotation.setX((displayScaleManager.getRightDisplayBorder() / 2) - scoreAnnotation.getMeasuredWidth() / 2);
        scoreAnnotation.setY((darkBorderView.getObjectYPosition() + darkBorderView.getHeight() / 2) - scoreAnnotation.getMeasuredHeight() / 2);

        scoreText.measure(0, 0);
        scoreText.setX((displayScaleManager.getRightDisplayBorder() / 2) - scoreText.getMeasuredWidth() / 2);
        scoreText.setY(scoreAnnotation.getY() + scoreAnnotation.getMeasuredHeight());

        highscoreAnnotation.measure(0, 0);
        highscoreAnnotation.setX((displayScaleManager.getRightDisplayBorder() / 2) - highscoreAnnotation.getMeasuredWidth() / 2);
        highscoreAnnotation.setY(darkBorderView.getObjectYPosition() + highscoreAnnotation.getMeasuredHeight() / 2);

        highscoreText.measure(0, 0);
        highscoreText.setX((displayScaleManager.getRightDisplayBorder() / 2) - highscoreText.getMeasuredWidth() / 2);
        highscoreText.setY(highscoreAnnotation.getY() + highscoreAnnotation.getMeasuredHeight());
    }

    /**
     * Blendet die UI-Elemente für den Pause oder Ende-Screen ein.
     *
     * @param endScreenFlag Regelt ob der Score & Highscore angezeigt werden sollen.
     */
    private void setUiVisible(Boolean endScreenFlag) {
        scoreAnnotation.setX((displayScaleManager.getRightDisplayBorder() / 2) - scoreAnnotation.getMeasuredWidth() / 2);
        darkBorderView.bringToFront();
        darkBorderView.setVisibility(View.VISIBLE);

        scoreAnnotation.bringToFront();
        scoreAnnotation.setVisibility(View.VISIBLE);

        restartButton.bringToFront();
        restartButton.setVisibility(View.VISIBLE);

        homeButton.bringToFront();
        homeButton.setVisibility(View.VISIBLE);

        resumeButton.bringToFront();
        resumeButton.setVisibility(View.VISIBLE);
        if (endScreenFlag) {
            scoreText.bringToFront();
            scoreText.setVisibility(View.VISIBLE);

            highscoreAnnotation.bringToFront();
            highscoreAnnotation.setVisibility(View.VISIBLE);

            highscoreText.bringToFront();
            highscoreText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Blendet die UI-Elemente für den Pause oder Ende-Screen aus.
     */
    private void setUiInvisible() {
        darkBorderView.setVisibility(View.INVISIBLE);
        scoreAnnotation.setVisibility(View.INVISIBLE);
        restartButton.setVisibility(View.INVISIBLE);
        resumeButton.setVisibility(View.INVISIBLE);
        homeButton.setVisibility(View.INVISIBLE);
        scoreText.setVisibility(View.INVISIBLE);
        blurBackgroundView.setVisibility(View.INVISIBLE);


    }

    /**
     * Startet die derzeitige Activity neu.
     */
    private void resetGame() {
        finish();
        startActivity(getIntent());
        Animatoo.animateShrink(this);
    }

    /**
     * Prüft bei einem "Klick"-Ereigniss, welcher Button betätigt wurde.
     *
     * @param v View des onClick ereignisses.
     */
    public void onClick(View v) {
        if (v.equals(gameLayout)) {
            switch (getGameState()) {
                case "ready":
                    setGameState("running");
                    startBallMovement(currentBall, false);
                    break;
            }
        } else if (v.equals(pauseButton)) {
            if (getGameState() != "paused" && getGameState() != "ended") {
                lastGameState = getGameState();
                setGameState("paused");
                showScoreText("- PAUSED -");
                setUiVisible(false);
            }
        } else if (v.equals(restartButton)) {
            resetGame();
        } else if (v.equals(homeButton)) {
            startActivity(homeIntent);
            Animatoo.animateFade(this);
        } else if (v.equals(resumeButton) && getGameState() != "ended") {
            setUiInvisible();
            setGameState(lastGameState);
        }
    }

    /**
     * Beendet das Spiel und zeigt den Spielstand an.
     */
    private void endGame() {
        if (hasAlreadyBeenCalled) return;
        hasAlreadyBeenCalled = true;
        setGameState("ended");
        saveHigscore();
        showScoreText("   SCORE  ");
        setUiVisible(true);
    }

    /**
     * Erzeugt ein View welcher einen Unscharfen Screenshot des derzeitgen Layouts macht.
     */
    private void blurBackground() {
        blurBackgroundView = new GameObjectView(this, displayScaleManager.getRightDisplayBorder(), displayScaleManager.getBottomDisplayBorder(), 0, 0, 0);
        blurBackgroundView.setBackgroundDrawable(new BitmapDrawable(getResources(), blur(this, captureScreenShot(gameLayout))));
        setupLayout(blurBackgroundView);
        blurBackgroundView.setScaleType(ImageView.ScaleType.FIT_XY);
        gameLayout.addView(blurBackgroundView);
    }


    //Quelle: https://developerandroidguide.blogspot.com/2017/05/how-to-blur-background-images.html
    public Bitmap captureScreenShot(View view) {
        /*
         * Creating a Bitmap of view with ARGB_4444.
         * */
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        Drawable backgroundDrawable = view.getBackground();
        if (backgroundDrawable != null) {
            backgroundDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.parseColor("#80000000"));
        }
        view.draw(canvas);
        return bitmap;
    }

    //Quelle: https://developerandroidguide.blogspot.com/2017/05/how-to-blur-background-images.html
    public static Bitmap blur(Context context, Bitmap image) {
        float BITMAP_SCALE = 0.4f;
        float BLUR_RADIUS = 15f;

        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    /**
     * Startet die Bewegung des Balls in eine zufällige Richtung in -Y Richtung, wenn dieser sich nicht um ein Item handelt.
     * Ansonsten wird der "Ball" als Item behandelt und lässt dieses mit konstanter Geschwindigkeit fallen.
     *
     * @param ball   Objekt auf welches die Bewegung gesetzt werden soll.
     * @param isItem Wenn Wahr, wird der Ball als Item behandelt und fliegt statt in eine zufällige richtung, mit konstanter Geschwindkeit in +Y Richtung..
     */
    private void startBallMovement(BallView ball, boolean isItem) {
        if (isItem) {
            ball.setDeltaX(0);
            ball.setDeltaY(5);

        } else {
            int generatedRandom = rng.nextInt(Constants.MAX_BALL_SPEED) + 10;
            if (rng.nextInt(9) > 4) {
                generatedRandom = generatedRandom * -1;
            }
            ball.setDeltaX(generatedRandom);
            ball.setDeltaY(Constants.MAX_BALL_SPEED * -1);
        }
    }

    /**
     * Setzt den derzeitigen Spiel-Zustand.
     *
     * @param state Zu setzender Zustand.
     */
    private void setGameState(String state) {
        gameState = state;
    }

    /**
     * Gibt den derzeitigen Zustand des Spiels zurück. Mögliche Zustände: Pausiert, Laufend, Beendet.
     *
     * @return Derzeitiger Spiel-Zustand.
     */
    public String getGameState() {
        return gameState;
    }

    /**
     * Konfiguriert den Sensor Manager welcher die Werte für die Neigung des Geräts ausliest.
     */
    private void initSensor() {
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    /**
     * Akualisert die Variable für die Neigung des Geräts.
     */
    public void onSensorChanged(SensorEvent event) {
        sensorState = event.values[1];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Entzieht dem Spieler leben und spielt dabei den passenden Sound ab. Hat dieser mehr als 1 Leben wird der Ball auf die Ausgangsposition zurück gesetzt. Andernfalls wird das Spiel beendet.
     */
    public void playerHit() {
        switch (playerObject.getHealth()) {

            case 3:
                playSoundFile("bottom");
                heartView_3.setVisibility(View.INVISIBLE);
                playerObject.removeHealth();
                resetBall();
                break;

            case 2:
                playSoundFile("bottom");
                heartView_2.setVisibility(View.INVISIBLE);
                playerObject.removeHealth();
                resetBall();
                break;

            case 1:
                playSoundFile("bottom");
                heartView_1.setVisibility(View.INVISIBLE);
                playerObject.removeHealth();
                endGame();
                break;
        }
    }

    /**
     * Setzt den Ball auf die Ausgangsposition zurück wie auch den Spielstatus auf "bereit".
     */
    private void resetBall() {
        setGameState("ready");
        gameLayout.removeView(currentBall);
        activeBallsHashSet.remove(currentBall);
        spawnBall(false);
        updateViewPosition(currentBall);
    }

    /**
     * Löscht den SoundPool bei schließen der App.
     */
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }

    /**
     * Erhöht den Score um den übergebenen Betrag.
     *
     * @param amount Um wieviel der Score erhöht werden soll.
     */
    private void addScore(int amount) {
        score += amount;
    }
}//END