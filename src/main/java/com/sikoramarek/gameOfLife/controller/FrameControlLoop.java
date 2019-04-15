package com.sikoramarek.gameOfLife.controller;


import com.sikoramarek.gameOfLife.common.Logger;

/**
 * This class is for controlling how many frames is generated for second
 * This is clock for theoretical model, that run selected functions in requested frequency
 */

public class FrameControlLoop implements TimingInterface{

	private int FRAME_RATE = 30;

	private boolean isRunning = false;

	private int tics = 0; //For FPS Debugging

	private long initialTime = System.currentTimeMillis(); //time for Loop Control
	private long startTime = System.currentTimeMillis(); //initial time for FPS console logging
	private long timeFrame = 1000 / FRAME_RATE; //time in milliseconds for one loop;
	private long timeCounterMs = 0; //milliseconds counter
	private int FPS = 0;

	private boolean update = false;


	FrameControlLoop() {
	}

	/**
	 * Run function for starting loop control
	 * checks time between current time and start time, waits for rest ms,
	 * if time between current and start time is greater than time frame, than runs required command.
	 */
	@Override
	public void run() {
		isRunning = true;
		while (isRunning) {
			long currentTime = System.currentTimeMillis();
			timeCounterMs += (currentTime - initialTime);
			initialTime = currentTime;

			if (timeCounterMs >= timeFrame) {
				update = true;
				synchronized (this){
					notifyAll();
				}

				Thread.yield();
				tics += 1;
				timeCounterMs = 0;
			}else
			if(timeFrame - timeCounterMs > 0){
				try {
					Thread.sleep(timeFrame - timeCounterMs);
				} catch (InterruptedException e) {
					Logger.error(e, this);
				}
			}

			//FPS logging ======================
			if (currentTime - startTime > 1000) {
//				statTimer.run();
				startTime = System.currentTimeMillis();
				FPS = tics;
				tics = 0;
			}
			//===============================================
		}
	}

	public boolean getUpdate(){
		if (update){
			update = false;
			return true;
		}else {
			synchronized (this){
				try {
					wait(Math.abs(timeFrame - timeCounterMs));
				} catch (InterruptedException e) {
					Logger.error(e, this);
				}
			}
		}
		return false;
	}

	/**
	 * function for stopping FrameControlLoop
	 */
	void toggleLoopState() {
		isRunning = !isRunning;
	}

	/**
	 * decrease/increase speed
	 * decrease or increase update speed by altering timeframe value
	 */
	boolean decreaseSpeed() {
		if (FRAME_RATE > 1){
			timeFrame = 1000/FRAME_RATE-1;
			return true;
		}else
			return false;

	}

	boolean increaseSpeed() {
		if (FRAME_RATE < 200){
			timeFrame = 1000/FRAME_RATE+1;
			return true;
		}else
			return false;
	}

	public synchronized void setFRAME_RATE(int FRAME_RATE) {
		this.FRAME_RATE = FRAME_RATE;
		timeFrame = 1000 / FRAME_RATE;

	}

	int getCurrentFPS() {
		return FPS;
	}

	int getRequestedFramerate(){
		return FRAME_RATE;
	}

	@Override
	public String toString(){
		return "Control Loop";
	}
}
