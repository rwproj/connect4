package com.rwproj.game;

import com.rwproj.game.Side.SideNum;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.rwproj.game.Side.SideNum.SIDE1;
import static com.rwproj.game.Side.SideNum.SIDE2;


public class Main extends Application {

    public static final int BOARD_COLUMN_PERCENT = 70;
    //number of tiles vertically
    public static final int COLUMNS = 7;
    //number of tiles horizontally
    public static final int ROWS = 6;
    //size of each tile
    public static final int TILE_SIZE = 70;

    public static final int SELECT_MOVE_PANE_HEIGHT;

    private TileBoard tileBoard;

    private HBox chipsPane;

    private SideChips side1Chips;

    private SideChips side2Chips;

    private Disc discPlacer;

    private SideNum currentMove = SIDE1;

    private boolean isAnimationInProcess = false;

    private Color[] sideColors = {Color.RED, Color.YELLOW};

    static {
        SELECT_MOVE_PANE_HEIGHT = TILE_SIZE * ROWS * (100 - BOARD_COLUMN_PERCENT) / BOARD_COLUMN_PERCENT;
    }


    @Override
    public void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle("Connect 4");
        Pane root = createPanes();
        Scene scene = new Scene(new Group(root));
        scene.getStylesheets().add(getClass().getResource("/root.css").toExternalForm());
        TranslateTransition discPlacerAnimation = new TranslateTransition();
        discPlacerAnimation.setNode(discPlacer);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT && !isAnimationInProcess) {
                if (Animation.Status.RUNNING == discPlacerAnimation.getStatus()) {
                    discPlacerAnimation.setRate(4);
                } else {
                    discPlacerAnimation.setRate(1);
                    discPlacerAnimation.setDuration(Duration.millis(200));
                    Double x = discPlacer.getTranslateX() - TILE_SIZE / 2;
                    if (discPlacer.getTranslateX() - TILE_SIZE / 2 >= 0) {
                        discPlacerAnimation.setToX(x);
                        discPlacerAnimation.play();
                    } else {
                        discPlacer.setTranslateX(TILE_SIZE * (COLUMNS - 1));
                    }
                }
            } else if (e.getCode() == KeyCode.RIGHT && !isAnimationInProcess) {
                if (Animation.Status.RUNNING == discPlacerAnimation.getStatus()) {
                    discPlacerAnimation.setRate(4);
                } else {
                    discPlacerAnimation.setRate(1);
                    discPlacerAnimation.setDuration(Duration.millis(200));
                    if (discPlacerAnimation.getNode() == null) {
                        discPlacerAnimation.setNode(discPlacer);
                    }
                    Double x = discPlacer.getTranslateX() + TILE_SIZE / 2;
                    if ((discPlacer.getTranslateX() + TILE_SIZE / 2) <= TILE_SIZE * (COLUMNS - 1)) {
                        discPlacerAnimation.setToX(x);
                        discPlacerAnimation.play();
                    } else {
                        discPlacer.setTranslateX(0);
                    }
                }
            } else if (e.getCode() == KeyCode.DOWN && !isAnimationInProcess) {
                if (Animation.Status.RUNNING != discPlacerAnimation.getStatus() && discPlacer.getTranslateX() % TILE_SIZE == 0) {
                    placeDisc((int) (discPlacer.getTranslateX() / TILE_SIZE));
                }
            }

        });

        primaryStage.setScene(scene);
        primaryStage.show();

        ScaleService.letterbox(scene, root);

    }

    public StackPane createPanes() {
        StackPane root0 = new StackPane();
        root0.setId("bgPane");
        GridPane root = new GridPane();
        root.setMaxSize(TILE_SIZE * COLUMNS * 100 / BOARD_COLUMN_PERCENT, TILE_SIZE * ROWS * 100 / BOARD_COLUMN_PERCENT);
        root0.getChildren().add(root);
        //root.setGridLinesVisible(true);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(BOARD_COLUMN_PERCENT);
        ColumnConstraints columnConstraints1 = new ColumnConstraints();
        columnConstraints1.setPercentWidth(100 - BOARD_COLUMN_PERCENT);
        root.getColumnConstraints().addAll(columnConstraints, columnConstraints1);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPercentHeight(BOARD_COLUMN_PERCENT);
        RowConstraints rowConstraints1 = new RowConstraints();
        rowConstraints.setPercentHeight(100 - BOARD_COLUMN_PERCENT);
        root.getRowConstraints().addAll(rowConstraints, rowConstraints1);

        Pane selectMovePane = new Pane();
        root.add(selectMovePane, 0, 0);
        selectMovePane.toFront();

        discPlacer = nextTurn();
        selectMovePane.getChildren().add(discPlacer);

        Pane selectSide = new Pane();
        root.add(selectSide, 1, 0);

        chipsPane = new HBox();
        root.add(chipsPane, 1, 1);

        side1Chips = new SideChips(SIDE1, "RED", sideColors[0], 0);
        side2Chips = new SideChips(SIDE2, "YELLOW", sideColors[1], 0);
        chipsPane.getChildren().addAll(side1Chips, side2Chips);
        removeChipFromStack();
        chipsPane.setAlignment(Pos.CENTER);
        chipsPane.setAlignment(Pos.CENTER);
        chipsPane.setSpacing(15);

        tileBoard = new TileBoard(COLUMNS, ROWS, TILE_SIZE, TILE_SIZE);

        tileBoard.setPrefSize(TILE_SIZE * COLUMNS, TILE_SIZE * ROWS);

        root.add(tileBoard, 0, 1);
        tileBoard.toBack();

        return root0;
    }


    private Optional<Disc> getDisc(int column, int row) {
        if (column < 0 || column >= COLUMNS
                || row < 0 || row >= ROWS)
            return Optional.empty();

        return Optional.ofNullable(tileBoard.getTile(column, row).getDisc());
    }

    private boolean gameEnded(int column, int row) {
        List<Point2D> vertical = IntStream.rangeClosed(row - 3, row + 3)
                .mapToObj(r -> new Point2D(column, r))
                .collect(Collectors.toList());

        List<Point2D> horizontal = IntStream.rangeClosed(column - 3, column + 3)
                .mapToObj(c -> new Point2D(c, row))
                .collect(Collectors.toList());

        Point2D topLeft = new Point2D(column - 3, row - 3);
        List<Point2D> diagonal1 = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> topLeft.add(i, i))
                .collect(Collectors.toList());

        Point2D botLeft = new Point2D(column - 3, row + 3);
        List<Point2D> diagonal2 = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> botLeft.add(i, -i))
                .collect(Collectors.toList());


        return checkRange(vertical) || checkRange(horizontal)
                || checkRange(diagonal1) || checkRange(diagonal2);
    }

    private boolean checkRange(List<Point2D> points) {
        int chain = 0;

        for (Point2D p : points) {
            int column = (int) p.getX();
            int row = (int) p.getY();

            Disc disc = getDisc(column, row).orElse(null);
            if (disc != null && currentMove == disc.getSideNum()) {
                chain++;
                if (chain == 4) {
                    return true;
                }
            } else {
                chain = 0;
            }
        }

        return false;
    }


    private void gameOver() {
        System.out.println("Winner: " + (SIDE1 == currentMove ? "RED" : "YELLOW"));
        ButtonType okButton = new ButtonType("OK", ButtonData.OK_DONE);
        ButtonType exitButton = new ButtonType("EXIT", ButtonData.FINISH);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setContentText("Winner: " + getCurrentSide().getDisplayName() + "\n Start New Game?");
        dialog.getDialogPane().getButtonTypes().add(okButton);
        dialog.getDialogPane().getButtonTypes().add(exitButton);
        Platform.runLater(() -> {
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == okButton) {
                resetGame();
            }
            if (result.isPresent() && result.get() == exitButton) {
                System.exit(0);
            }
        });
    }

    /**
     * resetting field, switching sides
     */
    private void resetGame() {
        tileBoard.resetTiles();
        isAnimationInProcess = true;
        chipsPane.getChildren().removeAll(side1Chips, side2Chips);
        Side side1 = side1Chips.getSide();
        Side side2 = side2Chips.getSide();
        side1Chips = new SideChips(SIDE1, side2.getDisplayName(), side2.getColor(), 0);
        side2Chips = new SideChips(SIDE2, side1.getDisplayName(), side1.getColor(), 0);
        chipsPane.getChildren().addAll(side1Chips, side2Chips);

        //@see #nextTurn() will switch currentMove to opposite side and we need it to be SIDE1 in the end, so have thus setting it here to SIDE2
        // TODO: refactor
        currentMove = SIDE2;
        nextTurn();

        isAnimationInProcess = false;
    }

    private void placeDisc(int column) {
        int row = ROWS - 1;
        do {
            if (!getDisc(column, row).isPresent())
                break;

            row--;
        } while (row >= 0);

        if (row < 0) return;


        final int currentRow = row;


        Tile destTile = tileBoard.getTile(column, row);

        TranslateTransition animation = new TranslateTransition(Duration.seconds(1), discPlacer);
        animation.setByY(destTile.getLayoutY() + SELECT_MOVE_PANE_HEIGHT - discPlacer.getTranslateY());
        animation.setOnFinished(e -> {
            isAnimationInProcess = false;
            Disc placedDisc = new Disc(getCurrentSide());
            destTile.placeDisc(placedDisc);
            if (gameEnded(column, currentRow)) {
                gameOver();
            } else {
                nextTurn();
            }
        });
        animation.play();
        isAnimationInProcess = true;
    }

    private Side getCurrentSide() {
        return currentMove == side1Chips.getSide().getSideNum() ? side1Chips.getSide() : side2Chips.getSide();
    }

    private Disc nextTurn() {
        if (discPlacer == null) {
            discPlacer = new Disc(currentMove, sideColors[0]);
            discPlacer.setBlendMode(BlendMode.SRC_OVER);
        } else {
            discPlacer.changeToSide(currentMove == side1Chips.getSide().getSideNum() ? side2Chips.getSide() : side1Chips.getSide());
            currentMove = SIDE1 == currentMove ? SIDE2 : SIDE1;
            removeChipFromStack();
        }

        discPlacer.setTranslateX(TILE_SIZE * COLUMNS / 2 - TILE_SIZE / 2);
        discPlacer.setTranslateY(TILE_SIZE * ROWS * (100 - BOARD_COLUMN_PERCENT) / BOARD_COLUMN_PERCENT / 2 - TILE_SIZE / 2);
        return discPlacer;
    }

    private void removeChipFromStack() {
        SideChips sideChips = SIDE1 == this.currentMove ? side1Chips : side2Chips;
        sideChips.removeChip();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
