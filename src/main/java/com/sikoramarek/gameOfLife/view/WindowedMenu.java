package com.sikoramarek.gameOfLife.view;

import com.sikoramarek.gameOfLife.common.GameConfig;
import com.sikoramarek.gameOfLife.common.Logger;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

public class WindowedMenu {

	private MenuAction action;

	private GridPane menuGroup;
	private Scene menu;

	private GameConfig config;

	private TextField fps;
	private TextField boardXsize;
	private TextField boardYsize;

	public WindowedMenu(){
		menuGroup = new GridPane();
		menu = new Scene(menuGroup, 640, 480, Color.WHITE);
		init();
	}

	private void init(){
		menuGroup.setPadding(new Insets(10, 10, 10, 10));
		menuGroup.setVgap(5);
		menuGroup.setHgap(5);

		BackgroundSize backgroundSize = new BackgroundSize(
				(int) Screen.getPrimary().getBounds().getWidth(),
				(int)Screen.getPrimary().getBounds().getHeight(),
				true,
				true,
				true,
				true);

		try{
			menuGroup.setBackground(
					new Background(
							new BackgroundImage(
									new Image("gameoflife.jpg",
											640,
											480,
											false,
											true),
									BackgroundRepeat.NO_REPEAT,
									BackgroundRepeat.NO_REPEAT,
									BackgroundPosition.CENTER,
									backgroundSize)
					)
			);
		}catch (IllegalArgumentException exception){
			Logger.error(exception.getMessage(), this);
		}

		Label xSizeLabel = new Label("X size");
		Label ySizeLabel = new Label("Y size");
		boardXsize = new TextField("50");
		boardYsize = new TextField("50");
		Label fpsLabel = new Label("speed");
		fps = new TextField("30");



		Button startButton = new Button("Start");
		startButton.setOnAction(event -> {
			config = new GameConfig(Integer.valueOf(boardXsize.getText()),Integer.valueOf(boardYsize.getText()), Integer.valueOf(fps.getText()));
			action = MenuAction.START;
			Logger.log("start", this);
			synchronized (this){
				notifyAll();
			}
		});

		Button connectButton = new Button("Connect");
		connectButton.setOnAction(event -> {
			action = MenuAction.CONNECT;
			Logger.log("request connection", this);
			synchronized (this){
				notifyAll();
			}
		});

		GridPane.setConstraints(startButton, 0, 0);
		GridPane.setConstraints(connectButton, 0, 1);
		GridPane.setConstraints(boardXsize, 1,2);
		GridPane.setConstraints(boardYsize, 1,3);
		GridPane.setConstraints(ySizeLabel, 0,2);
		GridPane.setConstraints(xSizeLabel, 0,3);
		GridPane.setConstraints(fpsLabel, 0,4);
		GridPane.setConstraints(fps, 1,4);
		menuGroup.getChildren().addAll(startButton, connectButton, boardXsize, boardYsize, xSizeLabel, ySizeLabel, fps, fpsLabel);
	}

	@Override
	public String toString(){
		return "Menu";
	}

	public Scene getMenuScene() {
		return menu;
	}

	public synchronized MenuAction getAction() {
		if (action == null){
			synchronized (this){
				try {
					wait();
				} catch (InterruptedException e) {
					Logger.error(e, this);
				}
			}
		}

		MenuAction currentAction = action;
		action = null;
		return currentAction;
	}

	public GameConfig getConfig(){
		config = new GameConfig(Integer.valueOf(boardXsize.getText()),Integer.valueOf(boardYsize.getText()), Integer.valueOf(fps.getText()));
		return config;
	}
}
