package Lab2.UDP;

import java.io.*;
import java.net.*;
import java.util.*;
public class TimeServer extends Thread{
    boolean running=true;
    public TimeServer() {start();}
    public void run(){
        try{
            DatagramSocket socket = new DatagramSocket(1977);
            while(running){
                //asteapta client
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf,buf.length);
                socket.receive(packet);
                //citeste adresa si portul clientului
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                //trimite un reply catre client
                buf = ((new Date()).toString()).getBytes();
                packet = new DatagramPacket(buf,buf.length,address,port);
                socket.send(packet);
            }
        }catch(Exception ex){ex.printStackTrace();}
    }
    public static void main(String[] args) {
        TimeServer timeServer1 = new TimeServer();
    }
}