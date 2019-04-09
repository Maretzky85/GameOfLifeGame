package com.sikoramarek.gameOfLife.controller;

import com.sikoramarek.gameOfLife.common.Logger;
import com.sikoramarek.gameOfLife.model.Model;
import com.sikoramarek.gameOfLife.view.JavaFXView;
import com.sikoramarek.gameOfLife.view.ViewInterface;
import javafx.stage.Stage;

public class Controller implements Runnable{

	private static GameState gameState;

	private ResourceManager resourceManager;

	private Model model;

	private JavaFXView view;

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
					model = ResourceManager.getNewBoard(50,50);
					view = ResourceManager.getCurrentView();
					ResourceManager.getCurrentModel().changeOnPositions(positions);
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
						ResourceManager.getCurrentView().refresh(ResourceManager.getCurrentModel().getNextGenerationBoard());
					}
					break;
			}
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
