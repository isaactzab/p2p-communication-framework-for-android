package indexServerClient;

public class Player {
	
    private String email;
    private String IPaddress;
    private String blueToothAddress;
    private int score=0;

    public Player(){}
    
    public Player(String email, String IPaddress){
        this.email=email;
        this.IPaddress= IPaddress;
    }

    public void setScore(int score){
    	this.score=score;
    }
    
    public int getScore(){
    	return this.score;
    }
    
    public void setEmail(String email){
    	this.email=email;
    }
    
    public String getEmail(){
        return email;
    }

	public void setIPaddress(String iPaddress) {
		IPaddress = iPaddress;
	}

	public String getIPaddress() {
		return IPaddress;
	}

	public void setBlueToothAddress(String blueToothAddress) {
		this.blueToothAddress = blueToothAddress;
	}

	public String getBlueToothAddress() {
		return blueToothAddress;
	}


}
