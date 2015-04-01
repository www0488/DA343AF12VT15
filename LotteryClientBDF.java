package f12;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class LotteryClientBDF {

	public LotteryClientBDF(String name, String ticket, String ipAddress, int port) {
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
			long time = System.currentTimeMillis();
			try (Socket socket = new Socket(ipAddress,serverPort); 
				 DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				 DataInputStream dis = new DataInputStream(socket.getInputStream()) ){
				
				dos.writeUTF(ticket);
				dos.flush();

				result = dis.readUTF();
				time = System.currentTimeMillis()-time;
				System.out.println(name + ", time: " + time + ", Result: " + result + ", ticket=" + ticket);
			} catch(Exception e) { 
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		for(int i=1; i<=1000; i++) {
			new LotteryClientBDF("LotteryClientB "+i, "66666"+i ,"127.0.0.1" , 3461);
//			new LotteryClientBDF("LotteryClientD "+i, "66666"+(i%10) ,"127.0.0.1" , 3463);
//			Thread.sleep(10);
//			new LotteryClientBDF("LotteryClientF "+i, "66666"+(i%10) ,"127.0.0.1" , 3465);
		}
	}
}
