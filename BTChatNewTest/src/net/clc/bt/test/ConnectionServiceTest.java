package net.clc.bt.test;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.Assert;
import net.clc.bt.ConnectionService;
import net.clc.bt.DataPacket;
import android.test.AndroidTestCase;

import com.example.Geoscribe.comms.GeoscribeComms2;
import com.webservice.objects.RegisterDeviceResult;
import com.webservice.objects.isDeviceLoginedResult;

public class ConnectionServiceTest extends AndroidTestCase {

		public DataPacket pkt;
		public byte[] encryptStream;
		public DataPacket decodePkt;
		public GeoscribeComms2 webService;
		public String hardwareId = "1231241";
		public String device1 = "nexus2";
		public String device2 = "245";
		public String device3 = "HTC vision";
		public String device4 = "HTC magic";
		public String device5 = "samsung Galaxy";
		public double geoX = 103.8721;
		public double geoY = 1.367428;

		
	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		webService = new GeoscribeComms2();			
		pkt = new DataPacket();
		pkt.Ctr = 1;
		pkt.Src = "00:23:76:96:5B:C3";
		pkt.Dest= "00:22:A5:F8:81:E5";
		pkt.PktId = "00:23:76:96:5B:C31234";
		pkt.HopCount = 2;
		 
		pkt.data = "This is a test".getBytes();
		pkt.SeqNo = 29;
		pkt.dataSize = (short) pkt.data.length;
		//Log.e("TESTTEST","value "+pkt.dataSize);
		encryptStream = ConnectionService.encrypt(pkt);
		
		//Log.e("TESTTEST","EncryptStream "+encryptStream.length);
		//Log.e("TESTTEST","HeaderSize "+DataPacket.HEADER_SIZE);
		
		decodePkt = ConnectionService.decrypt(encryptStream);
		
		decodePkt.data = new byte[decodePkt.dataSize];
		for(int i=0; i<decodePkt.dataSize;i++){
			decodePkt.data[i] = encryptStream[DataPacket.HEADER_SIZE+i];
		}		
		
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

	public boolean arrayEqual(byte[] a, byte[] b){
		
		if (a.length != b.length) return false;
		
		for(int i=0; i<a.length; i++){
			if (a[i] != b[i]) return false;
		}	
		
		return true;
	}
	
	public void testFails(){
		Assert.assertTrue(false);
	}
	
	public void testIsEqual(){
		Assert.assertTrue(pkt.equals(pkt));
	}
	
	public void testJoinByteArray(){
		byte[] a = new byte[]{1};
		byte[] b = new byte[]{1};
		byte[] ab = new byte[]{1,1};
		byte[] c = new byte[]{1,2,3};
		byte[] ac = new byte[]{1,1,2,3};
		byte[] d = new byte[]{4,5,6,7};
		byte[] cd = new byte[]{1,2,3,4,5,6,7};
		byte[] acdcd = new byte[]{1,1,2,3,4,5,6,7,1,2,3,4,5,6,7};
		Assert.assertTrue(arrayEqual(a,b));
		Assert.assertTrue(arrayEqual(ConnectionService.joinByteArray(a, b),ab));
		Assert.assertTrue(arrayEqual(ConnectionService.joinByteArray(a, c),ac));
		Assert.assertTrue(arrayEqual(ConnectionService.joinByteArray(c, d),cd));		
		Assert.assertTrue(arrayEqual(ConnectionService.joinByteArray(ConnectionService.joinByteArray(a, cd), cd),acdcd));
		a= null;
		b= null;
		ab= null;
		c= null;
		ac =null;
		d= null;
		cd= null;
		acdcd=null;
	}
	
	public void testEncryptDecrypt(){
		Assert.assertEquals(pkt, pkt);
		Assert.assertEquals(pkt.Ctr, decodePkt.Ctr);
		Assert.assertEquals(pkt.Src, decodePkt.Src);
		Assert.assertEquals(pkt.Dest, decodePkt.Dest);
		Assert.assertEquals(pkt.PktId, decodePkt.PktId);
		Assert.assertEquals(pkt.HopCount, decodePkt.HopCount);
		Assert.assertEquals(pkt.dataSize, decodePkt.dataSize);
		Assert.assertEquals(pkt.SeqNo, decodePkt.SeqNo);
		Assert.assertTrue(arrayEqual(pkt.data,decodePkt.data));
	}
	
