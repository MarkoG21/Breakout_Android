package com.example.breakout_marko_guastella;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView collumnsEdit, rowsEdit, playerWidthEdit, maxBallSpeedEdit, fpsEdit, timeEdit, ballSizeEdit;
    private ImageButton saveButton, resetButton, goBackButton;
    private boolean saveFlag = false;
    private Intent goToMainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Setze benötigte Flags für den Fullscreen & Notch Support
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        initEditText();
        initButtons();

        //Initialisiere Intents.
        goToMainMenu = new Intent(this, MainActivity.class);
    }

    @Override
    /**
     * Prüft bei einem "Klick"-Ereigniss, welcher Button betätigt wurde.
     */
    public void onClick(View v) {
        if (saveButton.equals(v)) {
            saveButton.setBackgroundColor(Color.DKGRAY);
            saveFlag = true;
        }

        if (goBackButton.equals(v)) {
            if (saveFlag) saveSettings();
            startActivity(goToMainMenu);
            //Quelle: https://github.com/AtifSayings/Animatoo
            Animatoo.animateSwipeRight(this);
        }

        if (resetButton.equals(v)) {
            saveButton.setBackgroundColor(Color.RED);
            initEditText();
        }
    }

    /**
     * Speichert die geänderten Einstellungen ab.
     */
    private void saveSettings() {
        Constants.OBSTACLE_COLUMNS = Integer.valueOf(String.valueOf(collumnsEdit.getText()));
        Constants.OBSTACLE_ROWS = Integer.valueOf(String.valueOf(rowsEdit.getText()));
        Constants.MAX_BALL_SPEED = Integer.valueOf(String.valueOf(maxBallSpeedEdit.getText()));
        Constants.PLAYER_WIDTH = Integer.valueOf(String.valueOf(playerWidthEdit.getText()));
        Constants.START_TIME = Integer.valueOf(String.valueOf(timeEdit.getText()));
        Constants.FPS = Long.valueOf(String.valueOf(fpsEdit.getText()));
        Constants.BALL_HEIGHT = Integer.valueOf(String.valueOf(ballSizeEdit.getText()));
        Constants.BALL_WIDTH = Integer.valueOf(String.valueOf(ballSizeEdit.getText()));
    }

    /**
     * Konfiguriert die Buttons.
     */
    private void initButtons() {
        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(this);

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        goBackButton = findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(this);
    }

    /**
     * Konfiguriert die Text-Objekte.
     */
    private void initEditText() {
        collumnsEdit = findViewById(R.id.collumnsEdit);
        collumnsEdit.setText(String.valueOf(Constants.OBSTACLE_COLUMNS));
        collumnsEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);

        playerWidthEdit = findViewById(R.id.playerWidthEdit);
        playerWidthEdit.setText(String.valueOf(Constants.PLAYER_WIDTH));
        playerWidthEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);

        maxBallSpeedEdit = findViewById(R.id.maxBallSpeedEdit);
        maxBallSpeedEdit.setText(String.valueOf(Constants.MAX_BALL_SPEED));
        maxBallSpeedEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);

        fpsEdit = findViewById(R.id.fpsEdit);
        fpsEdit.setText(String.valueOf(Constants.FPS));
        fpsEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);

        timeEdit = findViewById(R.id.timeEdit);
        timeEdit.setText(String.valueOf(Constants.START_TIME));
        timeEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);

        rowsEdit = findViewById(R.id.rowsEdit);
        rowsEdit.setText(String.valueOf(Constants.OBSTACLE_ROWS));
        rowsEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);

        ballSizeEdit = findViewById(R.id.ballSizeEdit);
        ballSizeEdit.setText(String.valueOf(Constants.BALL_HEIGHT));
        ballSizeEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }
}