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

public class GameRoomSignup extends Activity {
	Button btn1;
	EditText inputbox1, inputbox2,inputbox3;
	String temp1, temp2,temp3;
	private static final String TAG = "MyActivity";
	
	@Override
	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);
		Log.d(TAG, "onCREAT");
		setContentView(R.layout.sign_up);
		btn1 = (Button) findViewById(R.id.sign_up);
		inputbox1 = (EditText) findViewById(R.id.inputnick);
		inputbox2 = (EditText) findViewById(R.id.inputemail);
		inputbox3 = (EditText) findViewById(R.id.inputpassword);
		btn1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "Button Log In is click");
				Log.d(TAG, inputbox1.getText().toString());
				Signup();
			}
		});
	}

	private void Signup() {
		temp1 = inputbox1.getText().toString();
		temp2 = inputbox2.getText().toString();
		temp3 = inputbox3.getText().toString();
		
		if (temp1.length() <= 0 || temp2.length() <= 0) {
			Toast.makeText(getApplicationContext(),
					"Please fill in Email Address and Password",
					Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Please fill in Email Address and Password");
		} else {
			if (!GameRoomLogin.iSC.listenSocket()){
				Toast.makeText(getApplicationContext(), "Server not Found.",Toast.LENGTH_SHORT).show();	

				Intent i = new Intent(this, GameRoomLogin.class);
				startActivity(i);
			}
			else{
			
			if(GameRoomLogin.iSC.signUp(temp1, temp2, temp3)){
			Toast.makeText(getApplicationContext(), "Sign up Successfully",
					Toast.LENGTH_SHORT).show();
			Toast.makeText(getApplicationContext(), "Please Log in",
					Toast.LENGTH_SHORT).show();

			Intent i = new Intent(this, GameRoomLogin.class);
			startActivity(i);
			}
			else
				Toast.makeText(getApplicationContext(), "Sign up Not Successful",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

}
