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
import android.widget.ImageButton;
import android.widget.Toast;

import com.game.activity.GameServer;

public class GameRoomChatHost extends Activity {
	Button btn1;
	ImageButton ibtn1,ibtn2;
	EditText inputbox;
	String GameRoomName;
	private static final String TAG = "MyActivity";
	
	@Override
	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);
		setContentView(R.layout.chat_host);
		btn1 = (Button) findViewById(R.id.button_send);
		ibtn1= (ImageButton) findViewById(R.id.imageButton1);
		ibtn2 = (ImageButton) findViewById(R.id.imageButton2);
		Intent i = getIntent();
		GameRoomName = i.getStringExtra("ChatRoomHost");
		inputbox = (EditText)findViewById(R.id.chatcontent);

		btn1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "Button Start is click");
				Log.d(TAG,inputbox.getText().toString());
			}
		});
		
		ibtn2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "Button Exit is click");
				if(GameRoomLogin.iSC.leaveChatRoom(GameRoomName)){
					Log.d(TAG, "Button Exit is click");
					Toast.makeText(getApplicationContext(),
							"You leave Game Room "+GameRoomName, Toast.LENGTH_SHORT).show();
			        Intent j = new Intent(GameRoomChatHost.this, GameRoomMain.class);
					startActivity(j);
				}
			}
		});
		
		ibtn1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(GameRoomChatHost.this, GameServer.class);
				i.putExtra("GameRoomName", GameRoomName);
				startActivity(i);
			}
		});

	}
	

}
		
		

		



