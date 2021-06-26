package com.zivlazarov.chessengine.ui;

import com.zivlazarov.chessengine.controllers.BoardController;
import com.zivlazarov.chessengine.controllers.PlayerController;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.player.Player;

import javax.swing.*;
import java.awt.*;

public class UIGame {

    JFrame frame;
    Board board;
    Container container;
    private JButton[][] buttons = new JButton[8][8];

    public UIGame() {
        board = new Board();
        Player whitePlayer = new Player(board, PieceColor.WHITE);
        Player blackPlayer = new Player(board, PieceColor.BLACK);

        CommandLineGame.initPieces(whitePlayer, blackPlayer);

        PlayerController playerController = new PlayerController();

        BoardController boardController = new BoardController();
        boardController.setBoard(board);
        boardController.setWhitePlayer(whitePlayer);
        boardController.setBlackPlayer(blackPlayer);

        playerController.setPlayer(whitePlayer);
        playerController.setOpponentPlayer(blackPlayer);

        int width = 800;
        int height = 600;
        frame = new JFrame("Chess");
        container = frame.getContentPane();
        container.setLayout(new GridLayout(8, 8, 0, 0));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JLabel label;
                ImageIcon icon;
                if (board.getBoard()[row][col].getPiece() != null) {
                    label = new JLabel();
                    icon = new ImageIcon("/Users/zivlazarov/Projects/Java/ChessGame/src/" +
                            board.getBoard()[row][col].getPiece().getImageName());
                    label.setIcon(icon);
                } else {
                    label = new JLabel("            -");
                }
                frame.add(label);
            }
        }
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setSize(width, height);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new UIGame();
    }
}
