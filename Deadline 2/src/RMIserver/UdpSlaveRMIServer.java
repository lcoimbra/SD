package RMIserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class UdpSlaveRMIServer extends Thread {
	
	private String IpServer;
	private String UdpMasterRMIPort;
	private RMIServer rmserver;
	private DatagramSocket aSocket = null;
	private DatagramPacket request = null;
	private DatagramPacket replay = null;
	private InetAddress aHost;
	private int failure;
	

	public UdpSlaveRMIServer(String IpServer, String UdpMasterRMIPort,RMIServer rmserver)
	{
		this.IpServer = IpServer;
		this.UdpMasterRMIPort = UdpMasterRMIPort;
		this.rmserver = rmserver;
		
		this.failure = 0;

		//poderá tomar o lugar do master - falta implementar
		this.start();
	}
	
	public void run()
	{
		System.out.println("I AM BACKUP");
		//será Cliente UDP
		
		
		String msg = null;
		byte[] m;
		//criar o socket UDP
		boolean isBroke = true;
		while(isBroke)	
		{
			try {
				
				this.aSocket = new DatagramSocket();
				this.aSocket.setSoTimeout(3000);
				boolean isUDPBroke = true;
				while(isUDPBroke)
				{
					try {
						
						//InetAddress addr = InetAddress.getByAddress("Localhost",ipAddr);
						this.aHost = InetAddress.getByName(this.IpServer);
						
						//enviar o pacote criado usando o socket
						try {
							
							//Criar o pacote UDP a enviar
							msg = new String("Ping from Client");
							m = msg.getBytes();
							this.request = new DatagramPacket(m,m.length,aHost,Integer.parseInt(this.UdpMasterRMIPort));
							this.aSocket.send(request);
							
							
							//vamos ler a resposta
							m = new byte[1024];
							this.replay = new DatagramPacket(m, m.length);
							
							this.aSocket.receive(replay);//e bloqueante
							//System.out.println("Recebeu do server UDP: " + new String(replay.getData(), 0, replay.getLength()));
							
							if(this.failure>0)
								this.failure = 0;
							/*System.out.println("Press enter to finish...");            
							try {
								System.in.read();
							} catch (IOException re1) {
								re1.printStackTrace();
							}*/
						
						} catch (SocketTimeoutException e) {
							
							System.out.println("TIMEOUT AO TENTAR LER DO SOCKET");
							this.failure += 1;
							if(this.failure>4){
								//System.out.println("WILL BE MASTER");
								this.rmserver.isMaster = true;
								//this.rmserver.notify();
								
								//close socket
								this.aSocket.close();
								//sair do ciclo
								isUDPBroke = false;
							}else{
								System.out.println("PRIMARY SERVER DOWN?!");
							}

						} catch (IOException e) {
							//System.out.println("ERROR RECEIVE MSG UDP");
							//e.printStackTrace();
						}
						
						//isUDPBroke = false;
						
					} catch (UnknownHostException e) {
						//System.out.println(" PROBLEM WITH HOST ");
						//e.printStackTrace();
						
						//System.out.println("Press enter to finish...");
						try {
							System.in.read();
						} catch (IOException re1) {
							//re1.printStackTrace();
						}
						isUDPBroke = true;
					}
					
				}
				
				isBroke = false;
			} catch (SocketException e) {
				//System.out.println("ERROR CREATING SOCKET");
				
				//System.out.println("Press enter to finish...");
				try {
					System.in.read();
				} catch (IOException re1) {
					re1.printStackTrace();
				}
			}
		}
		
		//System.out.println("UDP SLAVE IS GOINGO TO DIE");

	}

}
