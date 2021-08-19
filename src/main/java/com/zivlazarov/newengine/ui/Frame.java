package com.zivlazarov.newengine.ui;

import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.newengine.ZobristBoard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Frame {

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final ZobristBoard board;
    private final char[][] displayBoard;

    private static int chosenRow = -1;
    private static int chosenCol = -1;
    private static char chosenPiece = '!';
    private static int targetRow = -1;
    private static int targetCol = -1;

    public Frame() {
        board = new ZobristBoard();
        displayBoard = board.getDisplayBoard();

        gameFrame = new JFrame("Chess");
        gameFrame.setUndecorated(true);
        gameFrame.setLayout(new BorderLayout());
        boardPanel = new BoardPanel(board.getDisplayBoard());

        gameFrame.setSize(850, 600);
        gameFrame.add(boardPanel, BorderLayout.CENTER);
        gameFrame.setVisible(true);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setting window in center
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        gameFrame.setLocation(dim.width / 2 - gameFrame.getSize().width / 2, dim.height / 2 - gameFrame.getSize().height / 2);
    }

    private class BoardPanel extends JPanel {

        final ArrayList<TilePanel> tilePanels;

        BoardPanel(char[][] board) {
            super(new GridLayout(8, 8));
            tilePanels = new ArrayList<>();

            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    TilePanel tilePanel = new TilePanel(this, board, r, c);
                    tilePanels.add(tilePanel);
                    add(tilePanel);
                }
            }
            setPreferredSize(new Dimension(400, 350));
            validate();
        }

        public void drawBoard(boolean showDialog) {
            removeAll();

            for (TilePanel tilePanel : tilePanels) {
                tilePanel.drawTile();
                add(tilePanel);
            }
            // redraw side panels
            validate();
            repaint();

        }
    }

    private class TilePanel extends JPanel {
        int row;
        int col;
        char[][] board;

        private static final Color whiteTileColor = Color.decode("#FFFACD");
        private static final Color blackTileColor = Color.decode("#593E1A");

        private static final Map<Integer, Color> tileColorMap = Map.of(
                0, whiteTileColor,
                1, blackTileColor
        );

        private static ArrayList<Tile> markedTiles;

        TilePanel(BoardPanel boardPanel, char[][] board, int row, int col) {
            super(new GridBagLayout());
            this.board = board;
            this.row = row;
            this.col = col;

            markedTiles = new ArrayList<>();

            setPreferredSize(new Dimension(10, 10));

            drawTile();



            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (chosenRow == -1 && chosenCol == -1) {
                            chosenRow = row;
                            chosenCol = col;
                            if (displayBoard[row][col] != '-') {
                                chosenPiece = displayBoard[row][col];
                            }

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
        }

        private void drawTile() {
            removeAll();

            setBackground(tileColorMap.get((row + col) % 2));

            validate();
            repaint();

            removeAll();

            if (board[row][col] != '-') {
                add(new JLabel(createImageIcon(board[row][col])));
            }

            validate();
            repaint();
        }
    }

    public static ImageIcon createImageIcon(char piece) {
            try {
                BufferedImage image = ImageIO.read(new File(ZobristBoard.piecesImagesMap.get(piece)));
                return new ImageIcon(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }
}
