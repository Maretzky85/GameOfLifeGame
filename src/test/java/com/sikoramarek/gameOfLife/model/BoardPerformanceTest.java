package com.sikoramarek.gameOfLife.model;

import org.junit.Test;

public class BoardPerformanceTest {

	@Test
	public void performanceTestSmallBoard(){
		int REPEAT_TIMES = 10000;
		ModelSingleThread board = new ModelSingleThread(50,50);
		long startTime = System.currentTimeMillis();
		while(board.getCurrentGeneration() < REPEAT_TIMES){
			board.getNextGenerationBoard();
		}
		long timeTaken = System.currentTimeMillis() - startTime;
		int genPerSec = (int) (1000/ ((double)timeTaken/REPEAT_TIMES));
		System.out.println("Performance on 50x50 table: " + (double)timeTaken/REPEAT_TIMES+ "ms per generation, "+genPerSec+" generations/sec");
	}

	@Test
	public void performanceTestBigBoard(){
		int REPEAT_TIMES = 200;
		ModelSingleThread board = new ModelSingleThread(1000,1000);
		long startTime = System.currentTimeMillis();
		while(board.getCurrentGeneration() < REPEAT_TIMES){
			board.getNextGenerationBoard();
		}
		long timeTaken = System.currentTimeMillis() - startTime;
		int genPerSec = (int) (1000/ ((double)timeTaken/REPEAT_TIMES));
		System.out.println("Performance on 1000x1000 table: " + (double)timeTaken/REPEAT_TIMES+ "ms per generation, "+genPerSec+" generations/sec");
	}
}
