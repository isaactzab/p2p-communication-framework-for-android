package indexServerClient;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class ChatRoom {
	public class onlineUser{
		public String email;
		public String IPadd;
		public String BTadd;
		public int exp=0;
		
		onlineUser(String email, String IPadd, String BTadd, int exp){
			this.email=email;
			this.IPadd=IPadd;
			this.BTadd=BTadd;
			this.exp=exp;
		}
	}
	private List<onlineUser> userList;
	private String chatRoomName;
	
	ChatRoom(){
		userList= new ArrayList<onlineUser>();
	};
	
	ChatRoom(String email, String IPadd, String BTadd, int exp, String chatRoomName){
		onlineUser ou = new onlineUser(email,IPadd, BTadd,exp);
		userList= new ArrayList<onlineUser>();
		userList.add(ou);
		this.chatRoomName=chatRoomName;
		//Log.i(Constants.WifiLog,"inside ChatRoom Instatiation");
	}
	
	public List<onlineUser> getUserList(){
		return userList;
	}
	 
	public void setChatRoomName(String chatRoomName){
		this.chatRoomName=chatRoomName;
	}
	
	public boolean addUserToChatRoom(String email, String IPadd,int exp, String BTadd){
		boolean success=false; 
		onlineUser ou = new onlineUser(email,IPadd, BTadd,exp);
		int index=getUserIndex(email);
		//Log.i(Constants.WifiLog,"ChatRoom.java: addUserToChatRoom, index is"+ index);
		if(index==-1){
			success=userList.add(ou);
			//if(success)
				//Log.i(Constants.WifiLog,"ChatRoom.java: addUserToChatRoom, user added");
			//else
				//Log.i(Constants.WifiLog,"ChatRoom.java: addUserToChatRoom, user not added");
		}
		return success;
	}
	
	public int getUserIndex(String email){
		int index=0;
		while(index<userList.size()){
			if(userList.get(index).email.equals(email))
				return index;
			index++;
		}
		
		return -1;
	}


	public String getChatRoomName() {
		return chatRoomName;
	}
}

