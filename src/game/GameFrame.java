package game;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    GamePanell gamePanel;

    public GameFrame(String frameName) {
        super(frameName);
        gamePanel = new GamePanell();
        gamePanel.setPreferredSize(new Dimension(400, 400));
        getContentPane().add(gamePanel.getContentPane());
        setSize(new Dimension(400, 400));
        setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void displayIncomingLetter(char c) {
        gamePanel.displayIncommingLetter(c);
    }

    public void setRandomWordForGame() {
        gamePanel.setRandomWordForGame();
    }

    public GamePanell getGamePanel() {
        return gamePanel;
    }
}
