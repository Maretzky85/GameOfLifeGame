package com.sikoramarek.gameOfLife.controller;

import com.sikoramarek.gameOfLife.client.Client;
import com.sikoramarek.gameOfLife.common.*;
import com.sikoramarek.gameOfLife.model.Dot;
import com.sikoramarek.gameOfLife.model.Model;
import com.sikoramarek.gameOfLife.view.JavaFXView;
import com.sikoramarek.gameOfLife.view.MenuAction;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class Controller implements Runnable{

	private static GameState gameState;

	private ResourceManager resourceManager;

	private Model model;

	private JavaFXView view;

	private TimingInterface timing;

	private Client client;

	Stage primaryStage;

	Stage modelStage;
	private boolean multiplayer = false;
	private GameConfig config;
	private Dot[][] secondPlayerBoard;
	private Integer generation = -1;
	private long responseTime;
	private boolean update;

	public Controller(Stage stage){
		primaryStage = stage;
		gameState = GameState.INIT;
	}

	public static synchronized void setState(GameState state){
		gameState = state;
	}

	@Override
	public void run() {
		while(true){
			switch (gameState) {
				case INIT:
					//TODO load required assets and switch to menu
					resourceManager = ResourceManager.getInstance();
					gameState = GameState.MENU;
					break;
				case LOADING:
					//TODO load all needed to play assets
					Logger.log("Loading", this);
					if (config == null){
						config = resourceManager.getMenu().getConfig();
					}
					model = resourceManager.getNewBoard(config.xSize,config.ySize);
					model.changeOnPositions(positions);
					view = resourceManager.getNewView();
					if (client != null){
						view.setMulti(true);
					}
					view.viewInit(config.xSize, config.ySize);
					timing = resourceManager.getTimingControl();
					timing.setFRAME_RATE(config.fps);
					Platform.runLater(()->{

						modelStage = new Stage();
						modelStage.initStyle(StageStyle.UTILITY);
						modelStage.setScene(view.getScene());
						modelStage.show();

					});
					while(modelStage == null || !modelStage.isShowing()){
						synchronized (this){
							try {
								wait(5);
							} catch (InterruptedException e) {
								Logger.error(e, this);
							}
						}
					}
					new Thread(timing).start();
					if(multiplayer){
						gameState = GameState.MULTIPLAYER;
					}else {
						gameState = GameState.RUNNING;
					}

					break;
				case MENU:
					if (!primaryStage.isShowing()){
						Platform.runLater(() -> {
							primaryStage.setScene(resourceManager.getMenu().getMenuScene());
							primaryStage.show();
						});
						while(!primaryStage.isShowing()){
							synchronized (this){
								try {
									wait(5);
								} catch (InterruptedException e) {
									Logger.error(e, this);
								}
							}
						}
					}
					MenuAction action = resourceManager.getMenu().getAction();
					if (action != null){
						handleMenuAction(action);
					}else {
						try {
							synchronized (this){
								wait(10);
							}
						} catch (InterruptedException e) {
							Logger.error(e, this);
						}
					}
					break;
				case PAUSED:
					checkInput();
					synchronized (this){
						try {
							wait(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					break;
				case RUNNING:
					checkInput();
					if (modelStage.isShowing()){
						if(timing.getUpdate()){
							view.refresh(model.nextGenerationBoard());
						}
					}else {
						gameState = GameState.MENU;
					}
					break;
				case MULTIPLAYER_CONNECTING:
					if (client == null){
						Logger.log("NewClient", this);
						client = Client.getClient();
					}
					if (!client.isConnecting()){
						Logger.log("Trying to connect", this);
						client.connect();
					}
					while (client.isConnecting()){
						synchronized (this){
							try {
								wait(50);
							} catch (InterruptedException e) {
								Logger.error(e, this);
							}
						}
					}
					client.checkPing();
					if (client.isConnected()){
						gameState = GameState.MULTIPLAYER_CONFIG;
					}
				case MULTIPLAYER_CONFIG:
					if(!client.isConnected()){
						gameState = GameState.MULTIPLAYER_CONNECTING;
					}else {
						multiplayer = true;
						if (negotiateConfig()){
							gameState = GameState.LOADING;
						}
						synchronized (this){
							try {
								wait(20);
							} catch (InterruptedException e) {
								Logger.error(e, this);
							}
						}
					}
					break;
				case MULTIPLAYER:
					handleServerResponse();
					checkInput();
					if (System.currentTimeMillis() - responseTime > 2000){
						sendGetBoard();
						responseTime = System.currentTimeMillis();
					}
					if (modelStage.isShowing()){
						if(update){
//							if (generation >= model.getCurrentGeneration()){
								model.nextGenerationBoard();
								update = false;
//							}
							sendCurrentBoard();
							view.refresh(model.getCurrentBoard(), secondPlayerBoard);
						}else{
							update = timing.getUpdate();
						}
					}
					break;
			}
		}
	}
	private void sendGetBoard(){
		HashMap request = new HashMap();
		request.put(Request.class, Request.GET);
		request.put(MessageType.class, MessageType.BOARD);
		client.send(request);
	}

	private void sendCurrentBoard(){
		HashMap iteration = new HashMap();
		iteration.put(Request.class, Request.PUT);
		iteration.put(MessageType.class, MessageType.ITERATION);
		iteration.put(MessageType.ITERATION, model.getCurrentGeneration());
		HashMap board = new HashMap();
		board.put(Request.class, Request.PUT);
		board.put(MessageType.class, MessageType.BOARD);
		board.put(MessageType.BOARD, model.getCurrentBoard());
		client.send(board);
		client.send(iteration);
	}

	private boolean negotiateConfig() {
		GameConfig configToSend = resourceManager.getMenu().getConfig();
		if (configToSend == this.config){
			return true;
		}
		LinkedList received = client.getReceivedList();
		if (!received.isEmpty()){
			Object response = received.pop();
			if (response instanceof HashMap){
				HashMap data = (HashMap) response;
				if (data.get(Request.class) == Request.GET){
					if (data.get(MessageType.class) == MessageType.CONFIG){
						if (data.get(Response.class) == Response.ACCEPTED){
							this.config = configToSend;
							Logger.log("Config Accepted", this);
							return true;
						}else {
							HashMap request = new HashMap();
							request.put(Request.class, Request.GET);
							request.put(MessageType.class, MessageType.CONFIG);
							client.send(request);
							Logger.log("Request arena config", this);
						}
					}
				}else
				if (data.get(Request.class) == Request.PUT){
					if (data.get(MessageType.class) ==  MessageType.CONFIG){
						this.config = (GameConfig) data.get(MessageType.CONFIG);
						Logger.log("Config Loaded", this);
						return true;
					}
				}
			}else{
				Logger.error("Communication error", this);
			}
		}

		HashMap config = new HashMap();
		config.put(Request.class, Request.PUT);
		config.put(MessageType.class, MessageType.CONFIG);
		config.put(MessageType.CONFIG, configToSend);
		client.send(config);
		Logger.log("Sent Config", this);
		return false;
	}

	private void handleServerResponse() {
		LinkedList<HashMap> received = client.getReceivedList();
		while(!received.isEmpty()){
			HashMap data = received.pop();
			if (data.get(Request.class) == Request.PUT){
				handlePutRequest(data);
			}
			if (data.get(Request.class) == Request.GET){
				handleGetRequest(data);
			}

		}
	}

	private void handleGetRequest(HashMap data) {
		responseTime = System.currentTimeMillis();
		HashMap response = new HashMap();
		response.put(Request.class, Request.PUT);
		if (data.get(MessageType.class) == MessageType.ITERATION){
			response.put(MessageType.class, MessageType.ITERATION);
			response.put(MessageType.ITERATION, model.getCurrentGeneration());
		}else
		if (data.get(MessageType.class) == MessageType.BOARD){
			response.put(MessageType.class, MessageType.BOARD);
			response.put(MessageType.BOARD, model.getCurrentBoard());
		}else
		if (data.get(MessageType.class) == MessageType.MESSAGE){
			Logger.error("WTF, message in get request?", this);
		}else
		if (data.get(MessageType.class) == MessageType.CONFIG){
			if (data.containsKey(Response.class)){
				return;
			}
			response.put(MessageType.class, MessageType.CONFIG);
			response.put(MessageType.CONFIG, config);
			Logger.log("sending config", this);
		}else{
			Logger.error("Wrong format", this);
			return;
		}
		client.send(response);
	}

	private void handlePutRequest(HashMap data) {
		responseTime = System.currentTimeMillis();
		if (data.get(MessageType.class) == MessageType.ITERATION){
			Integer iteration =(Integer) data.get(MessageType.ITERATION);
			if (iteration != null){
				generation = iteration;
			}
		}else
		if (data.get(MessageType.class) == MessageType.BOARD){
			Dot[][] board = (Dot[][]) data.get(MessageType.BOARD);
			if (board != null) {
				secondPlayerBoard = board;
			}
		}else
		if (data.get(MessageType.class) == MessageType.MESSAGE){
			Logger.log("Message", this);
			Logger.log(data.get(MessageType.MESSAGE).toString(), this);
		}else
		if (data.get(MessageType.class) == MessageType.CONFIG){
			Logger.log("Config?", this);
		}else if ((data.get(MessageType.class) == MessageType.PONG)){

		}else if ((data.get(MessageType.class) == MessageType.POSITION)){
			Integer[] position = (Integer[]) data.get(MessageType.POSITION);
			System.out.println("position");
			model.changeOnPosition(position[0], position[1]);
		}
		else{
			Logger.log("WTF"+data.toString(), this);
		}
	}

	private void checkInput() {
		if(!SharedResources.positions.isEmpty()){
			for (int[] position: SharedResources.positions){
				System.out.println(position[0]);
				if (position[0] > model.getCurrentBoard()[0].length){
					HashMap data = new HashMap();
					data.put(Request.class, Request.PUT);
					data.put(MessageType.class, MessageType.POSITION);
					Integer[] positionToSend = new Integer[]{position[0]-model.getCurrentBoard()[0].length, position[1]};
					data.put(MessageType.POSITION, (Integer[]) positionToSend);
					client.send(data);
				}
			}
			model.changeOnPositions(SharedResources.positions);
			SharedResources.positions.clear();
		}
		LinkedList<KeyCode> keyboardInputs = SharedResources.getKeyboardInput();
		if(!keyboardInputs.isEmpty()){
			while (!keyboardInputs.isEmpty()){
				KeyCode key = keyboardInputs.pop();
				switch (key){
					case P:
						if (gameState == GameState.RUNNING){
							gameState = GameState.PAUSED;
						}else
							if (gameState == GameState.PAUSED){
								gameState = GameState.RUNNING;
							}

				}
			}
		}
	}

	private void handleMenuAction(MenuAction action) {
		switch (action) {
			case START:
				gameState = GameState.LOADING;
				break;
			case CONNECT:
				gameState = GameState.MULTIPLAYER_CONNECTING;
				break;
		}
	}

	@Override
	public String toString(){
		return "Controller";
	}
	int[][] positions = new int[][]{
			{8,1},
			{7,1},
			{8,2},
			{7,2},
			{8,12},
			{7,12},
			{6,12},
			{9,13},
			{5,13},
			{4,14},
			{10,14},
			{5,15},
			{9,15},
			{8,16},
			{7,16},
			{6,16},
			{8,17},
			{7,17},
			{6,17},
			{6,22},
			{5,22},
			{4,22},
			{3,23},
			{4,23},
			{6,23},
			{7,23},
			{3,24},
			{4,24},
			{6,24},
			{7,24},
			{3,24}
			, {3,25}
			, {4,25}
			, {5,25}
			, {6,25}
			, {7,25}
			, {2,26}
			, {3,26}
			, {7,26}
			, {8,26}
			, {3,31}
			, {4,31}
			, {5,35}
			, {6,35}
			, {5,36}
			, {6,36}
	};
}
