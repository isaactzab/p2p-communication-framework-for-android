package indexServerClient;

public class IndexServerClientConfig {
	private static String serverIP="172.17.150.108";
	private static int serverPort=4444;
	
	public static void setServerIP(String ip){
		serverIP=ip;
	}
	
	public static String getServerIP(){
		return serverIP;
	}
	public static int getServerPortNumber(){ 
		return serverPort;
	}	
}  
    