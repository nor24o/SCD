package Lab1;

import java.net.*;
import  java.io.*;

public class ServerSimplu {
        public static void main(String[] args) throws IOException{
            ServerSocket ss=null;
            Socket socket=null;
            try{

                ss = new ServerSocket(1900); //crează obiectul serversocket
                socket = ss.accept(); //incepe aşteptarea pe portul 1900
                //în momentul în care un client s-a conectat ss.accept() returnează
                //un socket care identifică conexiunea
                //crează fluxurile de intrare/ieşire
                BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter( new BufferedWriter(new OutputStreamWriter( socket.getOutputStream())),true);

                String recvevidMessage=in.readLine();
                System.out.println("recevied "+recvevidMessage);
                for (int i=0;i<10;i++){
                    out.println("mesaj "+i);
                    System.out.println("Scris "+i);
                    Thread.sleep(100);
                }
                out.println("END");
                socket.close();
                ss.close();
            }catch(Exception e){e.printStackTrace();}
            finally{
                ss.close();
                if(socket!=null) socket.close();
            }
        }
    }

