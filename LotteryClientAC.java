package f12;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class LotteryClientAC {

	public LotteryClientAC(String name, String ticket, String ipAddress, int port) {
			new Connection(name, ticket, ipAddress, port).start();
	}
	
	private class Connection extends Thread {
		private String result, name, ipAddress, ticket;
		private int serverPort;
		
		public Connection(String name, String ticket, String ipAddress, int serverPort) {
			this.name = name;
			this.ticket = ticket;
			this.ipAddress = ipAddress;
			this.serverPort = serverPort;
		}
		
		public void run() {
			DatagramSocket socket;
			DatagramPacket packet;
			InetAddress inetAddress;
			long time = System.currentTimeMillis();
			byte[] buffer = new byte[1024];
			try {
				inetAddress = InetAddress.getByName(ipAddress);
				socket = new DatagramSocket();  // ledig port

				buffer = ticket.getBytes();
				packet = new DatagramPacket(buffer,buffer.length,inetAddress,serverPort);
				socket.send(packet);

				packet = new DatagramPacket(buffer,buffer.length);
				socket.receive(packet);		
				time = System.currentTimeMillis()-time;
				result = new String(packet.getData(),0,packet.getLength());
				System.out.println(name + ", time: " + time + ", Result: " + result);
			} catch(Exception e) { 
			}
		}
	}
	
	public static void main(String[] args) {
		for(int i=1; i<=10; i++) {
			new LotteryClientAC("LotteryClientA "+i, "66666"+i ,"127.0.0.1" , 3460);
//			new LotteryClientAC("LotteryClientA "+i, "66666"+(i%10) ,"127.0.0.1" , 3462);
		}
	}
}
