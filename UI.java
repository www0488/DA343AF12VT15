package f12;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class UI extends JPanel {
	private Client client;
	private JTextField tfTicketNbr = new JTextField();
	private JButton btnCheck = new JButton("Kontrollera vinst");
	private JButton btnConnect = new JButton("Connect");
	private JLabel lblResult = new JLabel(" ");
	private boolean connected = false;
	
	public UI(Client client) {
		this.client = client;
		JPanel centerPanel = new JPanel(new BorderLayout());
		JPanel northPanel = new JPanel(new BorderLayout());
		JLabel lblTicketNbr = new JLabel("Lottnummer");
		setLayout(new BorderLayout());
		tfTicketNbr.setPreferredSize(new Dimension(200,25));
//		lblTicketNbr.setPreferredSize(new Dimension(200,20));
		btnConnect.setPreferredSize(new Dimension(150,25));
		btnCheck.setPreferredSize(new Dimension(150,25));
		northPanel.add(lblTicketNbr,BorderLayout.CENTER);
		northPanel.add(btnConnect,BorderLayout.EAST);
		centerPanel.add(tfTicketNbr,BorderLayout.CENTER);
		centerPanel.add(btnCheck,BorderLayout.EAST);
		add(northPanel,BorderLayout.NORTH);
		add(centerPanel,BorderLayout.CENTER);
		add(lblResult,BorderLayout.SOUTH);
		btnCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setResult(" ");
				client.checkTicket(tfTicketNbr.getText());
			}
		});
		btnConnect.addActionListener(new Connect());
	}
	
	private class Connect implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(!connected) {
				client.connect();
			} else {
				client.disconnect();
			}
			
		}
	}
	
	public void connected(boolean connected) {
		this.connected = connected;
		btnCheck.setEnabled(connected);
		tfTicketNbr.setEnabled(connected);
		lblResult.setText(" ");
		if(connected) {
    		btnConnect.setText("Koppla ner");
		} else  {
			btnConnect.setText("Koppla upp");
		}
	}
	
	public void setResult(String result) {
		lblResult.setText(result);
	}
}
