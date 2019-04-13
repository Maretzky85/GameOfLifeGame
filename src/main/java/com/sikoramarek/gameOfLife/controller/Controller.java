package com.sikoramarek.gameOfLife.controller;

import com.sikoramarek.gameOfLife.client.Client;
import com.sikoramarek.gameOfLife.common.GameConfig;
import com.sikoramarek.gameOfLife.common.GameState;
import com.sikoramarek.gameOfLife.common.Logger;
import com.sikoramarek.gameOfLife.common.SharedResources;
import com.sikoramarek.gameOfLife.model.Dot;
import com.sikoramarek.gameOfLife.model.Model;
import com.sikoramarek.gameOfLife.view.JavaFXView;
import com.sikoramarek.gameOfLife.view.MenuAction;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.LinkedList;

public class Controller implements Runnable{

	private static GameState gameState;

	private ResourceManager resourceManager;

	private Model model;

	private JavaFXView view;

	private TimingInterface timing;

	private Client multiplayerSync;

	Stage primaryStage;

	Stage modelStage;
	private boolean multiplayer = false;
	private GameConfig config;
	private Dot[][] secondPlayerBoard;
	private Integer generation = -1;

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
					//TODO load required assets and switch to menu
					resourceManager = ResourceManager.getInstance();
					gameState = GameState.MENU;
					break;
				case LOADING:
					//TODO load all needed to play assets
					Logger.log("Loading", this);
					if (config == null){
						config = resourceManager.getMenu().getConfig();
					}
					model = resourceManager.getNewBoard(config.xSize,config.ySize);
					model.changeOnPositions(positions);
					view = resourceManager.getNewView();
					if (multiplayerSync != null){
						view.setMulti(true);
					}
					view.viewInit(config.xSize, config.ySize);
					timing = resourceManager.getTimingControl();
					timing.setFRAME_RATE(config.fps);
					Platform.runLater(()->{

						modelStage = new Stage();
						modelStage.initStyle(StageStyle.UTILITY);
						modelStage.setScene(view.getScene());
						modelStage.show();

					});
					while(modelStage == null || !modelStage.isShowing()){
						synchronized (this){
							try {
								wait(5);
							} catch (InterruptedException e) {
								Logger.error(e, this);
							}
						}
					}
					new Thread(timing).start();
					if(multiplayer){
						gameState = GameState.MULTIPLAYER;
					}else {
						gameState = GameState.RUNNING;
					}

					break;
				case MENU:
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
					checkInput();
					synchronized (this){
						try {
							wait(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					break;
				case RUNNING:
					checkInput();
					if (modelStage.isShowing()){
						if(timing.getUpdate()){
							view.refresh(model.nextGenerationBoard());
						}
					}else {
						gameState = GameState.MENU;
					}
					break;
				case MULTIPLAYER_CONFIG:
					multiplayerSync = Client.getClient();
					multiplayerSync.connect();
					while (multiplayerSync.isConnecting()){
						synchronized (this){
							try {
								wait(5);
							} catch (InterruptedException e) {
								Logger.error(e, this);
							}
						}
					}
					if(!multiplayerSync.isConnected()){
						gameState = GameState.MENU;
					}else {
						config = resourceManager.getMenu().getConfig();
						multiplayerSync.send(config);
						multiplayer = true;
						while (!handleServerResponse()){
							multiplayerSync.send(config);
							synchronized (this){
								try {
									wait(50);
								} catch (InterruptedException e) {
									Logger.error(e, this);
								}
							}
						}
						gameState = GameState.LOADING;
					}
					break;
				case MULTIPLAYER:
					handleServerResponse();
					checkInput();
					if (modelStage.isShowing()){
						if(timing.getUpdate()){
							if (generation >= model.getCurrentGeneration()){
								model.nextGenerationBoard();
							}
							view.refresh(model.getCurrentBoard(), secondPlayerBoard);
						}else {
							multiplayerSync.send(model.getCurrentGeneration());
							Dot[][] board = model.getCurrentBoard();
							multiplayerSync.send(board);

							synchronized (this){
								try {
									wait(50);
								} catch (InterruptedException e) {
									Logger.error(e, this);
								}
							}
						}
					}
					break;
			}
		}
	}

	private boolean handleServerResponse() {
		LinkedList received = multiplayerSync.getReceived();
		while(!received.isEmpty()){
			Object object = received.pop();
			if (object instanceof GameConfig){
				config = (GameConfig) object;
				return true;
			}else
			if (object instanceof int[]){
				int[] position = (int[]) object;
				model.changeOnPosition(position[0], position[1]);
			}else
			if (object instanceof Dot[][]){
				secondPlayerBoard = (Dot[][]) object;
			}else
			if (object instanceof Integer){
				generation = (Integer) object;
			}else
			if (object instanceof String){
				Logger.log((String) object, this);
			}

		}
		received.clear();
		return false;
	}

	private void checkInput() {
		if(!SharedResources.positions.isEmpty()){
			model.changeOnPositions(SharedResources.positions);
			SharedResources.positions.clear();
		}
		LinkedList<KeyCode> keyboardInputs = SharedResources.getKeyboardInput();
		if(!keyboardInputs.isEmpty()){
			while (!keyboardInputs.isEmpty()){
				KeyCode key = keyboardInputs.pop();
				switch (key){
					case P:
						if (gameState == GameState.RUNNING){
							gameState = GameState.PAUSED;
						}else
							if (gameState == GameState.PAUSED){
								gameState = GameState.RUNNING;
							}

				}
			}
		}
	}

	private void handleMenuAction(MenuAction action) {
		switch (action) {
			case START:
				gameState = GameState.LOADING;
				break;
			case CONNECT:
				gameState = GameState.MULTIPLAYER_CONFIG;
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
