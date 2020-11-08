package Proiect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

class TratareClient extends Thread
{  
	TratareClient partner;
	private Socket socket;     
	private BufferedReader in;  
	private PrintWriter out;                
	TratareClient(Socket socket)throws IOException     {                        
		this.socket = socket;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));     
		out = new PrintWriter(   new BufferedWriter( new OutputStreamWriter( socket.getOutputStream())),true);
	}                         
	public void SetPartner(TratareClient partner) {
		this.partner = partner;
		out.println("start joc!");
	}
	public void run() {    
		try {   
			while (true) { 
				String str = in.readLine();  
				if (str.equals("end")) break;
				partner.out.println(str); // redirectionez mesajul catre partener	
				
			}
		}
		catch(IOException e) {
			System.err.println("Clientul s-a deconectat");}         

	}              
}
