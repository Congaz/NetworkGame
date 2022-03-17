package game2022;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.*;

public class GUI extends Application {

	private static GameEngine gameEngine;

	private static final int size = 20;
	private static final int scene_height = size * 20 + 100;
	private static final int scene_width = size * 20 + 200;

	private static Stage stage;

	private static Player me;
	private static List<Player> players = new ArrayList<Player>();

	// --- Start scene elements ---
	private TextField txtfIp;
	private TextField txtfName;

	// --- Game scene elements ---
	private static Image image_floor;
	private static Image image_wall;
	private static Image hero_right, hero_left, hero_up, hero_down;

	private Label[][] fields;
	private TextArea scoreList;
	
	private  String[] board = {    // 20x20
			"wwwwwwwwwwwwwwwwwwww",
			"w        ww        w",
			"w w  w  www w  w  ww",
			"w w  w   ww w  w  ww",
			"w  w               w",
			"w w w w w w w  w  ww",
			"w w     www w  w  ww",
			"w w     w w w  w  ww",
			"w   w w  w  w  w   w",
			"w     w  w  w  w   w",
			"w ww ww        w  ww",
			"w  w w    w    w  ww",
			"w        ww w  w  ww",
			"w         w w  w  ww",
			"w        w     w  ww",
			"w  w              ww",
			"w  w www  w w  ww ww",
			"w w      ww w     ww",
			"w   w   ww  w      w",
			"wwwwwwwwwwwwwwwwwwww"
	};

	
	// -------------------------------------------
	// | Maze: (0,0)              | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1)          | scorelist    |
	// |                          | (1,1)        |
	// -------------------------------------------

	@Override
	public void start(Stage stage) {

		GUI.gameEngine = new GameEngine();


		// Set-up stage
		GUI.stage = stage;
		GUI.stage.setTitle("The network game!");
		GUI.stage.setResizable(false);
		//Gui.stage.setMinWidth(1100);
        //Gui.stage.setMinHeight(400);
        GUI.stage.setWidth(GUI.scene_width);
        GUI.stage.setHeight(GUI.scene_height);
        GUI.stage.setFullScreen(false);

		// Create start scene
		GUI.stage.setScene(this.createConnectScene());


		// Create game scene
		//GUI.stage.setScene(this.createGameScene());

		// Display stage.
		GUI.stage.show();



	}

	private Scene createConnectScene() {
		GridPane mainGrid = new GridPane();
        Scene scene = new Scene(mainGrid);
		mainGrid.setHgap(10);
		mainGrid.setVgap(10);
		mainGrid.setPadding(new Insets(20));
		mainGrid.getStylesheets().add("/game2022/sceneStart.css");
		mainGrid.getStyleClass().add("mainGrid");
        mainGrid.setGridLinesVisible(false);
		mainGrid.setAlignment(Pos.TOP_CENTER);

		RowConstraints rowA = new RowConstraints();
		RowConstraints rowB = new RowConstraints();
		//rowA.setVgrow(Priority.NEVER);
		//rowB.setVgrow(Priority.ALWAYS);
       	rowB.setPrefHeight(60);

        mainGrid.getRowConstraints().addAll(rowA, rowA, rowB);

		// --- Game server IP ---
		Label lblIp = new Label("Game Server IP: ");
		mainGrid.add(lblIp, 0, 0);
		this.txtfIp = new TextField();
        mainGrid.add(this.txtfIp, 1, 0);

		// --- Player name ---
		Label lblName = new Label("Player name: ");
		mainGrid.add(lblName, 0, 1);
		this.txtfName = new TextField();
        mainGrid.add(this.txtfName, 1, 1);

		// --- Connect btn ---
		Button btnConnect = new Button("Connect");
		btnConnect.getStyleClass().add("btnConnect");
		mainGrid.add(btnConnect, 0, 2, 2, 1);
        btnConnect.setOnAction(event -> this.connectAction());
		GridPane.setHalignment(btnConnect, HPos.CENTER);

		return scene;
	}

	private void connectAction() {
		String serverIp = this.txtfIp.getText().trim();
		String playerName = this.txtfName.getText().trim();

		try {
			GUI.gameEngine.connect(serverIp, playerName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @return
	 */
	private Scene createGameScene() {
		Scene scene = null;

		try {
			GridPane grid = new GridPane();
			scene = new Scene(grid);
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(0, 10, 0, 10));

			Text mazeLabel = new Text("Maze:");
			mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			Text scoreLabel = new Text("Score:");
			scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			scoreList = new TextArea();
			scoreList.setEditable(false);

			// Load graphics
			image_wall  = new Image(getClass().getResourceAsStream("Image/wall4.png"),size,size,false,false);
			image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"),size,size,false,false);

			hero_right  = new Image(getClass().getResourceAsStream("Image/heroRight.png"),size,size,false,false);
			hero_left   = new Image(getClass().getResourceAsStream("Image/heroLeft.png"),size,size,false,false);
			hero_up     = new Image(getClass().getResourceAsStream("Image/heroUp.png"),size,size,false,false);
			hero_down   = new Image(getClass().getResourceAsStream("Image/heroDown.png"),size,size,false,false);

			// Create and populate boardGrid
			GridPane boardGrid = new GridPane();
			fields = new Label[20][20];
			for (int j = 0; j < 20; j++) {
				for (int i= 0; i < 20; i++) {
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
			grid.add(mazeLabel,  0, 0);
			grid.add(scoreLabel, 1, 0);
			grid.add(boardGrid,  0, 1);
			grid.add(scoreList,  1, 1);

			// Add eventlisteners to scene.
			scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				switch (event.getCode()) {
					case UP:    playerMoved(0, -1, "up");    break;
					case DOWN:  playerMoved(0 , +1, "down");  break;
					case LEFT:  playerMoved(-1, 0, "left");  break;
					case RIGHT: playerMoved(+1, 0, "right"); break;
					default: break;
				}
			});

            // *** Setting up players *************************************
			//me = new Player("Orville", 9, 4, "up");
			//players.add(me);
			//fields[9][4].setGraphic(new ImageView(hero_up));
			//
			//Player harry = new Player("Harry", 14, 15, "up");
			//players.add(harry);
			//fields[14][15].setGraphic(new ImageView(hero_up));

			// ************************************************************

			// Populate scorelist.
			scoreList.setText(getScoreList());


		} catch(Exception e) {
			e.printStackTrace();
		}

		return scene;
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
		} 
		else {
			// Get any player that may occupy destination pos.
			Player p = getPlayerAt(x + delta_x, y + delta_y);

			if (p != null) {
				// --- Destination pos is occupied ---
				// Ignore move request and award points.
				me.addPoints(10);
				p.addPoints(-10);
			}
			else {
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

		// Update scorelist
		scoreList.setText(getScoreList());
	}

	/**
	 * Generate and return scorelist as string.
	 * @return
	 */
	private String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (Player p : players) {
			b.append(p + "\r\n");
		}
		return b.toString();
	}

	/**
	 * Returns player at passed grid-pos (if any), or null.
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

