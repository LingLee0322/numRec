package comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;

public class Portsent {
	public CommPortIdentifier portId;
	public  SerialPort serialPort;
	private String portname="COM2";
	private OutputStream outputStream;
	private InputStream inputStream;
	public Portsent(){
		Enumeration<?> en = CommPortIdentifier.getPortIdentifiers();
		while (en.hasMoreElements()) {
			portId = (CommPortIdentifier) en.nextElement();
			// 如果端口类型是串口，则打印出其端口信息
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				System.out.println(portId.getName());
				portname=portId.getName();
			}
		}
		if(portname==null)System.out.println("no port found");
	}
	public void openSerialPort() { 
		// 获取要打开的端口
		try {
			portId = CommPortIdentifier.getPortIdentifier(portname);
		} catch (NoSuchPortException e) {
			System.out.println("抱歉,没有找到"+portname+"串行端口号!");
			return ;
		}
		// 打开端口
		try {
			serialPort = (SerialPort) portId.open("JavaRs232", 2000);
			System.out.println(portname+"串口已经打开!");
		} catch (PortInUseException e) {
			System.out.println(portname+"端口已被占用,请检查!");
			return ;
		}
		
		// 设置端口参数
		try {
			int rate = 9600;
			int data = 8;
			int stop = 1;
			int parity =0;
			serialPort.setSerialPortParams(rate,data,stop,parity);
		} catch (UnsupportedCommOperationException e) {
			System.out.println(e.getMessage());
		}

		// 打开端口的IO流管道 
		try { 
			outputStream = serialPort.getOutputStream(); 
			inputStream = serialPort.getInputStream(); 
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} 

	

		serialPort.notifyOnDataAvailable(true); 
	} 
	public void sentmsg(String mesg){//发送数据
		try { 
			outputStream.write(mesg.getBytes()); 
			outputStream.flush(); 

		} catch (IOException e) { 
			System.out.println(e.getMessage());
		} 
		
		System.out.println("  发送: "+mesg);
	}

public void closeSerialPort() {   //关闭串口
        try {   
            if(outputStream != null)  
                outputStream.close();  
            if(serialPort != null)  
                serialPort.close();
            serialPort = null;  
            System.out.println(portname+"串口已经关闭!");  
        } catch (Exception e) {   
            System.out.println(e.getMessage());  
        }   
    }    
}

