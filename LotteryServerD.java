package f12;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class LotteryServerD implements Runnable{
	private String[] winningTickets = {"111111","222222","333333","444444","555555","666666","777777","888888","999999"};
    private Thread server = new Thread(this);
    private ServerSocket serverSocket;
    private Random rand = new Random();
	
    public LotteryServerD(int port) throws IOException {
    	serverSocket = new ServerSocket(port);
    	server.start();
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

	public void run() {
    	System.out.println("Server running");
    	while(true) {
    		try  {
    			Socket socket = serverSocket.accept();
    			new ClientHandler(socket).start();
    		} catch(IOException e) { 
    			System.err.println(e);
    		}
    	}
	}
	
	private class ClientHandler extends Thread {
		private Socket socket;
		
		public ClientHandler(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			String ticket,response;
			try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				 DataInputStream dis = new DataInputStream(socket.getInputStream())	) {
				ticket = dis.readUTF();
				response = getResponse(ticket);
				dos.writeUTF(response);
				dos.flush();
			} catch(IOException e) {}
			try {
				socket.close();
			} catch(Exception e) {}
		}
	}

	public static void main(String[] args) throws IOException {
		new LotteryServerD(3463);
	}
}
