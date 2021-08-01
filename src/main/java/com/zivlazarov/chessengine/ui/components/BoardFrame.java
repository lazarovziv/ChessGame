package com.zivlazarov.chessengine.ui.components;

import com.zivlazarov.chessengine.db.PlayerDao;
import com.zivlazarov.chessengine.model.board.*;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.pieces.RookPiece;
import com.zivlazarov.chessengine.model.player.Player;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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
import java.util.*;
import java.util.List;

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

    private static Tile sourceTile;
    private static Tile destinationTile;
    private static Piece playerPiece;

    public BoardFrame() {
        board = Board.getInstance();

        initGame(false);

        PlayerDao playerDao = new PlayerDao();

        gameFrame = new JFrame("Chess");
        gameFrame.setUndecorated(true);
        gameFrame.setLayout(new BorderLayout());
        boardPanel = new BoardPanel(board);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener((event) -> System.exit(1));
        JMenuItem restartGameItem = new JMenuItem("Restart");
        restartGameItem.addActionListener((event) -> {
            restartGame();
        });

        JMenuItem saveGameItem = new JMenuItem("Save Game");
        saveGameItem.addActionListener((event) -> {
            playerDao.insertPlayer(whitePlayer);
            playerDao.insertPlayer(blackPlayer);
        });

        JMenuItem loadGameItem = new JMenuItem("Load Game");
        loadGameItem.addActionListener((event) -> {
//            whitePlayer = playerDao.findPlayerByID(whitePlayer.getId());
//            blackPlayer = playerDao.findPlayerByID(blackPlayer.getId());
//            initGame(true);
        });

        menu.add(saveGameItem);
        menu.add(loadGameItem);
        menu.add(restartGameItem);
        menu.add(exitMenuItem);

        menuBar.add(menu);

        gameFrame.setJMenuBar(menuBar);

        gameFrame.setSize(600, 600);
        gameFrame.add(boardPanel, BorderLayout.CENTER);
        gameFrame.setVisible(true);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setting window in center
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        gameFrame.setLocation(dim.width / 2 - gameFrame.getSize().width / 2, dim.height / 2 - gameFrame.getSize().height / 2);

//        playRandomly(1200);
    }

    public static void initGame(boolean loadGame) {
        if (!loadGame) {
            whitePlayer = new Player(board, PieceColor.WHITE);
            blackPlayer = new Player(board, PieceColor.BLACK);

            whitePlayer.setName("Ziv");
            blackPlayer.setName("Guy");

            whitePlayer.setAI(false);
            blackPlayer.setAI(false);

            whitePlayer.setOpponentPlayer(blackPlayer);
        }

        checkSituations.put(PieceColor.WHITE, GameSituation.WHITE_IN_CHECK);
        checkSituations.put(PieceColor.BLACK, GameSituation.BLACK_IN_CHECK);

        checkmateSituations.put(PieceColor.WHITE, GameSituation.WHITE_CHECKMATED);
        checkmateSituations.put(PieceColor.BLACK, GameSituation.BLACK_CHECKMATED);

        board.setWhitePlayer(whitePlayer);
        board.setBlackPlayer(blackPlayer);

        board.initBoard();

        if (!loadGame) {
            board.setCurrentPlayer(whitePlayer);
        } else {
            if (whitePlayer.isCurrentPlayer()) board.setCurrentPlayer(whitePlayer);
            else board.setCurrentPlayer(blackPlayer);
        }

        board.checkBoard();
    }

    public void playRandomly(long milliseconds) {
        while (board.getGameSituation() != GameSituation.BLACK_CHECKMATED ||
                board.getGameSituation() != GameSituation.WHITE_CHECKMATED ||
                board.getGameSituation() != GameSituation.STALEMATE) {
            try {
                Thread.sleep(milliseconds);

                Collections.shuffle((List<?>) board.getCurrentPlayer().getMoves());

                int number = new Random().nextInt(board.getCurrentPlayer().getMoves().size());

                Iterator<Move> iterator = board.getCurrentPlayer().getMoves().iterator();
                Move move = iterator.next();
//                Move move = board.getCurrentPlayer().getMoves().get(number);
                System.out.println(board.getCurrentPlayer());
                System.out.println(move.getMovingPiece().getCurrentTile() + " -> " + move.getTargetTile());
                move.makeMove(true);
                board.printBoard();
                System.out.println(board.getGameSituation());
                if (board.getGameSituation() == GameSituation.STALEMATE || board.getGameSituation() == GameSituation.BLACK_CHECKMATED ||
                board.getGameSituation() == GameSituation.WHITE_CHECKMATED) {
                    if (board.getGameSituation() == GameSituation.BLACK_CHECKMATED ||
                            board.getGameSituation() == GameSituation.WHITE_CHECKMATED) {
                        ImageIcon icon = null;
                        try {
                            BufferedImage image = ImageIO.read(new File(TilePanel.currentPath + "/src/" + whitePlayer.getKing().getImageName()));
                            icon = new ImageIcon(image);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String[] options = { "No", "Yes" };
                        int answer = JOptionPane.showOptionDialog(gameFrame, "Checkmate! Restart game? ", "End Game",
                                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, icon, options, options[1]);
                        if (answer == 1) {
                            gameFrame.dispose();
                            board.resetBoard();
                            new BoardFrame();
                        } else System.exit(1);
                    }
                    break;
                };

                SwingUtilities.invokeLater(boardPanel::drawBoard);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void restartGame() {
        gameFrame.dispose();
        board.resetBoard();
        new BoardFrame();
    }

    private class BoardPanel extends JPanel {

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

            showDialogInSituation();
        }

        public void showDialogInSituation() {
            if (board.getGameSituation() == GameSituation.BLACK_CHECKMATED ||
                    board.getGameSituation() == GameSituation.WHITE_CHECKMATED) {
                ImageIcon icon = null;
                try {
                    BufferedImage image = ImageIO.read(new File(TilePanel.currentPath + "/src/"
                            + board.getCurrentPlayer().getKing().getImageName()));
                    icon = new ImageIcon(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] options = { "No", "Yes" };
                int answer = JOptionPane.showOptionDialog(gameFrame, "Checkmate! Restart game? ", "End Game",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, icon, options, options[1]);
                if (answer == 1) {
                    gameFrame.dispose();
                    board.resetBoard();
                    new BoardFrame();
                } else System.exit(1);
            } else if (board.getGameSituation() == GameSituation.BLACK_IN_CHECK ||
                    board.getGameSituation() == GameSituation.WHITE_IN_CHECK) {

                JOptionPane.showMessageDialog(gameFrame, "Check!", "Chess", JOptionPane.PLAIN_MESSAGE);
            }
        }

        public void displayAlertDialogWhenCheckmate() {
            if (board.getGameSituation() == GameSituation.BLACK_CHECKMATED ||
                    board.getGameSituation() == GameSituation.WHITE_CHECKMATED) {
                Alert alertDialog = new Alert(Alert.AlertType.NONE,
                        "Checkmate! Exit?",
                        ButtonType.OK,
                        ButtonType.YES);

                alertDialog.showAndWait();

                if (alertDialog.getResult() == ButtonType.YES) System.exit(1);
                else if (alertDialog.getResult() == ButtonType.OK) System.exit(1);
            }
        }
    }

    private class TilePanel extends JPanel {

        private final Tile tile;

        private static final Color whiteTileColor = Color.decode("#FFFACD");
        private static final Color blackTileColor = Color.decode("#593E1A");

        private static final Map<TileColor, Color> tileColorMap = Map.of(TileColor.WHITE, whiteTileColor,
                TileColor.BLACK, blackTileColor);

        private static final Path path = Paths.get("");
        private static final String currentPath = path.toAbsolutePath().toString();

        private static ArrayList<Tile> markedTiles;

        private static boolean drawPossibleMoves = false;

        private MouseEvent mouseEvent;

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
                                        System.out.println(sourceTile.getPiece().getName() + " can't move!");
                                        sourceTile = null;
                                        return;
                                    }
                                    playerPiece = sourceTile.getPiece();
                                    if (playerPiece instanceof RookPiece) System.out.println(playerPiece.hasMoved());
//                                    System.out.println("Possible Moves: ");
                                    for (Tile possibleMove : playerPiece.getPossibleMoves()) {
//                                        System.out.println(possibleMove);
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

                                    move.makeMove(true);
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
                    BufferedImage image = ImageIO.read(new File(currentPath + "/src/main/java/" + piece.getImageName()));
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