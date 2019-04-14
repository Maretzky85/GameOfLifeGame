package com.sikoramarek.gameOfLife.client;

import com.sikoramarek.gameOfLife.common.Logger;
import com.sikoramarek.gameOfLife.model.Dot;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

public class Client implements Runnable, Connection{

	private static Client instance;
	private Socket serviceSocket;

	private ObjectInputStream inputStream;
	private BufferedInputStream bufferedInputStream;
	private ObjectOutputStream outputStream;

	String host = "192.168.8.144";

	private boolean connected = false;
	private boolean connecting =false;

	private LinkedList<String> messagesToSend;
	private LinkedList received;


	private Client(){
		Logger.log("Client created", this);
	}

	public static Client getClient(){
		if (instance == null){
			instance = new Client();
		}
		return instance;
	}

	public void connect(){
		connecting = true;
		if (connected){
			Logger.error("Already connected", this);
		}else{
			new Thread(this).start();
		}
	}

	@Override
	public void connect(String host) {
		this.host = host;
		connect();
	}

	@Override
	public void send(Object object) {
		try {
			outputStream.reset();
			outputStream.writeObject(object);
			outputStream.flush();
		} catch (IOException e) {
			Logger.error(e.getMessage(), this);
		}

	}

	@Override
	public LinkedList getReceived() {
		return received;
	}

	@Override
	public void disconnect(){
		if(serviceSocket != null){
			try {
				inputStream.close();
				outputStream.close();
				serviceSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isConnected() {
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
				messagesToSend = new LinkedList<>();
				received = new LinkedList();
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
	public String toString(){
		return "Client";
	}

	@Override
	public void run() {
		//			serviceSocket = new Socket("217.182.73.80", 65432);
		createConnection();
		while(connected){
			while (messagesToSend.size() > 0){
				send(messagesToSend.removeFirst());
			}
			handleResponse();
		}
		disconnect();
	}

	private void handleResponse() {
		try{
			while (bufferedInputStream.available() > 0){
				received.add(inputStream.readObject());
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			disconnect();
			Platform.exit();
		}

	}

	public boolean isConnecting() {
		return connecting;
	}
}

