package com.sikoramarek.gameOfLife.common;

public class Logger {

	public static void message(String msg, Object source){
		System.out.println(source.toString() + " - "+msg);
	}

	public static void log(String msg, Object source){
		System.out.println(source.toString() + " - "+msg);
	}

	public static void error(Exception e, Object source){
		System.out.println(source.toString()+ "- ERROR - " + e.getMessage());
	}

	public static void error(String msg, Object source){
		System.out.println(source.toString()+ "- ERROR - " + msg);
	}

}
