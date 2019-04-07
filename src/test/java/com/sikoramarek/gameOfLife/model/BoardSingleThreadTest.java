package com.sikoramarek.gameOfLife.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoardSingleThreadTest {
	private BoardSingleThread board;
	private Dot[][] testArray = new Dot[10][10];

	@Before
	public void before(){
		board = new BoardSingleThread(10,10);
		testArray = new Dot[10][10];
	}

	@Test
	public void getCurrentBoard() {
		assertArrayEquals(testArray, board.getCurrentBoardIndex());
		testArray[0][0] = Dot.ALIVE;
		board.changeOnPosition(0,0);
		assertArrayEquals(testArray, board.getCurrentBoardIndex());
	}

	@Test
	public void getNextGenerationBoard() {
		assertArrayEquals(board.getCurrentBoardIndex(), board.getNextGenerationBoard());
		board.changeOnPosition(0,1);
		board.changeOnPosition(1,1);
		board.changeOnPosition(2,1);
		testArray[0][1] = Dot.ALIVE;
		testArray[1][1] = Dot.ALIVE;
		testArray[2][1] = Dot.ALIVE;
		assertArrayEquals(testArray, board.getNextGenerationBoard());
	}

	@Test
	public void getCurrentGeneration() {
		assertSame(0, board.getCurrentGeneration());
		board.getNextGenerationBoard();
		assertSame(1, board.getCurrentGeneration());
		for (int i = 0; i < 10; i++) {
			board.getNextGenerationBoard();
		}
		assertSame(11, board.getCurrentGeneration());
	}

	@Test
	public void changeOnPosition() {
		assertNull(board.getCurrentBoardIndex()[0][0]);
		assertTrue(board.changeOnPosition(0,0));
		assertFalse(board.changeOnPosition(15,15));
		assertFalse(board.changeOnPosition(-5,-5));
		assertNotNull(board.getCurrentBoardIndex()[0][0]);
		assertTrue(board.changeOnPosition(0,0));
		assertNull(board.getCurrentBoardIndex()[0][0]);
	}
}