	public void testGeneratePacketId(){
		Assert.assertEquals(21, ConnectionService.generatePacketId(pkt.Dest).getBytes().length);
		Assert.assertEquals(21, ConnectionService.generatePacketId(pkt.Dest).getBytes().length);
		Assert.assertEquals(21, ConnectionService.generatePacketId(pkt.Dest).getBytes().length);
		Assert.assertEquals(21, ConnectionService.generatePacketId(pkt.Dest).getBytes().length);
		Assert.assertEquals(21, ConnectionService.generatePacketId(pkt.Dest).getBytes().length);
		Assert.assertEquals(21, ConnectionService.generatePacketId(pkt.Dest).getBytes().length);
		Assert.assertEquals(21, ConnectionService.generatePacketId(pkt.Dest).getBytes().length);

	}
	
	public void testShortToByteArrayAndBack(){
		Assert.assertEquals(127, ConnectionService.byteArrayToShort(ConnectionService.shortToByteArray((short)127)));
		Assert.assertEquals(0, ConnectionService.byteArrayToShort(ConnectionService.shortToByteArray((short)0)));
		Assert.assertEquals(128, ConnectionService.byteArrayToShort(ConnectionService.shortToByteArray((short)128)));
		Assert.assertEquals(0, ConnectionService.byteArrayToShort(ConnectionService.shortToByteArray((short)-5)));
		Assert.assertEquals(32767, ConnectionService.byteArrayToShort(ConnectionService.shortToByteArray((short)32767)));
		Assert.assertEquals(0, ConnectionService.byteArrayToShort(ConnectionService.shortToByteArray((short)32768)));
		Assert.assertEquals(255, ConnectionService.byteArrayToShort(ConnectionService.shortToByteArray((short)255)));
		
	}
	
//	public void testDataPacketSort(){
//		DataPacket [] list = new DataPacket[5];
//		DataPacket A = new DataPacket();
//		A.SeqNo = 126;
//		DataPacket B = new DataPacket();
//		B.SeqNo = 127;
//		DataPacket C = new DataPacket();
//		C.SeqNo = 0;
//		DataPacket D = new DataPacket();
//		D.SeqNo = 1;
//		DataPacket E = new DataPacket();
//		E.SeqNo = 2;
//		
//		list[0] = E;
//		list[1] = B;
//		list[2] = C;
//		list[3] = A;
//		list[4] = D;
//		
//		Arrays.sort(list);
//		
//		Assert.assertEquals((byte)0,list[0].SeqNo);
//		Assert.assertEquals((byte)1,list[1].SeqNo);
//		Assert.assertEquals((byte)2,list[2].SeqNo);
//		Assert.assertEquals((byte)126,list[3].SeqNo);
//		Assert.assertEquals((byte)127,list[4].SeqNo);
//		
//	}
//	
//	public void testSortArrayList(){
//		ArrayList<DataPacket> res = new ArrayList<DataPacket>();
//		DataPacket A = new DataPacket();
//		A.SeqNo = 126;
//		DataPacket B = new DataPacket();
//		B.SeqNo = 127;
//		DataPacket C = new DataPacket();
//		C.SeqNo = 0;
//		DataPacket D = new DataPacket();
//		D.SeqNo = 1;
//		DataPacket E = new DataPacket();
//		E.SeqNo = 2;
//		
//		res.add(A);
//		res.add(C);
//		res.add(B);
//		res.add(E);
//		res.add(D);
//		
//		Comparator<DataPacket> comperator = new Comparator<DataPacket>(){
//
//			@Override
//			public int compare(DataPacket object1, DataPacket object2) {
//				// TODO Auto-generated method stub
//				if(object1.SeqNo == object2.SeqNo){
//					return 0;
//				}
//				else if((object1.SeqNo - object2.SeqNo) <0){ 
//					return -1;
//				}
//				else{
//					return 1;
//				}
//			}
//			
//		};
//		
//		Collections.sort(res,comperator);
//		
//				
//		Assert.assertEquals((byte)0,res.get(0).SeqNo);
//		Assert.assertEquals((byte)1,res.get(1).SeqNo);
//		Assert.assertEquals((byte)2,res.get(2).SeqNo);
//		Assert.assertEquals((byte)126,res.get(3).SeqNo);
//		Assert.assertEquals((byte)127,res.get(4).SeqNo);
//		
//			
//	}
	
//	public void testInsertDeviceEntry(){
//		Assert.assertTrue(webService.insertDeviceEntry(device3,device4));
//		Assert.assertTrue(webService.deviceEntryExist(device3, device4));
//		Assert.assertTrue(webService.removeDeviceEntry(device3, device4));
//	}
	
