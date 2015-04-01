package f12;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmServer implements Runnable{
	private Timer timer = new Timer();
    private Thread server = new Thread(this);
    private ServerSocket serverSocket;
	
    public AlarmServer(int port) throws IOException {
    	serverSocket = new ServerSocket(port);
    	server.start();
    }
	
    private String getResponse(InetAddress inetAddress, int port, long ms) {
    	timer.schedule(new Notification(inetAddress,port), ms);
    	return "ALARM to " + inetAddress + ":" + port +" om " + ms + " ms";
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
			int notificationPort; 
			long ms;
			String response;
			try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				 DataInputStream dis = new DataInputStream(socket.getInputStream())	) {
				notificationPort = dis.readInt();
				ms = dis.readLong(); 
				response = getResponse(socket.getInetAddress(), notificationPort, ms);
				dos.writeUTF(response);
				dos.flush();
			} catch(IOException e) {}
			try {
				socket.close();
			} catch(Exception e) {}
		}
	}
	
	private class Notification extends TimerTask {
		private InetAddress inetAddress;
		private int port;
		
		public Notification(InetAddress inetAddress, int port) {
			this.inetAddress = inetAddress;
			this.port = port;
		}
		
		public void run() {
			try (Socket socket = new Socket(inetAddress,port);
				 DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
				dos.writeUTF("ALARM");
				dos.flush();
			} catch(IOException e) {}
		}
	}

	public static void main(String[] args) throws IOException {
		new AlarmServer(3466);
	}
}
