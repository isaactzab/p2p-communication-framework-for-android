package indexServerClient;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class IndexServerClientMain {
	private WifiConnector wc;
	private User u;
	private List<Player> playerList;
	private List<String> crList;
	
	//Constructor
	public IndexServerClientMain(String blueToothAddress){
		wc= new WifiConnector(blueToothAddress);
		u = new User();
		playerList = new ArrayList<Player>();
	}
	
	public boolean listenSocket(){
		boolean success=wc.listenSocket();
		return success;
	}
	
	public User getUser(){
		return u;
	}
	
	//input user data 
	public void inputUserData(String userName, String email, String password){
		u.setUserName(userName);
		u.setEmail(email);
		u.setPassword(password);
	}
	
	public void setServerIP(String ip){
		IndexServerClientConfig.setServerIP(ip);
	}
	public String getServerIP(){
		return IndexServerClientConfig.getServerIP();
	}
	
	//sign in
	public boolean signIn(){
		boolean signInSuccess= true;
		
		String name =wc.signIn(u.getEmail(),u.getPassword());
		if(name==null)
			signInSuccess=false;
			else
				this.u.setUserName(name);
		return signInSuccess;
	}
	
	//sign out
	public void signOut(){
		wc.signOut();
	}
	
	//sign up new account
	public boolean signUp(String userName, String email, String password){
		boolean signUpSuccess;
		//Log.i(Constants.WifiLog, "IndexServerClientMain.java: signUp()");
		signUpSuccess=wc.signUp(userName, email, password);
		return signUpSuccess;
	}
	
	//get list of online users on the server
	public List<Player> getPlayerList() {
		//Log.i(Constants.WifiLog,"inside indexServerClientMain: getPlayerList");
		playerList=wc.getPlayerListFromServer();
		return playerList;
	}
	
	//get list of chat rooms in the server
	public List<String> getChatRoomList(){
		//Log.i(Constants.WifiLog,"inside indexServerClientMain: getChatRoomList");
		crList =wc.getChatRoomListFromServer();
		return crList;
	}
	
	//create chat room
	//returns true if creation is successful
	//returns false if creation is not successful : chat room name in use
	public boolean createChatRoom(String chatRoomName){
		boolean create = wc.createChatRoom(u.getEmail(), chatRoomName);
		return create;
	}
	
	//join chat room
	//returns true if join is successful
	//return false if join is not successful: chat room name is invalid 
	public boolean joinChatRoom(String chatRoomName){
		boolean join = wc.joinChatRoom(u.getEmail(), chatRoomName);
		return join;
	}
	
	//get chat room details
	public ChatRoom getChatRoom(String chatRoomName){
		ChatRoom cr= wc.getChatRoomUserList(chatRoomName);
		return cr;
	}
	
	//leave chat room, if the user is the last person on the chatroom, chatroom will be gone.
	public boolean leaveChatRoom(String chatRoomName){
		boolean leave = wc.leaveChatRoom(chatRoomName,u.getEmail());
		return leave;
	}
	
	//get a sorted list of bt add to connect to.
	public List<String> connectToWho(){
		List<String> BTadd=wc.connectToWho(u.getEmail()); 
		return BTadd;
	}
	
	public void iConnectedTo(String BTadd){
		wc.iConnectTo(BTadd);
	}
	
	public void updateScore(String email, int score){
		wc.updateScoreToServer(email, score);
	}
	
	public int getExp(String email){
		return wc.getExpFromServer(email);
	}
}
