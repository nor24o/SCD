package Teste;

import java.net.*;
import java.util.ArrayList;



import java.io.*;

public class TCPServer {
    static ArrayList<String> client=new ArrayList<String>();
    static ArrayList<Socket> clientSock=new ArrayList<Socket>();
    static int i = 0;


    public static void main (String args[]) {
        try{
            int serverPort = 7899; // the server port
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while(true) {
                System.out.println("Server listening for a connection");
                Socket clientSocket = listenSocket.accept();
                i++;
                System.out.println("Received connection " + i );
                Connection c = new Connection(clientSocket);
                client.add("guest"+i);
                clientSock.add(clientSocket);
                System.out.println(client+ "sec "+ clientSock);
            }
        }
        catch(IOException e)
        {
            System.out.println("Listen socket:"+e.getMessage());
        }
    }
    public void display(String BroadMsg1)
    {
        for(int j=0;j<client.size();j++)
        {
            System.out.println(clientSock.get(j));
        }
    }
    public void broadcast(String BroadMsg)
    {
        String clientName=null;
        Socket cSock=null;
        //DataInputStream inBroad;
        DataOutputStream outBroad=null;
        for(int j=0;j<client.size();j++)
        {
            clientName=client.get(j);
            cSock=clientSock.get(j);
            try{
                outBroad=new DataOutputStream(cSock.getOutputStream());
                outBroad.writeUTF(clientName+">"+BroadMsg);
            }catch(Exception ex)
            {
        /*client.remove(j);
        clientSock.remove(j);*/
                System.out.println(ex.getMessage());
            }
        }
    }
}

class Connection extends Thread {
    TCPServer tcpser=new TCPServer();
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    static int uy=0;

    public Connection (Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream( clientSocket.getInputStream());
            out =new DataOutputStream( clientSocket.getOutputStream());
            this.start();
        } catch(IOException e) {
            System.out.println("Connection:"+e.getMessage());
        }
    }

    public void run(){
        String serObj=null;

        try {           // an echo server
            while(true)
            {
                System.out.println("server reading data");
                String data = in.readUTF();        // read a line of data from the stream
                try {
                    serObj=data;         //parsing JSONObject
                    System.out.println(serObj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("server writing data");
                // tcpser.display(serObj.get("ID").toString());
                System.out.println(serObj.substring(0,2));


                if(uy==0) {
                    System.out.println(serObj+ " trimis");
                    tcpser.broadcast(serObj.substring(3));
                    uy=1;
                }
            }}
        catch (EOFException e){
            System.out.println("EOF:"+e.getMessage());
        } catch(IOException e) {
            System.out.println("readline:"+e.getMessage());
        } finally{
            try {
                clientSocket.close();
            }catch (IOException e){/*close failed*/}
        }
    }
}