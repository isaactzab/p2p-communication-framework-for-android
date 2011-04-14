package com.networking.game;

import java.util.ArrayList;

import net.clc.bt.Connection;
import net.clc.bt.Connection.OnConnectionLostListener;
import net.clc.bt.Connection.OnConnectionServiceReadyListener;
import net.clc.bt.Connection.OnIncomingConnectionListener;
import net.clc.bt.Connection.OnMaxConnectionsReachedListener;
import net.clc.bt.Connection.OnMessageReceivedListener;
import android.util.Log;

import com.game.interfaces.GameCreatorInterface;
import com.game.models.Game;
import com.game.models.Player;

public class GameCreator {

	int expectedNumberOfPlayers;
	int numberOfConnectedPlayers;
	ArrayList<Player> players;
	private Connection mConnection;
	GameCreatorInterface gci;

	public GameCreator(int expectedNumberOfPlayers) {
		this.expectedNumberOfPlayers = expectedNumberOfPlayers;
		players = new ArrayList<Player>();
	}

	public void waitForPlayersToJoin() {
		while (!startConditionsMet()) {
			Log.i("PlayerNumber", "" + players.size());
		}
	}

	private boolean startConditionsMet() {
		return players.size() == expectedNumberOfPlayers;
	}

	public void startGameCreation(GameCreatorInterface gci) {
		this.gci = gci;	
	}

	public Game createGame() {

		waitForPlayersToJoin();
		return new Game();
	}
	
	
	
}
