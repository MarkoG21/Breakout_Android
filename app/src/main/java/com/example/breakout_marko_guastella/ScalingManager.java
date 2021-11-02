package com.example.breakout_marko_guastella;

import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ScalingManager {
    private int displayWidth, displayHeight;

    /**
     * Konstruktor für den Skalierungs-Manager
     *
     * @param m
     */
    ScalingManager(WindowManager m) {
        //Initialisiere Display Daten
        Point size = new Point();
        Display d = m.getDefaultDisplay();
        d.getRealSize(size);
        displayWidth = size.x;
        displayHeight = size.y;
    }

    /**
     * Berechnet die Größe der Blöcke in abhängigkeit der Displayhöhe & Breite.
     */
    public void calculateObstacleSize() {
        Constants.OBSTACLE_WIDTH = displayWidth / Constants.OBSTACLE_COLUMNS;
        Constants.OBSTACLE_HEIGHT = ((displayHeight / 3) / Constants.OBSTACLE_ROWS);
    }

    /**
     * Berechnet die X-Position des Balls, damit dieser in der Mitte des Schlägers gespawnt wird.
     *
     * @param playerObject Spieler-Objekt.
     * @return Berechnete X-Position für den Spawn des Balls.
     */
    public int getBallSpawnX(PlayerView playerObject) {
        return playerObject.getPlayerCenterPositionX() - (Constants.BALL_WIDTH / 2);
    }

    /**
     * Berechnet die Y-Position des Balls, damit dieser auf dem Schläger gespawnt wird.
     *
     * @param playerObject Spieler-Objekt.
     * @return Berechnete Y-Position für den Spawn des Balls.
     */
    public int getBallSpawnY(PlayerView playerObject) {
        return (int) (playerObject.objectYPosition - Constants.BALL_HEIGHT * 1.1);
    }

    /**
     * Berechne die Breite/Höhe des Pause-Buttons.
     *
     * @return Breite/Höhe des Pause-Buttons.
     */
    public int getPauseButtonDimensions() {
        return displayWidth / 20;
    }

    /**
     * Berechne die Breite/Höhe der Buttons für den Pause-Bildschirm.
     *
     * @return Breite/Höhe des Buttons im Pausebildschirm.
     */
    public int getButtonDimensions() {
        return displayWidth / 15;
    }

    /**
     * Berechne den Spieler-Spawnpunkt (Mitte des Bildschirms) in abhängigkeit der Displaybreite.
     *
     * @return Berechnete X-Position für den Spawn des Balls.
     */
    public int getPlayerSpawnX() {
        int spawnCalculation = (displayWidth / 2) - (Constants.PLAYER_WIDTH / 2);
        return spawnCalculation;
    }

    /**
     * Berechne den Spieler-Spawnpunkt in abhängigkeit der Displayhöhe.
     *
     * @return Berechnete Y-Position für den Spawn des Balls.
     */
    public int getPlayerSpawnY() {
        int spawnCalculation = (int) (displayHeight - (1.25 * Constants.PLAYER_HEIGHT));
        return spawnCalculation;
    }

    /**
     * Gibt die X-Position der Highscore abdunklung zurück.
     *
     * @return Berechnete X-Position für den Spawn der Drawables.
     */
    public int getHighscoreBorderX() {
        return displayWidth / 8;
    }

    /**
     * Gibt die Y-Position der Highscore abdunklung zurück.
     *
     * @return Berechnete Y-Position für den Spawn der Drawables.
     */
    public int getHighscoreBorderY() {
        return displayHeight / 8;
    }

    /**
     * Gibt die Breite der Highscore abdunklung zurück.
     *
     * @return Berechnete Breite des Drawables.
     */
    public int getHighscoreBorderWidth() {
        return (displayWidth / 8) * 6;
    }

    /**
     * Gibt die Höhe der Highscore abdunklung zurück.
     *
     * @return Berechnete Höhe des Drawables.
     */
    public int getHighscoreBorderHeight() {
        return (displayHeight / 8) * 6;
    }

    /**
     * Gibt die Mitte des Balls zurück.
     *
     * @return Mitte des Ball-Objekts als Ganzzahl.
     */
    public int getBallMiddle() {
        return Constants.BALL_WIDTH / 2;
    }

    /**
     * Gibt die Mitte des Spielers zurück.
     *
     * @return Mitte des Spieler-Objekts in X-Richtung als Ganzzahl.
     */
    public int getPlayerMiddle() {
        return (int) Constants.PLAYER_WIDTH / 2;
    }

    /**
     * Gibt den X-Wert für den linken Bildschirmrand zurück.
     *
     * @return Position des linken Displayrands als Ganzzahl.
     */
    public int getLeftDisplayBorder() {
        return 0;
    }

    /**
     * Gibt den Breite des Bildschirms zurück.
     *
     * @return Breite des Displays.
     */
    public int getRightDisplayBorder() {
        return displayWidth;
    }

    /**
     * Gibt den Y-Wert für die obere Kante des Bildschirms zurück.
     *
     * @return Y-Position des oberen Displayrands als Ganzzahl.
     */
    public int getUpperDisplayBorder() {
        return 0;
    }

    /**
     * Gibt den Y-Wert für die untere Kante des Bildschirms zurück.
     *
     * @return Höhe des Displays.
     */
    public int getBottomDisplayBorder() {
        return displayHeight;
    }

    /**
     * Berechne die Zeit je Frame in Millisekunden.
     *
     * @param fps Gewünsche Bilder pro Sekunden.
     * @return Zeit je Frame in ms.
     */
    public long calculateFramesPerSecond(long fps) {
        return (1000 / fps);
    }

    /**
     * Gibt die X-Position für den Spawn des Herz Icons zurück.
     *
     * @return Berechnete X-Position für den Spawn des Drawables.
     */
    public int getHeartSpawnX() {
        return (int) (displayWidth - Constants.HEART_IMG_SIZE * 1.2);
    }

    /**
     * Gibt die Y-Position für den Spawn des Herz Icons zurück.
     *
     * @return Berechnete Y-Position für den Spawn des Drawables.
     */
    public int getHeartSpawnY() {
        return (int) (getPlayerSpawnY() - Constants.HEART_IMG_SIZE * 1.2);
    }
}
