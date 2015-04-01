package f12;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class LotteryClientE implements Client {
	private UI ui = new UI(this);
	private Socket socket;
	private Connection connection;
	private String ipAddress;
	private int serverPort;
	
	public LotteryClientE(String ipAddress, int serverPort) {
		this.ipAddress = ipAddress;
		this.serverPort = serverPort;
		ui.connected(false);
		showClient();
	}
	
	private void showClient() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(ui);
		frame.pack();
		frame.setVisible(true);
	}

	public void connect() {
		if(connection == null) {
			try {
				socket = new Socket(ipAddress, serverPort);
				connection = new Connection(socket);
				connection.start();
				ui.connected(true);
			} catch(IOException e) {
				disconnect();
			}
		}
	}
	
	public void disconnect() {
		try {
			connection = null;
			socket.close();
		} catch(Exception e) {}
		ui.connected(false);
	}

	public void checkTicket(String ticket) {
		if(connection!=null) {
		    connection.checkTicket(ticket);
		}
	}
	
	private void setResult(final String result) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ui.setResult(result);
			}
		});
	}
	
	private class Connection extends Thread {
		private DataInputStream dis;
		private DataOutputStream dos;
		private String result;
		
		public Connection(Socket socket) throws IOException {
			    dis = new DataInputStream(socket.getInputStream());
			    dos = new DataOutputStream(socket.getOutputStream());
		}
		
		public void checkTicket(String ticket) {
			try {
    			dos.writeUTF(ticket);
	    		dos.flush();
			} catch(IOException e) {}
		}
		
		public void run() {
			try {
				while(true) {
					result = dis.readUTF();
					setResult(result);
				}
			} catch(IOException e) {}
			try {
				disconnect();
			} catch(Exception e) {}
		}
	}
	
	public static void main(String[] args) {
    	new LotteryClientE("127.0.0.1" , 3464);
	}
}
