package Lab1_1;

/**
 *
 * @author horvat
 */

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClientSimplu {

    public static void main(String[] args) throws Exception {
        Socket socket = null;
        try {
//creare obiect address care identifică adresa serverului
            InetAddress address = InetAddress.getByName("localhost");
//se putea utiliza varianta alternativă: InetAddress.getByName("127.0.0.1")
            socket = new Socket(address, 7899);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
// Output is automatically flushed
// by PrintWriter:
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);



            String line ="a";
            while(!line.equals("END")){
                line= in.readLine();
                if(line==null){
                    System.out.println("Enter your mesage: ");
                    Scanner scanner = new Scanner(System.in);
                    String mesaj = scanner.nextLine();
                    System.out.println("Your mesage is " + mesaj);
                    out.println(mesaj);
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
