package com.sikoramarek.gameOfLife.model;

import java.io.Serializable;

public class Dot implements Serializable {
	private static Dot[] dots = new Dot[2];
	public boolean alive;

	private Dot(boolean alive){
		this.alive = alive;
	}

	public static Dot getAlive(){
		if(dots[0]==null){
			dots[0] = new Dot(true);
		}
		return dots[0];
	}
	public static Dot getDead(){
		if(dots[1]==null){
			dots[1] = new Dot(false);
		}
		return dots[1];
	}
}