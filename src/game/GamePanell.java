package game;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

public class GamePanell extends JDialog {
    private JPanel contentPane;
    private JButton sendLetter;
    private JTable wordPanel;
    private JFormattedTextField letterInputField;
    private JTextArea infoTextArea;
    private JTextField saidLettersTextField;
    public static ArrayList<Character> saidLetters = new ArrayList<>();
    private char[] randomWordForGame = null;

    public GamePanell() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(sendLetter);

        sendLetter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        tuneLetterInputField();
    }

    private void tuneLetterInputField() {
        try {
            MaskFormatter linearFormatter = new MaskFormatter("U");
            linearFormatter.setPlaceholderCharacter('?');
            DefaultFormatterFactory defaultFormatterFactory = new DefaultFormatterFactory(linearFormatter);
            letterInputField.setFormatterFactory(defaultFormatterFactory);
            letterInputField.setColumns(1);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }

    private void onOK() {
        String value = (String) letterInputField.getValue();
        saidLetters.add(value.charAt(0));
        infoTextArea.setText(infoTextArea.getText() + "\n" + "ВЫ НАЗВАЛИ БУКВУ:" + value.charAt(0));
        boolean alreadySaid = Collections.frequency(saidLetters, value.charAt(0)) > 1;
        boolean inWord = false;
        for (Character letter: randomWordForGame) {
            inWord = letter == value.charAt(0);
            if (inWord) {
                break;
            }
        }
        if (value != "?") {
            Game.letter = value.charAt(0);
            if (Game.isServer) {
                if (!alreadySaid) {
                    if (inWord) {
                        Game.serverDataReady = true;
                        Game.serverCanGo = false;
                        sendLetter.setEnabled(false);
                    }
                }
            } else {
                if (!alreadySaid) {
                    if (inWord) {
                        Game.clientDataReady = true;
                        Game.clientCanGo = false;
                        sendLetter.setEnabled(false);
                    }
                }
            }
        }
    }

    private void isVictory() {
        boolean victory = true;
        DefaultTableModel model = (DefaultTableModel) wordPanel.getModel();
        for (int i = 0; i < randomWordForGame.length; i++) {
            char currentElement = '?';
            if (model.getValueAt(0, i) instanceof String) {
                String elementToAdd = (String) model.getValueAt(0, i);
                currentElement = elementToAdd.charAt(0);
            } else if (model.getValueAt(0, i) instanceof Character) {
                currentElement = (char) model.getValueAt(0, i);
            }
            if (currentElement == '?') {
                victory = false;
                break;
            }
        }
        if (victory) {
            if (Game.isServer) {
                JOptionPane.showMessageDialog(null, "Вы проиграли!");
            } else {
                JOptionPane.showMessageDialog(null, "Вы победили!");
            }
            System.exit(0);
        }
    }

    private void onCancel() {
        dispose();
    }

    public void displayIncommingLetter(char c) {
        DefaultTableModel model = (DefaultTableModel) wordPanel.getModel();
        Vector rowData = new Vector(randomWordForGame.length);
        for (int i = 0; i < randomWordForGame.length; i++) {
            char charElementToAdd = '?';
            if (model.getValueAt(0, i) instanceof String) {
                String elementToAdd = (String) model.getValueAt(0, i);
                charElementToAdd = elementToAdd.charAt(0);
            } else if (model.getValueAt(0, i) instanceof Character) {
                charElementToAdd = (char) model.getValueAt(0, i);
            }
            if (randomWordForGame[i] == c) {
                charElementToAdd = c;
            } else if (charElementToAdd == '?') {
                charElementToAdd = '?';
            }
            rowData.addElement(charElementToAdd);
        }
        model.removeRow(0);
        model.addRow(rowData);
        wordPanel.setModel(model);
        isVictory();
    }

    public void setRandomWordForGame() {
        wordPanel.setRowHeight(25);
        DefaultTableModel model = (DefaultTableModel) wordPanel.getModel();
        Vector colHdrs = new Vector(randomWordForGame.length);
        Vector rowData = new Vector(randomWordForGame.length);
        for (int i = 0; i < randomWordForGame.length; i++) {
            rowData.addElement("?");
            colHdrs.addElement("");
        }
        model.setColumnIdentifiers(colHdrs);
        model.addRow(rowData);
        wordPanel.setModel(model);
    }


    public char[] getRandomWordForGame() {
        return randomWordForGame;
    }

    public void setRandomWord(char[] randomWordForGame) {
        this.randomWordForGame = randomWordForGame;
    }

    public JButton getSendLetter() {
        return sendLetter;
    }

    public JTextField getSaidLettersTextField() {
        return saidLettersTextField;
    }

    public void setSaidLettersTextField(JTextField saidLettersTextField) {
        this.saidLettersTextField = saidLettersTextField;
    }
}
