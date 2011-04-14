package com.EE4210.GameRoom;

/* This class is the main logic when the game setting button is clicked
 * 
 */
import indexServerClient.ChatRoom;

import java.util.ArrayList;
import java.util.List;

import net.clc.bt.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.game.activity.GameClient;

public class GameRoomJoin extends ListActivity {
	private static final String TAG = "ListActivity";
	private List<String> GameRoomList = new ArrayList<String>();
	private String[] GameRoomListString = new String[GameRoomList.size()];
	@Override
	public void onCreate(Bundle icicle) {

		
		super.onCreate(icicle);
		Log.d(TAG, "onCREAT");
		try{
			GameRoomList = GameRoomLogin.iSC.getChatRoomList();
	
			GameRoomListString = BuildString2(GameRoomList);
			setListAdapter(new ArrayAdapter<String>(this, R.layout.join, BuildString(GameRoomList)));
			getListView().setTextFilterEnabled(true);
			
			ListView lv = getListView();
			lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// When clicked, show a toast with the TextView text
					// Toast.makeText(getApplicationContext(),
					//	((TextView) view).getText(), Toast.LENGTH_SHORT).show();
					Log.d(TAG,GameRoomListString [position]); 
					JoinGame(GameRoomListString [position]);
				}
			});
	
			Log.d(TAG, "onEnding");
		}catch (Exception e){
			
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);


	}


	private void JoinGame(String gr) {
		if(GameRoomLogin.iSC.joinChatRoom(gr)){
			Toast.makeText(getApplicationContext(),
					"Welcome to "+gr, Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this, GameClient.class);
		i.putExtra("GameRoomToJoinFrom", gr);
		startActivity(i);
		}
		else{
			Toast.makeText(getApplicationContext(),
					"Join Unsuccessful", Toast.LENGTH_SHORT).show();
		}
	}

	private String[] BuildString(List<String> RoomList){
		String[] sa = new String[RoomList.size()];
		ChatRoom cr = null;
		int i=0;
		while(i<RoomList.size()){
			cr = GameRoomLogin.iSC.getChatRoom(RoomList.get(i));
			sa[i]=RoomList.get(i)+"       ("+Integer.toString(cr.getUserList().size())+"/6)";
			i++;
		}
		return sa;	
	}

	private String[] BuildString2(List<String> RoomList){
		String[] sa = new String[RoomList.size()];
		ChatRoom cr = null;
		int i=0;
		while(i<RoomList.size()){
			cr = GameRoomLogin.iSC.getChatRoom(RoomList.get(i));
			sa[i]=RoomList.get(i);
			i++;
		}
		return sa;	
	}
}
