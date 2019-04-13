package com.sikoramarek.gameOfLife.common;

import javafx.scene.input.KeyCode;

import java.util.LinkedList;

public class SharedResources {

	private static LinkedList<KeyCode> keyboardInput = new LinkedList<>();

	public static LinkedList<int[]> positions = new LinkedList<>();

	public static void addKeyboardInput(KeyCode key){
		if(!keyboardInput.contains(key)){
			keyboardInput.add(key);
		}
	}

	public static LinkedList<KeyCode> getKeyboardInput(){
		return keyboardInput;
	}

	public static void clearKeyboardInput(){
		keyboardInput.clear();
	}

}
