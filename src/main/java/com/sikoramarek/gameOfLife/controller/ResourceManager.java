package com.sikoramarek.gameOfLife.controller;

import com.sikoramarek.gameOfLife.common.Logger;
import com.sikoramarek.gameOfLife.model.Model;
import com.sikoramarek.gameOfLife.model.ModelSingleThread;
import com.sikoramarek.gameOfLife.view.JavaFXView;
import com.sikoramarek.gameOfLife.view.WindowedMenu;
import javafx.application.Platform;
import javafx.stage.Stage;

public class ResourceManager {

	static ResourceManager instance;

	private ResourceManager(){
	}

	private Stage primaryStage;

	private Stage secondaryStage;

	private TimingInterface loop;

	private Model board;

	private JavaFXView view;

	private WindowedMenu menu;

	private static int X_SIZE = 0;

	private static int Y_SIZE = 0;


	public static ResourceManager getInstance(){
		if (instance == null){
			instance = new ResourceManager();
		}
		return instance;
	}

	public WindowedMenu getMenu() {
		if (menu == null){
			menu = new WindowedMenu();
		}
		return menu;
	}

	public void setPrimaryStage(Stage primaryStage){
		this.primaryStage = primaryStage;
	}

	public void loadResources() {
		loop = new FrameControlLoop();
		view = new JavaFXView();
		menu = new WindowedMenu();

		Platform.runLater(() -> {
			view.viewInit(X_SIZE, Y_SIZE);
			primaryStage.setScene(view.getScene());
			primaryStage.show();
			secondaryStage = new Stage();
			secondaryStage.setScene(menu.getMenuScene());
			secondaryStage.show();
		});

	}

	public TimingInterface getLoop() {
		if (loop == null){
			loop = new FrameControlLoop();
		}
		return loop;
	}

	public TimingInterface getTimingControl(){
		if(loop == null){
			loop = new FrameControlLoop();
		}
		return loop;
	}

	public Model getCurrentModel() {
		if (board == null){
			NullPointerException exception = new NullPointerException("Board must need be initialized by getNewBoard");
			Logger.error(exception, Model.class);
			throw exception;
		}
		return board;
	}

	public Model getNewBoard(int x_size, int y_size) {
		X_SIZE = x_size;
		Y_SIZE = y_size;
		board = new ModelSingleThread(x_size, y_size);
		return board;
	}

	public JavaFXView getCurrentView(){
		if(view == null){
			view = new JavaFXView();
		}
		return view;
	}

	public JavaFXView getNewView() {
		view = new JavaFXView();
		return view;
	}
}