	public void testRemoveDeviceEntry(){
		Assert.assertTrue(webService.insertDeviceEntry("special2","HTmagic2"));
		Assert.assertTrue(webService.deviceEntryExist("special2","HTmagic2"));
		Assert.assertTrue(webService.removeDeviceEntry("special2", "HTmagic2"));
		Assert.assertFalse(webService.deviceEntryExist("special2","HTmagic2"));
	}
	
	public void testDeviceEntryExist(){
		Assert.assertNotNull(webService);
		Assert.assertTrue(webService.insertDeviceEntry(device1,device2));		
		Assert.assertTrue(webService.deviceEntryExist(device1, device2));
		Assert.assertTrue(webService.removeDeviceEntry(device1, device2));
	}
		
	public void testDeviceLogined(){
		Assert.assertTrue(webService.insertDeviceLogined(device5, (double)12.1231,(double)123.1512, 1));
		Assert.assertNotNull(webService.isDeviceLogined(device5));

		isDeviceLoginedResult result = new isDeviceLoginedResult();
		result = webService.isDeviceLogined(device5);
			
		Assert.assertEquals(result.hardwareId,device5);
		Assert.assertEquals(result.geoX,(double)12.1231);
		Assert.assertEquals(result.geoY,(double)123.1512);
		Assert.assertEquals(result.cluster,1);
		
		Assert.assertTrue(webService.removeDeviceLogined(device5));
		Assert.assertNull(webService.isDeviceLogined(device5));
	}
	
	public void testRegisterDevice(){
		String name = "test4";
		
		RegisterDeviceResult[] result;
		result = webService.registerDevice(name,geoX,geoY);
		
		Assert.assertEquals(result[0].hardwareId, "nexus");
		isDeviceLoginedResult result2 = new isDeviceLoginedResult();
		result2 = webService.isDeviceLogined("test4");
		Assert.assertEquals(result2.hardwareId, "test4");
		Assert.assertTrue(webService.removeDeviceLogined("test4"));
		
	}
	
	public void testDeviceConnected(){
		webService.registerDevice("test3",geoX,geoY);
		
		Assert.assertTrue(webService.deviceConnected("nexus", "test3"));
		isDeviceLoginedResult result2;
		result2 = webService.isDeviceLogined("test3");
		Assert.assertEquals(result2.cluster, 1);
		Assert.assertTrue(webService.deviceEntryExist("nexus", "test3"));
		
		Assert.assertTrue(webService.removeDeviceEntry("nexus", "test3"));
		Assert.assertTrue(webService.removeDeviceLogined("test3"));
	}
	
	public void testDeviceConnectionLost(){
		webService.registerDevice("test6", geoX, geoY);
		webService.deviceConnected("nexus", "test6");
		
		isDeviceLoginedResult result = webService.isDeviceLogined("test6");
		
		Assert.assertEquals(result.cluster, 1);
		
		Assert.assertTrue(webService.deviceEntryExist("nexus", "test6"));
		
		String[] results = webService.deviceConnectionLost("test6");
		
		ArrayList<String> list = new ArrayList(Arrays.asList(results));
		
		Assert.assertEquals(results[0], "nexus");
		
		Assert.assertFalse(list.contains("123")==false);
		Assert.assertFalse(list.contains("htc")==false);
		
		
		Assert.assertEquals(null,webService.isDeviceLogined("test6"));
				
		
	}

	
	
}
