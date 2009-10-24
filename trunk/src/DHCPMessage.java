import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.BitSet;

/**
 * This class represents a DHCP application level message packet 
 */

/**
 * @author Laivz
 *
 */
public class DHCPMessage {
	public static final int BOOTREQUEST = 1;
	public static final int BOOTREPLY = 2;
	public static final int DHCPREQUEST = 1;
	public static final int DHCPREPLY = 2;
	public static final int ETHERNET10MB = 1;
	//private static String NL = ;
	public static final String NL = System.getProperty("line.separator");
	
	
	//Operation Code:
	//Specifies the general type of message
	private byte op; 
	
	//Hardware Type:
	//Specifies the type of hardware used for the local network
	private byte hType; 
	
	//Hardware Address Length: 
	//Specifies how long hardware addresses are in this message. 
	private byte hLen;
	
	//Hops: 
	private byte hops;
	
	//Transaction Identifier: (32-bit)
	//Identification field generated by client
	//private byte[] xid = new byte[3];
	private BitSet xid;
	
	//Seconds: (16-bit)
	//Number of seconds elapsed since a client began an attempt to acquire or renew a lease. 
	//private byte[] secs = new byte[1];
	private BitSet secs;
	
	//Flags: (16-bit)
	//1bit broadcast flag (0-1)
	//15 bit reserverd
	//private byte[] flags = new byte[1];
	private BitSet flags;
	
	//Client IP Address: (32-bit)
	private byte[] cIAddr;
	//private InetAddress cIAddr = new Inet4Address();

	//"Your" IP Address: (32-bit)
	private byte[] yIAddr;
	//Server IP Address: (32-bit)
	private byte[] sIAddr;
	//Gateway IP Address: (32-bit)
	private byte[] gIAddr;
	
	//Client Hardware Address: (128-bit : 16 bytes)
	private byte[] cHAddr;
	
	//Server Name: (512-bit : 64 bytes)
	private byte[] sName;
	
	//Boot Filename: (1024-bit : 128 bytes)
	private byte[] file;
	
	//Options: (variable)
	private DHCPOptions options;
	
	

	public DHCPMessage() {
		xid = new BitSet();
		secs = new BitSet();
		flags = new BitSet();
		cIAddr = new byte[4];
		yIAddr = new byte[4];
		sIAddr = new byte[4];
		gIAddr = new byte[4];
		cHAddr = new byte[16];
		sName = new byte[64];
		file = new byte[128];
		options = new DHCPOptions();
		//NL = System.getProperty("line.seperator");
	}
	
	public DHCPMessage(byte[] msg) {
		internalize(msg);
	}
	
