package com.sikoramarek.gameOfLife.controller;

import com.sikoramarek.gameOfLife.common.Logger;
import com.sikoramarek.gameOfLife.model.ModelInterface;
import com.sikoramarek.gameOfLife.view.ViewInterface;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Controller implements Runnable{

	private static GameState gameState;

	private ResourceManager resourceManager;

	private ModelInterface model;

	private ViewInterface view;

	private TimingInterface timing;

	public Controller(Stage stage){
		gameState = GameState.INIT;
		resourceManager = new ResourceManager(stage);
	}

	public synchronized void setState(GameState state){
		gameState = state;
	}

	@Override
	public void run() {
		while(true){
			switch (gameState) {
				case INIT:
					ResourceManager.loadResources();
					ResourceManager.getNewBoard(50,50);
					ResourceManager.getCurrentBoard().changeOnPositions(new int[][]{ {2,2},{2,3},{2,4} });
					Platform.runLater(() -> ResourceManager.getView().viewInit(50,50));
					new Thread(ResourceManager.getControlLoop()).start();
					gameState = GameState.RUNNING;
				case LOADING:
					break;
				case MENU:
					break;
				case PAUSED:
					break;
				case RUNNING:
					if(ResourceManager.getControlLoop().getUpdate()){
						ResourceManager.getView().refresh(ResourceManager.getCurrentBoard().getNextGenerationBoard());
						;
					}else {
						synchronized (ResourceManager.getControlLoop()){
							try {
								ResourceManager.getControlLoop().wait();
							} catch (InterruptedException e) {
								Logger.error(e, this);
							}
						}
					}
					break;
			}
		}

	}
}
