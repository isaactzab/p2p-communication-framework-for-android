package com.game.activity;

import net.clc.bt.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.game.models.PlayerCredential;

public class GameStart extends Activity implements OnClickListener{

	
	public EditText userNameInput;
	public EditText passwordInput;
	public Button signInButton;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_in);
		
		userNameInput = (EditText) findViewById(R.id.UserNameInput);
		passwordInput = (EditText) findViewById(R.id.PasswordInput);
		signInButton = (Button) findViewById(R.id.SignInButton);
		
		signInButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		PlayerCredential cred= new PlayerCredential(userNameInput.getText().toString());
		Toast.makeText(this, "I have been clicked " + cred.toString(), Toast.LENGTH_SHORT).show(); 
		launchMainMenu(cred);
	}
	
	private void launchMainMenu(PlayerCredential cred){
		Intent startMainMenu = new Intent(this, MainMenu.class);
		startActivity(startMainMenu);
	}
	
	
}
