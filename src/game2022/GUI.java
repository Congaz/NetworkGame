package game2022;

import java.util.ArrayList;
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

	// --- Connect scene elements ---
	private TextField txtfIp;
	private TextField txtfName;
	private Button btnConnect;
	private Button btnStart;



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
		//GUI.stage.setScene(this.createConnectScene());

		GameScene gs = new GameScene(GUI.stage);

		// Create game scene
		//GUI.stage.setScene(this.createGameScene());

		// Display stage.
		GUI.stage.show();

		// Test

	}

	private Scene createConnectScene() {
		GridPane mainGrid = new GridPane();
        Scene scene = new Scene(mainGrid);
		mainGrid.setHgap(10);
		mainGrid.setVgap(10);
		mainGrid.setPadding(new Insets(20));
		mainGrid.getStylesheets().add("/game2022/sceneConnect.css");
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
		this.btnConnect = new Button("Connect");
		this.btnConnect.getStyleClass().add("btnConnect");
		mainGrid.add(this.btnConnect, 0, 2, 2, 1);
        this.btnConnect.setOnAction(event -> this.connectAction());
		GridPane.setHalignment(this.btnConnect, HPos.CENTER);

		// --- Start btn ---
		this.btnStart = new Button("Start Game");
		this.btnStart.getStyleClass().add("btnStart");
		mainGrid.add(this.btnStart, 0, 3, 2, 1);
        this.btnStart.setOnAction(event -> this.startAction());
		this.btnStart.setDisable(true);
		GridPane.setHalignment(this.btnStart, HPos.CENTER);

		return scene;
	}

	private void connectAction() {
		String serverIp = this.txtfIp.getText().trim();
		String playerName = this.txtfName.getText().trim();

		try {
			GUI.gameEngine.connect(serverIp, playerName);
			btnConnect.setDisable(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		btnStart.setDisable(false);
	}

	private void startAction() {
		GUI.gameEngine.startGame();
	}



	
}

