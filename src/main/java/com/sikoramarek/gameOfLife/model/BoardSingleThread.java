package com.sikoramarek.gameOfLife.model;

import java.util.Arrays;

public class BoardSingleThread implements BoardInterface {

	private final Dot[][][] boards;
	private int generation = 0;

	private boolean boardsSwapped = false;
	private boolean worldWrapping = true;

	private static int[] ruleToLive = new int[]{2, 3};
	private static int[] ruleToGetAlive = new int[]{3};

	private int currentBoardIndex = 0;
	private int nextGenBoardIndex = 1;

	public BoardSingleThread(int x_size, int y_size){
		boards = new Dot[2][y_size][x_size];
	}

	@Override
	public Dot[][] getCurrentBoardIndex() {

		return boardsSwapped ? boards[1]:boards[0];
	}

	@Override
	public Dot[][] getNextGenerationBoard() {
		for (int y = 0; y < boards[currentBoardIndex].length-1; y++) {
			for (int x = 0; x < boards[currentBoardIndex][x].length-1; x++) {
				int aliveNeighbors = getNeighbors(x, y);

				if (boards[currentBoardIndex][y][x] == Dot.ALIVE) {
					if (Arrays.stream(ruleToLive).anyMatch(value -> value == aliveNeighbors)) {
						boards[nextGenBoardIndex][y][x] = Dot.ALIVE;
					}else {
						boards[nextGenBoardIndex][y][x] = null;
					}
				}else {
					if(Arrays.stream(ruleToGetAlive).anyMatch(value -> value == aliveNeighbors)){
						boards[nextGenBoardIndex][y][x] = Dot.ALIVE;
					}else {
						boards[nextGenBoardIndex][y][x] = null;
					}
				}

			}
		}
		swapBoards();
		generation++;
		return boards[currentBoardIndex];
	}

	private void swapBoards() {
		if(currentBoardIndex == 0){
			currentBoardIndex = 1;
			nextGenBoardIndex = 0;
		}else {
			currentBoardIndex = 0;
			nextGenBoardIndex = 1;
		}
	}

	private int getNeighbors(int boardTargetPositionX, int boardTargetPositionY) {
		int neighbors = 0;
		for (int y_pos_offset = -1; y_pos_offset <= 1; y_pos_offset++) {
			for (int x_pos_offset = -1; x_pos_offset <= 1; x_pos_offset++) {
				int currentCheckPositionY = boardTargetPositionY + y_pos_offset;
				int currentCheckPositionX = boardTargetPositionX + x_pos_offset;

				if(worldWrapping){
					if(currentCheckPositionX == boards[currentBoardIndex][0].length){
						currentCheckPositionX = 0;
					}
					if(currentCheckPositionX < 0){
						currentCheckPositionX = boards[currentBoardIndex][0].length-1;
					}
					if(currentCheckPositionY == boards[currentBoardIndex].length){
						currentCheckPositionY = 0;
					}
					if(currentCheckPositionY < 0){
						currentCheckPositionY = boards[currentBoardIndex].length-1;
					}
				}
				try {
					if (
							boards[currentBoardIndex][currentCheckPositionY][currentCheckPositionX] != null &&
									!(boardTargetPositionX == currentCheckPositionX &&
											boardTargetPositionY == currentCheckPositionY)
					)
						neighbors++;
				}catch (ArrayIndexOutOfBoundsException ignored){
					//If world wrapping is off ArrayIndexOutOfBound is ignored
					if(!worldWrapping){
						throw ignored;
					}
				}

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
		if(x > boards[currentBoardIndex][0].length || y > boards[currentBoardIndex].length || x < 0 || y < 0){
			return false;
		}
		if(boards[currentBoardIndex][y][x] == null){
			boards[currentBoardIndex][y][x] = Dot.ALIVE;
		}else {
			boards[currentBoardIndex][y][x] = null;
		}
		return true;
	}
}
