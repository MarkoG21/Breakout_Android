package com.example.breakout_marko_guastella;

import android.util.Log;

public class ColllisionDetection {
    /**
     * Prüft die Kollision zwischen einem Ball-Objekt und dem linken und seitlichen Bildschirmrändern.
     *
     * @param ballObject Ball-Objekt.
     * @param d          Skalierungs-Manager.
     * @return Gibt Wahr zurück, wenn eine Kollision stattfindet.
     */
    public boolean checkWallCollision(BallView ballObject, ScalingManager d) {
        boolean collided = false;
        //Ball Kollision mit rechtem && linkem Bildschirmrand, invertiere die X-Beschleunigung wenn eine Kollision auftritt.
        if (ballObject.getPositionXNextFrameLeftSide() < d.getLeftDisplayBorder() || ballObject.getPositionXNextFrameRightSide() > d.getRightDisplayBorder()) {
            ballObject.invertDeltaX();
            collided = true;
        }
        //Ball Kollision mit Oberem Bildschirmrand, invertiere die Y-Beschleunigung wenn eine Kollision auftritt.
        if (ballObject.getPositionYNextFrameUpperSide() < d.getUpperDisplayBorder()) {
            ballObject.invertDeltaY();
            collided = true;
        }
        return collided;
    }

    /**
     * Prüft die Kollision zwischen Ball-Objekt und dem unterem Bildschirmrand.
     *
     * @param ballObject Ball-Objekt.
     * @param d          Skalierungs-Manager.
     * @return Gibt Wahr zurück, wenn eine Kollision stattfindet.
     */
    public boolean checkBottomCollision(BallView ballObject, ScalingManager d) {
        boolean collided = false;
        if (ballObject.getPositionYNextFrameBottomSide() > d.getBottomDisplayBorder()) {
            collided = true;
        }
        return collided;
    }

    /**
     * Prüft die Kollision zwischen einem Ball-Objekt und einem Spieler-Objekt.
     * Der Ball prallt, je nachdem, auf welcher Hälfte dieser mit dem Schläger kollidiert dementsprechend ab.
     *
     * @param ballObject   Ball-Objekt.
     * @param playerObject Spieler-Objekt.
     * @param d            Skalierungs-Manager.
     * @return Gibt Wahr zurück, wenn eine Kollision stattfindet.
     */
    public boolean checkPlayerCollision(BallView ballObject, PlayerView playerObject, ScalingManager d) {
        boolean collided = false;

        //Prüfe ob der Ball X innerhalb von Spieler X liegt. Wenn zusätzlich die Höhe des Balls unterhalb des Spielers ist, findet eine Kollision statt.
        if (ballObject.getPositionXNextFrameRightSide() >= playerObject.getPositionXNextFrameLeftSide() && ballObject.getPositionXNextFrameLeftSide() <= playerObject.getPositionXNextFrameRightSide()) {
            if (ballObject.getPositionYNextFrameBottomSide() >= playerObject.getObjectYPosition()) {
                collided = true;
                ballObject.invertDeltaY();

                //Steuert den Abrall des Balls, je nachdem mit welcher Hälfte des Schlägers dieser kollidiert.
                if (ballObject.getObjectXPosition() + d.getBallMiddle() >= playerObject.getObjectXPosition() + d.getPlayerMiddle()) {

                    if (ballObject.getDeltaX() < 0) {
                        ballObject.invertDeltaX();
                    }
                } else {
                    if (ballObject.getDeltaX() > 0) {
                        ballObject.invertDeltaX();
                    }
                }
            }
        }

        return collided;
    }

    /**
     * Prüft die Kollision zwischen einem Ball-Objekt und einem Spiel-Objekt.
     *
     * @param ballObject     Ball-Objekt
     * @param obstacleObject Spiel-Objekt
     * @return Gibt Wahr zurück, wenn eine Kollision stattfindet.
     */
    public boolean checkCollision(BallView ballObject, GameObjectView obstacleObject) {
        boolean deleteObject = false;

        //Prüfe die Kollision der linken & rechten "Seite" des Objekts, invertiere die X-Beschleunigung wenn eine Kollision auftritt.
        if (ballObject.getObjectYPosition() < obstacleObject.getObjectHeightY() && ballObject.getObjectHeightY() > obstacleObject.getObjectYPosition()) {

            if (ballObject.getPositionXNextFrameLeftSide() < obstacleObject.getObjectWidthX() && ballObject.getObjectWidthX() > obstacleObject.getObjectWidthX() || ballObject.getPositionXNextFrameRightSide() > obstacleObject.getObjectXPosition() && ballObject.getObjectXPosition() < obstacleObject.getObjectXPosition()) {
                ballObject.invertDeltaX();
                deleteObject = true;
            }
        }

        //Prüfe die Kollision der oberen & unteren "Seite" des Objekts, invertiere die Y-Beschleunigung wenn eine Kollision auftritt.
        if (ballObject.getObjectXPosition() < obstacleObject.getObjectWidthX() && ballObject.getObjectWidthX() > obstacleObject.getObjectXPosition()) {

            if (ballObject.getPositionYNextFrameUpperSide() + Constants.COLLISION_FIX < obstacleObject.getObjectHeightY() && ballObject.getObjectYPosition() > obstacleObject.getObjectYPosition() || ballObject.getPositionYNextFrameBottomSide() + Constants.COLLISION_FIX > obstacleObject.getObjectYPosition() && ballObject.getObjectYPosition() < obstacleObject.getObjectYPosition()) {
                ballObject.invertDeltaY();
                deleteObject = true;
            }
        }
        return deleteObject;
    }
}
