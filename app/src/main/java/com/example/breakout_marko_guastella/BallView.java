package com.example.breakout_marko_guastella;

import android.content.Context;

public class BallView extends GameObjectView {
    private int deltaX, deltaY;
    private String itemType;

    /**
     * Konstruktor eines Ball-Objekts.
     *
     * @param context   Activity, welches dieses Objekt erzeugt.
     * @param width     Breite des Balls.
     * @param height    Hoehe des Balls.
     * @param positionX X-Position des Balls.
     * @param positionY Y-Position des Balls.
     * @param health    Lebenspunkte des Balls.
     * @param item      Item-Typ, wenn der "Ball" ein Item ist.
     */
    public BallView(Context context, int width, int height, int positionX, int positionY, int health, String item) {
        super(context, width, height, positionX, positionY, health);
        deltaX = 0;
        deltaY = 0;
        itemType = item;
    }

    /**
     * Ball Bewegung pro Frame.
     */
    public void move() {
        objectWidthPositionX = objectXPosition + objectWidth;
        objectHeightPositionY = objectYPosition + objectHeigth;
        objectXPosition += deltaX;
        objectYPosition += deltaY;
    }

    /**
     * Setzt die Y-Position des Ball-Objekts.
     *
     * @param posY Y-Position des Ball-Objekts als Ganzzahl.
     */
    public void setPositionY(int posY) {
        objectYPosition = posY;
    }

    /**
     * Gibt die X-Position der linken Kante im nächsten Frame zurück.
     *
     * @return X-Position des Ball-Objekts im nächsten Frame als Ganzzahl.
     */
    public int getPositionXNextFrameLeftSide() {
        return objectXPosition + deltaX;
    }

    /**
     * Gibt die Y-Position der oberen Kante im nächsten Frame zurück.
     *
     * @return Y-Position des Balls im nächsten Frame als Ganzzahl.
     */
    public int getPositionYNextFrameUpperSide() {
        return objectYPosition + deltaY;
    }

    /**
     * Gibt die X-Position der rechten Kante im nächsten Frame zurück.
     *
     * @return X-Position + Breite des Ball-Objekts im nächsten Frame als Ganzzahl.
     */
    public int getPositionXNextFrameRightSide() {
        return objectWidthPositionX + deltaX;
    }

    /**
     * Gibt die Y-Position der unteren Kante im nächsten Frame zurück.
     *
     * @return Y-Position + Höhe des Ball-Objekts im nächsten Frame als Ganzzahl.
     */
    public int getPositionYNextFrameBottomSide() {
        return objectHeightPositionY + deltaY;
    }

    /**
     * Invertiert die Bewegung in X-Richtung.
     */
    public void invertDeltaX() {
        deltaX = deltaX * -1;
    }

    /**
     * Invertiert die Bewegung in Y-Richtung.
     */
    public void invertDeltaY() {
        deltaY = deltaY * -1;
    }

    /**
     * Gibt die X-Verschiebung pro Frame zurück.
     *
     * @return X-Verschiebung als Ganzzahl.
     */
    public int getDeltaX() {
        return deltaX;
    }

    /**
     * Setzt die X-Verschiebung pro Frame.
     *
     * @param _deltaX X-Verschiebung.
     */
    public void setDeltaX(int _deltaX) {
        deltaX = _deltaX;
    }

    /**
     * Gibt den Typ des Items zurück.
     *
     * @return Typ des Items als String.
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * Setzt die Y-Verschiebung pro Frame.
     *
     * @param deltaY Y-Verschiebung.
     */
    public void setDeltaY(int deltaY) {
        this.deltaY = deltaY;
    }
}
