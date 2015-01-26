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
						String str="";
						for (int i = 0; i < size; i++) {
							str += Integer.toHexString(buffer[i] & 0xff)+" ";
						}
						Log.v("ReadThread", "size:"+size+" "+str.toUpperCase());
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
				/*		int[] temp = new int[]{0xaa,0x55,0x00,0x2e,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x12,0x4B,0x00,0x02,0x3a,0x3E,0xB9,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x01,0x00,0x00,0x00,0x00,0x00,0x41,0x01,0x10,0x00,0x00,0x00,0x00};
						byte[] arrTamp = new byte[temp.length];
						for (int i = 0; i < temp.length; i++) {
							arrTamp[i] = (byte) temp[i];
						}*/
						String StrHex = " ";
						for (int i = 0; i < arr.length; i++) {
							
							StrHex += Integer.toHexString(arr[i] & 0xff);
							StrHex += " ";
						}
						Log.v("SerialComm", "arrTamp:"+StrHex.toUpperCase());
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
	private byte[] dataMaxLength = new byte[500];
	
	private boolean readyNewData = false;
	protected void onDataReceived(final byte[] buffer, final int size) {
				try {
					if (!readyNewData) {
						for (int i = 0; i < size; i++) {
							if (0x00aa == (buffer[i] & 0xff)) {//若出现帧头AA则设置接收状态为开始
								Log.v("onDataReceived", "Start New");
								readyNewData = true;
								System.arraycopy(buffer,i,dataMaxLength,0,size-i);
								receivedSize = size-i;//已经接收到多少Byte
							}
						}
					}else {
						System.arraycopy(buffer,0,dataMaxLength,receivedSize,size);
						receivedSize += size;
						if (receivedSize > 3) {
							int nLen = (dataMaxLength[2] & 0xff) << 8 | dataMaxLength[3] & 0xff;
							if(0x00aa != (dataMaxLength[0] & 0xff) || 0x55 != (dataMaxLength[1] & 0xff) || nLen > 546)
							{
								Log.v("onDataReceived", "Head Wrong or nLen:"+nLen);
								readyNewData = false;
								return;
							}
							
							if ((receivedSize > (nLen-1)) && receivedSize != 0 && nLen != 0) {
								byteDataTamp = new byte[nLen];
								System.arraycopy(dataMaxLength,0,byteDataTamp,0,nLen);
								
								byte[] dataMaxLengthTemp = null;
								int startCopyPosition = 0;
								for (int i = nLen; i < receivedSize; i++) {
									if (0x00aa == (dataMaxLength[i] & 0xff)) {
										startCopyPosition = i;
										dataMaxLengthTemp = new byte[500];
										System.arraycopy(dataMaxLength,i,dataMaxLengthTemp,0,(receivedSize - i));
									}
								}
								if (dataMaxLengthTemp != null && startCopyPosition != 0) {
									System.arraycopy(dataMaxLengthTemp,0,dataMaxLength,0,(receivedSize - startCopyPosition));
									readyNewData = true;
									receivedSize = receivedSize - startCopyPosition;
									Log.v("onDataReceived", "Start Again");
								}else {
									readyNewData = false;
									Log.v("onDataReceived", "Set Start New");
								}		
								
			
								//Send
								String str="";
								for (int i = 0; i < byteDataTamp.length; i++) {
									str += Integer.toHexString(byteDataTamp[i] & 0xff)+" ";
								}
								ISmartFrame ReadFrame = null;
								ReadFrame = new ISmartFrame(byteDataTamp);
								if(byteDataTamp[32] != 0 && byteDataTamp[33] != 0)//SEQ = 0 为APP自动查询返回值，只做更新出来，不发送
								{
									Log.v("SerialComm","手机查询返回");
									Log.v("LZP", "Data Send:"+str);
									Protocol.getInstance().DealSerialFrame(ReadFrame);
								}
								try{
									Log.v("SerialComm", "查询结果!!!");
									addQueryRequest(ReadFrame);//查询若是查询结果则~
									Log.v("LZP", "Data Send:"+str);
								}catch(Exception e)
								{
									Log.v("SerialComm", "addQueryRequest:"+e.toString());
								}
							}
						}
						
					}
				} catch (Exception e) {
					// TODO: handle exception
					readyNewData = false;
					Log.v("SerialComm", "Exception:"+e.toString());
				}	
	}
	
	// 查询某个节点状态,若原不存在，则插入List中，若存在则更新
		public void addQueryRequest(ISmartFrame frame) {// 地址2个字节 1个字节的类型 1个字节的状态

			
			// 1个字节的时间
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
			case SmartNode.PROTOCOL_TYPE_GATEWAY:/* 网关不提取节点信息 */
				return;
			default:
				SmartNode.GetItemFromAny(frame, node);
				break;
			}
			if(nodeStatusList.size() > 0){
				for (SmartNode nodeTemp : nodeStatusList) {
					if(Arrays.equals(frame.GetSourceMac(), nodeTemp.getMac())){
						//若存在，更新状态与时间
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
