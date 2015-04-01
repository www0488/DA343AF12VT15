package f12;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class LotteryServerB implements Runnable{
	private String[] winningTickets = {"111111","222222","333333","444444","555555","666666","777777","888888","999999"};
    private Thread server = new Thread(this);
    private ServerSocket serverSocket;
    private Random rand = new Random();
	
	public LotteryServerB(int port) {
		try {
			serverSocket = new ServerSocket(port);
            server.start();
		} catch (IOException e) {
			// Logging
		}
	}
	
	private String checkTicket(String ticket) {
		try {
			Thread.sleep(rand.nextInt(4)*1000);
		} catch (InterruptedException e) {}
		for(int i=0; i<winningTickets.length; i++) {
			if(ticket.equals(winningTickets[i])) {
				return "VINST";
			}
		}
		return "NIT";
	}
	
	public void run() {
    	String ticket,response;
    	System.out.println("Server running");
    	while(true) {
    		try (Socket socket = serverSocket.accept();
    				DataInputStream dis = new DataInputStream(socket.getInputStream());
    				DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
    			ticket = dis.readUTF();

    			response = checkTicket(ticket);

    			dos.writeUTF(response);
    			dos.flush();
    		} catch(IOException e) { 
    			System.err.println(e);
    		}
    	}
	}

	public static void main(String[] args) {
		new LotteryServerB(3461);
	}
}
