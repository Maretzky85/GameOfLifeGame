package com.sikoramarek.gameOfLife.view;

import com.sikoramarek.gameOfLife.common.Logger;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

public class WindowedMenu {

	TextField wHeight;
	TextField wWidth;

	private Runnable gameStarter;
	private GridPane menuGroup;
	private Scene menu;
	private String[] labels = new String[]{
			"Window Height",
			"Window Width",
			"Board X size",
			"Board Y size",
			"Frame Rate",
			"Console view",
			"JavaFX view",
			"JavaFX3D view",
			"World wrapping"};

	public WindowedMenu(){
		menuGroup = new GridPane();
		menu = new Scene(menuGroup, 500, 500, Color.WHITE);
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
											(int) Screen.getPrimary().getBounds().getWidth(),
											(int)Screen.getPrimary().getBounds().getHeight(),
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


		wHeight = new TextField("500");
		GridPane.setConstraints(wHeight, 1, 0);

		wWidth = new TextField("500");
		GridPane.setConstraints(wWidth, 1, 1);

		TextField xSize = new TextField("500");
		GridPane.setConstraints(xSize, 1, 2);

		TextField ySize = new TextField("500");
		GridPane.setConstraints(ySize, 1, 3);

		TextField frameRate = new TextField("500");
		GridPane.setConstraints(frameRate, 1, 4);

		CheckBox consoleViewBox = new CheckBox();
		consoleViewBox.setSelected(true);
		GridPane.setConstraints(consoleViewBox, 1, 5);

		CheckBox javaFXViewBox = new CheckBox();
		javaFXViewBox.setSelected(true);
		GridPane.setConstraints(javaFXViewBox, 1, 6);

		CheckBox jFX3dBox = new CheckBox();
		jFX3dBox.setSelected(true);
		GridPane.setConstraints(jFX3dBox, 1, 7);

		CheckBox worldWrappingBox = new CheckBox();
		worldWrappingBox.setSelected(true);
		GridPane.setConstraints(worldWrappingBox, 1, 8);

		menuGroup.getChildren().addAll(
//				wHeight,
//				wWidth,
//				xSize,
//				ySize,
//				frameRate,
				consoleViewBox,
				javaFXViewBox,
				jFX3dBox,
				worldWrappingBox);

		labelBuilder(labels);

		Button saveButton = new Button("Start");
		saveButton.setOnAction(event -> {
			Logger.log("start", this);
		});

		Button connectButton = new Button("Connect");
		connectButton.setOnAction(event -> {
			Logger.log("request connection", this);
		});

		GridPane.setConstraints(saveButton, 0, labels.length);
		GridPane.setConstraints(connectButton, 0, labels.length+1);
		menuGroup.getChildren().addAll(saveButton, connectButton);
	}

	private void labelBuilder(Object[] any){
		for(int i = 0; i < any.length; i++){
			Label newLabel = new Label(any[i].toString());
			GridPane.setConstraints(newLabel, 0, i);
			menuGroup.getChildren().add(newLabel);
		}
	}
	@Override
	public String toString(){
		return "Menu";
	}

	public Scene getMenu() {
		return menu;
	}
}
