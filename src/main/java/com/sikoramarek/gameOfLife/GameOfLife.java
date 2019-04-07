package com.sikoramarek.gameOfLife;


import com.sikoramarek.gameOfLife.controller.Controller;
import javafx.application.Application;
import javafx.stage.Stage;

public class GameOfLife extends Application {

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage stage){
		Controller controller = new Controller(stage);
		new Thread(controller).start();
	}
}
