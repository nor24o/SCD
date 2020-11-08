package Proiect;

import java.io.*;
import java.net.*;

public class ServerMultifir {
	public static final int PORT = 1900;
	TratareClient client_fara_partener;

	void startServer() {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(PORT);
			while (true) {
				Socket socket = ss.accept();
				if (client_fara_partener == null)
				{
					client_fara_partener = new TratareClient(socket);
				}
				else
				{
					TratareClient client;
					client = new TratareClient(socket);
					client.SetPartner(client_fara_partener);
					client_fara_partener.SetPartner(client);
					client.start();
					client_fara_partener.start();
					
					client_fara_partener = null;
				}				 
				
			}
		} catch (IOException ex) {
			System.err.println("Eroare :" + ex.getMessage());
		} finally {
			try {
				ss.close();
			} catch (IOException ex2) {
			}
		}
	}

	public static void main(String args[]) {
		ServerMultifir smf = new ServerMultifir();
		smf.startServer();
	}
}
