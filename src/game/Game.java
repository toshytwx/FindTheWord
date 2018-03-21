package game;

import javax.swing.*;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Game extends JFrame implements Runnable {
    static boolean serverCanGo = false;
    static boolean clientCanGo = true;
    static boolean isServer = false;
    static char letter = '?';
    private char[] randomWordForGame;
    private final static int MY_WIDTH = 400;
    private final static int MY_HEIGHT = 200;
    private int counter = 0;

    private boolean modeReady = false;
    private JButton serverButton;
    private JButton clientButton;
    private JTextField ipField;
    private JButton ipButton;

    private ServerSocket serverSocket = null;
    private Socket remoteClientSocket = null;
    private PrintWriter fromClientToServer = null;
    private BufferedReader toClientFromServer = null;

    private GameFrame gameFrame = null;

    private boolean clientSideConnectionOk = false;
    private boolean serverSideConnectionOk = false;

    static boolean clientDataReady = false;
    static boolean serverDataReady = false;

    public Game() {
        super("The Game");
        this.setSize(new Dimension(MY_WIDTH, MY_HEIGHT));
        this.setResizable(false);
        JPanel borderPanel = new JPanel();
        borderPanel.setSize(300, 20);
        borderPanel.setMinimumSize(new Dimension(300, 20));

        serverButton = new JButton("Play!");
        ipButton = new JButton("Connect!");
        int wordKey = ThreadLocalRandom.current().nextInt(0, 10);
        randomWordForGame = Dictionary.getRandomWordForGame(wordKey);
        ipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InetAddress ia = null;
                try {
                    remoteClientSocket = new Socket(ia = InetAddress.getByName(ipField.getText()), 1234);
                    fromClientToServer = new PrintWriter(remoteClientSocket.getOutputStream(), true);
                    toClientFromServer = new BufferedReader(new InputStreamReader(remoteClientSocket.getInputStream()));
                    clientSideConnectionOk = true;
                    gameFrame = new GameFrame("game.Game of Client");
                    gameFrame.gamePanel.setRandomWord(randomWordForGame);
                    gameFrame.setRandomWordForGame(); //TO DO: place randomNum here.
                    fromClientToServer.println(wordKey);
                } catch (UnknownHostException uhe) {
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
        clientButton = new JButton("Play!");
        ipField = new JTextField("ip:");
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
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));
        JPanel ipPanel = new JPanel();
        ipPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        this.getContentPane().add(innerPanel);
        innerPanel.add(serverButton);
        innerPanel.add(clientButton);
        ipPanel.add(ipField);
        ipPanel.add(ipButton);
        innerPanel.add(ipPanel);
        innerPanel.add(borderPanel);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    static boolean isDraw() {
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
        int randomNum = ThreadLocalRandom.current().nextInt(0, 10 + 1);
        if (!isServer) {
            while (!clientSideConnectionOk) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            while (true) {
                try {
                    while (true) {
                        if (clientDataReady) {
                            System.out.println("writing: " + letter);
                            gameFrame.displayIncomingLetter(letter);
                            fromClientToServer.println(letter);
                            clientDataReady = false;
                            break;
                        } else {
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                    String str;
                    while ((str = toClientFromServer.readLine()) != null) {
                        char letter = str.toCharArray()[0];
                        System.out.println("reading:" + letter);
                        gameFrame.gamePanel.getSendLetter().setEnabled(true);
                        gameFrame.gamePanel.saidLetters.add(letter);
                        gameFrame.displayIncomingLetter(letter);
                        gameFrame.repaint();
                        if (!serverCanGo) {
                            clientCanGo = true;
                        }
                        break;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            try {
                Socket clientSocket = serverSocket.accept();
                PrintWriter fromServerToClient = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader toServerFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                java.awt.Toolkit.getDefaultToolkit().beep();
                Thread.sleep(500);
                java.awt.Toolkit.getDefaultToolkit().beep();
                Thread.sleep(500);
                java.awt.Toolkit.getDefaultToolkit().beep();

                String inputLine;
                gameFrame = new GameFrame("game.Game of Server");
                setWordForServerGame(toServerFromClient);
                while (true) {
                    while ((inputLine = toServerFromClient.readLine()) != null) {
                        char symbol = inputLine.toCharArray()[0];
                        System.out.println("reading:" + symbol);
                        gameFrame.gamePanel.getSendLetter().setEnabled(true);
                        gameFrame.gamePanel.saidLetters.add(symbol);
                        gameFrame.displayIncomingLetter(symbol);
                        gameFrame.repaint();
                        if (!clientCanGo) {
                            serverCanGo = true;
                        }
                        break;
                    }
                    while (true) {
                        if (serverDataReady) {
                            serverDataReady = false;
                            System.out.println("writing: " + letter);
                            gameFrame.displayIncomingLetter(letter);
                            fromServerToClient.println(letter);
                            break;
                        } else {
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

    private void setWordForServerGame(BufferedReader toServerFromClient) throws IOException {
        if (counter == 0) {
            char key = toServerFromClient.readLine().toCharArray()[0];
            if (((int) key) >= 48 && ((int) key) <= 57) {
                char[] randomWord = Dictionary.getRandomWordForGame(key);
                gameFrame.gamePanel.setRandomWord(randomWord);
                gameFrame.setRandomWordForGame();
                counter++;
            }
        }
    }
}