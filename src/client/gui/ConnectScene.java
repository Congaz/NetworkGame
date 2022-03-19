package client.gui;

import client.model.GameEngine;
import client.model.Player;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.util.HashMap;

public class ConnectScene {

    private GameEngine gameEngine;

    // --- Input grid elements ---
    private TextField txtfIp;
	private TextField txtfName;
	private Button btnConnect;
	private Button btnStart;

	// --- Message grid elements ---
	private GridPane messageGrid;
	private Label lblMessage;
	private Label lblValue;

	// --- Players grid elements ---
	private GridPane playersGrid;

    public ConnectScene(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public Scene createScene() {
		// --- Master Grid ---
        GridPane masterGrid = new GridPane();
        Scene scene = new Scene(masterGrid);
		masterGrid.setHgap(10);
		masterGrid.setVgap(10);
		masterGrid.setPadding(new Insets(0));
		masterGrid.getStylesheets().add("/client/gui/connectScene.css");
		masterGrid.getStyleClass().add("masterGrid");
        masterGrid.setGridLinesVisible(false);
		masterGrid.setAlignment(Pos.TOP_CENTER);

		// --- Add sub-grids ---
		masterGrid.add(this.createInputGrid(), 0, 0);
		this.messageGrid = this.createMessageGrid();
		masterGrid.add(this.messageGrid, 0, 1);
		this.playersGrid = this.createPlayersGrid();
		masterGrid.add(this.playersGrid, 0, 2);

		// --- RowConstraints ---
		RowConstraints rowInput = new RowConstraints();
		RowConstraints rowPlayers = new RowConstraints();

		masterGrid.getRowConstraints().addAll(rowInput, rowPlayers);

        return scene;
    }

	public void updatePlayers() {
		// Remove all nodes exept legends from players grid.
		int cols = this.playersGrid.getColumnCount();
		int size = this.playersGrid.getChildren().size();
		if (size > cols) {
			this.playersGrid.getChildren().remove(cols, size);
		}

		HashMap<Integer, Player> players = this.gameEngine.getPlayers();

		// Add row pr. player.
		for (Player p : players.values()) {
			this.addPlayerRow(p);
		}
	}

	public void updateCountdown(int countdown) {
		this.messageGrid.getStyleClass().add("countdown");
		this.lblMessage.setText("Starting game in:");
		this.lblValue.setText(" " + String.valueOf(countdown));
	}

	private GridPane createInputGrid() {
		// --- Grid ---
        GridPane grid = new GridPane();
        grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(0));
		grid.getStyleClass().add("inputGrid");
        grid.setGridLinesVisible(false);
		grid.setAlignment(Pos.TOP_CENTER);

		RowConstraints rowA = new RowConstraints();
		RowConstraints rowB = new RowConstraints();
       	rowB.setPrefHeight(60);

        grid.getRowConstraints().addAll(rowA, rowA, rowB);

		// --- Game server IP ---
		Label lblIp = new Label("Game Server IP: ");
		grid.add(lblIp, 0, 0);
		String testIp = "127.0.0.1";
		this.txtfIp = new TextField(testIp);
        grid.add(this.txtfIp, 1, 0);

		// --- Player name ---
		Label lblName = new Label("Player name: ");
		grid.add(lblName, 0, 1);
		String testName = "Mig";
		this.txtfName = new TextField(testName);
        grid.add(this.txtfName, 1, 1);

		// --- Connect btn ---
		this.btnConnect = new Button("Connect");
		this.btnConnect.getStyleClass().add("btnConnect");
		grid.add(this.btnConnect, 0, 2, 2, 1);
        this.btnConnect.setOnAction(event -> this.connectAction());
		GridPane.setHalignment(this.btnConnect, HPos.CENTER);

		// --- Start btn ---
		this.btnStart = new Button("Start Game");
		this.btnStart.getStyleClass().add("btnStart");
		grid.add(this.btnStart, 0, 3, 2, 1);
        this.btnStart.setOnAction(event -> this.readyAction());
		this.btnStart.setDisable(true);
		GridPane.setHalignment(this.btnStart, HPos.CENTER);

        return grid;
	}

