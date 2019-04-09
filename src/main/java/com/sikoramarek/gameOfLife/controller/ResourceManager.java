package com.sikoramarek.gameOfLife.controller;

import com.sikoramarek.gameOfLife.common.Logger;
import com.sikoramarek.gameOfLife.model.Model;
import com.sikoramarek.gameOfLife.model.ModelSingleThread;
import com.sikoramarek.gameOfLife.view.JavaFXView;
import javafx.application.Platform;
import javafx.stage.Stage;

public class ResourceManager {

	public ResourceManager(){

	}

	ResourceManager(Stage primaryStage){
		stage = primaryStage;
	}

	private static Stage stage;

	private static TimingInterface loop;

	private static Model board;

	private static JavaFXView view;

	private static int X_SIZE = 0;
	private static int Y_SIZE = 0;

	public static void loadResources() {
		loop = new FrameControlLoop();
		view = new JavaFXView();
		Platform.runLater(() -> {
			view.viewInit(X_SIZE, Y_SIZE);
			stage.setScene(view.getScene());
			stage.show();
		});

	}

	public TimingInterface getLoop() {
		if (loop == null){
			loop = new FrameControlLoop();
		}
		return loop;
	}

	public static TimingInterface getTimingControl(){
		if(loop == null){
			loop = new FrameControlLoop();
		}
		return loop;
	}

	public static Model getCurrentModel() {
		if (board == null){
			NullPointerException exception = new NullPointerException("Board must need be initialized by getNewBoard");
			Logger.error(exception, Model.class);
			throw exception;
		}
		return board;
	}

	public static Model getNewBoard(int x_size, int y_size) {
		X_SIZE = x_size;
		Y_SIZE = y_size;
		board = new ModelSingleThread(x_size, y_size);
		return board;
	}

	public static JavaFXView getCurrentView(){
		if(view == null){
			view = new JavaFXView();
		}
		return view;
	}
}
