package Teste;

import java.net.*;
import java.util.Scanner;



import java.io.*;

public class TCPClient {
    public static void main (String args[]) {
        // arguments supply message and hostname
        String clientObj;
        Socket s = null;
        try{
            int serverPort = 7899;
            Scanner scan=new Scanner(System.in);
            s = new Socket("127.0.0.1", serverPort);
            System.out.println("Connection Established");
            DataInputStream in = new DataInputStream( s.getInputStream());
            DataOutputStream out =new DataOutputStream( s.getOutputStream());
            while(true)
            {
                System.out.print(">");
                // String inputdata="hehg";
                String inputdata=scan.nextLine();
                clientObj=("ID "+inputdata );
                System.out.println("Sending data");
                out.writeUTF(clientObj);     // UTF is a string encoding see Sn. 4.4
                String data = in.readUTF();   // read a line of data from the stream
                System.out.println(data) ;       //writing received data
            }
        }catch (UnknownHostException e) {
            System.out.println("Socket:"+e.getMessage());
        }catch (EOFException e){
            System.out.println("EOF:"+e.getMessage());
        }catch (IOException e){
            System.out.println("readline:"+e.getMessage());
        }finally {
            if(s!=null) try {
                s.close();
            }catch (IOException e){
                System.out.println("close:"+e.getMessage());
            }
        }
    }
}
