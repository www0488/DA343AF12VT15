package f12;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AlarmClient {
	
	public AlarmClient(String ipAddress, int serverPort, int notificationPort, long ms) throws IOException {
		new Connection(ipAddress, serverPort, notificationPort, ms).start();
	}
	
	private class Connection extends Thread {
		private String ipAddress;
		private int serverPort, notificationPort;
		private long ms;
		
		public Connection(String ipAddress, int serverPort, int notificationPort, long ms) {
			this.ipAddress = ipAddress;
			this.serverPort = serverPort;
			this.notificationPort = notificationPort;
			this.ms = ms;
		}
		
		public void run() {
			String response;
			try (ServerSocket notificationSocket = new ServerSocket(notificationPort) ) {
				try (Socket socket = new Socket(ipAddress,serverPort); 
						DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
						DataInputStream dis = new DataInputStream(socket.getInputStream()) ){

					dos.writeInt(notificationPort);
					dos.writeLong(ms);
					dos.flush();

					response = dis.readUTF();
					System.out.println(response);
				} 
				try(Socket nSocket = notificationSocket.accept();
					DataInputStream nis = new DataInputStream(nSocket.getInputStream())) {
					response = nis.readUTF();
					System.out.println(response + " till " + ipAddress +":" + notificationPort);
				}
			} catch(IOException e) {}
		}
	}
	
	public static void main(String[] args) throws IOException {
		new AlarmClient("127.0.0.1",3466, 3480, 15000);
		new AlarmClient("127.0.0.1",3466, 3481, 10000);
		new AlarmClient("127.0.0.1",3466, 3482, 5000);
	}
}