	private GridPane createMessageGrid() {
		// --- Grid ---
        GridPane grid = new GridPane();
        grid.setHgap(0);
		grid.setVgap(0);
		grid.getStyleClass().add("messageGrid");
        grid.setGridLinesVisible(false);
		grid.setAlignment(Pos.TOP_CENTER);
		GridPane.setMargin(grid, new Insets(20, 0, 0, 0));

		// --- Elements ---
		this.lblMessage = new Label("Connected players");
		this.lblMessage.getStyleClass().add("lblMessage");
		grid.add(this.lblMessage, 0, 0);

		this.lblValue = new Label("");
		this.lblValue.getStyleClass().add("lblValue");
		grid.add(this.lblValue, 1, 0);

		return grid;
	}

	private GridPane createPlayersGrid() {
		// --- Grid ---
        GridPane grid = new GridPane();
        grid.setHgap(0);
		grid.setVgap(0);
		grid.getStyleClass().add("playersGrid");
        grid.setGridLinesVisible(false);
		GridPane.setMargin(grid, new Insets(10, 0, 0, 0));
		//grid.setAlignment(Pos.TOP_CENTER);

		ColumnConstraints col1 = new ColumnConstraints();
		ColumnConstraints col2 = new ColumnConstraints();
		ColumnConstraints col3 = new ColumnConstraints();

		col1.setPercentWidth(50);
		col2.setPercentWidth(25);
		col3.setPercentWidth(25);

        grid.getColumnConstraints().addAll(col1, col2, col3);

		Pane pn;
		int col = 0;
		// --- Legends --
		pn = new Pane();
		Label lblName = new Label("Player");

		pn.getChildren().add(lblName);
		pn.getStyleClass().add("legendPane");
		grid.add(pn, col++, 0);

		pn = new Pane();
		Label lblColor = new Label("Color");
		pn.getChildren().add(lblColor);
		pn.getStyleClass().add("legendPane");
		grid.add(pn, col++, 0);

		pn = new Pane();
		Label lblStart = new Label("Ready");
		pn.getChildren().add(lblStart);
		pn.getStyleClass().add("legendPane");
		grid.add(pn, col++, 0);

        return grid;
	}

	private void addPlayerRow(Player p) {
		int row = this.playersGrid.getRowCount();
		GridPane grid = this.playersGrid; // Short hand.

		Pane pn;
		int col = 0;
		String parity = row % 2 == 0 ? "Even" : "Odd";
		// --- Player info ---
		pn = new Pane();
		Label lblName = new Label(p.getName());
		pn.getChildren().add(lblName);
		pn.getStyleClass().add("playerPaneRow" + parity);
		grid.add(pn, col++, row);

		pn = new Pane();
		Label lblColor = new Label(p.getColor());
		pn.getChildren().add(lblColor);
		pn.getStyleClass().add("playerPaneRow" + parity);
		grid.add(pn, col++, row);

		pn = new Pane();
		String state = p.isReady() ? "OK" : "pending";
		Label lblState = new Label(state);
		pn.getChildren().add(lblState);
		pn.getStyleClass().add("playerPaneRow" + parity);
		grid.add(pn, col++, row);
	}

  	private void connectAction() {
		String serverIp = this.txtfIp.getText().trim();
		String playerName = this.txtfName.getText().trim();

		try {
			this.gameEngine.connectAction(serverIp, playerName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		txtfIp.setDisable(true);
		txtfName.setDisable(true);
		btnConnect.setDisable(true);
		btnStart.setDisable(false);
	}

	private void readyAction() {
		this.gameEngine.readyAction();
		btnStart.setDisable(true);
	}

}
