package com.sikoramarek.gameOfLife.client;

import com.sikoramarek.gameOfLife.common.Logger;
import com.sikoramarek.gameOfLife.common.MessageType;
import com.sikoramarek.gameOfLife.common.Request;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

public class Client implements Runnable, Connection{

	private static Client instance;
	private Socket serviceSocket;

	private ObjectInputStream inputStream;
	private BufferedInputStream bufferedInputStream;
	private ObjectOutputStream outputStream;

	private boolean connected = false;
	private boolean connecting = false;

	private LinkedList<HashMap> receivedList;
	private Vector<HashMap> objectsToSendList;

	private long pingSendTime;

	String host = "localhost";
//  String host = "192.168.8.144";
//  String host = "217.182.73.80";

	private Client(){
		Logger.log("Client created", this);
	}

	public static Client getClient(){
		if (instance == null){
			instance = new Client();
		}
		return instance;
	}

	@Override
	public void connect(){
		if (!connecting && !connected){
			new Thread(this).start();
		}
		connecting = true;
		if (connected){
			Logger.error("Already connected", this);
		}
	}

	@Override
	public void connect(String host) {
		this.host = host;
		connect();
	}

	@Override
	public synchronized void send(HashMap data) {
		objectsToSendList.add(data);
	}

	private void sendToServer(Object object){
		try {
			outputStream.reset();
			outputStream.writeObject(object);
			outputStream.flush();
		} catch (IOException e) {
			Logger.error(e.getMessage(), this);
			disconnect();
		}
	}

	@Override
	public synchronized LinkedList<HashMap> getReceivedList() {
		return receivedList;
	}

	@Override
	public void disconnect(){
		Logger.log("Closing port", this);
		connecting = false;
		connected = false;
		if(serviceSocket != null){
			try {
				serviceSocket.close();
			} catch (IOException e) {
				Logger.error(e, this);
			}
			Logger.log("Client exit", this);
		}
	}

	public boolean isConnected() {
		if (connecting){
			synchronized (this){
				try {
					Logger.log("waiting for connection", this);
					wait();
				} catch (InterruptedException e) {
					Logger.error(e, this);
				}
			}
		}
		return connected;
	}

	private void createConnection(){
		int retry = 3;
		connecting = true;
		do {
			try {
				serviceSocket = new Socket(host, 65432);
				outputStream = new ObjectOutputStream(serviceSocket.getOutputStream());
				bufferedInputStream = new BufferedInputStream(serviceSocket.getInputStream());
				inputStream = new ObjectInputStream(bufferedInputStream);
				receivedList = new LinkedList<>();
				objectsToSendList = new Vector<>();
				connected = true;
				Logger.log("Connected", this);
			} catch (IOException e) {
				retry -= 1;
				Logger.error("Connection problem -  "+e.getMessage(), this);
				connected = false;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					Logger.error("Interrupted", this);
				}
			}
		}while (!connected && retry > 0);
		connecting = false;
		synchronized (this){
			notifyAll();
		}
	}

	@Override
	public void run() {
		createConnection();
		while(connected){
			handleCommunication();
		}
		disconnect();
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			Logger.error(e, this);
		}
	}

	private void handleCommunication() {
		try{
			while (bufferedInputStream.available() > 0){
				Object data = inputStream.readObject();
				if (data instanceof HashMap){
					if (((HashMap) data).containsValue(MessageType.PONG)){
						long pongResponseTime = System.currentTimeMillis();
						Logger.log("ping: "+(pongResponseTime-pingSendTime), this);
					}else{
						receivedList.add((HashMap) data);
						synchronized (this){
							notifyAll();
						}
					}
				}else{
					Logger.error("Wrong data format "+data.toString(), this);
				}
			}
			while (!objectsToSendList.isEmpty()){
				sendToServer(objectsToSendList.get(0));
				objectsToSendList.remove(0);
			}
		} catch (IOException | ClassNotFoundException e) {
			Logger.error(e, this);
			disconnect();
		}

	}

	public void checkPing() {
		HashMap data = new HashMap();
		data.put(Request.class, Request.GET);
		data.put(MessageType.class, MessageType.PING);
		send(data);
		pingSendTime = System.currentTimeMillis();
	}

	@Override
	public String toString(){
		return "Client";
	}

	public synchronized HashMap getResponse() {
		if (receivedList.isEmpty()){
			synchronized (this){
				try {
					Logger.log("waiting", this);
					wait();
				} catch (InterruptedException e) {
					Logger.error(e, this);
				}
			}
		}
		return receivedList.pop();
	}
}