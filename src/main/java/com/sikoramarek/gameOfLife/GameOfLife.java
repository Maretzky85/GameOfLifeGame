package com.sikoramarek.gameOfLife;


import com.sikoramarek.gameOfLife.model.BoardSingleThread;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class GameOfLife extends Application {

	public static void main(String[] args) {
		BoardSingleThread board = new BoardSingleThread(10,10);
		board.changeOnPosition(1,1);
		board.changeOnPosition(2,1);
		board.changeOnPosition(3,1);
		board.getNextGenerationBoard();
		Platform.exit();
	}

	@Override
	public void start(Stage stage) throws Exception {
	}
}
