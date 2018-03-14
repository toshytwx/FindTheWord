package game;

import game.GamePanel;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    GamePanel gamePanel;

    public GameFrame(String frameName, int frameHeight) {
        super(frameName);
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(400, 400));
        getContentPane().add(gamePanel);
        setSize(new Dimension(400, 400));
        setResizable(false);
        setLocation(Game.elderFrameXLocation, Game.elderFrameYLocation + frameHeight + 10);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
}
