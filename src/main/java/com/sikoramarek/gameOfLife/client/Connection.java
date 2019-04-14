package com.sikoramarek.gameOfLife.client;

import java.util.HashMap;

public interface Connection {

	void connect();

	void connect(String host);

	void send(HashMap data);

	Object getReceivedList();

	void disconnect();

}
