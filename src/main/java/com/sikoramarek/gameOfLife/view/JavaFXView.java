package com.sikoramarek.gameOfLife.view;

import com.sikoramarek.gameOfLife.common.Logger;
import com.sikoramarek.gameOfLife.model.Dot;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;

import java.text.DecimalFormat;

import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;

/**
 * JavaFX view Class implements ViewInterface class
 * For viewing passed board model and passing input from user to Observable Class
 */
public class JavaFXView implements ViewInterface{

	private Group viewBoard = new Group();
	private Scene gameScene = new Scene(viewBoard, Screen.getPrimary().getBounds().getHeight(), Screen.getPrimary().getBounds().getWidth(), Color.BLACK);
	private Rectangle[][] viewRectangleTable;
	private boolean ongoingUpdateFromModel = false;
	private boolean ongoingUpdateFromView = false;
	private int droppedFrames = 0;
	private int renderedFrames = 0;
	private Dot[][] secondPlayerBoard;

	private int iterator = 0;

	private Text tutorialPlaceholder;

	public JavaFXView() {
	}

	public Scene getScene() {
		return gameScene;
	}

	/**
	 * viewInit method
	 * Sets stages title, creates and initialises reflecting rectangle table for holding view`s side rectangles
	 * Defines rectangle appearance
	 * Calls gameScene set and view methods for showing window.
	 * Calls attachResizeListeners function to attach proper listeners to stage and gameScene
	 */

	public void viewInit(int X_SIZE, int Y_SIZE){
		Logger.log("Initialising Scene.", this);

		long startTime = System.currentTimeMillis();

		Logger.log("Initialising grid", this);

		initGrid(X_SIZE, Y_SIZE);

		Logger.log("Done. Initialising grid took " + (System.currentTimeMillis() - startTime) + " ms", this);

		gameScene.setCursor(Cursor.CROSSHAIR);

		tutorialPlaceholder = new Text(100, 50, "");
		tutorialPlaceholder.setFill(Color.WHITE);
		tutorialPlaceholder.setFont(new Font(30));
		viewBoard.getChildren().add(tutorialPlaceholder);

		attachResizeListeners();

		Logger.log("Initialising took " + (System.currentTimeMillis() - startTime) + " ms", this);

	}

	/**
	 * Attaches listeners for stage width and height and calls resizeGrid if needed
	 */
	private void attachResizeListeners(){
//		final int WINDOW_UPPER_BAR_THRESHOLD = -30;
//
//		gameScene.widthProperty().addListener((observable, oldValue, newValue) -> {
//			Config.setRequestedWindowWidth(newValue.intValue());
//			resizeGrid();
//		});
//
//		gameScene.heightProperty().addListener((observable, oldValue, newValue) -> {
//			Config.setRequestedWindowHeight(newValue.intValue()+WINDOW_UPPER_BAR_THRESHOLD);
//			resizeGrid();
//		});
	}

	private void resizeGrid(){
//		for (int boardYposition = 0; boardYposition < Y_SIZE; boardYposition++) {
//			for (int boardXposition = 0; boardXposition < X_SIZE; boardXposition++) {
//				viewRectangleTable[boardYposition][boardXposition].setHeight(RECTANGLE_HEIGHT);
//				viewRectangleTable[boardYposition][boardXposition].setWidth(RECTANGLE_WIDTH);
//				viewRectangleTable[boardYposition][boardXposition].setX(RECTANGLE_WIDTH * boardXposition);
//				viewRectangleTable[boardYposition][boardXposition].setY(RECTANGLE_HEIGHT * boardYposition);
//				viewRectangleTable[boardYposition][boardXposition].setArcHeight(RECTANGLE_ARC_HEIGHT);
//				viewRectangleTable[boardYposition][boardXposition].setArcWidth(RECTANGLE_ARC_WIDTH);
//			}
//		}
	}

