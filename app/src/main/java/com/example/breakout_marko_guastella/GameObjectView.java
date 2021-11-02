package com.example.breakout_marko_guastella;

import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;

public class GameObjectView extends AppCompatImageView {
    int objectWidth, objectHeigth, objectXPosition, objectYPosition, objectHealth, objectWidthPositionX, objectHeightPositionY;

    /**
     * Konstruktor eines Spiel-Objekts.
     *
     * @param context   Activity, welches dieses Objekt erzeugt.
     * @param width     Breite des Spiel-Objekts.
     * @param height    Höhe des Spiel-Objekts.
     * @param positionX X-Position des Spiel-Objekts.
     * @param positionY Y-Position des Spiel-Objekts.
     * @param health    Leben des Spiel-Objekts.
     */
    public GameObjectView(Context context, int width, int height, int positionX, int positionY, int health) {
        super(context);
        objectWidth = width;
        objectHeigth = height;
        objectHealth = health;
        objectXPosition = positionX;
        objectYPosition = positionY;
        objectWidthPositionX = positionX + objectWidth;
        objectHeightPositionY = positionY + objectHeigth;
        objectHealth = health;
    }

    /**
     * Setzt die X-Position des Spiel-Objekts.
     *
     * @param xPosition X-Position.
     */
    public void setObjectXPosition(int xPosition) {
        objectXPosition = xPosition;
    }

    /**
     * Setzt die Y-Position des Spiel-Objekts.
     *
     * @param yPosition Y-Position.
     */
    public void setObjectYPosition(int yPosition) {
        objectYPosition = yPosition;
    }

    /**
     * Gibt die X-Position des Spiel-Objekts zurück.
     *
     * @return X-Position des Objekts als Ganzzahl.
     */
    public int getObjectXPosition() {
        return objectXPosition;
    }

    /**
     * Gibt die Y-Position des Spiel-Objekts zurück.
     *
     * @return Y-Position des Objekts als Ganzzahl.
     */
    public int getObjectYPosition() {
        return objectYPosition;
    }

    /**
     * Gibt die X-Position mit drauf-gerechneter Breite zurück.
     *
     * @return X-Position + Breite des Objekts als Ganzzahl.
     */
    public int getObjectWidthX() {
        return objectWidthPositionX;
    }

    /**
     * Gibt die Y-Position mit drauf-gerechneter Höhe zurück.
     *
     * @return Y-Position + Höhe des Objekts als Ganzzahl.
     */
    public int getObjectHeightY() {
        return objectHeightPositionY;
    }

    /**
     * Zieht dem Spiel-Objekt ein Leben ab.
     */
    public void removeHealth() {
        if (objectHealth > 0) objectHealth -= 1;
    }

    /**
     * Fügt dem Spiel-Objekt ein Leben hinzu.
     */
    public void addHealth() {
        if (objectHealth < 3) objectHealth += 1;
    }

    /**
     * Gibt die Anzahl der Leben zurück.
     *
     * @return Übrige Leben des Objekts als Ganzzahl.
     */
    public int getHealth() {
        return objectHealth;
    }
}
