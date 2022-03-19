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

            // *** Setting up players *************************************
            //me = new Player(1, "Orville", 9, 4, "up");
            //players.add(me);
            //fields[9][4].setGraphic(new ImageView(hero_up));
            //
            //Player harry = new Player(2, "Harry", 14, 15, "up");
            //players.add(harry);
            //fields[14][15].setGraphic(new ImageView(hero_up));

            // ************************************************************


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Scene getScene() {
        return this.scene;
    }


    public void updateScore() {
        StringBuffer b = new StringBuffer(100);
        for (Player p : this.gameEngine.getPlayers().values()) {
            b.append(p.getName() + ": " + p.getPoints() + "\r\n");
            System.out.println(p.getName() + ": " + p.getPoints() + "\r\n");
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
        int x = me.getXpos(), y = me.getYpos();

        // --- Game logic ---------------------------------
        if (board[y + delta_y].charAt(x + delta_x) == 'w') {
            // --- Destination pos is a wall ---
            // Ignore move request and award points.
            me.addPoints(-1);
        } else {
            // Get any player that may occupy destination pos.
            Player p = getPlayerAt(x + delta_x, y + delta_y);

            if (p != null) {
                // --- Destination pos is occupied ---
                // Ignore move request and award points.
                me.addPoints(10);
                p.addPoints(-10);
            } else {
                // --- Destination pos is a floor tile ---
                // Move is allowed.

                // Award points.
                me.addPoints(1);

                // Set current player grid-pos to display floor tile.
                fields[x][y].setGraphic(new ImageView(image_floor));

                // Calc player position post-move.
                x += delta_x;
                y += delta_y;

                // Update playerObj.
                me.setXpos(x);
                me.setYpos(y);

            }
        }
        // ------------------------------------------------


        // Update player grid-pos and icon orientation
        if (direction.equals("right")) {
            fields[x][y].setGraphic(new ImageView(hero_right));
        } else if (direction.equals("left")) {
            fields[x][y].setGraphic(new ImageView(hero_left));
        } else if (direction.equals("up")) {
            fields[x][y].setGraphic(new ImageView(hero_up));
        } else if (direction.equals("down")) {
            fields[x][y].setGraphic(new ImageView(hero_down));
        }


    }


    /**
     * Returns player at passed grid-pos (if any), or null.
     *
     * @param x
     * @param y
     * @return
     */
    private Player getPlayerAt(int x, int y) {
        for (Player p : players) {
            if (p.getXpos() == x && p.getYpos() == y) {
                return p;
            }
        }
        return null;
    }


}
