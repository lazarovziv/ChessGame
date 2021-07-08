package com.zivlazarov.chessengine.ui.components;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.board.TileColor;
import com.zivlazarov.chessengine.model.pieces.Piece;
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

    private final Player whitePlayer;
    private final Player blackPlayer;
    private static Player currentPlayer;

    private static Tile sourceTile;
    private static Tile destinationTile;
    private static Piece playerPiece;
    
    public BoardFrame() {
        board = Board.getInstance();

        whitePlayer = new Player(board, PieceColor.WHITE);
        blackPlayer = new Player(board, PieceColor.BLACK);

        whitePlayer.setOpponentPlayer(blackPlayer);
        blackPlayer.setOpponentPlayer(whitePlayer);

        CommandLineGame.initPieces(whitePlayer, blackPlayer);
//        board.checkBoard(whitePlayer);

        currentPlayer = whitePlayer;

        board.checkBoard(currentPlayer);

        gameFrame = new JFrame("Chess");
        gameFrame.setLayout(new BorderLayout());
        gameFrame.setSize(600, 600);
        boardPanel = new BoardPanel(board);
        gameFrame.add(boardPanel, BorderLayout.CENTER);
        gameFrame.setVisible(true);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setting window in center
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        gameFrame.setLocation(dim.width/2-gameFrame.getSize().width/2, dim.height/2-gameFrame.getSize().height/2);
    
    }
    
    private static class BoardPanel extends JPanel {
        
        final ArrayList<TilePanel> tilePanels;
        final Map<Tile, TilePanel> tilePanelMap;
        final Map<TilePanel, Tile> tilePanelTileMap;
        
        BoardPanel(Board board) {
            super(new GridLayout(8,8));
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
        
        TilePanel(BoardPanel boardPanel, Tile tile) {
            super(new GridBagLayout());
            this.tile = tile;

            markedTiles = new ArrayList<>();

            setPreferredSize(new Dimension(10, 10));

            drawTile();

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // first left mouse click
                    if (isLeftMouseButton(e)) {
                        System.out.println(tile);
                        if (sourceTile == null) {
                            sourceTile = tile;
                            if (!sourceTile.isEmpty()) {
                                if (sourceTile.getPiece().getPieceColor() == currentPlayer.getPlayerColor()) {
                                    playerPiece = sourceTile.getPiece();
                                    System.out.println(playerPiece.getName() + ": ");
                                    System.out.println("Moves: ");
                                    for (Tile possibleMove : playerPiece.getPossibleMoves()) {
                                        System.out.println(possibleMove);
                                        markedTiles.add(possibleMove);
                                        boardPanel.tilePanelMap.get(possibleMove).setBackground(Color.DARK_GRAY);
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
                                    currentPlayer.movePiece(playerPiece, destinationTile);
                                    board.checkBoard(currentPlayer);
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

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

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
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}