	public byte[] discoverMsg(byte[] cMacAddress) {
		op = DHCPREQUEST;
		hType = ETHERNET10MB; // (0x1) 10Mb Ethernet
		hLen = 6; // (0x6)
		hops = 0; // (0x0)
	/*	xid = 556223005; // (0x21274A1D)
		secs = 0;  // (0x0)
		flags = 0; // (0x0)
*/		// DHCP: 0............... = No Broadcast

		cIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		yIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		sIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		gIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		
		// 08002B2ED85E 6 bytes + padding 12 bytes
		for (int i=0; i < cMacAddress.length; i++) {
			cHAddr[i] = cMacAddress[i]; 
		}
		
		sName = new byte[sName.length]; // <Blank>
		file = new byte[file.length]; // <Blank>
		
		// DHCP: Magic Cookie = [OK]
		// DHCP: Option Field (options)
		options = new DHCPOptions();
		
		// DHCP: DHCP Message Type = DHCP Discover
		options.setOptionData(DHCPOptions.DHCPMESSAGETYPE, new byte[]{(byte) DHCPOptions.DHCPDISCOVER});
		
		// DHCP: Client-identifier = (Type: 1) 08 00 2b 2e d8 5e
		byte[] ci = new byte[1+cMacAddress.length];
		ci[0] = hType;
		for (int i=1; i <= cMacAddress.length; i++) ci[i] = cMacAddress[i-1];
		options.setOptionData(DHCPOptions.DHCPCLIENTIDENTIFIER, ci);
		
		/*// DHCP: Host Name = JUMBO-WS
		options.setOptionData(DHCPOptions.DHCPHOSTNAME, new Byte[]{});
		*/
		
		// DHCP: Parameter Request List = (Length: 7) 01 0f 03 2c 2e 2f 06
		options.setOptionData(DHCPOptions.DHCPPARAMREQLIST, new byte[]{0x01, 0x0f, 0x03, 0x2c, 0x2e, 0x2f, 0x06});
		
		// DHCP: End of this option field
		return this.externalize();
	}
	
/*	public byte[] offerMsg(int xid, byte[] cMacAddress, byte[] offerIP) {
		op = DHCPREPLY;
		hType = ETHERNET10MB; // (0x1) 10Mb Ethernet
		hLen = 6; // (0x6)
		hops = 0; // (0x0)
		this.xid =  xid; //556223005; // (0x21274A1D)
		secs = 0;  // (0x0)
		flags = 0; // (0x0)
		// DHCP: 0............... = No Broadcast

		cIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		yIAddr = offerIP; // 0.0.0.0
		sIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		gIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		
		// 08002B2ED85E 6 bytes + padding 10 bytes
		for (int i=0; i < cMacAddress.length; i++) {
			cHAddr[i] = cMacAddress[i]; 
		}
		
		sName = new byte[sName.length]; // <Blank>
		file = new byte[file.length]; // <Blank>
		
		// DHCP: Magic Cookie = [OK]
		// DHCP: Option Field (options)
		options = new DHCPOptions();
		
		// DHCP: DHCP Message Type = DHCP Discover
		options.setOptionData(DHCPOptions.DHCPMESSAGETYPE, new byte[]{(byte) DHCPOptions.DHCPOFFER});
		
		
		// DHCP: Subnet Mask            = 255.255.240.0
		options.setOptionData(DHCPOptions.DHCPSUBNETMASK, new byte[]{(byte) 255, (byte) 255, (byte) 240, (byte) 0});
		
		
		// DHCP: Renewal Time Value (T1) = 8 Days,  0:00:00
        //DHCP: Rebinding Time Value (T2) = 14 Days,  0:00:00

		
		//   DHCP: IP Address Lease Time  = 16 Days,  0:00:00
		//32-bit given in seconds (16days = 1382400 secs)
		options.setOptionData(DHCPOptions.DHCPLEASETIME, new byte[]{(byte) 0, (byte) 21, (byte) 24, (byte) 0});

		// DHCP: Server Identifier      = 157.54.48.151
		byte[] si = new byte[]{(byte) 157, (byte) 54, (byte) 48, (byte) 151};
		options.setOptionData(DHCPOptions.DHCPSERVERIDENTIFIER, si);
		
		// DHCP: Router                 = 157.54.48.1
		byte[] router = new byte[]{(byte) 157, (byte) 54, (byte) 48, (byte) 1};
		options.setOptionData(DHCPOptions.DHCPROUTER, router);
	  
		// DHCP: NetBIOS Name Service   = 157.54.16.154
	    // DHCP: NetBIOS Node Type      = (Length: 1) 04
		
		// DHCP: End of this option field
		return this.externalize();
	}
*/
	public byte[] requestMsg(byte[] cMacAddress, byte[] requestIP) {
		op = DHCPREQUEST;
		hType = ETHERNET10MB; // (0x1) 10Mb Ethernet
		hLen = 6; // (0x6)
		hops = 0; // (0x0)
	/*	xid = 556223005; // (0x21274A1D)
		secs = 0;  // (0x0)
		flags = 0; // (0x0)
*/		// DHCP: 0............... = No Broadcast

		cIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		yIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		sIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		gIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		
		// 08002B2ED85E 6 bytes + padding 10 bytes
		for (int i=0; i < cMacAddress.length; i++) {
			cHAddr[i] = cMacAddress[i]; 
		}
		
		sName = new byte[sName.length]; // <Blank>
		file = new byte[file.length]; // <Blank>
		
		// DHCP: Magic Cookie = [OK]
		// DHCP: Option Field (options)
		options = new DHCPOptions();
		
		// DHCP: DHCP Message Type = DHCP Discover
		options.setOptionData(DHCPOptions.DHCPMESSAGETYPE, new byte[]{(byte) DHCPOptions.DHCPREQUEST});
		

		// DHCP: Client-identifier = (Type: 1) 08 00 2b 2e d8 5e
		byte[] ci = new byte[1+cMacAddress.length];
		ci[0] = hType;
		for (int i=1; i <= cMacAddress.length; i++) ci[i] = cMacAddress[i-1];
		options.setOptionData(DHCPOptions.DHCPCLIENTIDENTIFIER, ci);
		
		// DHCP: Requested Address      = 157.54.50.5
		options.setOptionData(DHCPOptions.DHCPREQUESTIP, requestIP);

		// DHCP: Server Identifier      = 157.54.48.151
		byte[] si = new byte[]{(byte) 157, (byte) 54, (byte) 48, (byte) 151};
		options.setOptionData(DHCPOptions.DHCPSERVERIDENTIFIER, si);
		
		// DHCP: Host Name = JUMBO-WS
		options.setOptionData(DHCPOptions.DHCPHOSTNAME, new byte[]{});
		
		
		// DHCP: Parameter Request List = (Length: 7) 01 0f 03 2c 2e 2f 06
		options.setOptionData(DHCPOptions.DHCPPARAMREQLIST, new byte[]{0x01, 0x0f, 0x03, 0x2c, 0x2e, 0x2f, 0x06});

		// DHCP: End of this option field
		return this.externalize();
	}
	/*
	public byte[] ackMsg(int xid, byte[] cMacAddress, byte[] offerIP) {
		op = DHCPREPLY;
		hType = ETHERNET10MB; // (0x1) 10Mb Ethernet
		hLen = 6; // (0x6)
		hops = 0; // (0x0)
		this.xid =  xid; // 556223005; // (0x21274A1D)
		secs = 0;  // (0x0)
		flags = 0; // (0x0)
		// DHCP: 0............... = No Broadcast

		cIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		yIAddr = offerIP; // 0.0.0.0
		sIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		gIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		
		// 08002B2ED85E 6 bytes + padding 10 bytes
		for (int i=0; i < cMacAddress.length; i++) {
			cHAddr[i] = cMacAddress[i]; 
		}
		
		sName = new byte[sName.length]; // <Blank>
		file = new byte[file.length]; // <Blank>
		
		// DHCP: Magic Cookie = [OK]
		// DHCP: Option Field (options)
		options = new DHCPOptions();
		
		// DHCP: DHCP Message Type = DHCP Discover
		options.setOptionData(DHCPOptions.DHCPMESSAGETYPE, new byte[]{(byte) DHCPOptions.DHCPACK});
		
		
		// DHCP: Renewal Time Value (T1) = 8 Days,  0:00:00
        //DHCP: Rebinding Time Value (T2) = 14 Days,  0:00:00

		
		//   DHCP: IP Address Lease Time  = 16 Days,  0:00:00
		//32-bit given in seconds (16days = 1382400 secs)
		options.setOptionData(DHCPOptions.DHCPLEASETIME, new byte[]{(byte) 0, (byte) 21, (byte) 24, (byte) 0});

		// DHCP: Server Identifier      = 157.54.48.151
		byte[] si = new byte[]{(byte) 157, (byte) 54, (byte) 48, (byte) 151};
		options.setOptionData(DHCPOptions.DHCPSERVERIDENTIFIER, si);
		
		// DHCP: Subnet Mask            = 255.255.240.0
		options.setOptionData(DHCPOptions.DHCPSUBNETMASK, new byte[]{(byte) 255, (byte) 255, (byte) 240, (byte) 0});
		
		// DHCP: Router                 = 157.54.48.1
		byte[] router = new byte[]{(byte) 157, (byte) 54, (byte) 48, (byte) 1};
		options.setOptionData(DHCPOptions.DHCPROUTER, router);
	  
		// DHCP: NetBIOS Name Service   = 157.54.16.154
	    // DHCP: NetBIOS Node Type      = (Length: 1) 04
		
		// DHCP: End of this option field
		return this.externalize();
	}
	*/
	/**
	 * Internalises a byte array to this DHCPMessage object.
	 * @return  ?? not sure yet: a DHCPMessage object with information from a byte array.
	 */
	public void internalize(byte[] msg) {
		int staticSize = 236;
		int msgLength = msg.length;
		assert(msgLength >= staticSize);
		
		
		op = msg[0];    //1 byte
		hType = msg[1]; //1 byte
		hLen = msg[2];  //1 byte
		hops = msg[3];  //1 byte
	
		xid = DHCPUtility.bytes2Bits(new byte[]{msg[4],msg[5],msg[6],msg[7]});  	//4 byte int
		secs = DHCPUtility.bytes2Bits(new byte[]{msg[8],msg[9]});          //2 byte short
		flags = DHCPUtility.bytes2Bits(new byte[]{msg[10], msg[11]});        //2 byte int
		String debug = msg[10] + " " + msg[11] + " flags: " + flags;
		System.out.println(debug);
		// note: only first bit used for flags 
		// DHCP: 0............... = No Broadcast
		// DHCP: 1............... = Broadcast
		//128 in 1st byte means flag of 1
		//assert(flags.get(0) || !flags.get(0)) : (debug);

		//4 bytes
		cIAddr = new byte[]{msg[12],msg[13],msg[14],msg[15]};  
		yIAddr = new byte[]{msg[16],msg[17],msg[18],msg[19]}; 
		sIAddr = new byte[]{msg[20],msg[21],msg[22],msg[23]}; 
		gIAddr = new byte[]{msg[24],msg[25],msg[26],msg[27]}; 
		
		//cIAddr = ;
		//yIAddr = new byte[4];
		//sIAddr = new byte[4];
		//gIAddr = new byte[4];
		cHAddr = new byte[16];
		sName = new byte[64];
		file = new byte[128];
        
		for (int i=0; i < 16; i++) cHAddr[i] = msg[28+i];   //16 bytes
		for (int i=0; i < 64; i++) sName[i] = msg[44+i];    //64 bytes
		for (int i=0; i < 128; i++) file[i] = msg[108+i];   //128 bytes
		
		int optionsLength = msg.length - staticSize;
		this.options = new DHCPOptions();
		
		if (optionsLength > 4) {
			byte[] options = new byte[optionsLength];
			for (int i=0; i < optionsLength; i++) {
				options[i] = msg[staticSize+i];
			}
			
			this.options.internalize(options);
		}
		
		//this.printMessage();
	}
	
	

