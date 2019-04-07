package com.sikoramarek.gameOfLife.model;

interface BoardInterface {

	/**
	 * Returns current calculated board
	 *
	 * @return Dot[][]
	 */
	Dot[][] getCurrentBoardIndex();

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

}
