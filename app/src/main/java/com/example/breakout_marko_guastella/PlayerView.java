package com.example.breakout_marko_guastella;

import android.content.Context;


public class PlayerView extends GameObjectView {
    private float deltaX;

    /**
     * Konstruktor des Spieler-Objekts.
     *
     * @param context   Activity, welches dieses Objekt erzeugt.
     * @param width     Breite des Spielers.
     * @param height    Höhe des Spielers.
     * @param positionX X-Position des Spielers.
     * @param positionY Y-Position des Spielers.
     * @param health    Lebensanzahl des Spielers.
     */
    public PlayerView(Context context, int width, int height, int positionX, int positionY, int health) {
        super(context, width, height, positionX, positionY, health);
    }

    /**
     * Bewegt den Spieler in X-Richtung.
     *
     * @param speed Geschwindigkeit der Bewegung.
     */
    public void movePlayer(float speed) {
        deltaX = speed * Constants.PLAYER_SPEED_MULTIPILIER;
        if (deltaX > Constants.MAX_PLAYER_SPEED || deltaX < Constants.MAX_PLAYER_SPEED * -1) {
            if (deltaX > 0) {
                deltaX = Constants.MAX_PLAYER_SPEED;
            } else {
                deltaX = Constants.MAX_PLAYER_SPEED * -1;
            }
        }
        objectXPosition += deltaX;
        objectWidthPositionX = objectXPosition + Constants.PLAYER_WIDTH;
    }

    /**
     * Berechnet die Mitte des Spielers in X-Richtung.
     *
     * @return X-Position der Mitte des Spieler-Objekts als Ganzzahl.
     */
    public int getPlayerCenterPositionX() {
        int i = objectXPosition + (objectWidth / 2);
        return i;
    }

    /**
     * Gibt die linke Kante des Spielers im nächsten Frame zurück.
     *
     * @return X-Position im nächsten Frame als Ganzzahl.
     */
    public float getPositionXNextFrameLeftSide() {
        return objectXPosition + deltaX;
    }

    /**
     * Gibt die rechte Kante des SPielers im nächsten Frame zurück.
     *
     * @return X-Position + Breite des Spieler-Objekts im nächsten Frame als Ganzzahl.
     */
    public float getPositionXNextFrameRightSide() {
        return objectWidthPositionX + deltaX;
    }
}
