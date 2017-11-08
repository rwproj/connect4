package com.rwproj.game;

import com.rwproj.game.Side.SideNum;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

import static com.rwproj.game.Main.*;

public class SideChips extends AnchorPane {

    private Side side;

    private ArrayList<Circle> chips = new ArrayList<>();

    public SideChips(SideNum sideNum, String sideName, Color color, double offset) {
        this.side = new Side(sideNum, sideName, color);
        for (int i = 0; i < COLUMNS * ROWS / 2; i++) {
            Circle chip = new Circle(TILE_SIZE / 2, TILE_SIZE / 2, TILE_SIZE / 2, color);
            Light.Distant light = new Light.Distant();
            light.setElevation(45.0);
            Lighting lighting = new Lighting();
            lighting.setSurfaceScale(2.0);
            lighting.setLight(light);
            chip.setEffect(lighting);
            this.chips.add(chip);
            this.getChildren().add(chip);
            AnchorPane.setTopAnchor(chip, (double) (i * 15));
        }

    }

    public void removeChip() {
        Circle lastChip = chips.get(chips.size() - 1);
        this.getChildren().remove(lastChip);
        chips.remove(lastChip);
    }

    public Side getSide() {
        return side;
    }

    public String getDisplayName() {
        return this.side.getDisplayName();
    }


}
