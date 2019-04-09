package com.sikoramarek.gameOfLife;


import com.sikoramarek.gameOfLife.controller.Controller;
import javafx.application.Application;
import javafx.stage.Stage;

public class GameOfLife extends Application {

	Thread controllerThread;

	public static boolean running = true;

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage stage){
		Controller controller = new Controller(stage);
		controllerThread = new Thread(controller);
		controllerThread.setDaemon(true);
		controllerThread.start();
	}
}
