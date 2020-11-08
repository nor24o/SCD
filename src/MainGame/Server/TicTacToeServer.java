package MainGame.Server;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import javax.swing.*;
import javax.swing.text.NumberFormatter;

class TicTacToeServer extends JFrame {
    private final static int PLAYER_X = 0; // constant for first player
    private final static int PLAYER_O = 1; // constant for second player
    private final static String[] MARKS = {"X", "O"}; // array of marks
    private String[] board = new String[9]; // tic-tac-toe board
    private JTextArea outputArea; // for outputting moves
    private JTextField v0TextField; // for input
    private Player[] players; // array of Players
    private ServerSocket server; // server socket to connect with clients
    private int currentPlayer; // keeps track of player with current move
    private ExecutorService runGame; // will run players
    private Lock gameLock; // to lock game for synchronization
    private Condition otherPlayerConnected; // to wait for other player
    private Condition otherPlayerTurn; // to wait for other player's turn
    private InetAddress addr;
    private int port_vall;
    private int W_Width = 300;
    private int W_Heigth = 400;
    private String ip;
    private String reponsetoclient="";

    // set up tic-tac-toe server and GUI that displays messages
    TicTacToeServer(String ip, int port_vall) throws UnknownHostException {
        super("Tic-Tac-Toe Server"); // set title of window
        this.port_vall = port_vall;
        this.ip = ip;

// Cream Thread pentru ambi playeri
        runGame = Executors.newFixedThreadPool(2);
        gameLock = new ReentrantLock(); // create lock for game

//     Asteptam ambi pleyeri sa se conecteze
        otherPlayerConnected = gameLock.newCondition();

        // Variabila pentru a astepta player 2 ca sa faca o miscare
        otherPlayerTurn = gameLock.newCondition();

        for (int i = 0; i < 9; i++)
            board[i] = new String(""); // create tic-tac-toe board
        players = new Player[2]; // create array of players
        currentPlayer = PLAYER_X; // set current player to first player

        getContentPane().setLayout(null);

        v0TextField = new JTextField();
        add(v0TextField);
        v0TextField.setText("Server Log");
        v0TextField.setEditable(false);
        v0TextField.setBounds(4, 0, W_Width, 20);


        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        //   this.setLocation(dim.width/4-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        this.setLocation(-400, 100);
        setSize(W_Width, W_Heigth); // set size of window
        setVisible(true); // show window

        outputArea = new JTextArea(); // create JTextArea for output
        outputArea.setBounds(4, 25, 300, W_Heigth - 25);
        outputArea.setEditable(false);
        add(outputArea);

        outputArea.setText("Server awaiting connections\n");
        String out_p = "on port : " + port_vall + "\n";
        outputArea.append(out_p);

        addr = InetAddress.getByName(ip);
        setResizable(false);
        try {

            server = new ServerSocket(port_vall, 2, addr); // set up ServerSocket
        } // end try
        catch (IOException ioException) {
            ioException.printStackTrace();
            System.exit(1);
        } // end catch


    } // end TicTacToeServer constructor

    // Asteptam 2 conectiuni
    void execute() {
        // Asteptam dup ambi pleyeri
        for (int i = 0; i < players.length; i++) {
            try // wait for connection, create Player, start runnable
            {
                players[i] = new Player(server.accept(), i);
                runGame.execute(players[i]); // execute player runnable
            } // end try
            catch (IOException ioException) {
                ioException.printStackTrace();
                System.exit(1);
            } // end catch
        } // end for

        gameLock.lock(); // lock game to signal player X's thread

        try {
            players[PLAYER_X].setSuspended(false); // resume player X
            otherPlayerConnected.signal(); // wake up player X's thread
        } // end try
        finally {
            gameLock.unlock(); // unlock game after signalling player X
        } // end finally
    } // end method execute

