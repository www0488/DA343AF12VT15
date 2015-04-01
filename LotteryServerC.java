package f12;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

public class LotteryServerC implements Runnable{
	private String[] winningTickets = {"111111","222222","333333","444444","555555","666666","777777","888888","999999"};
    private Thread server = new Thread(this);
    private DatagramSocket socket;
    private Random rand = new Random();
	
    public LotteryServerC(int port) throws SocketException {
    	socket = new DatagramSocket(port);
    	server.start();
    }
	
	public void run() {
    	DatagramPacket packet;
    	byte[] buffer = new byte[256];
    	String ticket;
    	System.out.println("Server running");
    	while(true) {
    		try {
    			packet = new DatagramPacket(buffer,buffer.length);
    			socket.receive(packet);
    			ticket = new String(packet.getData(),0,packet.getLength());
    			
    			new ClientHandler(packet.getAddress(), packet.getPort(), ticket);
    			
    		} catch(Exception e) { 
    			System.err.println(e);
    		}
    	}
	}
	
    private String getResponse(String ticket) {
    	randomPause();
    	return checkTicket(ticket);
    }
    
    private void randomPause() {
		try {
			Thread.sleep(rand.nextInt(4)*1000);
		} catch (InterruptedException e) {}    	
    }
	
    // synchronisering behövs ofta då flera trådar använder samma resurs (ej i detta fall)
	private synchronized String checkTicket(String ticket) { 
		for(int i=0; i<winningTickets.length; i++) {
			if(ticket.equals(winningTickets[i])) {
				return "VINST";
			}
		}
		return "NIT";
	}

	private class ClientHandler extends Thread {
		private InetAddress address;
		private int port;
		private String ticket;
		
		public ClientHandler(InetAddress address, int port, String ticket) {
			this.address = address;
			this.port = port;
			this.ticket = ticket;
			start();
		}
		
		public void run() {
			String response = getResponse(ticket);
			byte[] outData = response.getBytes();
			DatagramPacket packet = new DatagramPacket(outData, outData.length, address, port);
			try {
				socket.send(packet);
			} catch (IOException e) {}
		}
	}
	
	public static void main(String[] args) throws SocketException {
		new LotteryServerC(3462);
	}
}
