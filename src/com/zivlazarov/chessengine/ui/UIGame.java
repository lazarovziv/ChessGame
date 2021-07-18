package com.zivlazarov.chessengine.ui;

import com.zivlazarov.chessengine.controllers.BoardController;
import com.zivlazarov.chessengine.controllers.PlayerController;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.board.TileColor;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.ui.game.CommandLineGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class UIGame {

    JFrame frame;
    Board board;
    JLayeredPane mainPanel;
    GridLayout gridLayout;

    private final Map<JButton, Tile> buttonTileMap;
    private final Map<Tile, JButton> tileComponentMap;

    private final Map<Tile, Piece> tilePieceMap;
    private final Map<Piece, JButton> pieceButtonMap;

    boolean gameStarted = false;

    public UIGame() {
        board = Board.getInstance();
        Player whitePlayer = new Player(board, PieceColor.WHITE);
        Player blackPlayer = new Player(board, PieceColor.BLACK);

        buttonTileMap = new HashMap<>();
        tileComponentMap = new HashMap<>();
        tilePieceMap = new HashMap<>();
        pieceButtonMap = new HashMap<>();

        gridLayout = new GridLayout(8, 8, 0, 0);

        CommandLineGame.initPieces(whitePlayer, blackPlayer);

        PlayerController playerController = new PlayerController();

        BoardController boardController = new BoardController(board);
//        boardController.setBoard(board);
        boardController.setWhitePlayer(whitePlayer);
        boardController.setBlackPlayer(blackPlayer);

        playerController.setPlayer(whitePlayer);
        playerController.setOpponentPlayer(blackPlayer);

        int width = 800;
        int height = 600;
        frame = new JFrame("Chess");
        mainPanel = new JLayeredPane();
        mainPanel.setLayout(gridLayout);
        mainPanel.setBounds(0, 0, 0, 0);
        mainPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        frame.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // alert dialog for asking if player wants to start
        Object[] buttons = { "Yes", "No" };
        int optionClicked = JOptionPane.showOptionDialog(null, "Would you like to start a game? ", "Chess", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
        if (optionClicked != -1) {
            // clicked yes
            if (optionClicked == 0) gameStarted = true;
            else if (optionClicked == 1) System.exit(1);
        }

        Map<TileColor, String> tileColorNameMap = new HashMap<>();
        tileColorNameMap.put(TileColor.WHITE, "whiteTile.png");
        tileColorNameMap.put(TileColor.BLACK, "blackTile.png");

        for (Tile[] tiles : board.getBoard()) {
            for (Tile tile : tiles) {
                JButton tileButton = new JButton();
                tileButton.setIcon(new ImageIcon(
                        "/Users/zivlazarov/Projects/Java/ChessGame/src/" + tileColorNameMap.get(tile.getTileColor())));
                tileButton.setBorderPainted(false);
                tileButton.setLocation(tile.getCol(), tile.getRow());

                boolean addedATileButton = false;

                JButton pieceButton = null;
                if (!tile.isEmpty()) {
                    pieceButton = new JButton();
                    pieceButton.setIcon(new ImageIcon("/Users/zivlazarov/Projects/Java/ChessGame/src/" + tile.getPiece().getImageName()));
                    pieceButton.setSize(48, 48);
                    pieceButton.setLocation(tileButton.getLocation());
                    pieceButton.setBorderPainted(false);
                    tile.getPiece().setImageIcon(pieceButton.getIcon());
//                    tileButton.add(pieceButton);
                    pieceButtonMap.put(tile.getPiece(), pieceButton);
                    tilePieceMap.put(tile, tile.getPiece());

                    mainPanel.add(tileButton, JLayeredPane.DEFAULT_LAYER);
                    mainPanel.add(pieceButton, JLayeredPane.DRAG_LAYER);
                    addedATileButton = true;
                }

                tile.setButton(tileButton);
                buttonTileMap.put(tileButton, tile);
                tileComponentMap.put(tile, tileButton);

                if (!addedATileButton) {
                    mainPanel.add(tileButton, JLayeredPane.DEFAULT_LAYER);
                }
            }
        }

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setSize(width, height);
        frame.setVisible(true);

        for (Component component : mainPanel.getComponents()) {
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    JButton button = (JButton) component;
                    Tile tile = buttonTileMap.get(button);
                    Piece piece = tilePieceMap.get(tile);

                    System.out.println(piece.getName());
                }
            });
        }

//        while (gameStarted) {
//
//        }
    }

    public static void main(String[] args) {
        new UIGame();
    }
}
