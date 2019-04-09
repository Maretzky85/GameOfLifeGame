package com.sikoramarek.gameOfLife.controller;

public interface TimingInterface extends Runnable{


	/**
	 * timing mechanism for controlling update frequency. Returning true for next update
	 * obligated to notify treads if ready
	 */
	boolean getUpdate();

}
