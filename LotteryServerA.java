package f12;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;

public class LotteryServerA implements Runnable{
	private String[] winningTickets = {"111111","222222","333333","444444","555555","666666","777777","888888","999999"};
    private Thread server = new Thread(this);
    private DatagramSocket socket;
    private Random rand = new Random();
	
	public LotteryServerA(int port) {
		try {
			socket = new DatagramSocket(port);
            server.start();
		} catch (SocketException e) {
			// Logging
		}
	}
	
	private boolean checkTicket(String ticket) {
		try {
			Thread.sleep(rand.nextInt(4)*1000);
		} catch (InterruptedException e) {}
		for(int i=0; i<winningTickets.length; i++) {
			if(ticket.equals(winningTickets[i])) {
				return true;
			}
		}
		return false;
	}
	
	public void run() {
    	DatagramPacket packet;
    	String ticket, response;
    	boolean win;
    	byte[] readBuffer = new byte[256];
    	byte[] outData;
    	System.out.println("Server running");
    	while(true) {
    		try {
    			packet = new DatagramPacket(readBuffer,readBuffer.length);

    			socket.receive(packet);
    			ticket = new String(packet.getData(),0,packet.getLength());
    			
    			win = checkTicket(ticket);
    			if(win) {
    			    response = "VINST";
    			} else {
    			    response = "NIT";
    			}
    			outData = response.getBytes();
    			packet = new DatagramPacket(outData,outData.length,packet.getAddress(),packet.getPort());
    			socket.send(packet);
    		} catch(Exception e) { 
    			System.err.println(e);
    		}
    	}
	}

	public static void main(String[] args) {
		new LotteryServerA(3460);
	}
}