    // display message in outputArea
    private void displayMessage(final String messageToDisplay) {
        // display message from event-dispatch thread of execution
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() // updates outputArea
                    {
                        outputArea.append(messageToDisplay); // add message
                    } // end method run
                } // end inner class
        ); // end call to SwingUtilities.invokeLater
    } // end method displayMessage

    // determine if move is valid
    private boolean validateAndMove(int location, int player) {
        // while not current player, must wait for turn
        while (player != currentPlayer) {
            gameLock.lock(); // lock game to wait for other player to go

            try {
                otherPlayerTurn.await(); // wait for player's turn
            } // end try
            catch (InterruptedException exception) {
                exception.printStackTrace();
            } // end catch
            finally {
                gameLock.unlock(); // unlock game after waiting
            } // end finally
        } // end while

        // if location not occupied, make move
        if (!isOccupied(location)) {
            board[location] = MARKS[currentPlayer]; // set move on board
            currentPlayer = (currentPlayer + 1) % 2; // change player

            // let new current player know that move occurred
            players[currentPlayer].otherPlayerMoved(location);

            gameLock.lock(); // lock game to signal other player to go

            try {
                otherPlayerTurn.signal(); // signal other player to continue
            } // end try
            finally {
                gameLock.unlock(); // unlock game after signaling
            } // end finally

            return true; // notify player that move was valid
        } // end if
        else // move was not valid
            return false; // notify player that move was invalid
    } // end method validateAndMove

    // determine whether location is occupied
    private boolean isOccupied(int location) {
        // location is not occupied
        return board[location].equals(MARKS[PLAYER_X]) || board[location].equals(MARKS[PLAYER_O]); // location is occupied
    } // end method isOccupied

    // place code in this method to determine whether game over
    private boolean isGameOver() {
        if(PlayerOWinCheck()){
            reponsetoclient="Player O WIns";
            System.out.println("Player O WIns");
            return true;
        }
        if(PlayerXWinCheck()){
            reponsetoclient="Player X WIns";
            System.out.println("Player X WIns");
            return true;

        }
        else
        return false; // this is left as an exercise
    } // end method isGameOver


    public boolean PlayerOWinCheck() {
        if ((board[0].equals(MARKS[PLAYER_O])) && (board[1].equals(MARKS[PLAYER_O])) && (board[1].equals(MARKS[PLAYER_O])) && (board[2].equals(MARKS[PLAYER_O])))
            return true;
        else if ((board[3].equals(MARKS[PLAYER_O])) && (board[4].equals(MARKS[PLAYER_O])) && (board[4].equals(MARKS[PLAYER_O])) && (board[5].equals(MARKS[PLAYER_O])))
            return true;
        else if ((board[6].equals(MARKS[PLAYER_O])) && (board[7].equals(MARKS[PLAYER_O])) && (board[7].equals(MARKS[PLAYER_O])) && (board[8].equals(MARKS[PLAYER_O])))
            return true;

        if ((board[0].equals(MARKS[PLAYER_O])) && (board[3].equals(MARKS[PLAYER_O])) && (board[3].equals(MARKS[PLAYER_O])) && (board[6].equals(MARKS[PLAYER_O])))
            return true;
        else if ((board[1].equals(MARKS[PLAYER_O])) && (board[4].equals(MARKS[PLAYER_O])) && (board[4].equals(MARKS[PLAYER_O])) && (board[7].equals(MARKS[PLAYER_O])))
            return true;
        else if ((board[2].equals(MARKS[PLAYER_O])) && (board[5].equals(MARKS[PLAYER_O])) && (board[5].equals(MARKS[PLAYER_O])) && (board[8].equals(MARKS[PLAYER_O])))
            return true;

        if ((board[0].equals(MARKS[PLAYER_O])) && (board[4].equals(MARKS[PLAYER_O])) && (board[4].equals(MARKS[PLAYER_O])) && (board[8].equals(MARKS[PLAYER_O])))
            return true;
        else if ((board[2].equals(MARKS[PLAYER_O])) && (board[4].equals(MARKS[PLAYER_O])) && (board[4].equals(MARKS[PLAYER_O])) && (board[6].equals(MARKS[PLAYER_O])))
            return true;
        else
            return false;
    }



    public boolean PlayerXWinCheck() {

        if((board[0].equals(MARKS[PLAYER_X]))&&(board[1].equals(MARKS[PLAYER_X]))&&(board[1].equals(MARKS[PLAYER_X]))&&(board[2].equals(MARKS[PLAYER_X])))
            return true;
        else if ((board[3].equals(MARKS[PLAYER_X]))&&(board[4].equals(MARKS[PLAYER_X]))&&(board[4].equals(MARKS[PLAYER_X]))&&(board[5].equals(MARKS[PLAYER_X])))
            return true;
        else if ((board[6].equals(MARKS[PLAYER_X]))&&(board[7].equals(MARKS[PLAYER_X]))&&(board[7].equals(MARKS[PLAYER_X]))&&(board[8].equals(MARKS[PLAYER_X])))
            return true;

        if((board[0].equals(MARKS[PLAYER_X]))&&(board[3].equals(MARKS[PLAYER_X]))&&(board[3].equals(MARKS[PLAYER_X]))&&(board[6].equals(MARKS[PLAYER_X])))
            return true;
        else if ((board[1].equals(MARKS[PLAYER_X]))&&(board[4].equals(MARKS[PLAYER_X]))&&(board[4].equals(MARKS[PLAYER_X]))&&(board[7].equals(MARKS[PLAYER_X])))
            return true;
        else if ((board[2].equals(MARKS[PLAYER_X]))&&(board[5].equals(MARKS[PLAYER_X]))&&(board[5].equals(MARKS[PLAYER_X]))&&(board[8].equals(MARKS[PLAYER_X])))
            return true;

        if((board[0].equals(MARKS[PLAYER_X]))&&(board[4].equals(MARKS[PLAYER_X]))&&(board[4].equals(MARKS[PLAYER_X]))&&(board[8].equals(MARKS[PLAYER_X])))
            return true;
        else if ((board[2].equals(MARKS[PLAYER_X]))&&(board[4].equals(MARKS[PLAYER_X]))&&(board[4].equals(MARKS[PLAYER_X]))&&(board[6].equals(MARKS[PLAYER_X])))
            return true;
        else
            return false;




    }






    // private inner class Player manages each Player as a runnable
    private class Player implements Runnable {
        private Socket connection; // connection to client
        private Scanner input; // input from client
        private Formatter output; // output to client
        private int playerNumber; // tracks which player this is
        private String mark; // mark for this player
        private boolean suspended = true; // whether thread is suspended

        // set up Player thread
        Player(Socket socket, int number) {
            playerNumber = number; // store this player's number
            mark = MARKS[playerNumber]; // specify player's mark
            connection = socket; // store socket for client

            try // obtain streams from Socket
            {
                input = new Scanner(connection.getInputStream());
                output = new Formatter(connection.getOutputStream());
            } // end try
            catch (IOException ioException) {
                ioException.printStackTrace();
                System.exit(1);
            } // end catch
        } // end Player constructor

        // send message that other player moved
        void otherPlayerMoved(int location) {
            output.format("Opponent moved\n");
            output.format("%d\n", location); // send location of move
            output.flush(); // flush output
        } // end method otherPlayerMoved

        // control thread's execution
        public void run() {
            // send client its mark (X or O), process messages from client
            try {
                displayMessage("Player " + mark + " connected\n");
                output.format("%s\n", mark); // send player's mark
                output.flush(); // flush output

                // if player X, wait for another player to arrive
                if (playerNumber == PLAYER_X) {
                    output.format("%s\n%s", "Player X connected", "Waiting for another player\n");
                    output.flush(); // flush output

                    gameLock.lock(); // lock game to wait for second player

                    try {
                        while (suspended) {
                            otherPlayerConnected.await(); // wait for player O
                        } // end while
                    } // end try
                    catch (InterruptedException exception) {
                        exception.printStackTrace();
                    } // end catch
                    finally {
                        gameLock.unlock(); // unlock game after second player
                    } // end finally

                    // send message that other player connected
                    output.format("Other player connected. Your move.\n");
                    output.flush(); // flush output
                } // end if
                else {
                    output.format("Player O connected, please wait\n");
                    output.flush(); // flush output
                } // end else

                // while game not over
                while (!isGameOver()) {
                    int location = 0; // initialize move location

                    if (input.hasNext())
                        location = input.nextInt(); // get move location

                    // check for valid move
                    if (validateAndMove(location, playerNumber)) {
                        if(!PlayerOWinCheck()&&!PlayerXWinCheck()) {
                            displayMessage("\nlocation: " + location + " Player : " + playerNumber);
                            output.format("Valid move.\n"); // notify client
                            output.flush(); // flush output
                        }
                        else{
                            output.format("Valid move.\n"); // notify client
                            output.flush(); // flush output
                            displayMessage("\n"+reponsetoclient );
                            output.format(reponsetoclient+"\n"); // notify client
                            output.flush(); // flush output
                            output.format(reponsetoclient+"\n"); // notify client
                            output.flush(); // flush output
                        }
                    } // end if
                    else // move was invalid
                    {
                        output.format("Invalid move, try again\n");
                        output.flush(); // flush output
                    } // end else
                } // end while
            } // end try
            finally {
                try {
                    connection.close(); // close connection to client
                } // end try
                catch (IOException ioException) {
                    ioException.printStackTrace();
                    System.exit(1);
                } // end catch
            } // end finally
        } // end method run

        // set whether or not thread is suspended
        void setSuspended(boolean status) {
            suspended = status; // set value of suspended
        } // end method setSuspended
    }


    // end class Player
} // end class TicTacToeServer
