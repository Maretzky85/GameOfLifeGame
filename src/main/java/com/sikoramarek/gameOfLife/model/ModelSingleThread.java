package com.sikoramarek.gameOfLife.model;

import java.util.Arrays;
import java.util.LinkedList;

public class ModelSingleThread implements Model {

	private final int X_SIZE;
	private final int Y_SIZE;

	private final Dot[][][] boards;
	private int generation = 0;

	private boolean boardsSwapped = false;
	private boolean worldWrapping = true;

	private int[] ruleToLive = new int[]{2, 3};
	private int[] ruleToGetAlive = new int[]{3};

	public ModelSingleThread(int x_size, int y_size){
		X_SIZE = x_size;
		Y_SIZE = y_size;
		boards = new Dot[2][Y_SIZE][X_SIZE];
		initBoards();
	}

	private void initBoards() {
		for (int board = 0; board < boards.length; board++) {
			for (int line = 0; line < boards[board].length; line++) {
				for (int point = 0; point < boards[board][line].length; point++) {
					boards[board][line][point] = Dot.getDead();
				}
			}
		}
	}

	@Override
	public Dot[][] getCurrentBoard() {
		return boardsSwapped ? boards[1]:boards[0];
	}

	@Override
	public Dot[][] nextGenerationBoard() {
		Dot[][] currentBoard = boardsSwapped ? boards[1]:boards[0];
		Dot[][] nextGenBoard = boardsSwapped ? boards[0]:boards[1];
		for (int y = 0; y < currentBoard.length-1; y++) {
			for (int x = 0; x < currentBoard[x].length-1; x++) {
				int aliveNeighbors = getAliveNeighbors(x, y);

				if (currentBoard[y][x] == Dot.getAlive()) {
					if (Arrays.stream(ruleToLive).anyMatch(value -> value == aliveNeighbors)) {
						nextGenBoard[y][x] = Dot.getAlive();
					}else {
						nextGenBoard[y][x] = Dot.getDead();
					}
				}else {
					if(Arrays.stream(ruleToGetAlive).anyMatch(value -> value == aliveNeighbors)){
						nextGenBoard[y][x] = Dot.getAlive();
					}else {
						nextGenBoard[y][x] = Dot.getDead();
					}
				}

			}
		}
		boardsSwapped = !boardsSwapped;
		generation++;
		return getCurrentBoard();
	}


	private int getAliveNeighbors(int x, int y) {
		Dot[][] currentBoard = getCurrentBoard();
		int neighbors = 0;
		for (int dy = -1; dy <= 1; dy++) {
			for (int dx = -1; dx <= 1; dx++) {
				int currentY = y + dy;
				int currentX = x + dx;

				if(worldWrapping){
					if(currentX == currentBoard[0].length){
						currentX = 0;
					}
					if(currentX < 0){
						currentX = currentBoard[0].length-1;
					}
					if(currentY == currentBoard.length){
						currentY = 0;
					}
					if(currentY < 0){
						currentY = currentBoard.length-1;
					}
				}else{
					if (currentX < 0 || currentX == currentBoard[0].length ||
						currentY < 0 || currentY == currentBoard.length){
						continue;
					}
				}
					if (
							!(currentX == x && currentY == y) &&
							currentBoard[currentY][currentX] != Dot.getDead()
					)
						neighbors++;
			}

		}
		return neighbors;
	}

	@Override
	public int getCurrentGeneration() {
		return generation;
	}

	@Override
	public boolean changeOnPosition(int x, int y) {
		Dot[][] currentBoard = getCurrentBoard();
		if(x >= currentBoard[0].length || y >= currentBoard.length || x < 0 || y < 0){
			return false;
		}
		if(currentBoard[y][x] == Dot.getDead()){
			currentBoard[y][x] = Dot.getAlive();
		}else {
			currentBoard[y][x] = Dot.getDead();
		}
		return true;
	}

	@Override
	public boolean changeOnPositions(int[][] arrayOfXY) {
		boolean success = false;
		for (int point = 0; point < arrayOfXY.length; point++) {
			int x = arrayOfXY[point][0];
			int y =  arrayOfXY[point][1];
			if (changeOnPosition(x,y))
				success = true;
		}
		return success;
	}

	@Override
	public boolean changeOnPositions(LinkedList<int[]> array) {
		array.forEach(ints -> changeOnPosition(ints[0], ints[1]));
		return true;
	}

	@Override
	public boolean importBoard(Dot[][] board) {
		int arrayIndex = boardsSwapped ? 1 : 0;
		if(board.length != Y_SIZE || board[0].length != X_SIZE){
			return false;
		}
		boards[arrayIndex] = board;
		return true;
	}

}
