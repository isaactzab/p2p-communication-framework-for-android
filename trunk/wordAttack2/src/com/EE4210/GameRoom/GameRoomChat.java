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

public class GameRoomChat extends Activity {
	Button btn1;
	ImageButton btn2;
	EditText inputbox;
	String GameRoomName;
	private static final String TAG = "MyActivity";
	private String ChatRoomName;
	

	
	@Override
	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);
		setContentView(R.layout.gameroomchat);
		btn1 = (Button) findViewById(R.id.button_send);
		btn2 = (ImageButton) findViewById(R.id.iBexit);
		Intent startingIntent = getIntent();
		ChatRoomName = startingIntent.getStringExtra("GameRoomToJoinFrom");
		inputbox = (EditText)findViewById(R.id.chatcontent);

		btn1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "Button Start is click");
				Log.d(TAG,inputbox.getText().toString());
			}
		});
		
		btn2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(GameRoomLogin.iSC.leaveChatRoom(ChatRoomName)){
				Log.d(TAG, "Button Exit is click");
				Toast.makeText(getApplicationContext(),
						"You leave Game Room "+ChatRoomName, Toast.LENGTH_SHORT).show();
		        Intent j = new Intent(GameRoomChat.this, GameRoomMain.class);
				startActivity(j);
				}
			}
		});

	}
	

}
		
		

		



