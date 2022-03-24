package client.gui;

import client.model.GameEngine;
import client.model.Player;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.HashMap;

public class GameScene {
    private GameEngine gameEngine;
    private Scene scene;

    // --- Graphics ---
    private static Image image_floor;
    private static Image image_wall;
    private static HashMap<String, Image> avatars;
    private static HashMap<String, Image> icons;
    private final static String[] colors = {"blue", "purple", "red", "white", "yellow"};

    private Label[][] fields;
    private GridPane scoreMainGrid;
    private HashMap<Integer, Label> scoreValues;

    // Board params
    private static final int TILE_SIZE = 20; // Pixel size of graphics.
    private final static int BOARD_NUM_COLS = 20;
    private final static int BOARD_NUM_ROWS = 20;

    // -------------------------------------------
    // | Maze: (0,0)              | Score: (1,0) |
    // |-----------------------------------------|
    // | boardGrid (0,1)          | scorelist    |
    // |                          | (1,1)        |
    // -------------------------------------------

    public GameScene(GameEngine gameEngine, String[] board) {

        try {
            this.gameEngine = gameEngine;

            GameScene.avatars = new HashMap<>();
            GameScene.icons = new HashMap<>();
            this.scoreValues = new HashMap<>();

            GridPane masterGrid = new GridPane();
            this.scene = new Scene(masterGrid);
            masterGrid.getStylesheets().add("/client/gui/gameScene.css");
            masterGrid.getStyleClass().add("masterGrid");
            masterGrid.setHgap(10);
            masterGrid.setVgap(10);
            masterGrid.setPadding(new Insets(10));
            masterGrid.setGridLinesVisible(false);

            Text mazeLabel = new Text("Maze:");
            mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            Text scoreLabel = new Text("Score:");
            scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));


            // Load graphics
            String imageDir = "images/";
            image_wall = new Image(
                    getClass().getResourceAsStream(imageDir + "wall4.png"), TILE_SIZE, TILE_SIZE, false, false
            );
            image_floor = new Image(
                    getClass().getResourceAsStream(imageDir + "floor1.png"), TILE_SIZE, TILE_SIZE, false, false
            );

            // Avatars
            String[] directions = {"up", "down", "left", "right"};
            for (String color : GameScene.colors) {
                for (String direction : directions) {
                    String filename = "hero_" + color + "_" + direction + ".png";
                    GameScene.avatars.put(
                            color + "_" + direction,
                            new Image(
                                    getClass().getResourceAsStream(imageDir + filename), TILE_SIZE, TILE_SIZE, false, false
                            )
                    );
                }
            }

            // Icons
            //double ratio = 293.0 / 263.0;
            double ratio = 263.0 / 293.0;
            int icon_height = (int) Math.round(((TILE_SIZE * TILE_SIZE) - (4 * 10) - 40) / 5.0); // Src: 263px.
            int icon_width = (int) Math.round(icon_height * ratio); // Src: 293px.
            for (String color : GameScene.colors) {
                String filename = "icon_" + color + ".png";
                GameScene.icons.put(
                        color,
                        new Image(
                                getClass().getResourceAsStream(imageDir + filename), icon_width, icon_height, false, true
                        )
                );

            }

            // Create and populate boardGrid
            GridPane boardGrid = new GridPane();
            fields = new Label[BOARD_NUM_COLS][BOARD_NUM_ROWS];
            for (int row = 0; row < BOARD_NUM_ROWS; row++) {
                for (int col = 0; col < BOARD_NUM_COLS; col++) {
                    switch (board[row].charAt(col)) {
                        case 'w':
                            fields[col][row] = new Label("", new ImageView(image_wall));
                            break;
                        case ' ':
                            fields[col][row] = new Label("", new ImageView(image_floor));
                            break;
                        default:
                            throw new Exception("Illegal field value: " + board[row].charAt(col));
                    }
                    boardGrid.add(fields[col][row], col, row);
                }
            }

