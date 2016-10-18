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
			// ����˿������Ǵ��ڣ����ӡ����˿���Ϣ
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				System.out.println(portId.getName());
				portname=portId.getName();
			}
		}
		if(portname==null)System.out.println("no port found");
	}
	public void openSerialPort() { 
		// ��ȡҪ�򿪵Ķ˿�
		try {
			portId = CommPortIdentifier.getPortIdentifier(portname);
		} catch (NoSuchPortException e) {
			System.out.println("��Ǹ,û���ҵ�"+portname+"���ж˿ں�!");
			return ;
		}
		// �򿪶˿�
		try {
			serialPort = (SerialPort) portId.open("JavaRs232", 2000);
			System.out.println(portname+"�����Ѿ���!");
		} catch (PortInUseException e) {
			System.out.println(portname+"�˿��ѱ�ռ��,����!");
			return ;
		}
		
		// ���ö˿ڲ���
		try {
			int rate = 9600;
			int data = 8;
			int stop = 1;
			int parity =0;
			serialPort.setSerialPortParams(rate,data,stop,parity);
		} catch (UnsupportedCommOperationException e) {
			System.out.println(e.getMessage());
		}

		// �򿪶˿ڵ�IO���ܵ� 
		try { 
			outputStream = serialPort.getOutputStream(); 
			inputStream = serialPort.getInputStream(); 
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} 

	

		serialPort.notifyOnDataAvailable(true); 
	} 
	public void sentmsg(String mesg){//��������
		try { 
			outputStream.write(mesg.getBytes()); 
			outputStream.flush(); 

		} catch (IOException e) { 
			System.out.println(e.getMessage());
		} 
		
		System.out.println("  ����: "+mesg);
	}

public void closeSerialPort() {   //�رմ���
        try {   
            if(outputStream != null)  
                outputStream.close();  
            if(serialPort != null)  
                serialPort.close();
            serialPort = null;  
            System.out.println(portname+"�����Ѿ��ر�!");  
        } catch (Exception e) {   
            System.out.println(e.getMessage());  
        }   
    }    
}

