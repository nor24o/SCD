package MainGame.Client;

// Fig. 24.16: TicTacToeClientTest.java
// Tests the TicTacToeClient class.
import javax.swing.JFrame;

public class TicTacToeRun
{
    public static void main( String args[] )
    {
        TicTacToeClient application; // declare client application

        // if no command line args
        if ( args.length == 0 )
            application = new TicTacToeClient( "127.0.0.1" ); // localhost
        else
            application = new TicTacToeClient( args[ 0 ] ); // use args

        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    } // end main
} // end class TicTacToeClientTest