            // Create score mainGrid
            this.scoreMainGrid = new GridPane();
            this.scoreMainGrid.setGridLinesVisible(false);
            this.scoreMainGrid.setHgap(10);
            this.scoreMainGrid.setVgap(10);
            this.scoreMainGrid.getStyleClass().add("scoreGrid");
            int row = 0;

            // Make sure this player is first
            HashMap<Integer, Player> players = this.gameEngine.getPlayers();
            int selfId = this.gameEngine.getPlayerId();
            GridPane scorePlayerGrid = this.createScorePlayerGrid(players.get(selfId));
            this.scoreMainGrid.add(scorePlayerGrid, 0, row);
            row++;

            for (Player p : players.values()) {
                if (p.getPlayerId() != selfId) {
                    scorePlayerGrid = this.createScorePlayerGrid(p);
                    this.scoreMainGrid.add(scorePlayerGrid, 0, row);
                    row++;
                }
            }

            // Add elements to main grid
            masterGrid.add(mazeLabel, 0, 0);
            masterGrid.add(scoreLabel, 1, 0);
            masterGrid.add(boardGrid, 0, 1);
            masterGrid.add(this.scoreMainGrid, 1, 1);

            // Add eventlisteners to scene.
            this.scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                switch (event.getCode()) {
                    case UP:
                        this.gameEngine.requestMove(0, -1, "up");
                        break;
                    case DOWN:
                        this.gameEngine.requestMove(0, +1, "down");
                        break;
                    case LEFT:
                        this.gameEngine.requestMove(-1, 0, "left");
                        break;
                    case RIGHT:
                        this.gameEngine.requestMove(+1, 0, "right");
                        break;
                    default:
                        break;
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Scene getScene() {
        return this.scene;
    }

    public void updatePlayers() {
        HashMap<Integer, Player> players = this.gameEngine.getPlayers();
        for (Player p : players.values()) {
            this.updatePlayer(p);
        }
    }

    public void updatePlayer(Player p) {
        int posX = p.getPosX();
        int posY = p.getPosY();

        // Set current player grid-pos to display floor tile.
        if (p.getPrevPosX() != -1 && p.getPrevPosY() != -1) {
            fields[p.getPrevPosX()][p.getPrevPosY()].setGraphic(new ImageView(image_floor));
        }

        // Prepare color and direction
        String color = p.getColor();
        String direction = p.getDirection();
        // Paint
        fields[posX][posY].setGraphic(new ImageView(avatars.get(color + "_" + direction)));
    }

    public void updateScore() {
        for (Player p : this.gameEngine.getPlayers().values()) {
            this.scoreValues.get(p.getPlayerId()).setText(String.valueOf(p.getPoints()));
        }
    }

    private GridPane createScorePlayerGrid(Player p) {
        // Create a grid per player
        GridPane scorePlayerGrid = new GridPane();
        scorePlayerGrid.setGridLinesVisible(false);
        scorePlayerGrid.setHgap(10);
        scorePlayerGrid.setVgap(0);

        RowConstraints normal = new RowConstraints();
        RowConstraints grow = new RowConstraints();
        grow.setVgrow(Priority.ALWAYS);
        scorePlayerGrid.getRowConstraints().addAll(normal, grow, normal);

        Label lblIcon = new Label("", new ImageView(GameScene.icons.get(p.getColor())));
        scorePlayerGrid.add(lblIcon, 0, 0, 1, 3);

        Label lblName = new Label(p.getPlayerName());
        lblName.getStyleClass().add("lblName");
        scorePlayerGrid.add(lblName, 1, 0);

        Label lblScore = new Label("Score:");
        lblName.getStyleClass().add("lblScore");
        scorePlayerGrid.add(lblScore, 1, 1);
        GridPane.setValignment(lblScore, VPos.BOTTOM);

        Label lblScoreValue = new Label(String.valueOf(p.getPoints()));
        lblScoreValue.getStyleClass().add("lblScoreValue");
        scorePlayerGrid.add(lblScoreValue, 1, 2);
        this.scoreValues.put(p.getPlayerId(), lblScoreValue);

        return scorePlayerGrid;
    }


}
