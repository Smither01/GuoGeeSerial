package com.guogu.serial;

public class SmartNode {

	public final static byte PROTOCOL_TYPE_GATEWAY = 0x00; /* 网关盒子 */
	public final static byte PROTOCOL_TYPE_GATEWAY_SECOND = 0x01; /* 网关盒子 */
	public final static byte PROTOCOL_TYPE_COLORLIGHT = 0x10; /* 彩色�? */
	public final static byte PROTOCOL_TYPE_FOURLIGNT = 0x1c; /* 四路�? */
	public final static byte PROTOCOL_TYPE_THREELIGNT = 0x1f; /* 三路�? */
	public final static byte PROTOCOL_TYPE_TWOLIGNT = 0x1e; /* 二路�? */
	public final static byte PROTOCOL_TYPE_ONELIGNT = 0x1d;/* �?路灯 */
	public final static byte PROTOCOL_TYPE_INFRARED = 0x20;/* 红外盒子 */
	public final static byte PROTOCOL_TYPE_ENVIRONMENT = 0x30; /* 环境采集 */
	public final static byte PROTOCOL_TYPE_POWERSOCKET = 0x40; /* 功�?�插�? */
	public final static byte PROTOCOL_TYPE_CONTROLSOCKET = 0x41; /* 控制插座 */
	public final static byte PROTOCOL_TYPE_VOICE = 0x05; /* 语音 */
	
	private byte[] shortMac;
	private byte type;
	private byte status;
	private long time;
	private byte[] mac;

	public void setMac(byte[] mac)
	{
		this.mac = mac;
	}
	public byte[] getMac()
	{	
		return this.mac;
	}
	public void setShortMac(byte[] mac) {
		this.shortMac = mac;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public byte[] getShortMac() {
		return this.shortMac;
	}

	public byte getStatus() {
		return this.status;
	}

	public long getTime() {
		return this.time;
	}

	public byte getType() {
		return this.type;
	}
	
	public static void GetItemFromColorLight(ISmartFrame ismartFrame, SmartNode node)
	{
		byte[] shortMac = new byte[]{ismartFrame.sourceMac[6],ismartFrame.sourceMac[7]};
		node.setShortMac(shortMac);
		node.setMac(ismartFrame.GetSourceMac());
		node.setType(ismartFrame.GetDev());	
		if (1 == ismartFrame.GetData()[0])
		{

			node.setStatus((byte)0x01);
		}
		else{
			node.setStatus((byte)0x00);
		}
		node.setTime(System.currentTimeMillis());
	}
	
	public static void GetItemFromOneLight(ISmartFrame ismartFrame, SmartNode node)
	{
		byte[] shortMac = new byte[]{ismartFrame.sourceMac[6],ismartFrame.sourceMac[7]};
		node.setShortMac(shortMac);
		node.setMac(ismartFrame.GetSourceMac());
		node.setType(ismartFrame.GetDev());	
		if (1 == ismartFrame.GetData()[0])
		{

			node.setStatus((byte)0x01);
		}
		else{
			node.setStatus((byte)0x00);
		}
		node.setTime(System.currentTimeMillis());
	}
	
	public static void GetItemFromTwoLight(ISmartFrame ismartFrame, SmartNode node)
	{

		byte[] shortMac = new byte[]{ismartFrame.sourceMac[6],ismartFrame.sourceMac[7]};
		node.setShortMac(shortMac);
		node.setMac(ismartFrame.GetSourceMac());
		node.setType(ismartFrame.GetDev());	
		if (1 == ismartFrame.GetData()[0])
		{

			node.setStatus((byte)0x01);	
		}
		else if (1 == ismartFrame.GetData()[1])
		{
			node.setStatus((byte)0x02);
		}
		else{
			node.setStatus((byte)0x00);
		}
		node.setTime(System.currentTimeMillis());
	}
	
	public static void GetItemFromThreeLight(ISmartFrame ismartFrame, SmartNode node)
	{
		byte[] shortMac = new byte[]{ismartFrame.sourceMac[6],ismartFrame.sourceMac[7]};
		node.setShortMac(shortMac);
		node.setMac(ismartFrame.GetSourceMac());
		node.setType(ismartFrame.GetDev());	
		if (1 == ismartFrame.GetData()[0])
		{

			node.setStatus((byte)0x01);	
		}
		else if (1 == ismartFrame.GetData()[1])
		{
			node.setStatus((byte)0x02);
		}
		else if (1 == ismartFrame.GetData()[2])
		{
			node.setStatus((byte)0x04);
		}
		else{
			node.setStatus((byte)0x00);
		}
		node.setTime(System.currentTimeMillis());;
	}
	
	public static void GetItemFromFourLight(ISmartFrame ismartFrame, SmartNode node)
	{
		byte[] shortMac = new byte[]{ismartFrame.sourceMac[6],ismartFrame.sourceMac[7]};
		node.setShortMac(shortMac);
		node.setMac(ismartFrame.GetSourceMac());
		node.setType(ismartFrame.GetDev());	
		if (1 == ismartFrame.GetData()[0])
		{

			node.setStatus((byte)0x01);	
		}
		else if (1 == ismartFrame.GetData()[1])
		{
			node.setStatus((byte)0x02);
		}
		else if (1 == ismartFrame.GetData()[2])
		{
			node.setStatus((byte)0x04);
		}
		else if (1 == ismartFrame.GetData()[3])
		{
			node.setStatus((byte)0x08);
		}
		else{
			node.setStatus((byte)0x00);
		}
		node.setTime(System.currentTimeMillis());
	}
	
	public static void GetItemFromPowerSocket(ISmartFrame ismartFrame, SmartNode node)
	{
		byte[] shortMac = new byte[]{ismartFrame.sourceMac[6],ismartFrame.sourceMac[7]};
		node.setShortMac(shortMac);
		node.setMac(ismartFrame.GetSourceMac());
		node.setType(ismartFrame.GetDev());	
		if (1 == ismartFrame.GetData()[0])
		{

			node.setStatus((byte)0x01);	
		}
		else{
			node.setStatus((byte)0x00);
		}
		node.setTime(System.currentTimeMillis());
	}
	
	public static void GetItemFromControlSocket(ISmartFrame ismartFrame, SmartNode node)
	{
		byte[] shortMac = new byte[]{ismartFrame.sourceMac[6],ismartFrame.sourceMac[7]};
		node.setShortMac(shortMac);
		node.setMac(ismartFrame.GetSourceMac());
		node.setType(ismartFrame.GetDev());	
		if (1 == ismartFrame.GetData()[0])
		{

			node.setStatus((byte)0x01);	
		}
		else{
			node.setStatus((byte)0x00);
		}
		node.setTime(System.currentTimeMillis());
	}
	
	public static void GetItemFromAny(ISmartFrame ismartFrame, SmartNode node)
	{
		byte[] shortMac = new byte[]{ismartFrame.sourceMac[6],ismartFrame.sourceMac[7]};
		node.setShortMac(shortMac);
		node.setMac(ismartFrame.GetSourceMac());
		node.setType(ismartFrame.GetDev());	
		node.setStatus(ismartFrame.GetData()[0]);
		node.setTime(System.currentTimeMillis());
	}

}
