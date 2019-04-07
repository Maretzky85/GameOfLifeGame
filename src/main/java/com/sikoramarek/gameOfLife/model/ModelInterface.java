package com.sikoramarek.gameOfLife.model;

import java.lang.reflect.Array;

public interface ModelInterface {

	/**
	 * Returns current calculated board
	 *
	 * @return Dot[][]
	 */
	Dot[][] getCurrentBoard();

	/**
	 * Calculates and return next generation board
	 *
	 * @return Dot[][]
	 */
	Dot[][] getNextGenerationBoard();

	/**
	 * returns current (already calculated) board generation counted from beginning of class
	 *
	 * @return int
	 */
	int getCurrentGeneration();

	/**
	 * Change Dot status on current or next board.
	 *
	 * @return true if success
	 */
	boolean changeOnPosition(int x, int y);

	/**
	 * change array of positions
	 * @param array - 2D array of x,y points
	 * @return true if any points successfully changed
	 */
	boolean changeOnPositions(int[][] array);

	/**
	 * Allows to load new board state
	 *  - loads into current board
	 * @param board - board state to load
	 * @return true if successful
	 */
	boolean importBoard(Dot[][] board);

}
