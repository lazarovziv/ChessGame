package com.zivlazarov.chessengine.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class UIGame {

    JFrame frame;
    GridLayout gridLayout;

    public UIGame() {
        int width = 800;
        int height = 600;
        frame = new JFrame("Chess");
        gridLayout = new GridLayout(8, 8);
        frame.setSize(width, height);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(gridLayout);
        frame.add(panel);
        panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        ImageIcon imageIcon = new ImageIcon("/Users/zivlazarov/Projects/Java/ChessGame/src/blackKing.png");
        JLabel label = new JLabel(imageIcon);
        label.setVisible(true);
        panel.add(label);
        panel.setVisible(true);
    }

    public static void main(String[] args) {
        new UIGame();
    }
}
