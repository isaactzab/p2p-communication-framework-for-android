package com.EE4210.GameRoom;

/* This class is the main logic when the game setting button is clicked
 * 
 */


import net.clc.bt.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.game.activity.GameServer;

public class GameRoomCreate extends Activity {
	Button btn1;
	EditText inputbox;
	String GameRoomName;
	private static final String TAG = "MyActivity";
	
	@Override
	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);
		Log.d(TAG, "onCREAT");
		setContentView(R.layout.create);
		btn1 = (Button) findViewById(R.id.gr_create);
		inputbox = (EditText)findViewById(R.id.gr_editText1);
		btn1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "Button OK is click");
				Log.d(TAG,inputbox.getText().toString());

				GameRoomCreateBack();
			}
		});
	}
	
	private void GameRoomCreateBack(){
		GameRoomName = inputbox.getText().toString();
		if ( GameRoomName.length()<=0){
			Toast.makeText(getApplicationContext(),
					"Please fill in name", Toast.LENGTH_SHORT).show();
			Log.d(TAG,"Please fill in name");
		}
		else{
			Log.d(TAG,"Create Game Room");
			if (GameRoomLogin.iSC.createChatRoom(GameRoomName)){
				Toast.makeText(getApplicationContext(),
				"Room Created Successfully", Toast.LENGTH_SHORT).show();
				Toast.makeText(getApplicationContext(),
						"Welcome to "+GameRoomName, Toast.LENGTH_SHORT).show();

        Intent i = new Intent(GameRoomCreate.this, GameServer.class);
        i.putExtra("ChatRoomHost", GameRoomName);
		startActivity(i);
			}
			
			else{
				Toast.makeText(getApplicationContext(),
						"Room Created Unsuccessful", Toast.LENGTH_SHORT).show();
			}
		}	
	}

}
		



