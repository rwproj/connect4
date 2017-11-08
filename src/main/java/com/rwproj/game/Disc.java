package com.rwproj.game;

import com.rwproj.game.Side.SideNum;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

class Disc extends Circle {

    private SideNum sideNum;

    public Disc(Side side) {
        this(side.getSideNum(), side.getColor());
    }

    public Disc(SideNum sideNum, Color color) {
        super(Main.TILE_SIZE / 2 - 5, color);
        this.sideNum = sideNum;

        setCenterX(Main.TILE_SIZE / 2);
        setCenterY(Main.TILE_SIZE / 2);

        Light.Distant light = new Light.Distant();
        light.setElevation(45.0);
        Lighting lighting = new Lighting();
        lighting.setSurfaceScale(2.0);
        lighting.setLight(light);
        setEffect(lighting);
    }

    public SideNum getSideNum() {
        return this.sideNum;
    }

    public void changeToSide(Side side) {
        this.sideNum = side.getSideNum();
        setFill(side.getColor());
    }

}
