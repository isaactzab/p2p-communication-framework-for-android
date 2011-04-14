package net.clc.bt;

import java.util.concurrent.atomic.AtomicLong;

public class DataStream implements Comparable {
	static final AtomicLong seq = new AtomicLong(0);
	final long seqNum;
	final String address;
	final byte[] data;
	
   public DataStream(String address,byte[] data) {
	     seqNum = seq.getAndIncrement();
	     this.address = address;
	     this.data = data;	     
   }

   public DataStream(String address,byte[] data,long seqNum) {
	     this.seqNum = seqNum;
	     this.address = address;
	     this.data = data;	     
 }

   
   public String getAddress() { return address; }
   public byte[] getData() { return data; }
   
   public int compareTo(Object other) {
	   	 
//	     int res = entry.compareTo(other.entry);
//	     if (res == 0 && other.entry != this.entry)
	    	 int res = (seqNum < ((DataStream)other).seqNum ? -1 : 1);
	     return res;
   	}


   
}
