package com.sikoramarek.gameOfLife.common;

import java.io.Serializable;

public class GameConfig implements Serializable {
	public final int xSize;
	public final int ySize;
	public final int fps;

	public GameConfig(int xSize, int ySize, int fps){
		this.xSize = xSize;
		this.ySize = ySize;
		this.fps = fps;
	}
}
