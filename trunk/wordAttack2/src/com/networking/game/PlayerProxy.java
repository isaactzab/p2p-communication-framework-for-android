package com.networking.game;

import net.clc.bt.Connection;
import android.util.Log;

import com.game.models.PlayerCredential;

public class PlayerProxy {
	private PlayerCredential pc;
	private Connection connection;
	private String gameHost;
	
	/**
	 * @return the gameHost
	 */
	public String getGameHost() {
		return gameHost;
	}

	/**
	 * @param gameHost the gameHost to set
	 */
	public void setGameHost(String gameHost) {
		this.gameHost = gameHost;
	}

	public PlayerProxy(PlayerCredential cred, Connection connection, String gameHost){
		this.pc = cred;
		this.connection = connection;	
		this.gameHost = gameHost;
	}
	
	public void sendMessageToServer(Integer i){
		Log.e("Message To Pass", gameHost + " "+ encode(i.toString()));
		
		connection.sendMessage(gameHost, encode(i.toString()));
	}
	
	public void sendMessageToServer(String i){
		connection.sendMessage(gameHost, encode(i));
	}
	
	public byte[] encode(String message){
		return (message + pc.toString()).getBytes();
	}
}
