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
            application = new TicTacToeClient( "86.126.227.141","12345" ); // localhost
        else
            application = new TicTacToeClient( args[ 0 ] ,args[1] ); // use args

        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    } // end main
} // end class TicTacToeClientTest