	/**
	 * Converts a DHCPMessage object to a byte array.
	 * @return  a byte array with   information from DHCPMessage object.
	 */
	public byte[] externalize() {
		int staticSize = 236;
		byte[] options = this.options.externalize();
		int size = staticSize + options.length;
		byte[] msg = new byte[size];
		
		//add each field to the msg array
		//single bytes
		msg[0] = this.op;
		msg[1] = this.hType;
		msg[2] = this.hLen;
		msg[3] = this.hops;
		
		//add multibytes
		for (int i=0; i < 4; i++) msg[4+i] = DHCPUtility.bits2Bytes(xid)[i];
		for (int i=0; i < 2; i++) msg[8+i] = DHCPUtility.bits2Bytes(secs)[i];
		for (int i=0; i < 2; i++) msg[10+i] = DHCPUtility.bits2Bytes(flags)[i];
		for (int i=0; i < 4; i++) msg[12+i] = cIAddr[i];
		for (int i=0; i < 4; i++) msg[16+i] = yIAddr[i];
		for (int i=0; i < 4; i++) msg[20+i] = sIAddr[i];
		for (int i=0; i < 4; i++) msg[24+i] = gIAddr[i];
		for (int i=0; i < 16; i++) msg[28+i] = cHAddr[i];
		for (int i=0; i < 64; i++) msg[44+i] = sName[i];
		for (int i=0; i < 128; i++) msg[108+i] = file[i];
		
		//add options
		for (int i=0; i < options.length; i++) msg[staticSize+i] = options[i];
      
		return msg;
	}

