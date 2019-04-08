package com.sikoramarek.gameOfLife.controller;

import com.sikoramarek.gameOfLife.common.Logger;
import com.sikoramarek.gameOfLife.model.ModelInterface;
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

	private static ModelInterface board;

	private static JavaFXView view;

	private static int x_size = 0;
	private static int y_size = 0;

	public static void loadResources() {
		loop = new FrameControlLoop();
		view = new JavaFXView();
		Platform.runLater(() -> {
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

	public static ModelInterface getCurrentBoard() {
		if (board == null){
			NullPointerException exception = new NullPointerException("Board must need be initialized by getNewBoard");
			Logger.error(exception, ModelInterface.class);
			throw exception;
		}
		return board;
	}

	public static ModelInterface getNewBoard(int x_size, int y_size) {
		x_size = x_size;
		y_size = y_size;
		board = new ModelSingleThread(x_size, y_size);
		return board;
	}

	public static JavaFXView getView(){
		if(view == null){
			view = new JavaFXView();
		}
		return view;
	}
}
