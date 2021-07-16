package com.zivlazarov.chessengine.ui.components;

import com.zivlazarov.chessengine.model.board.*;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Minimax;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.ui.CommandLineGame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class BoardFrame {

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private static Board board;

    private static final Map<PieceColor, GameSituation> checkSituations = new HashMap<>();
    private static final Map<PieceColor, GameSituation> checkmateSituations = new HashMap<>();

    private static Player whitePlayer;
    private static Player blackPlayer;

    private static Minimax minimax;

    private static Tile sourceTile;
    private static Tile destinationTile;
    private static Piece playerPiece;

    public BoardFrame() {
        board = Board.getInstance();

        whitePlayer = new Player(board, PieceColor.WHITE);
        blackPlayer = new Player(board, PieceColor.BLACK);
        minimax = new Minimax();

        whitePlayer.setAI(false);
        blackPlayer.setAI(true);

        whitePlayer.setOpponentPlayer(blackPlayer);
//        blackPlayer.setOpponentPlayer(whitePlayer);

        checkSituations.put(PieceColor.WHITE, GameSituation.WHITE_IN_CHECK);
        checkSituations.put(PieceColor.BLACK, GameSituation.BLACK_IN_CHECK);

        checkmateSituations.put(PieceColor.WHITE, GameSituation.WHITE_CHECKMATED);
        checkmateSituations.put(PieceColor.BLACK, GameSituation.BLACK_CHECKMATED);

        board.setWhitePlayer(whitePlayer);
        board.setBlackPlayer(blackPlayer);

        board.initBoard();

//        CommandLineGame.initPieces(whitePlayer, blackPlayer);
//        board.checkBoard(whitePlayer);

        board.setCurrentPlayer(whitePlayer);

        board.checkBoard(board.getCurrentPlayer());

//        board.getCurrentPlayer().minimax(board, 6, Integer.MIN_VALUE, Integer.MAX_VALUE);
        gameFrame = new JFrame("Chess");
        gameFrame.setLayout(new BorderLayout());
        gameFrame.setSize(600, 600);
        boardPanel = new BoardPanel(board);
        gameFrame.add(boardPanel, BorderLayout.CENTER);
        gameFrame.setVisible(true);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setting window in center
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        gameFrame.setLocation(dim.width / 2 - gameFrame.getSize().width / 2, dim.height / 2 - gameFrame.getSize().height / 2);

    }

    private static class BoardPanel extends JPanel {

        final ArrayList<TilePanel> tilePanels;
        final Map<Tile, TilePanel> tilePanelMap;
        final Map<TilePanel, Tile> tilePanelTileMap;

        BoardPanel(Board board) {
            super(new GridLayout(8, 8));
            tilePanels = new ArrayList();
            tilePanelMap = new HashMap<>();
            tilePanelTileMap = new HashMap<>();

            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    Tile tile = board.getBoard()[r][c];
                    TilePanel tilePanel = new TilePanel(this, tile);
                    tilePanels.add(tilePanel);
                    tilePanelMap.put(tile, tilePanel);
                    tilePanelTileMap.put(tilePanel, tile);
                    add(tilePanel);
                }
            }
            setPreferredSize(new Dimension(400, 350));
            validate();
        }

        public void drawBoard() {
            removeAll();

            for (TilePanel tilePanel : tilePanels) {
                tilePanel.drawTile();
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    private static class TilePanel extends JPanel {

        private final Tile tile;

        private static final Color whiteTileColor = Color.decode("#FFFACD");
        private static final Color blackTileColor = Color.decode("#593E1A");

        private static final Map<TileColor, Color> tileColorMap = Map.of(TileColor.WHITE, whiteTileColor,
                TileColor.BLACK, blackTileColor);

        private static final Path path = Paths.get("");
        private static final String currentPath = path.toAbsolutePath().toString();

        private static ArrayList<Tile> markedTiles;

        private static boolean drawPossibleMoves = false;

        TilePanel(BoardPanel boardPanel, Tile tile) {
            super(new GridBagLayout());
            this.tile = tile;

            markedTiles = new ArrayList<>();

            setPreferredSize(new Dimension(10, 10));

            drawTile();

//            if (!board.getCurrentPlayer().isAI()) {
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // first left mouse click
                    if (isLeftMouseButton(e)) {
                        if (sourceTile == null) {
                            sourceTile = tile;
                            if (!sourceTile.isEmpty()) {
                                if (sourceTile.getPiece().getPieceColor() == board.getCurrentPlayer().getPlayerColor()) {
                                    if (!sourceTile.getPiece().canMove()) {
                                        sourceTile = null;
                                        return;
                                    }
                                    playerPiece = sourceTile.getPiece();
                                    System.out.println("Possible Moves: ");
                                    for (Tile possibleMove : playerPiece.getPossibleMoves()) {
                                        System.out.println(possibleMove);
                                        markedTiles.add(possibleMove);
                                        // drawing a circle in the possible move tile
                                        drawPossibleMoves = true;
//                                        boardPanel.tilePanelMap.get(possibleMove).paintComponent(getGraphics());
                                        boardPanel.tilePanelMap.get(possibleMove).setBackground(Color.LIGHT_GRAY);
                                        drawPossibleMoves = false;
                                    }
                                } else sourceTile = null;
                            }

                            // second left mouse click
                        } else {
                            if (playerPiece != null) {
                                destinationTile = tile;
                                // checking if tile clicked is any of the chosen piece's possible moves
                                boolean noneMatch = playerPiece.getPossibleMoves().stream().noneMatch(t -> t.equals(tile));
                                // if it does not included in possible moves, destination tile needs to be reset
                                if (noneMatch) {
                                    System.out.println("Can't move to " + destinationTile + " !");
                                }
                                // if it does, make move
                                else {
                                    Move move = new Move.Builder()
                                            .board(board)
                                            .player(board.getCurrentPlayer())
                                            .movingPiece(playerPiece)
                                            .targetTile(destinationTile)
                                            .build();

                                    move.makeMove();

//                                    board.getCurrentPlayer().movePiece(playerPiece, destinationTile);

                                    //board.setCurrentPlayer(board.getCurrentPlayer().getOpponentPlayer());

                                    board.checkBoard(board.getCurrentPlayer());

                                    System.out.println(board.getGameSituation());

                                    System.out.println();
                                    System.out.println(
                                            board.getGameHistoryMoves().lastElement().getFirst().getName()
                                                    + " -> " + board.getGameHistoryMoves().lastElement().getSecond());
                                }

                                sourceTile = null;
                                playerPiece = null;
                                destinationTile = null;

                                for (Tile markedTile : markedTiles) {
                                    boardPanel.tilePanelMap.get(markedTile).setBackground(tileColorMap.get(markedTile.getTileColor()));
                                }
                                SwingUtilities.invokeLater(boardPanel::drawBoard);
                            }
                        }
                        // right clicking resets chosen piece/tile
                    } else if (isRightMouseButton(e)) {
                        sourceTile = null;
                        destinationTile = null;
                        playerPiece = null;
                        for (Tile markedTile : markedTiles) {
                            boardPanel.tilePanelMap.get(markedTile).setBackground(tileColorMap.get(markedTile.getTileColor()));
                        }
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
//                    if (isLeftMouseButton(e)) {
//                        System.out.println(tile);
//                        if (sourceTile == null) {
//                            sourceTile = tile;
//                            if (!sourceTile.isEmpty()) {
//                                if (sourceTile.getPiece().getPieceColor() == currentPlayer.getPlayerColor()) {
//                                    if (!sourceTile.getPiece().canMove()) {
//                                        sourceTile = null;
//                                        return;
//                                    }
//                                    playerPiece = sourceTile.getPiece();
//                                    System.out.println(playerPiece.getName() + ": ");
//                                    System.out.println("Moves: ");
//                                    for (Tile possibleMove : playerPiece.getPossibleMoves()) {
//                                        System.out.println(possibleMove);
//                                        markedTiles.add(possibleMove);
//                                        // drawing a circle in the possible move tile
//                                        drawPossibleMoves = true;
////                                        boardPanel.tilePanelMap.get(possibleMove).paintComponent(getGraphics());
//                                        boardPanel.tilePanelMap.get(possibleMove).setBackground(Color.LIGHT_GRAY);
//                                        drawPossibleMoves = false;
//                                    }
//                                } else sourceTile = null;
//                            }
//                        }
//                    } else if (isRightMouseButton(e)) {
//                        sourceTile = null;
//                        destinationTile = null;
//                        playerPiece = null;
//                        drawPossibleMoves = false;
//                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
//                    if (isLeftMouseButton(e)) {
//                        if (sourceTile != null) {
//                            if (playerPiece != null) {
//                                destinationTile = tile;
//                                // checking if tile clicked is any of the chosen piece's possible moves
//                                boolean noneMatch = playerPiece.getPossibleMoves().stream().noneMatch(t -> t.equals(tile));
//                                // if it does not included in possible moves, destination tile needs to be reset
//                                if (noneMatch) {
//                                    System.out.println("Can't move to " + destinationTile + " !");
//                                }
//                                // if it does, make move
//                                else {
//                                    currentPlayer.movePiece(playerPiece, destinationTile);
//                                    board.checkBoard(currentPlayer);
//
//                                    System.out.println();
//                                    System.out.println(
//                                            board.getGameHistoryMoves().lastElement().getFirst().getName()
//                                                    + " -> " + board.getGameHistoryMoves().lastElement().getSecond());
//
//                                    if (currentPlayer.equals(whitePlayer)) currentPlayer = blackPlayer;
//                                    else currentPlayer = whitePlayer;
//                                }
//                                sourceTile = null;
//                                playerPiece = null;
//                                destinationTile = null;
//                                for (Tile markedTile : markedTiles) {
//                                    boardPanel.tilePanelMap.get(markedTile).setBackground(tileColorMap.get(markedTile.getTileColor()));
//                                }
//                                SwingUtilities.invokeLater(boardPanel::drawBoard);
//                            }
//                        }
//                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
//            } else if (board.getCurrentPlayer().isAI()) {
//                Move move = board.getCurrentPlayer().calculateNextMove(new Minimax(), 6);
//                System.out.println(move);
//                move.makeMove();
//
//                board.checkBoard(board.getCurrentPlayer());
//
//                System.out.println(board.getGameSituation());
//
//                System.out.println();
//                System.out.println(
//                        board.getGameHistoryMoves().lastElement().getFirst().getName()
//                                + " -> " + board.getGameHistoryMoves().lastElement().getSecond());
//
//                SwingUtilities.invokeLater(boardPanel::drawBoard);
//            }

            validate();
        }

        public void drawTile() {

//            if (tile.getTileColor() == TileColor.WHITE) setBackground(whiteTileColor);
//            else setBackground(blackTileColor);

            removeAll();

            setBackground(tileColorMap.get(tile.getTileColor()));

            validate();
            repaint();

            removeAll();

            if (!tile.isEmpty()) {
                add(new JLabel(createImageIcon(tile.getPiece())));
            }

            validate();
            repaint();
        }

        private ImageIcon createImageIcon(Piece piece) {
            if (piece != null) {
                try {
                    BufferedImage image = ImageIO.read(new File(currentPath + "/src/" + piece.getImageName()));
                    return new ImageIcon(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

//        @Override
//        public void paintComponent(Graphics g){
//            super.paintComponent(g);
//            g.setColor(Color.BLACK);
//
//            if(drawPossibleMoves) {
//                g.fillOval(tile.getRow() + (this.getSize().width / 2), tile.getCol() + (this.getSize().height / 2), 30, 30);
//                g.setColor(Color.BLACK);
//                g.drawOval(tile.getRow() + (this.getSize().width / 2), tile.getCol() + (this.getSize().height / 2), 30, 30);
//            }
//            validate();
////            repaint();
//        }

//        @Override
//        public void paint(Graphics g) {
//            super.paint(g);
//            int x = tile.getRow() - (5 / 2);
//            int y = tile.getCol() - (5 / 2);
//
//            g.setColor(tileColorMap.get(tile.getTileColor()));
//            g.fillOval(x, y, 5, 5);
//            g.setColor(Color.LIGHT_GRAY);
//            g.drawOval(x, y, 5, 5);
//        }
    }
}