package Proiect;

import java.net.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*; 

public class Client {
	PrintWriter out;
	BufferedReader in;
	JTextArea tarea;
    public void initConnection() throws IOException {
		InetAddress address =InetAddress.getByName("localhost");
		Socket socket = new Socket(address,1900); 
		in = new BufferedReader( new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter( new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
	}
	void initFrame() {
		JFrame frame = new JFrame("Demo program for JFrame");
		frame.setLayout(null);
		
		JButton bsend = new JButton("send");
		bsend.setBounds(200,100,80,30);
		JTextField txtMesaj = new JTextField(50);
		txtMesaj.setBounds(20,100, 150,30);
		tarea = new JTextArea(5,50);
		tarea.setBounds(20,20,150,70);
		bsend.addActionListener(new ActionListener()	
		{
			 public void actionPerformed(ActionEvent e)
			 {
				 System.out.println("mesaj: " + txtMesaj.getText());
				 Client.this.out.println(txtMesaj.getText());
				 txtMesaj.setText("");
			 }
			 });
		frame.add(txtMesaj);
		frame.add(bsend);
		frame.add(tarea);
		frame.setVisible(true);
		frame.setSize(300, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args)throws Exception{
		Client c = new Client();
		c.initConnection();
		c.initFrame();

		while (true) {
			String msg = c.in.readLine();
			c.tarea.setText(msg);
		}
	}
}