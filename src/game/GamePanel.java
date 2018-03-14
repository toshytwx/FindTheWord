package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel {
    boolean gameOverHappened = false;

    public GamePanel() {
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent me) {
                if((Game.isServer && !Game.serverCanGo) || (!Game.isServer && !Game.clientCanGo) ){
                    return;
                }
                int type;
                if (!Game.isServer) {

                    type = 1;
                } else {

                    type = 2;
                }
                int x = me.getX();
                int y = me.getY();
                int xIndex = 0;
                int yIndex = 0;
                for (int i = 0; i < Game.N; i++) {
                    if (x < (i + 1) * getWidth() / Game.N) {
                        xIndex = i;
                        break;
                    }
                }
                for (int i = 0; i < Game.N; i++) {
                    if (y < (i + 1) * getHeight() / Game.N) {
                        yIndex = i;
                        break;
                    }
                }
                if (Game.board[yIndex][xIndex] == 0) {
                    Game.board[yIndex][xIndex] = type;
                    Game.verStep = yIndex;
                    Game.horStep = xIndex;
                    if (!Game.isServer) {
                        Game.clientDataReady = true;
                        Game.clientCanGo = false;
                    }
                    else {
                        Game.serverDataReady = true;
                        Game.serverCanGo = false;
                    }
                    System.out.println("Client: new click processed!");
                } else {
                    java.awt.Toolkit.getDefaultToolkit().beep();
                }
                repaint();
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {



        Graphics2D g2d = (Graphics2D) g;


        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, getWidth(), getHeight());


        g2d.setColor(Color.BLACK);


        g2d.setStroke(new BasicStroke(5.0f));
        for (int i = 0; i < Game.N - 1; i++) {

            g2d.drawLine((i + 1) * getWidth() / Game.N, 0, (i + 1) * getWidth() / Game.N, getHeight());
        }
        for (int i = 0; i < Game.N - 1; i++) {

            g2d.drawLine(0, (i + 1) * getHeight() / Game.N, getWidth(), (i + 1) * getHeight() / Game.N);
        }


        for (int i = 0; i < Game.N; i++) {

            for (int j = 0; j < Game.N; j++) {

                if (Game.board[i][j] == 1) {

                    drawKrestik(g2d, j, i, getWidth(), getHeight(), Game.N);
                } else {

                    if (Game.board[i][j] == 2) {

                        drawNolik(g2d, j, i, getWidth(), getHeight(), Game.N);
                    }
                }
            }
        }


        if (Game.isKrestikWin() && !gameOverHappened ) {

            System.out.println("Krestik win!!!");

            gameOverHappened = true;

            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {


                    if(Game.isServer) {
                        JOptionPane.showMessageDialog(null, "Вы проиграли!");
                    }
                    else{

                        JOptionPane.showMessageDialog(null, "Вы победили!");
                    }
                    System.exit(0);
                }
            });

            return;
        }
        if (Game.isNolikWin() && !gameOverHappened ) {
            gameOverHappened = true;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if(Game.isServer) {
                        JOptionPane.showMessageDialog(null, "Вы победили!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Вы проиграли!");
                    }
                    System.exit(0);
                }
            });
            return;
        }


        if(Game.isDraw() && !gameOverHappened ) {
            gameOverHappened = true;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null, "Ничья!!!");
                    System.exit(0);
                }
            });
            return;
        }
    }


    private void drawKrestik(Graphics2D g2d, int xStep, int yStep, int width, int height, int N) {
        g2d.setColor(Color.red);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(8.0f));
        int xLeft = xStep * width / N + 25;
        int xRight = (xStep + 1) * width / N - 25;
        int yUp = yStep * height / N + 20;
        int yLow = (yStep + 1) * height / N - 20;
        g2d.drawLine(xLeft, yUp, xRight, yLow);
        g2d.drawLine(xLeft, yLow, xRight, yUp);
    }

    private void drawNolik(Graphics2D g2d, int xStep, int yStep, int width, int height, int N) {
        g2d.setColor(Color.blue);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(8.0f));
        int xLeft = xStep * width / N + 25;
        int yUp = yStep * height / N + 25;
        int diameter = width / N - 50;
        g2d.drawOval(xLeft, yUp, diameter, diameter);
    }
}
