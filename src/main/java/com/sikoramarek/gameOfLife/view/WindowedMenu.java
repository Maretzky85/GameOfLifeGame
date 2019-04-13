package com.sikoramarek.gameOfLife.view;

import com.sikoramarek.gameOfLife.common.Logger;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

public class WindowedMenu {

	private MenuAction action;

	private GridPane menuGroup;
	private Scene menu;

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


		Button startButton = new Button("Start");
		startButton.setOnAction(event -> {
			action = MenuAction.START;
			synchronized (this){
				System.out.println("notify");
				this.notifyAll();
			}
			Logger.log("start", this);
		});

		Button connectButton = new Button("Connect");
		connectButton.setOnAction(event -> {
			action = MenuAction.CONNECT;
			synchronized (this){
				this.notifyAll();
			}
			Logger.log("request connection", this);
		});

		GridPane.setConstraints(startButton, 0, 0);
		GridPane.setConstraints(connectButton, 0, 1);
		menuGroup.getChildren().addAll(startButton, connectButton);
	}

	@Override
	public String toString(){
		return "Menu";
	}

	public Scene getMenuScene() {
		return menu;
	}

	public synchronized MenuAction getAction() {
		MenuAction currentAction = action;
		action = null;
		return currentAction;
	}
}
