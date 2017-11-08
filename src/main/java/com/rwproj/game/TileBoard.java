package com.rwproj.game;

import javafx.scene.layout.Pane;

public class TileBoard extends Pane {

    private Tile[][] tiles;

    public Tile getTile(int column, int row) {
        return tiles[column][row];
    }

    public TileBoard(int columns, int rows, int tileSizeX, int tileSizeY) {
        tiles = new Tile[columns][rows];
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                Tile tile = new Tile(x, y, tileSizeX, tileSizeY);
                tiles[x][y] = tile;
                getChildren().add(tile);
                tile.getStyleClass().add("tile");
            }
        }
    }

    public void resetTiles() {
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                Tile tile = tiles[x][y];
                if (tile.hasDisc()) {
                    tile.reset();
                }
            }
        }
    }

}
