package com.sikoramarek.gameOfLife.view;

import com.sikoramarek.gameOfLife.common.Logger;
import com.sikoramarek.gameOfLife.common.SharedResources;
import com.sikoramarek.gameOfLife.model.Dot;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;

import java.text.DecimalFormat;

import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;

/**
 * JavaFX view Class implements ViewInterface class
 * For viewing passed board model and passing input from user to Observable Class
 */
public class JavaFXView implements ViewInterface{

	private Group viewBoard = new Group();
	private Scene gameScene = new Scene(viewBoard, 500, 500, Color.BLACK);
	private Rectangle[][] viewRectangleTable;
	private Rectangle[][] viewRectangleTableSecondPlayer;
	private boolean ongoingUpdateFromModel = false;
	private boolean ongoingUpdateFromView = false;
	private int droppedFrames = 0;
	private int renderedFrames = 0;
	private int mouseY = 0;
	private int mouseX = 0;
	private boolean multi = false;
	private boolean firstBoardUpdate = true;


	public JavaFXView() {
		gameScene.widthProperty().addListener(observable -> resize());
		gameScene.heightProperty().addListener(observable -> resize());

	}

	private void resize(){
		if(viewRectangleTable != null){
			double rectangleHeight = gameScene.getHeight() / viewRectangleTable.length;
			double rectangleWidth = gameScene.getWidth() / viewRectangleTable[0].length;
			for (int i = 0; i < viewRectangleTable.length; i++) {
				for (int j = 0; j < viewRectangleTable[0].length; j++) {
					Rectangle rectangle = viewRectangleTable[i][j];
					rectangle.setY(rectangleHeight * i);
					rectangle.setX(rectangleWidth * j);
					rectangle.setHeight(rectangleHeight);
					rectangle.setWidth(rectangleWidth);
				}
			}
		}
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
		initGrid(X_SIZE, Y_SIZE);
		gameScene.setOnKeyPressed(this::handleKeyboard);
		gameScene.setOnMouseReleased(this::handleMouse);
		Logger.log("Done.", this);

	}

	private void initGrid(int X_SIZE, int Y_SIZE){
		long initStartTime = System.currentTimeMillis();
		long counter = System.currentTimeMillis();

		int RECTANGLE_WIDTH = (int) Screen.getPrimary().getBounds().getWidth() / X_SIZE;
		int RECTANGLE_HEIGHT = (int) Screen.getPrimary().getBounds().getHeight() / Y_SIZE;

		viewRectangleTable = new Rectangle[Y_SIZE][X_SIZE];
		viewRectangleTableSecondPlayer = multi ? new Rectangle[Y_SIZE][X_SIZE] : null;
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

				if (multi){
					int offset = viewRectangleTable[0].length;
					Rectangle rectangleToAddSecond = new Rectangle
							((offset+boardXposition) * RECTANGLE_WIDTH,
									boardYposition * RECTANGLE_HEIGHT,
									RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
					rectangleToAddSecond.setArcHeight(5);
					rectangleToAddSecond.setArcWidth(5);
					viewRectangleTableSecondPlayer[boardYposition][boardXposition] = rectangleToAddSecond;
					viewBoard.getChildren().add(viewRectangleTableSecondPlayer[boardYposition][boardXposition]);
				}

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
		Platform.runLater(this::resize);
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
				if (rectangle.getFill().equals(Color.BLACK)) {
					rectangle.
							setFill(Color.RED);
				} else {
					rectangle.
							setFill(Color.BLACK);
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
		refreshHelper(board, null);
	}

	public void refresh(Dot[][] board, Dot[][] boardSecondPlayer){
		refreshHelper(board, boardSecondPlayer);
	}

	private void refreshHelper(Dot[][] board, Dot[][] board2){
		if(viewRectangleTable == null){
			Platform.runLater(() -> viewInit(board[0].length, board.length));
		}
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
						if (multi && board2 != null){
							Rectangle rectangle2 = viewRectangleTableSecondPlayer[i][j];
							if (board2[i][j] == Dot.ALIVE) {
								rectangle.setFill(Color.RED);
							} else {
								rectangle.setFill(Color.BLACK);
							}
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

	@Override
	public void setMulti(boolean multi) {
		this.multi = true;
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
		SharedResources.addKeyboardInput(event.getCode());
	}

	public void handleMouse(MouseEvent me) {
		Node node = me.getPickResult().getIntersectedNode();

		if (me.getEventType().equals(MOUSE_RELEASED)) {
			if (me.getButton() == MouseButton.PRIMARY) {
				if(node != null && node instanceof Rectangle){
					Rectangle rectangle =(Rectangle) me.getPickResult().getIntersectedNode();
					SharedResources.positions.add(new int[]{
							(int) (rectangle.getX()/rectangle.getWidth()),
							(int) (rectangle.getY()/rectangle.getHeight())
					});
				}
				updateViewOnPos(me);
			}
			if (me.getButton() == MouseButton.SECONDARY){
				SharedResources.addKeyboardInput(KeyCode.P);
			}
		}
	}

	@Override
	public String toString(){
		return "JavaFX";
	}
}