	private void initGrid(int X_SIZE, int Y_SIZE){
		long initStartTime = System.currentTimeMillis();
		long counter = System.currentTimeMillis();

		int RECTANGLE_WIDTH = (int) Screen.getPrimary().getBounds().getWidth() / X_SIZE;
		int RECTANGLE_HEIGHT = (int) Screen.getPrimary().getBounds().getHeight() / Y_SIZE;

		viewRectangleTable = new Rectangle[Y_SIZE][X_SIZE];
		for (int boardYposition = 0; boardYposition < Y_SIZE; boardYposition++) {

			long timeTaken = System.currentTimeMillis() - initStartTime;

			for (int boardXposition = 0; boardXposition < X_SIZE; boardXposition++) {

				Rectangle rectangleToAdd = new Rectangle
						(boardXposition * RECTANGLE_WIDTH,
								boardYposition * RECTANGLE_HEIGHT,
								RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
				rectangleToAdd.setArcHeight(5);
				rectangleToAdd.setArcWidth(5);
				viewRectangleTable[boardYposition][boardXposition] = rectangleToAdd;
				viewBoard.getChildren().add(viewRectangleTable[boardYposition][boardXposition]);

				if(timeTaken > 500){
					long currentTime = System.currentTimeMillis();
					if(counter - currentTime < -500){
						counter = System.currentTimeMillis();
						Logger.log( ""+
								new DecimalFormat("#0.0")
										.format((double)
												(X_SIZE*boardYposition+boardXposition)/(Y_SIZE*X_SIZE)*100) +" % ", this);
					}
				}

			}
		}
	}


	/**
	 * updateViewOnPos function
	 * updates view side of model on-the-fly (before sending to model)
	 * for immediate reaction to clicks
	 *
	 * ongoingUpdateFromView - variable for synchronising update rates - prevents flooding updates to view
	 *
	 * @param event - any input event acceptable, mouse or key event are supported here, else is discarded
	 */
	private void updateViewOnPos(MouseEvent event) {
		ongoingUpdateFromView = true;
		Platform.runLater(() -> {
			if (event.getPickResult().getIntersectedNode() != null
					&& event.getPickResult().getIntersectedNode().getClass().equals(Rectangle.class)) {
				Rectangle rectangle = (Rectangle) event.
						getPickResult().
						getIntersectedNode();
				if (rectangle.getFill().equals(Color.WHITE)) {
					rectangle.
							setFill(Color.RED);
				} else {
					rectangle.
							setFill(Color.WHITE);
				}
			}
			ongoingUpdateFromView = false;
		});

	}


	/**
	 * refresh method takes board as argument, scans model board and updates representing view board accordingly.
	 *
	 * ongoingUpdateFromModel and ongoingUpdateFromView must be set to false for update to take place
	 * if not, function add a drop frame and discard this view update
	 *
	 * @param board - 2D board from model
	 */

	public void refresh(Dot[][] board) {
		if (!ongoingUpdateFromModel && !ongoingUpdateFromView) {
			ongoingUpdateFromModel = true;
			Platform.runLater(() -> {
					for (int i = 0; i < viewRectangleTable.length; i++) {
						for (int j = 0; j < viewRectangleTable[0].length; j++) {
							Rectangle rectangle = viewRectangleTable[i][j];
							if (board[i][j] == Dot.ALIVE) {
								rectangle.setFill(Color.RED);
							} else {
								rectangle.setFill(Color.BLACK);
							}
						}
					}
					renderedFrames++;
					ongoingUpdateFromModel = false;
			});

		} else {
			droppedFrames++;
		}
	}


	/**
	 * getDroppedFrames function returns dropped frames count from last call
	 * when called set droppedFrames to 0, and return count from call moment
	 *
	 * @return int count of dropped frames in time from last call to now
	 */
	public int getDroppedFrames() {
		int droppedFramesCurrent = droppedFrames;
		droppedFrames = 0;
		return droppedFramesCurrent;
	}

	public int getRenderedFrames() {
		int currentRenderedFrames = renderedFrames;
		renderedFrames = 0;
		return currentRenderedFrames;
	}

	public void handleKeyboard(KeyEvent event) {

	}

	public void handleMouse(MouseEvent me) {
		if (me.getEventType().equals(MOUSE_RELEASED)) {
			if (me.getButton() == MouseButton.PRIMARY) {
				updateViewOnPos(me);
			}
		}
	}

	public Text getTutorialPlaceholder() {
		return tutorialPlaceholder;
	}

	public void refreshSecond(Dot[][] secondPlayerBoard) {
		this.secondPlayerBoard = secondPlayerBoard;
	}


	@Override
	public String toString(){
		return "JavaFX";
	}


}
