package com.rwproj.game;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;


public class Tile extends StackPane {

    private Disc disc;

    public Tile(int x, int y, int tileSizeX, int tileSizeY) {
        this.setWidth(tileSizeX);
        this.setHeight(tileSizeY);
        this.relocate(x * tileSizeX, y * tileSizeY);
        Shape rectangle = new Rectangle(x * tileSizeX, y * tileSizeY, tileSizeX, tileSizeY);
        rectangle.setStroke(Color.WHEAT);
        rectangle.setFill(Color.BLUE);

        Circle circle = new Circle(tileSizeX / 2 - 5);
        circle.setCenterX(tileSizeX / 2);
        circle.setCenterY(tileSizeX / 2);
        circle.setTranslateX(x * (tileSizeX));
        circle.setTranslateY(y * (tileSizeX));

        circle.setFill(Color.color(0, 0, 0, 0));
        circle.setStroke(Color.WHEAT);


        rectangle = Shape.subtract(rectangle, circle);
        rectangle.setFill(Color.GREEN);

        this.getChildren().add(rectangle);

    }

    public void placeDisc(Disc disc) {
        this.disc = disc;
        getChildren().add(disc);

    }

    public Disc getDisc() {
        return this.disc;
    }

    public boolean hasDisc() {
        return this.disc != null;
    }

    public void reset() {
        this.getChildren().remove(this.disc);
        this.disc = null;
    }
}
