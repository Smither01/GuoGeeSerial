package com.guogu.serial;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.guogu.serailport.SerialPort;

public class SerialComm {

	private static SerialComm mInstance;
	private String TAG = "LZP";


	Protocol protocol;

	private boolean UsbNotExist = true;
	private final ExecutorService mExecutor;
	
	
	protected Application mApplication;
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private WriteThread mWriteThread;
	
	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			while(!isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[4];
					if (mInputStream == null) return;
					size = mInputStream.read(buffer);
					if (size > 0) {
							onDataReceived(buffer, size);					
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	private volatile List<SmartNode> nodeStatusList;

	// private AdbDevice mAdbDevice;
	private SerialComm() {
		Log.v("LZP", "SerialComm Thread Start!");
		protocol = Protocol.getInstance();
		mExecutor = Executors.newSingleThreadExecutor();


		nodeStatusList = new ArrayList<SmartNode>();
		
		mApplication = Application.getApplication();
		try {
			mSerialPort  = new SerialPort(new File("/dev/ttyS2"), 38400, 0);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
			
			mReadThread = new ReadThread();
			mReadThread.start();
			mWriteThread = new WriteThread();
			mWriteThread.start();
			UsbNotExist = false;
		} catch (Exception e) {
			UsbNotExist = true;
			Log.v("SerialComm","SerialComm OnCreate:"+e.toString());
		}
		Log.v("SerialComm", "SerialComm Construct end");
		
	}
	
	private boolean falg  = true;
	private class WriteThread extends Thread{
		@Override
		public void run() {
			Log.v("SerialComm", "WriteThread Created");
			while (true) {	
				if (falg) {
					
	
				byte[] arr = protocol.getFirstDataByte();
				if (arr.length > 0) {
					try {
						Thread.sleep(30);
					/*	int[] temp = new int[]{0xAA,0x55,0x00,0x2e,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x12,0x4B,0x00,0x02,0x8A,0x95,0x82
								,0x00,0x00,0x00,0x00,0x00,0x00,0xc8,0x60,0x00,0x02,0x6f,0xa2,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x01,0xff,0x00,0x00,0xF2,0x5D};
						byte[] arrTamp = new byte[temp.length];
						for (int i = 0; i < temp.length; i++) {
							arrTamp[i] = (byte) temp[i];
						}*/
						String StrHex = " ";
						for (int i = 0; i < arr.length; i++) {
							
							StrHex += Integer.toHexString(arr[i] & 0xff);
							StrHex += " ";
						}
						Log.v("SerialComm", "StrHex:"+StrHex.toUpperCase());
						if (mOutputStream == null) {
							return;
						}
						mOutputStream.write(arr);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.v("SerialComm", "WriteThread Exception:"+e.toString());
					}
				}	
				}
			}		
		}
	}

	private int receivedSize = 0;
	private byte[] byteDataTamp;
	private byte[] dataLength = new byte[2];
	private byte[] dataMaxLength = new byte[500];
	private short packageLength = 0;
	protected void onDataReceived(final byte[] buffer, final int size) {
				try {
					if (receivedSize < 500) {
						
						String test="";
						for (int i = 0; i < buffer.length; i++) {
							test += Integer.toHexString(buffer[i] & 0xff)+" ";
						}
			//			Log.v("LZP", "Data Receive:"+test);						
						System.arraycopy(buffer,0,dataMaxLength,receivedSize,size);
						receivedSize += size; 
						if (receivedSize > 546) {
							packageLength = 0;
							dataLength[0] =0;
							dataLength[1] = 0;
							return;
						}
						
				//		Log.v("SerialComm", "receivedSize += size:"+receivedSize);
					/*	String str1="";
						for (int i = 0; i < dataMaxLength.length; i++) {
							str1 += Integer.toHexString(dataMaxLength[i] & 0xff)+" ";
						}
						Log.v("LZP", "dataMaxLength:"+str1);*/
						if (receivedSize > 3) {
							int nLen = (dataMaxLength[2] & 0xff) << 8 | dataMaxLength[3] & 0xff;
				        	if(0x00aa != (dataMaxLength[0] & 0xff) || 0x55 != (dataMaxLength[1] & 0xff) || nLen > 546)
				        	{
				        		System.out.println(0xaa + " " + 0x55);
				        		System.out.println("No Heda " + (dataMaxLength[0] & 0xff) + " " + (dataMaxLength[1] & 0xff) + " " + nLen);
				        		packageLength = 0;
								dataLength[0] =0;
								dataLength[1] = 0;
								return;
				        	}
				        	
							System.arraycopy(dataMaxLength,2,dataLength,0,2);
							packageLength = Util.Byte2Short(dataLength, 0);
						}
						if ((receivedSize > (packageLength-1)) && receivedSize != 0 && packageLength != 0) {
							byteDataTamp = new byte[packageLength];
							System.arraycopy(dataMaxLength,0,byteDataTamp,0,byteDataTamp.length);
							if (receivedSize > (packageLength - 1)) {
								
								System.arraycopy(dataMaxLength,packageLength,dataMaxLength,0,(receivedSize - byteDataTamp.length));
								
								receivedSize = receivedSize - byteDataTamp.length;
								packageLength = 0;
								dataLength[0] =0;
								dataLength[1] = 0;
								Log.v("SerialComm","After receivedSize:"+receivedSize);
							}
							//Send
							String str="";
							for (int i = 0; i < byteDataTamp.length; i++) {
								str += Integer.toHexString(byteDataTamp[i] & 0xff)+" ";
							}
							
							ISmartFrame ReadFrame = null;
							ReadFrame = new ISmartFrame(byteDataTamp);
							if(byteDataTamp[32] != 0 && byteDataTamp[33] != 0)//SEQ = 0 涓篈PP鑷姩鏌ヨ杩斿洖鍊硷紝鍙仛鏇存柊鍑烘潵锛屼笉鍙戦�
							{
								Log.v("SerialComm","鎵嬫満鏌ヨ杩斿洖");
								Log.v("LZP", "Data Send:"+str);
								Protocol.getInstance().DealSerialFrame(ReadFrame);
							}
							try{
								Log.v("SerialComm", "鏌ヨ缁撴灉!!!");
								addQueryRequest(ReadFrame);//鏌ヨ鑻ユ槸鏌ヨ缁撴灉鍒檦
								Log.v("LZP", "Data Send:"+str);
							}catch(Exception e)
							{
								Log.v("SerialComm", "addQueryRequest:"+e.toString());
							}
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					Log.v("SerialComm", "Exception:"+e.toString());
				}	
	}
	
	// 鏌ヨ鏌愪釜鑺傜偣鐘舵�,鑻ュ師涓嶅瓨鍦紝鍒欐彃鍏ist涓紝鑻ュ瓨鍦ㄥ垯鏇存柊
		public void addQueryRequest(ISmartFrame frame) {// 鍦板潃2涓瓧鑺�1涓瓧鑺傜殑绫诲瀷 1涓瓧鑺傜殑鐘舵�

			
			// 1涓瓧鑺傜殑鏃堕棿
			boolean hasFlag = true;
			SmartNode node = new SmartNode();
			switch (frame.GetDev()) {
			case SmartNode.PROTOCOL_TYPE_COLORLIGHT:
				SmartNode.GetItemFromColorLight(frame, node);
				break;
			case SmartNode.PROTOCOL_TYPE_ONELIGNT:
				SmartNode.GetItemFromOneLight(frame, node);
				break;
			case SmartNode.PROTOCOL_TYPE_TWOLIGNT:
				SmartNode.GetItemFromTwoLight(frame, node);
				break;
			case SmartNode.PROTOCOL_TYPE_THREELIGNT:
				SmartNode.GetItemFromThreeLight(frame, node);
				break;
			case SmartNode.PROTOCOL_TYPE_FOURLIGNT:
				SmartNode.GetItemFromFourLight(frame, node);
				break;
			case SmartNode.PROTOCOL_TYPE_POWERSOCKET:
				SmartNode.GetItemFromColorLight(frame, node);
				break;
			case SmartNode.PROTOCOL_TYPE_CONTROLSOCKET:
				SmartNode.GetItemFromControlSocket(frame, node);
				break;
			case SmartNode.PROTOCOL_TYPE_GATEWAY:/* 缃戝叧涓嶆彁鍙栬妭鐐逛俊鎭�*/
				return;
			default:
				SmartNode.GetItemFromAny(frame, node);
				break;
			}
			if(nodeStatusList.size() > 0){
				for (SmartNode nodeTemp : nodeStatusList) {
					if(Arrays.equals(frame.GetSourceMac(), nodeTemp.getMac())){
						//鑻ュ瓨鍦紝鏇存柊鐘舵�涓庢椂闂�
						nodeTemp.setStatus(frame.GetData()[0]);
						nodeTemp.setTime(System.currentTimeMillis());
						hasFlag = false;
						break;
					}
				}
			}		
			if (hasFlag) {
				// Add into nodeStatusList
				nodeStatusList.add(node);
			}

		}
	
	public static SerialComm getInstance() {
		if (mInstance == null) {
			synchronized (SerialComm.class) {
				if (mInstance == null) {
					mInstance = new SerialComm();
				}
			}
		}
		return mInstance;
	}
	
	public void clearTaskInThreadPool(){
		mExecutor.shutdownNow();
	}


	public List<SmartNode> getNodeStatusList()
	{
		return this.nodeStatusList;
	}
	public void setEmptyNodeList()
	{
		this.nodeStatusList.clear();
	}

	public boolean getUsbNotExist() {
		return UsbNotExist;
	}

}
