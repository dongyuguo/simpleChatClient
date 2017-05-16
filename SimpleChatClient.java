package simpleChatClient;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class SimpleChatClient {

	JTextArea incoming;
	JTextField outgoing;
	BufferedReader reader;
	PrintWriter writer;
	Socket sock;
	String hostName;
	String hostIP;
	JButton sendButton;
	
	public static void main(String[] args) {
		SimpleChatClient client = new SimpleChatClient();
		client.go();
	}

	private void go() {
		setUpGui();
		setUpNetworking();
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
	}

	private void setUpGui() {
		JFrame frame = new JFrame("Simple Chat Client");
		JPanel mainPanel = new JPanel();
		JLabel labelHostName = new JLabel("主机名:");
		JLabel labelHostIP = new JLabel("IP地址：");
		
		getLocalIPAddress(labelHostName, labelHostIP);
		incoming = new JTextArea(15, 20);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		outgoing = new JTextField(15);
		sendButton = new JButton("Send");
		JScrollPane qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		outgoing.addKeyListener(new SendKeyListener());
		sendButton.addActionListener(new SendButtonListener());
		mainPanel.add(labelHostName);
		mainPanel.add(labelHostIP);
		mainPanel.add(qScroller);
		mainPanel.add(outgoing);
		mainPanel.add(sendButton);
		
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.setSize(270, 360);
		frame.setVisible(true);
	}

	private void getLocalIPAddress(JLabel labelHostName, JLabel labelHostIP) {
		String hostInfo = null;
		String[] hostInfoArr = new String[10];
		try {
			hostInfo = InetAddress.getLocalHost().toString();
			hostInfoArr = hostInfo.split("/");
			hostName = hostInfoArr[0];
			hostIP = hostInfoArr[1];
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if (hostInfo != null) {
			labelHostName.setText(labelHostName.getText() + hostName);
			labelHostIP.setText(labelHostIP.getText() + hostIP);
		} else {
			labelHostName.setText(labelHostName.getText() + "offline");
			labelHostIP.setText(labelHostIP.getText() + "offline");
		}
	}

	private void setUpNetworking() {
		try {
			sock = new Socket("127.0.0.1", 5000);
			InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(streamReader);
			writer = new PrintWriter(sock.getOutputStream());
			System.out.println("neworking established");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	class SendKeyListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent arg0) {
			if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
				sendButton.doClick();
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) { }

		@Override
		public void keyTyped(KeyEvent arg0) { }
		
	}

	class SendButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				writer.println(outgoing.getText());
				writer.flush();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			outgoing.setText("");
			outgoing.requestFocus();
		}
		
	}
	
	class IncomingReader implements Runnable {

		@Override
		public void run() {
			String message;
			try {
				while((message = reader.readLine()) != null) {
					System.out.println("read " + message);
					incoming.append(hostName + ":\n    " +message + "\n");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}
}
