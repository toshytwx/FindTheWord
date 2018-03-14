package game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;


public class Game extends JFrame implements Runnable {
    public static boolean serverCanGo = false;
    public static boolean clientCanGo = true;
    public static boolean isServer = false;
    public static final int N = 4;
    public static int[][] board;

    private final static int MY_WIDTH = 400;
    private final static int MY_HEIGHT = 200;
    public static int elderFrameXLocation = 0;
    public static int elderFrameYLocation = 0;
    public static int verStep;
    public static int horStep;

    boolean modeReady = false;
    JButton serverButton;
    JButton clientButton;
    JPanel innerPanel;
    JPanel ipPanel;
    JTextField ipField;
    JButton ipButton;


    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    Socket remoteClientSocket = null;
    PrintWriter fromServerToClient = null;
    BufferedReader toServerFromClient = null;
    PrintWriter fromClientToServer = null;
    BufferedReader toClientFromServer = null;

    private GameFrame gameFrame = null;

    JPanel borderPanel = null;


    boolean clientSideConnectionOk = false;
    boolean serverSideConnectionOk = false;



    static boolean clientDataReady = false;
    static boolean serverDataReady = false;




    public Game() {


        super("Net Tic Tac Toe 2012");


        this.setSize(new Dimension(MY_WIDTH, MY_HEIGHT));
        this.setResizable(false);


        board = new int[N][N];


        for (int i = 0; i < N; i++) {

            for (int j = 0; j < N; j++) {

                board[i][j] = 0;
            }
        }


        borderPanel = new JPanel();
        borderPanel.setSize(300, 20);
        borderPanel.setMinimumSize(new Dimension(300, 20));

        serverButton = new JButton("I want to be a server!");
        ipButton = new JButton("Connect!");



        ipButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                InetAddress ia = null;

                try {

                    remoteClientSocket = new Socket(ia = InetAddress.getByName(ipField.getText()), 1234);
                    fromClientToServer = new PrintWriter(remoteClientSocket.getOutputStream(), true);
                    toClientFromServer = new BufferedReader(new InputStreamReader(remoteClientSocket.getInputStream()));

                    clientSideConnectionOk = true;

                    gameFrame = new GameFrame("game.Game of Client", MY_HEIGHT);




                }
                catch (UnknownHostException uhe) {
                    System.err.println("Don't know about host: " + ia.getHostAddress());
                    System.exit(1);
                } catch (IOException ioe) {
                    System.err.println("Couldn't get I/O for the connection to: " + ia.getHostAddress());
                    System.exit(1);
                }

            }
        });




        serverButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                clientButton.setEnabled(false);

                InetAddress thisIp = null;

                try {
                    thisIp = InetAddress.getLocalHost();
                } catch (UnknownHostException ex) {
                    JOptionPane.showMessageDialog(null, "Fatal error! Can't get IP!", "Sorry, fatal error!", 1);
                    System.exit(1);
                }


                setTitle("Your IP: " + thisIp.getHostAddress());

                isServer = true;
                modeReady = true;


                try {


                    serverSocket = new ServerSocket(1234);


                } catch (IOException ioe) {

                    JOptionPane.showMessageDialog(null, "Error!");
                    System.exit(1);
                }



                serverSideConnectionOk = true;

                serverButton.setEnabled(false);


            }
        });

        clientButton = new JButton("I want to be a client!");
        ipField = new JTextField("Input server's IP here...");
        ipField.setColumns(24);

        ipField.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent me) {
                if (ipField.getText().equals("Input server's IP here...")) {
                    ipField.setText("");
                }
            }
        });
        ipField.setEnabled(false);
        ipButton.setEnabled(false);

        clientButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                clientButton.setEnabled(false);

                serverButton.setEnabled(false);
                ipField.setEnabled(true);
                ipButton.setEnabled(true);


                isServer = false;
                modeReady = true;
            }
        });




        innerPanel = new JPanel();
        innerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));


        ipPanel = new JPanel();
        ipPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));


        this.getContentPane().add(innerPanel);

        innerPanel.add(serverButton);
        innerPanel.add(clientButton);

        ipPanel.add(ipField);
        ipPanel.add(ipButton);

        innerPanel.add(ipPanel);
        innerPanel.add(borderPanel);



        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        Toolkit t = getToolkit();

        Dimension screenSize = t.getScreenSize();


        elderFrameXLocation = screenSize.width / 2 - MY_WIDTH / 2;
        elderFrameYLocation = (screenSize.height / 2 - MY_HEIGHT / 2) - 300;

        this.setLocation(elderFrameXLocation, elderFrameYLocation);

        this.setVisible(true);
    }

    public static boolean isDraw(){

        System.out.println("Check for draw...");

        for(int i = 0; i < N; i++){

            for(int j = 0; j < N; j++){

                if( board[i][j] == 0 ){

                    return false;
                }
            }
        }

        System.out.println("All cells are filled!");

        if( isKrestikWin() ){

            System.out.println("Krestik win, not draw!");
            return false;
        }

        if( isNolikWin() ){

            System.out.println("Nolik win, not draw!");
            return false;
        }


        return true;
    }

    public static boolean isKrestikWin() {

        System.out.println("Is krestiki win?");



        for (int i = 0; i < N; i++) {

            boolean horWin = true;

            for (int j = 0; j < N; j++) {

                if (board[i][j] != 1) {

                    horWin = false;
                    break;
                }
            }

            if (horWin) {

                return true;
            }
        }


        for (int i = 0; i < N; i++) {


            boolean verWin = true;

            for (int j = 0; j < N; j++) {

                if (board[j][i] != 1) {

                    verWin = false;
                    break;
                }
            }

            if (verWin) {

                return true;
            }
        }


        boolean diagWin = true;

        for (int i = 0; i < N; i++) {

            int j = i;

            if (board[i][j] != 1) {

                diagWin = false;
                break;
            }
        }

        if (diagWin) {


            return true;
        }

        diagWin = true;



        for (int i = 0; i < N; i++) {

            int j = N - 1 - i;

            if (board[i][j] != 1) {

                diagWin = false;
                break;
            }
        }

        if (diagWin) {


            return true;
        }



        return false;
    }

    // то же самое для ноликов
    public static boolean isNolikWin() {


        for (int i = 0; i < N; i++) {

            boolean horWin = true;

            for (int j = 0; j < N; j++) {

                if (board[i][j] != 2) {

                    horWin = false;
                    break;
                }
            }

            if (horWin) {

                return true;
            }
        }

        for (int i = 0; i < N; i++) {


            boolean verWin = true;

            for (int j = 0; j < N; j++) {

                if (board[j][i] != 2) {

                    verWin = false;
                    break;
                }
            }

            if (verWin) {

                return true;
            }
        }



        boolean diagWin = true;

        for (int i = 0; i < N; i++) {

            int j = i;

            if (board[i][j] != 2) {

                diagWin = false;
                break;
            }
        }

        if (diagWin) {

            return true;
        }

        diagWin = true;

        for (int i = 0; i < N; i++) {

            int j = N - 1 - i;

            if (board[i][j] != 2) {

                diagWin = false;
                break;
            }
        }

        if (diagWin) {

            return true;
        }

        return false;
    }

    @Override
    public void run() {
        while (!modeReady) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (!isServer) {
            while (!clientSideConnectionOk) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                }
            }
            while (true) {
                try {
                    while (true) {
                        if (clientDataReady) {
                            System.out.println("Client: ready to send data!");
                            String s = "";
                            s = s.concat(String.valueOf(verStep));
                            s = s.concat(" ");
                            s = s.concat(String.valueOf(horStep));
                            fromClientToServer.println(s);
                            clientDataReady = false;
                            System.out.println("Client: data transported!");
                            break;
                        } else {
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException ex) {
                            }
                        }
                    }
                    String str;
                    while ((str = toClientFromServer.readLine()) != null) {
                        System.out.println("Client called from server...");
                        String[] words = str.split(" ");
                        int verStep = Integer.parseInt(words[0]);
                        int horStep = Integer.parseInt(words[1]);
                        board[verStep][horStep] = 2;
                        gameFrame.repaint();
                        clientCanGo = true;
                        break;
                    }
                }
                catch (IOException ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {

            try {

                clientSocket = serverSocket.accept();


                fromServerToClient = new PrintWriter(clientSocket.getOutputStream(), true);
                toServerFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                java.awt.Toolkit.getDefaultToolkit().beep();
                Thread.sleep(500);
                java.awt.Toolkit.getDefaultToolkit().beep();
                Thread.sleep(500);
                java.awt.Toolkit.getDefaultToolkit().beep();

                String inputLine;
                String outputLine;




                gameFrame = new GameFrame("game.Game of Server", MY_HEIGHT);


                while (true) {


                    while ((inputLine = toServerFromClient.readLine()) != null) {


                        System.out.println("Client say: " + inputLine);


                        String[] coords = inputLine.split(" ");

                        int vStep = Integer.parseInt(coords[0]);
                        int hStep = Integer.parseInt(coords[1]);

                        if (board[vStep][hStep] != 0) {

                            java.awt.Toolkit.getDefaultToolkit().beep();
                        } else {


                            board[vStep][hStep] = 1;
                        }


                        gameFrame.repaint();


                        serverCanGo = true;

                        break;
                    }

                    while( true ){



                        if( serverDataReady ){

                            System.out.println("Server: starting transmission...");
                            outputLine = String.valueOf(verStep);

                            outputLine = outputLine.concat(" ");

                            outputLine = outputLine.concat(String.valueOf(horStep));

                            serverDataReady = false;

                            fromServerToClient.println(outputLine);

                            break;
                        }
                        else{

                            Thread.sleep(5);
                        }

                    }
                }

            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ioe) {

                JOptionPane.showMessageDialog(null, "Client disconnected...");
                System.exit(1);
            }

        }

    }

    public static void main(String[] args) {

        (new Thread(new Game())).start();
    }
}