package com.rwproj.game;

import javafx.scene.paint.Color;


public class Side {

    public enum SideNum {
        SIDE1,
        SIDE2;
    }

    private SideNum sideNum;

    private Color color;

    private String displayName;

    public Side(SideNum sideNum, String displayName, Color color) {
        this.sideNum = sideNum;
        this.color = color;
        this.displayName = displayName;
    }

    public Color getColor() {
        return color;
    }

    public SideNum getSideNum() {
        return sideNum;
    }

    public String getDisplayName() {
        return displayName;
    }
}
