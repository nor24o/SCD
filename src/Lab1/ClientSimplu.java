package Lab1;

/**
 *
 * @author horvat
 */
import java.net.*;
import java.io.*;

public class ClientSimplu {

    public static void main(String[] args) throws Exception {
        Socket socket = null;
        try {
//creare obiect address care identifică adresa serverului
            InetAddress address = InetAddress.getByName("localhost");
//se putea utiliza varianta alternativă: InetAddress.getByName("127.0.0.1")
            socket = new Socket(address, 1900);
            BufferedReader in
                    = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));
// Output is automatically flushed
// by PrintWriter:
            PrintWriter out
                    = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())), true);
            out.println("sending tralalal..");
            String line ="a";
            while(!line.equals("END")){
                line= in.readLine();
                if(line==null){
                    System.out.println("nul");
                    line ="a";
                }
                else
                    System.out.println("recevied " +line);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
