package MainGame.Server;

// Fig. 24.16: TicTacToeClientTest.java
// Tests the TicTacToeClient class.
import javax.swing.JFrame;
import java.net.UnknownHostException;


public class TicTacToeServerTest
    {
        public static void main( String args[] ) throws UnknownHostException {
            TicTacToeServer application = new TicTacToeServer("192.168.100.99",12345);
            application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            application.execute();
        } // end main
    }  // end main
