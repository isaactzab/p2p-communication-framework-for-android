package com.networking.game;

import java.util.ArrayList;

import com.game.models.Player;
import com.game.models.PlayerCredential;

import net.clc.bt.Connection.OnConnectionLostListener;
import net.clc.bt.Connection.OnConnectionServiceReadyListener;
import net.clc.bt.Connection.OnIncomingConnectionListener;
import net.clc.bt.Connection.OnMaxConnectionsReachedListener;
import net.clc.bt.Connection.OnMessageReceivedListener;
import android.util.Log;

public class PlayerNetworkingIntent{
	
	ArrayList<Player> players;
	
	public PlayerNetworkingIntent(){
		this.players = new ArrayList<Player>();
	}
	
	public void addPlayer(Player p){
		this.players.add(p);
	}
	
	public void triggerWithCredentials(PlayerCredential c, String message){
		for(Player p : players){
			if(p.getPc().equals(c)){
				p.sendMessage(message);
				Log.d("Player", p.getPc().toString());
			}
		}
	}
	
	   private OnMessageReceivedListener dataReceivedListener = new OnMessageReceivedListener() {
	        @Override
			public void OnMessageReceived(String device, byte[] message) {
	         
	        }
	    };
	    
	    private OnMaxConnectionsReachedListener maxConnectionsListener = new OnMaxConnectionsReachedListener() {
	        @Override
			public void OnMaxConnectionsReached() {

	        }
	    };

	    private OnIncomingConnectionListener connectedListener = new OnIncomingConnectionListener() {
	        @Override
			public void OnIncomingConnection(String device) {       		       		

	        }
	    };

	    private OnConnectionLostListener disconnectedListener = new OnConnectionLostListener() {
	        @Override
			public void OnConnectionLost(String device) {

	        }
	    };

	    private OnConnectionServiceReadyListener serviceReadyListener = new OnConnectionServiceReadyListener() {
	        @Override
			public void OnConnectionServiceReady() {
	        	Log.i("Prj", "Hello World");
	        }
	    };
}
