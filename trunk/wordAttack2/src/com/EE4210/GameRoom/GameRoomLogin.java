package com.EE4210.GameRoom;

/* This class is the main logic when the game setting button is clicked
 * 
 */

import indexServerClient.IndexServerClientMain;
import net.clc.bt.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GameRoomLogin extends Activity {
	Button btn1, btn2,btn3;
	TextView ipaddress;
	EditText inputbox1, inputbox2,inputbox3;
	String UserEmail, UserPassword;
	String TAG = "LoginActivity";
	String IPaddress;
	Vibrator v;
	MediaPlayer mp;
//	private static final String TAG = "MyActivity";
	public static IndexServerClientMain iSC;
	public static boolean connectSuccess = false;

	
	@Override
	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);
		setContentView(R.layout.gameroom_welcome);
		btn1 = (Button) findViewById(R.id.log_in);
		btn2 = (Button) findViewById(R.id.sign_up);
		btn3 = (Button) findViewById(R.id.connect);
		inputbox1 = (EditText) findViewById(R.id.inputemail);
		inputbox2 = (EditText) findViewById(R.id.inputpassword);
		inputbox3 = (EditText) findViewById(R.id.inputIP);
		ipaddress = (TextView) findViewById(R.id.text2);
		inputbox3.setVisibility(View.INVISIBLE);
		btn3.setVisibility(View.INVISIBLE);
		iSC = new IndexServerClientMain(getBlueToothAddress());
		connectSuccess = iSC.listenSocket();
		if (connectSuccess){
			ipaddress.setText("       "+iSC.getServerIP());
			btn1.setEnabled(true);
			btn2.setEnabled(true);
		}
		else{
			btn1.setEnabled(false);
			btn2.setEnabled(false);
			ipaddress.setText("  Server Not Found");
			inputbox3.setVisibility(View.VISIBLE);
			btn3.setVisibility(View.VISIBLE);
			Toast.makeText(getApplicationContext(),
					"Server Not Found, Please Configure",
					Toast.LENGTH_SHORT).show();
		}
		// Get instance of Vibrator from current Context
		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		btn1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				Log.d(TAG, "Button Log In is click");
//				Log.d(TAG, inputbox1.getText().toString());
				Login();
			}
		});

	
		btn2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	//			Log.d(TAG, "Button Sign up is click");
				Signup();
			}
		}); 
		
		btn3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			IPaddress = inputbox3.getText().toString();
			iSC.setServerIP(IPaddress);
			connectSuccess = iSC.listenSocket();
			if (connectSuccess){
				Toast.makeText(getApplicationContext(),
						"Server Connected",
						Toast.LENGTH_SHORT).show();
				inputbox3.setVisibility(View.INVISIBLE);
				btn3.setVisibility(View.INVISIBLE);
				btn1.setEnabled(true);
				btn2.setEnabled(true);
				ipaddress.setText("        "+iSC.getServerIP());
			}
			else{    
				btn1.setEnabled(false);
				btn2.setEnabled(false);
				Toast.makeText(getApplicationContext(),
						"Server Not Found",
						Toast.LENGTH_SHORT).show();
			}
			}
		});
	}

	private String getBlueToothAddress(){
		BluetoothAdapter adt;
		adt = BluetoothAdapter.getDefaultAdapter();
		String dev = adt.getAddress();
		adt = null;
		return dev;
	}
	
	private void Login() {
		UserEmail = inputbox1.getText().toString();
		UserPassword = inputbox2.getText().toString();
		if (UserEmail.length() <= 0 || UserPassword.length() <= 0) {
			Toast.makeText(getApplicationContext(),
					"Please fill in Email Address and Password",
					Toast.LENGTH_SHORT).show();
			v.vibrate(300);

			//Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			//v.vibrate(300);

			Log.d(TAG, "vibarate");
		} else {
			if (!connectSuccess){
				Toast.makeText(getApplicationContext(), "Server not Found.",Toast.LENGTH_SHORT).show();	
				inputbox3.setVisibility(View.VISIBLE);
				ipaddress.setText("Server Not Found");
				btn3.setVisibility(View.VISIBLE);
			}
				
			else{
			//create indexServerClient
			//iSC = new IndexServerClientMain("bluetoothAddress");
			iSC.inputUserData("u1",UserEmail, UserPassword);
	//		Log.d(TAG,"Enter the password");
			if(iSC.signIn()){
			Toast.makeText(getApplicationContext(), "Log in Successfully",Toast.LENGTH_SHORT).show();
			
			Intent i = new Intent(this, GameRoomMain.class);
			startActivity(i);
			}
			else{
				Toast.makeText(getApplicationContext(), "Account and Password Mismatch",
						Toast.LENGTH_SHORT).show();
				v.vibrate(300);
				}
			}
		}

	}
	private void Signup(){
		Intent i = new Intent(this, GameRoomSignup.class);
		startActivity(i);
	}

}
