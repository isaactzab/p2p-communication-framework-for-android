package net.clc.bt;

public class DataPacket {
	public byte Ctr;
	public String Src;
	public String Dest;
	public byte HopCount;
	public String PktId;
	public short dataSize;
	public byte SeqNo;
	public byte[] data;
	public static final int PACKET_SIZE = 1024;
	public static final int HEADER_SIZE = 1+17+17 +1 +21+1+2;
	public static final int DATA_MAX_SIZE = PACKET_SIZE-HEADER_SIZE;
	//short DataSize;
	
	public DataPacket(){
		Ctr = 0;
		HopCount = 0;
		data = new byte[]{1};
		dataSize = 1;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		
		if(o instanceof DataPacket){
			
			if ( ((DataPacket) o).Ctr != Ctr) return false;
			if ( ((DataPacket) o).Src.compareTo(Src) != 0) return false;
			if ( ((DataPacket) o).Dest.compareTo(Dest) != 0) return false;
			if ( ((DataPacket) o).PktId.compareTo(PktId) != 0) return false;
			if ( ((DataPacket) o).HopCount != HopCount) return false;
			if ( ((DataPacket) o).dataSize != dataSize) return false;
			if ( ((DataPacket) o).data.length != data.length) return false;
			for(int i=0; i < data.length; i++){
				if(((DataPacket) o).data[i] != data[i]) return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	

}
