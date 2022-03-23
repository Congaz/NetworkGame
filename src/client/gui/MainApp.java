package client.gui;

import client.model.GameEngine;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

	private static Stage stage;
	private static GameEngine gameEngine;

	// --- Gui params ---
	private static final int size = 20;
	private static final int scene_width = size * 20 + 200; // 200
	private static final int scene_height = size * 20 + 90; // 90


	@Override
	public void start(Stage stage) {

		// --- Set-up stage ---
		MainApp.stage = stage;
		MainApp.stage.setTitle("The network game!");
		MainApp.stage.setResizable(false);
		//Gui.stage.setMinWidth(1100);
        //Gui.stage.setMinHeight(400);
        MainApp.stage.setWidth(MainApp.scene_width);
        MainApp.stage.setHeight(MainApp.scene_height);
        //MainApp.stage.setFullScreen(false);

		// --- Create game engine ---
		MainApp.gameEngine = new GameEngine(MainApp.stage);

		// --- Display stage ---
		MainApp.stage.show();
	}
}

