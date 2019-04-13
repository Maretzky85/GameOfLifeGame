package com.sikoramarek.gameOfLife.controller;

import com.sikoramarek.gameOfLife.common.Logger;
import com.sikoramarek.gameOfLife.model.Model;
import com.sikoramarek.gameOfLife.view.JavaFXView;
import com.sikoramarek.gameOfLife.view.MenuAction;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Controller implements Runnable{

	private static GameState gameState;

	private ResourceManager resourceManager;

	private Model model;

	private JavaFXView view;

	private TimingInterface timing;

	Stage primaryStage;

	Stage modelStage;

	public Controller(Stage stage){
		primaryStage = stage;
		gameState = GameState.INIT;
	}

	public static synchronized void setState(GameState state){
		gameState = state;
	}

	@Override
	public void run() {
		while(true){
			switch (gameState) {
				case INIT:
					Logger.log("Init", this);
					//TODO load required assets and switch to menu
					resourceManager = ResourceManager.getInstance();
					gameState = GameState.MENU;
					break;
				case LOADING:
					Logger.log("Loading", this);
					//TODO load all needed to play assets
					model = resourceManager.getNewBoard(50,50);
					model.changeOnPositions(positions);
					view = resourceManager.getCurrentView();
					Platform.runLater(()->{

						modelStage = new Stage();
						modelStage.setScene(view.getScene());
						primaryStage.close();
						modelStage.show();

					});
					synchronized (this){
						try {
							wait(5);
						} catch (InterruptedException e) {
							Logger.error(e, this);
						}
					}

					timing = resourceManager.getTimingControl();
					new Thread(timing).start();
					gameState = GameState.RUNNING;
					break;
				case MENU:
//					Logger.log("Menu", this);
					//TODO show menu
					if (!primaryStage.isShowing()){
						Platform.runLater(() -> {
							primaryStage.setScene(resourceManager.getMenu().getMenuScene());
							primaryStage.show();
						});
						while(!primaryStage.isShowing()){
							synchronized (this){
								try {
									wait(5);
								} catch (InterruptedException e) {
									Logger.error(e, this);
								}
							}
						}
					}
					MenuAction action = resourceManager.getMenu().getAction();
					if (action != null){
						handleMenuAction(action);
					}else {
						try {
							synchronized (this){
								wait(10);
							}
						} catch (InterruptedException e) {
							Logger.error(e, this);
						}
					}
					break;
				case PAUSED:
					try {
						wait(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
				case RUNNING:
					if (modelStage.isShowing()){
						if(timing.getUpdate()){
							view.refresh(model.nextGenerationBoard());
						}
					}else {
						Platform.runLater(() -> primaryStage.show());
						gameState = GameState.MENU;
					}
					break;
			}
		}
	}

	private void handleMenuAction(MenuAction action) {
		switch (action) {
			case START:
				gameState = GameState.LOADING;
				break;
			case CONNECT:
				break;
		}
	}

	int[][] positions = new int[][]{
			{8,1},
			{7,1},
			{8,2},
			{7,2},
			{8,12},
			{7,12},
			{6,12},
			{9,13},
			{5,13},
			{4,14},
			{10,14},
			{5,15},
			{9,15},
			{8,16},
			{7,16},
			{6,16},
			{8,17},
			{7,17},
			{6,17},
			{6,22},
			{5,22},
			{4,22},
			{3,23},
			{4,23},
			{6,23},
			{7,23},
			{3,24},
			{4,24},
			{6,24},
			{7,24},
			{3,24}
			, {3,25}
			, {4,25}
			, {5,25}
			, {6,25}
			, {7,25}
			, {2,26}
			, {3,26}
			, {7,26}
			, {8,26}
			, {3,31}
			, {4,31}
			, {5,35}
			, {6,35}
			, {5,36}
			, {6,36}
	};
}