	public byte getOp() {
		return op;
	}

	public void setOp(byte op) {
		this.op = op;
	}

	public byte getHType() {
		return hType;
	}

	public void setHType(byte type) {
		hType = type;
	}

	public byte getHLen() {
		return hLen;
	}

	public void setHLen(byte len) {
		hLen = len;
	}

	public byte getHops() {
		return hops;
	}

	public void setHops(byte hops) {
		this.hops = hops;
	}

	public BitSet getXid() {
		return xid;
	}

	public void setXid(BitSet xid) {
		this.xid = xid;
	}

	public BitSet getSecs() {
		return secs;
	}

	public void setSecs(BitSet secs) {
		this.secs = secs;
	}

	public BitSet getFlags() {
		return flags;
	}

	public void setFlags(BitSet flags) {
		this.flags = flags;
	}

	public byte[] getCIAddr() {
		return cIAddr;
	}

	public void setCIAddr(byte[] addr) {
		cIAddr = addr;
	}

	public byte[] getYIAddr() {
		return yIAddr;
	}

	public void setYIAddr(byte[] addr) {
		yIAddr = addr;
	}

	public byte[] getSIAddr() {
		return sIAddr;
	}

	public void setSIAddr(byte[] addr) {
		sIAddr = addr;
	}

	public byte[] getGIAddr() {
		return gIAddr;
	}

