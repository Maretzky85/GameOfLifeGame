package com.sikoramarek.gameOfLife.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoardSingleThreadTest {
	private ModelSingleThread board;
	private Dot[][] testArray;

	@Before
	public void before(){
		board = new ModelSingleThread(10,10);
		testArray = new Dot[10][10];
		for (int i = 0; i < testArray.length; i++) {
			for (int j = 0; j < testArray[0].length; j++) {
				testArray[i][j] = Dot.DEAD;
			}
		}
	}
	
	private Dot[][] boardFromStringArr(String[] pattern){
		for (int line = 0; line < pattern.length; line++) {
			for (int character = 0; character < pattern[line].length(); character++) {
				switch (pattern[line].charAt(character)){
					case '.':
						testArray[line][character] = Dot.DEAD;
						break;
					case '#':
						testArray[line][character] = Dot.ALIVE;
						break;
				}
			}
		}
		return testArray;
	}

	private String[] stringFromBoard(Dot[][] board){
		String[] resoult = new String[10];
		for (int y = 0; y < board.length; y++) {
			String stringToAppend = "";
			StringBuilder sb = new StringBuilder(stringToAppend);
			for (int x = 0; x < board[y].length; x++) {
				if (board[y][x] == Dot.ALIVE){
					sb.append('#');
				}else {
					sb.append('.');
				}
			}
			resoult[y] = stringToAppend;
		}
		return resoult;
	}

	@Test
	public void EmptyBoardTest() {
		assertArrayEquals(testArray, board.getCurrentBoard());

	}
	@Test
	public void PointInsertionTest(){
		testArray[0][0] = Dot.ALIVE;
		board.changeOnPosition(0,0);
		assertArrayEquals(testArray, board.getCurrentBoard());
	}

	@Test
	public void trafficLightTest() {
		assertArrayEquals(board.getCurrentBoard(), board.getNextGenerationBoard());
		String[] pattern1 = new String[]{
				".#.",
				".#.",
				".#."};
		String[] pattern2 = new String[]{
				"...",
				"###",
				"..."};
		board.importBoard(boardFromStringArr(pattern1));
		assertArrayEquals(stringFromBoard(boardFromStringArr(pattern2)), stringFromBoard(board.getNextGenerationBoard()));
	}

	@Test
	public void gliderTest(){
		String[] pattern1 = new String[]{
				".#..",
				"..#.",
				"###.",
				"...."};
		String[] pattern2 = new String[]{
				"....",
				"#.#.",
				".##.",
				".#.."};
		board.importBoard(boardFromStringArr(pattern1));
		assertArrayEquals(stringFromBoard(boardFromStringArr(pattern2)), stringFromBoard(board.getNextGenerationBoard()));
	}

	@Test
	public void generationCountTest() {
		assertSame(0, board.getCurrentGeneration());
		board.getNextGenerationBoard();
		assertSame(1, board.getCurrentGeneration());
		for (int i = 0; i < 10; i++) {
			board.getNextGenerationBoard();
		}
		assertSame(11, board.getCurrentGeneration());
	}

	@Test
	public void changeOnPositionSuccess() {
		assertTrue(Dot.DEAD == board.getCurrentBoard()[0][0]);
		assertTrue(board.changeOnPosition(0,0));
		assertTrue(board.getCurrentBoard()[0][0] == Dot.ALIVE);

		assertTrue(Dot.ALIVE == board.getCurrentBoard()[0][0]);
		assertTrue(board.changeOnPosition(0,0));
		assertTrue(Dot.DEAD == board.getCurrentBoard()[0][0]);
	}

	@Test
	public void changeOnPositionOutOfBoundsTest(){
		assertFalse(board.changeOnPosition(15,15));
		assertFalse(board.changeOnPosition(-5,-5));
	}

	@Test
	public void changeOnPositionArray(){
		assertArrayEquals(stringFromBoard(testArray), stringFromBoard(board.getCurrentBoard()));
		assertTrue(board.changeOnPositions(new int[][]{ {1,1},{1,2}, {1,3} }));
		testArray[1][1] = Dot.ALIVE;
		testArray[2][1] = Dot.ALIVE;
		testArray[3][1] = Dot.ALIVE;
		String[] pattern = new String[]{
				"...",
				".#.",
				".#.",
				".#."
		};
		testArray = boardFromStringArr(pattern);
		assertArrayEquals(stringFromBoard(testArray), stringFromBoard(board.getCurrentBoard()));
	}

	@Test
	public void importBoard(){
		String[] pattern = new String[]{
				"...",
				".#.",
				".#.",
				".#."
		};
		testArray = boardFromStringArr(pattern);
		assertTrue(board.importBoard(testArray));
		assertArrayEquals(stringFromBoard(testArray), stringFromBoard(board.getCurrentBoard()));
	}

	@Test
	public void differentSizeBoardImport(){
		assertFalse(board.importBoard(new Dot[0][0]));
		assertFalse(board.importBoard(new Dot[50][50]));
	}
}