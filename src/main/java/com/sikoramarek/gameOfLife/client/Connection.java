package com.sikoramarek.gameOfLife.client;

public interface Connection {

	void connect();

	void connect(String host);

	void send(Object object);

	Object getReceived();

	void disconnect();

}