	public void setGIAddr(byte[] addr) {
		gIAddr = addr;
	}

	public byte[] getCHAddr() {
		return cHAddr;
	}

	public String printCHAddr() {
		String str = "";
		//10mb ethernet?
		if (hType == ETHERNET10MB) {
			str += DHCPUtility.printMAC(cHAddr);
		} else {
			for (int i=0; i < cHAddr.length; i++) {
				str += cHAddr[i] + (i == cHAddr.length -1 ? "" : ",");
			}
		}
		return str;
	}
	
	public void setCHAddr(byte[] addr) {
		cHAddr = addr;
	}

	public String printSName() {
		return DHCPUtility.printString(sName);
	}
	public byte[] getSName() {
		return sName;
	}

	public void setSName(byte[] name) {
		sName = name;
	}

	public String printFile() {
		return DHCPUtility.printString(file);
	}
	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public DHCPOptions getOptions() {
		return options;
	}
	
	public void setOptions(DHCPOptions options) {
		this.options = options;
	}

	//no set options yet...
	/*public void setOptions(byte[] options) {
		this.options = options;
	}*/
	
	public void printMessage() {
		System.out.println(this.toString());
	}
	
	@Override
	public String toString() {
		String msg = new String();
		String[] row = new String[10];

		row[0] =  "Operation Code: " + this.op + " | ";
		row[0] += "Hardware Type: " + this.hType + " | ";
		row[0] += "Hardware Length: " + this.hLen + " | ";
		row[0] += "Hops: " + this.hops;

		row[1] = "xid: " + DHCPUtility.printBitSet(xid);

		row[2] = " secs: " +DHCPUtility.printBitSet(secs) + " | flags: ";
		row[2] += DHCPUtility.printBitSet(flags);

		row[3] = "cIAddr: " + DHCPUtility.printIP(cIAddr);
		row[4] = "yIAddr: " + DHCPUtility.printIP(yIAddr);
		row[5] = "sIAddr: " + DHCPUtility.printIP(sIAddr);
		row[6] = "gIAddr: " + DHCPUtility.printIP(gIAddr);
				
		//get length of longest static dhcp format row
		int width = 0;
		for (int i = 0; i < row.length-3; i++) {
			if (row[i].length() > width)
				width = row[i].length();
		}
			
		//format chAddr
		if (width < hLen || width < cHAddr.length) {
			int length = 0;
			row[7] = "cHAddr: " + cHAddr[0];
			for (int i=1; i < hLen && i < cHAddr.length; i++) {
				if (length > width) {
					row[7] += "||" + NL + "|| " + cHAddr[i];
					length = 6 + 3;
				} else {
					row[7] += ", " + cHAddr[i];
				}

			}
		} else {
			row[7] = "cHAddr: " + printCHAddr();
		}
		
		
		
		int length =0;
		String sname = printSName();
		row[8] = "sName: ";
		for (int i=0; i < sname.length(); i++) {
			if (length >  width) {
				row[8] += "||" + NL +"|| " + sname.charAt(i);
				length = 6 + 3;
			} else {
				row[8] += sname.charAt(i);
				length++;
			}
		}
		
		row[9] = " file: " + printFile();
		/*
		String file = printFile();
		for (int i=0; i < file.length(); i++) {
			if (i*3 % width == 0) row[9] += "||" + NL +"|| " + (char) file.charAt(i);
			row[9] += file.charAt(i);
		}*/
		

		String msgDivider = new String("");
		for (int i = 0; i < width+6; i++) msgDivider += "=";
		msgDivider += NL;
		
		String rowDivider = new String("");
		for (int i = 0; i < width+6; i++) rowDivider += "-";
		rowDivider += NL;

		msg += msgDivider + "|| " + row[0] + " ||" + NL +"";
		for (int i = 1; i < row.length; i++) {
			int halfWhiteSpace = (width - row[i].length()) / 2;
			String halfWhite = new String("");
			for (int j=0; j < halfWhiteSpace; j++) halfWhite += " ";
			msg += rowDivider + "|| " + halfWhite + row[i] + halfWhite + " ||" + NL;
		}
		msg += msgDivider + options.toString() + msgDivider;

		return msg;
	}
	
	
	
}
     