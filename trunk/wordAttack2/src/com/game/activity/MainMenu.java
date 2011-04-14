package com.game.activity;

import net.clc.bt.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainMenu extends Activity implements OnClickListener {

	
	Button createGame;
	Button joinGame;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		createGame = (Button)findViewById(R.id.StartGameServer);
		joinGame = (Button)findViewById(R.id.JoinExistingGame);
		
		createGame.setOnClickListener(this);
		joinGame.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Toast.makeText(this, ""+id, Toast.LENGTH_LONG);
		switch(id){
		case R.id.StartGameServer: 
			Toast.makeText(this, "Starting Game Server", Toast.LENGTH_LONG);
			startGameServer();
			break;
		case R.id.JoinExistingGame: 
			joinExistingGame();
			break;
			default:
		}
		
	}
	
	private void startGameServer(){
		Intent i = new Intent(this, GameServer.class);
		startActivity(i);
	}
	
	private void joinExistingGame(){
		Intent i = new Intent(this, GameClient.class);
		startActivity(i);
	}
 
}
