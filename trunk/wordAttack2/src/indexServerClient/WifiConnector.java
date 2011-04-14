package indexServerClient;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class WifiConnector {
	private ClientSocket frame;
	private String blueToothAddress;

	
	public WifiConnector(String blueToothAddress){
		this.blueToothAddress=blueToothAddress;
		frame = new ClientSocket();
		Log.i(Constants.WifiLog, "WifiConnector Created");
		
	}
	
	public boolean listenSocket(){
		boolean success = frame.listenSocket();
		return success;
	}
	public void signOut(){
		//Log.i(Constants.WifiLog, "WifiConnector.Sign Out()");
		frame.writeLineToServer("SGO");
	}
	
	 public String signIn(String email, String password){
	       // Log.i(Constants.WifiLog, "inside sign in");
	       /// Log.i(Constants.WifiLog, "email is "+email);
	        //Log.i(Constants.WifiLog, "password is "+password);
	        boolean signInSuccess = true;

	        String sgi = "SGI";
	        //Log.i(Constants.WifiLog,"WifiConnector.java: before frame.writeLinetoServer(sgi)");
	        frame.writeLineToServer(sgi);
	        //Log.i(Constants.WifiLog,"WifiConnector.java: after frame.writeLinetoServer(sgi)");
	        frame.writeLineToServer(email);
	        frame.writeLineToServer(password);
	        frame.writeLineToServer(blueToothAddress);
	        String userName = frame.readLineFromServer();
	        
	        if(userName.equals("SIF")){
	        	 signInSuccess=false;
	        	 userName = null;
	        }
	            

	        return userName;
	    }
	 
	 public boolean signUp(String userName, String userEmail, String userPassword){
	        boolean signUpSuccess = false;

	        frame.writeLineToServer("SGU");   
	        frame.writeLineToServer(userName);
	        frame.writeLineToServer(userEmail);
	        frame.writeLineToServer(userPassword);

	        if(frame.readLineFromServer().equals("SUS"))
	            signUpSuccess=true;
	        return signUpSuccess;
	    }
	 
	 public List<String> getChatRoomListFromServer(){
		 List<String> crList =new ArrayList<String>();

		 frame.writeLineToServer("SendChatRoomList");
		 int number = Integer.parseInt(frame.readLineFromServer());
		 while(number>0){
			 String name = frame.readLineFromServer();
			 crList.add(name);
			 number--;
		 }
		 String endOfList=frame.readLineFromServer();
		 
		 return crList;
	 }
	 
	 public boolean createChatRoom(String email, String chatRoomName ){
		 boolean success = true;
		 frame.writeLineToServer("CreateChatRoom");
		 frame.writeLineToServer(chatRoomName);
		 frame.writeLineToServer(email);
		 frame.writeLineToServer(blueToothAddress);
		 String result=frame.readLineFromServer();
		 if(result.equals("CreateChatRoomFailed")){
			 success=false;
		 }
		 return success;
	 }
	 
	 public ChatRoom getChatRoomUserList(String chatRoomName){
		 ChatRoom cr= new ChatRoom();
		 cr.setChatRoomName(chatRoomName);
		 frame.writeLineToServer("GetChatRoomList");
		 frame.writeLineToServer(chatRoomName);
		 String numberOfUserSTRING=frame.readLineFromServer();
		 int numberOfUserINT=Integer.parseInt(numberOfUserSTRING);
		 
		 while(numberOfUserINT>0){
			 String email = frame.readLineFromServer();
			 String IPadd = frame.readLineFromServer();
			 String BTadd = frame.readLineFromServer();
			 String expString = frame.readLineFromServer();
			 int exp = Integer.parseInt(expString);
			 cr.addUserToChatRoom(email, IPadd,exp, BTadd);
			 numberOfUserINT--;
		 }
		 
		 String result = frame.readLineFromServer();
		 
		return cr;	 
		 
	 }
	 
	 
	 public boolean joinChatRoom(String email, String chatRoomName){
		 boolean success=true;
		 frame.writeLineToServer("JoinChatRoom");
		 frame.writeLineToServer(chatRoomName);
		 frame.writeLineToServer(email);
		 frame.writeLineToServer(blueToothAddress);
		 String result=frame.readLineFromServer();
		 if(result.equals("JoinChatRoomFailed")){
			 success=false;
		 }
		 
		 return success;
	 }
	 
	 
	 //getting List from the server
	 public List<Player> getPlayerListFromServer(){
		// Log.i(Constants.WifiLog,"inside WifiConnector: getPlayerListFromServer()");
		 List<Player> pl = new ArrayList<Player>();
		 int numberOfOnlineUserInt;
		 int counter=0;
		 String numberOfOnlineUserString;
		 frame.writeLineToServer("REL");
		// Log.i(Constants.WifiLog,"REL sent" );
		 
		 numberOfOnlineUserString=frame.readLineFromServer();
		 numberOfOnlineUserInt= Integer.parseInt(numberOfOnlineUserString);    
		 String playerEmail;
		 String playerIPadd;
		 String playerBTadd;
		 String expString;
		 Player p = new Player();
		 boolean addingSuccess;
		 
		 while(counter<numberOfOnlineUserInt){
			 playerEmail=frame.readLineFromServer();
			 playerIPadd=frame.readLineFromServer();
			 playerBTadd=frame.readLineFromServer();
			 expString=frame.readLineFromServer();
			 int exp = Integer.parseInt(expString);
			 p.setEmail(playerEmail);
			 p.setIPaddress(playerIPadd);
			 p.setBlueToothAddress(playerBTadd);
			 p.setScore(exp);
			 addingSuccess=pl.add(p);
			 counter++;
		 }
		 
		 String command=frame.readLineFromServer(); //just to read for fun
		 	
		 
		 return pl;
	 }
	
	 public boolean leaveChatRoom(String chatRoomName, String email) {
		boolean leave=false;
		frame.writeLineToServer("LeaveChatRoom"); 
		frame.writeLineToServer(chatRoomName);
		frame.writeLineToServer(email);
		String result = frame.readLineFromServer();
		if(result.equals("LeaveChatRoomSuccess"))
			leave=true;
		
		return leave;
	}
	 
	public List<String> connectToWho(String email) {
		List<String> sl = new ArrayList<String>();
		frame.writeLineToServer("ConnectToWho");
		frame.writeLineToServer(email);
		frame.writeLineToServer(blueToothAddress);
		String numberOfUserSTRING=frame.readLineFromServer();
		int numberOfUserINT = Integer.parseInt(numberOfUserSTRING);
		int index=0;
		while(index<numberOfUserINT){
			String BTadd=frame.readLineFromServer();
			sl.add(BTadd);
			index++;
		}
		String result = frame.readLineFromServer(); // read "EndOfConnectToWho"
		return sl;
	}
	
	public void iConnectTo(String BTadd){
		frame.writeLineToServer("IConnectedTo");
		frame.writeLineToServer(BTadd); //dest BT
		frame.writeLineToServer(blueToothAddress);//own BT
		frame.writeLineToServer("EndOfIConnectTo");
	}
	
	public void updateScoreToServer(String email, int score){
		frame.writeLineToServer("updateScore");
		frame.writeLineToServer(email);
		String ScoreString = Integer.toString(score);
		frame.writeLineToServer(ScoreString);
	}

	public int getExpFromServer(String email) {
		// TODO Auto-generated method stub
		frame.writeLineToServer("getExp");
		frame.writeLineToServer(email);
		String expString = frame.readLineFromServer();
		int exp = Integer.parseInt(expString);
		return exp;
	}
	
}
