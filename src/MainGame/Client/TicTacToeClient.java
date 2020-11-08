package MainGame.Client;

// Fig. 24.15: TicTacToeClient.java
// Client that let a user play Tic-Tac-Toe with another across a network.

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import javax.swing.*;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class TicTacToeClient extends JFrame implements Runnable {
    private final String X_MARK = "X"; // mark for first client
    private final String O_MARK = "O"; // mark for second client
    private JTextField idField; // textfield to display player's mark
    private JTextArea displayArea; // JTextArea to display output
    private JPanel boardPanel; // panel for tic-tac-toe board
    private JPanel panel2; // panel to hold board
    private Square board[][]; // tic-tac-toe board
    private Square currentSquare; // current square
    private Socket connection; // connection to server
    private BufferedReader in;
    private Scanner input; // input from server
    private Formatter output; // output to server
    private String ticTacToeHost; // host name for server
    private String ticTacToePort; // host port for server
    private String myMark; // this client's mark
    private boolean myTurn; // determines which client's turn it is
    private int screen_size_W=600;
    private int screen_size_H=900;
    private  boolean tryreconnect=true;


    // set up user-interface and board
    public TicTacToeClient(String host,String port) {
        ticTacToeHost = host; // set name of server
        ticTacToePort =port;
        displayArea = new JTextArea(4, 30); // set up JTextArea
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.SOUTH);

        boardPanel = new JPanel(); // set up panel for squares in board
        boardPanel.setLayout(new GridLayout(3, 3, 0, 0));
        setResizable(false);
        board = new Square[3][3]; // create board

       // loop over the rows in the board
        for (int row = 0; row < board.length; row++) {
       // loop over the columns in the board
            for (int column = 0; column < board[row].length; column++) {
       // create square
                board[row][column] = new Square(" ", row * 3 + column);
                boardPanel.add(board[row][column]); // add square
            } // end inner for
        } // end outer for

        idField = new JTextField(); // set up textfield
        idField.setEditable(false);
        add(idField, BorderLayout.NORTH);

        panel2 = new JPanel(); // set up panel to contain boardPanel
        panel2.add(boardPanel, BorderLayout.CENTER); // add board panel
        add(panel2, BorderLayout.CENTER); // add container panel

        setSize(screen_size_W, screen_size_H); // set size of window
        setVisible(true); // show window

        startClient();
    } // end TicTacToeClient constructor

    // start the client thread
    public void startClient() {
      while (tryreconnect){
        try // connect to server, get streams and start outputThread
        {
// make connection to server
            connection = new Socket(InetAddress.getByName(ticTacToeHost), Integer.parseInt(ticTacToePort));

// get streams for input and output
            input = new Scanner(connection.getInputStream());
            in = new BufferedReader( new InputStreamReader(connection.getInputStream()));
            output = new Formatter(connection.getOutputStream());
            tryreconnect=false;
        } // end try
        catch (IOException ioException) {
          tryreconnect=true;

      } // end catch

    }

// create and start worker thread for this client
        ExecutorService worker = Executors.newFixedThreadPool(1);
        worker.execute(this); // execute client
    } // end method startClient

    // control thread that allows continuous update of displayArea
    public void run() {
        myMark = input.nextLine(); // get player's mark (X or O)

        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        // display player's mark
                        idField.setText("You are player \"" + myMark + "\"");
                    } // end method run
                } // end anonymous inner class
        ); // end call to SwingUtilities.invokeLater

        myTurn = (myMark.equals(X_MARK)); // determine if client's turn

        // receive messages sent to client and output them
        while (true) {
            if (input.hasNextLine())
                processMessage(input.nextLine());

        } // end while
    } // end method run

    // process messages received by client
    private void processMessage(String message) {
        if (message.equals("Player O WIns")) {
            displayMessage(message + "\n"); // display the message
            System.out.println(message);
            myTurn = false; //
        } // end else if

        if (message.equals("Player X WIns")) {
            displayMessage(message + "\n"); // display the message

            System.out.println(message);
            myTurn = false; //
        } // end else if

        // valid move occurred
        if (message.equals("Valid move.")) {
            displayMessage("Valid move, please wait.\n");
            setMark(currentSquare, myMark); // set mark in square
        } // end if
        else if (message.equals("Invalid move, try again")) {
            displayMessage(message + "\n"); // display invalid move
            myTurn = true; // still this client's turn
        } // end else if
        else if (message.equals("Opponent moved")) {
            int location = input.nextInt(); // get move location
            input.nextLine(); // skip newline after int location
            int row = location / 3; // calculate row
            int column = location % 3; // calculate column

            setMark(board[row][column], (myMark.equals(X_MARK) ? O_MARK : X_MARK)); // mark move
            displayMessage("Opponent moved. Your turn.\n");
            myTurn = true; // now this client's turn
        } // end else if



        else
            displayMessage(message + "\n"); // display the message
    } // end method processMessage

    // manipulate outputArea in event-dispatch thread
    private void displayMessage(final String messageToDisplay) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        displayArea.append(messageToDisplay); // updates output
                    } // end method run
                } // end inner class
        ); // end call to SwingUtilities.invokeLater
    } // end method displayMessage

    // utility method to set mark on board in event-dispatch thread
    private void setMark(final Square squareToMark, final String mark) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        squareToMark.setMark(mark); // set mark in square
                    } // end method run
                } // end anonymous inner class
        ); // end call to SwingUtilities.invokeLater
    } // end method setMark

    // send message to server indicating clicked square
    public void sendClickedSquare(int location) {
        // if it is my turn
        if (myTurn) {
            output.format("%d\n", location); // send location to server
            output.flush();
            myTurn = false; // not my turn anymore
        } // end if
    } // end method sendClickedSquare

    // set current Square
    public void setCurrentSquare(Square square) {
        currentSquare = square; // set current square to argument
    } // end method setCurrentSquare

    // private inner class for the squares on the board
    private class Square extends JPanel {
        private String mark; // mark to be drawn in this square
        private int location; // location of square

        public Square(String squareMark, int squareLocation) {
            mark = squareMark; // set mark for this square
            location = squareLocation; // set location of this square

            addMouseListener(
                    new MouseAdapter() {
                        public void mouseReleased(MouseEvent e) {
                            setCurrentSquare(Square.this); // set current square

                            // send location of this square
                            sendClickedSquare(getSquareLocation());
                        } // end method mouseReleased
                    } // end anonymous inner class
            ); // end call to addMouseListener
        } // end Square constructor

        // return preferred size of Square
        public Dimension getPreferredSize() {
            return new Dimension(screen_size_W/4, screen_size_H/4); // return preferred size
        } // end method getPreferredSize

        // return minimum size of Square
        public Dimension getMinimumSize() {
            return getPreferredSize(); // return preferred size
        } // end method getMinimumSize

        // set mark for Square
        public void setMark(String newMark) {
            mark = newMark; // set mark of square
            repaint(); // repaint square
        } // end method setMark

        // return Square location
        public int getSquareLocation() {
            return location; // return location of square
        } // end method getSquareLocation

        // draw Square
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setFont(new Font("TimesRoman", Font.PLAIN, screen_size_W/10));
            g.drawRect(0, 0, screen_size_W/4-35, screen_size_H/4-35); // draw square
            FontMetrics fm = g.getFontMetrics();
            Rectangle2D r = fm.getStringBounds(mark, g);
            int x = (screen_size_W/4-35 - (int) r.getWidth()) / 2;
            int y = (screen_size_H/4-35 - (int) r.getHeight()) / 2 + fm.getAscent();
            g.drawString(mark, x, y); // draw mark
        } // end method paintComponent
    } // end inner-class Square
} // end class TicTacToeClient
