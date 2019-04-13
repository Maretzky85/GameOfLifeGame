package com.sikoramarek.gameOfLife.common;

public class GameConfig {
	public final int xSize;
	public final int ySize;
	public final int fps;

	public GameConfig(int xSize, int ySize, int fps){
		this.xSize = xSize;
		this.ySize = ySize;
		this.fps = fps;
	}
}
