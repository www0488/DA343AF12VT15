package f12;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class LotteryServerF implements Runnable{
	private String[] winningTickets = {"111111","222222","333333","444444","555555","666666","777777","888888","999999"};
    private Thread server = new Thread(this);
    private ServerSocket serverSocket;
    private Random rand = new Random();
    private RunOnThreadN pool;
	
    public LotteryServerF(int port, int nbrOfThreads) throws IOException {
    	pool = new RunOnThreadN(nbrOfThreads);
    	serverSocket = new ServerSocket(port);
    	pool.start();
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
    			pool.execute(new ClientHandler(socket));
    		} catch(IOException e) { 
    			System.err.println(e);
    		}
    	}
	}
	
	private class ClientHandler implements Runnable {
		private Socket socket;
		
		public ClientHandler(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			String ticket,response;
			System.out.println("Klient uppkopplad");
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
			System.out.println("Klient nerkopplad");
		}
	}

	public static void main(String[] args) throws IOException {
		new LotteryServerF(3465,50);
	}
}
