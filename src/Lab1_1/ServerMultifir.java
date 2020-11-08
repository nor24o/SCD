package Lab1_1;

/**
 *
 * @author horva
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerMultifir {


    static ArrayList<String> client=new ArrayList<String>();
    static ArrayList<Socket> clientSock=new ArrayList<Socket>();
    static int i = 0;

    public static void main(String args[]){



        Socket s=null;
        ServerSocket ss2=null;
        System.out.println("Server Listening......");
        try{
            ss2 = new ServerSocket(1900); // can also use static final PORT_NUM , when defined

        }
        catch(IOException e){
            e.printStackTrace();
            System.out.println("Server error");

        }

        while(true){
            try{
                s= ss2.accept();
                System.out.println("connection Established");
                ServerThread st=new ServerThread(s);
                i++;
                client.add("guest"+i);
                clientSock.add(s);
                System.out.println(client);
                System.out.println(clientSock);
                st.start();


            }

            catch(Exception e){
                e.printStackTrace();
                System.out.println("Connection Error");

            }
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

class ServerThread extends Thread{

    String line=null;
    BufferedReader  is = null;
    PrintWriter os=null;
    Socket s=null;

    public ServerThread(Socket s){
        this.s=s;
    }

    public void run() {
        try{
            is= new BufferedReader(new InputStreamReader(s.getInputStream()));
            os=new PrintWriter(s.getOutputStream());

        }catch(IOException e){
            System.out.println("IO error in server thread");
        }

        try {
            line=is.readLine();
            System.out.println("Response to Client before :  "+line);

            // while(line.compareTo("END")!=0){
            while (true) {


                os.println(line);
                os.flush();
                for(int i=0;i<4;i++) {
                    System.out.println("Raspuns catre Client  :  " + line+" "+i);
                }
                line=is.readLine();

            }
        } catch (IOException e) {

            line=this.getName(); //reused String line for getting thread name
            System.out.println("IO Error/ Client "+line+" terminated abruptly");
        }
        catch(NullPointerException e){
            line=this.getName(); //reused String line for getting thread name
            System.out.println("Client "+line+" Closed");
        }

        finally{
            try{
                System.out.println("Connection Closing..");
                if (is!=null){
                    is.close();
                    System.out.println(" Socket Input Stream Closed");
                }

                if(os!=null){
                    os.close();
                    System.out.println("Socket Out Closed");
                }
                if (s!=null){
                    s.close();
                    System.out.println("Socket Closed");
                }

            }
            catch(IOException ie){
                System.out.println("Socket Close Error");
            }
        }//end finally
    }
}