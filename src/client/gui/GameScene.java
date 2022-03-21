package client.gui;

import client.model.GameEngine;
import client.model.Player;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameScene {
    private GameEngine gameEngine;
    private static final int size = 20; // Pixel size of graphics.

    private static Image image_floor;
    private static Image image_wall;
    private static Image hero_right, hero_left, hero_up, hero_down;

    private Label[][] fields;
    private TextArea scoreList;

    private Scene scene;

    private static Player me;
    private static List<Player> players = new ArrayList<Player>();

    private String[] board;


    // -------------------------------------------
    // | Maze: (0,0)              | Score: (1,0) |
    // |-----------------------------------------|
    // | boardGrid (0,1)          | scorelist    |
    // |                          | (1,1)        |
    // -------------------------------------------

    public GameScene(GameEngine gameEngine, String[] board) {

        try {
            this.gameEngine = gameEngine;

            GridPane grid = new GridPane();
            this.scene = new Scene(grid);
            grid.getStylesheets().add("/client/gui/gameScene.css");
            grid.getStyleClass().add("grid");
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(10));
            grid.setGridLinesVisible(false);

            Text mazeLabel = new Text("Maze:");
            mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            Text scoreLabel = new Text("Score:");
            scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            scoreList = new TextArea();
            scoreList.getStyleClass().add("scoreList");
            scoreList.setEditable(false);

            // Load graphics
            String imageDir = "images/";
            image_wall = new Image(getClass().getResourceAsStream(imageDir + "wall4.png"), size, size, false, false);
            image_floor = new Image(getClass().getResourceAsStream(imageDir + "floor1.png"), size, size, false, false);

            hero_right = new Image(getClass().getResourceAsStream(imageDir + "heroRight.png"), size, size, false, false);
            hero_left = new Image(getClass().getResourceAsStream(imageDir + "heroLeft.png"), size, size, false, false);
            hero_up = new Image(getClass().getResourceAsStream(imageDir + "heroUp.png"), size, size, false, false);
            hero_down = new Image(getClass().getResourceAsStream(imageDir + "heroDown.png"), size, size, false, false);

            // Create and populate boardGrid
            GridPane boardGrid = new GridPane();
            fields = new Label[20][20];
            for (int j = 0; j < 20; j++) {
                for (int i = 0; i < 20; i++) {
                    switch (board[j].charAt(i)) {
                        case 'w':
                            fields[i][j] = new Label("", new ImageView(image_wall));
                            break;
                        case ' ':
                            fields[i][j] = new Label("", new ImageView(image_floor));
                            break;
                        default:
                            throw new Exception("Illegal field value: " + board[j].charAt(i));
                    }
                    boardGrid.add(fields[i][j], i, j);
                }
            }

            // Add elements to main grid
            grid.add(mazeLabel, 0, 0);
            grid.add(scoreLabel, 1, 0);
            grid.add(boardGrid, 0, 1);
            grid.add(scoreList, 1, 1);

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
        } catch (Exception e) {
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
        String direction = p.getDirection();

        // Set current player grid-pos to display floor tile.
        fields[p.getPrevPosX()][p.getPrevPosY()].setGraphic(new ImageView(image_floor));

        // Update player grid-pos and icon orientation
        switch (direction) {
            case "right":
                fields[posX][posY].setGraphic(new ImageView(hero_right));
                break;
            case "left":
                fields[posX][posY].setGraphic(new ImageView(hero_left));
                break;
            case "up":
                fields[posX][posY].setGraphic(new ImageView(hero_up));
                break;
            case "down":
                fields[posX][posY].setGraphic(new ImageView(hero_down));
                break;
        }

    }


    public void updateScore() {
        StringBuffer b = new StringBuffer(100);
        for (Player p : this.gameEngine.getPlayers().values()) {
            b.append(p.getPlayerName() + ": " + p.getPoints() + "\r\n");
            System.out.println(p.getPlayerName() + ": " + p.getPoints() + "\r\n");
        }

        // Populate scorelist.
        scoreList.setText(b.toString());
    }

    private void requestMove(int delta_x, int delta_y, String direction) {
        System.out.println("MOVED!");

    }

    private void playerMoved(int delta_x, int delta_y, String direction) {
        // Update playerObj direction
        me.setDirection(direction);
        // Get pre-move player position.
        int x = me.getPosX(), y = me.getPosY();







    }





}
