package com.EE4210.GameRoom;

/* This class is the main logic when the game setting button is clicked
 * 
 */

import indexServerClient.User;
import net.clc.bt.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GameRoomMain extends Activity {
	Button btn1, btn2,btn3;
	TextView message;
	private static final String TAG = "MyActivity";
	//private User u;
	private String username;
	@Override
	public void onCreate(Bundle icicle) {

		
		super.onCreate(icicle);
		Log.d(TAG, "onCREAT");
		setContentView(R.layout.gameroommain);
		try{
		username =GameRoomLogin.iSC.getUser().getUserName();
		}catch(Exception e){
			username = "nobody";
			
		}
		btn1 = (Button) findViewById(R.id.gr_main_button1);
		message = (TextView) findViewById(R.id.hello);
		message.setText("  "+username);
		btn1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CreateGame();
			}
		});
		
		btn2 = (Button) findViewById(R.id.gr_main_button2);
		btn2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(),"Connecting......", Toast.LENGTH_SHORT).show();
				JoinGame();
				
			}
		});
		btn3 = (Button) findViewById(R.id.gr_main_button3);
		btn3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "Button 3 is click");
				Logout();
			}
		});
		Log.d(TAG, "onEnding");

	}
	private void CreateGame(){
		Log.d(TAG,"About to Create Game");
		Intent i = new Intent(this, GameRoomCreate.class);
		startActivity(i);
	}
	private void JoinGame(){
		Log.d(TAG,"About to Join Game");
		Intent i = new Intent(this, GameRoomJoin.class);
		startActivity(i);
	}
	private void Logout(){
		Log.d(TAG,"About to Log out");
		GameRoomLogin.iSC.signOut();
		//GameRoomLogin.iSC=null;
		Toast.makeText(getApplicationContext(),"Thanks you for using", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this, GameRoomLogin.class);
		startActivity(i);	
	}
}
		


