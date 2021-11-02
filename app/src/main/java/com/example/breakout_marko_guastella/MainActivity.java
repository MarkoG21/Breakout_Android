package com.example.breakout_marko_guastella;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton startButton, quitButton, settingsButton;
    private Intent gameIntent, settingsIntent;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setze benötigte Flags für den Fullscreen & Notch Support
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);
        getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;

        //Verweis auf die Image-Button-Objekte & OnClickListener hinzufügen.
        startButton = findViewById(R.id.startButton);
        quitButton = findViewById(R.id.quitButton);
        settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(this);
        startButton.setOnClickListener(this);
        quitButton.setOnClickListener(this);

        //Initialisiere Intents.
        gameIntent = new Intent(this, Game_Activity.class);
        settingsIntent = new Intent(this, SettingsActivity.class);
    }

    @Override
    /**
     * Prüft bei einem "Klick"-Ereigniss, welcher Button betätigt wurde.
     */
    public void onClick(View v) {
        //Prüft welcher der Buttons gedrückt wurde und führt die jeweilige Aktion/Activity aus.
        if (v == startButton) {
            startActivity(gameIntent);
            //Quelle: https://github.com/AtifSayings/Animatoo
            Animatoo.animateFade(this);
        }

        if (v == settingsButton) {
            startActivity(settingsIntent);
            //Quelle: https://github.com/AtifSayings/Animatoo
            Animatoo.animateSwipeLeft(this);
        }

        if (v == quitButton) {
            MainActivity.this.finish();
            moveTaskToBack(true);

        }
    }
}