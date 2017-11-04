package RMIserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class UdpMasterRMIServer extends Thread {
	
	private String BackupServer;
	private String UdpBackupRMIPort;
	private RMIServer rmserver;
	private DatagramSocket aSocket;
	private DatagramPacket request;
	private DatagramPacket replay;
	
	
	public UdpMasterRMIServer(String BackupServer, String UdpBackupRMIPort, RMIServer rmserver)
	{
		this.BackupServer = BackupServer;
		this.UdpBackupRMIPort = UdpBackupRMIPort;
		this.rmserver = rmserver;
		
		this.start();
	}
	
	
	public void run()
	{
		System.out.println("I AM A PRIMARY");
		//será Server UDP
		byte[] buffer;
		String response;
		
		boolean isBoken  = true;
		while(isBoken)
		{
			try {
				this.aSocket = new DatagramSocket(Integer.parseInt(this.UdpBackupRMIPort));
				//this.aSocket.setSoTimeout(5000);
				//comunicar com o cliente
				boolean isSocketBroken = true;
				while(isSocketBroken)
				{
					buffer = new byte[1024]; 			
					this.request = new DatagramPacket(buffer, buffer.length);
					try {
						
						
						this.aSocket.receive(this.request);//e bloaqueante
						//System.out.println("Server Recebeu: " + new String(request.getData(), 0, request.getLength()));
						
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							//e.printStackTrace();
						}
						response = new String("Ping from SERVER");
						this.replay = new DatagramPacket(response.getBytes(), 
								this.request.getLength(), this.request.getAddress(), this.request.getPort());
						
						this.aSocket.send(this.replay);
						
					} catch (SocketTimeoutException e) {
						System.out.println("TIMEOUT AO TENTAR LER DO SOCKET");
						
					} catch (IOException e) {}
				}
			
			} catch (SocketException e) {
				//if the socket could not be opened, or the socket could not bind to the specified local port.
				System.out.println("O Socket não pode ser aberto ou o porto está a ser usado: ");
			}
		}
	}

}
