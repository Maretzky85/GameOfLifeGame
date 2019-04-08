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
					ResourceManager.getCurrentBoard().changeOnPositions(new int[][]{ {8,1},
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
							});
					Platform.runLater(() -> ResourceManager.getView().viewInit(50,50));
					new Thread(ResourceManager.getTimingControl()).start();
					gameState = GameState.RUNNING;
				case LOADING:
					break;
				case MENU:
					break;
				case PAUSED:
					break;
				case RUNNING:
					if(ResourceManager.getTimingControl().getUpdate()){
						ResourceManager.getView().refresh(ResourceManager.getCurrentBoard().getNextGenerationBoard());
						;
					}else {
						synchronized (ResourceManager.getTimingControl()){
							try {
								ResourceManager.getTimingControl().wait();
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
