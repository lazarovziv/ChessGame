package com.zivlazarov.chessengine.ui.components;

import com.zivlazarov.chessengine.db.dao.MoveDao;
import com.zivlazarov.chessengine.db.dao.PieceDao;
import com.zivlazarov.chessengine.db.dao.PlayerDao;
import com.zivlazarov.chessengine.db.dao.TileDao;
import com.zivlazarov.chessengine.model.ai.Minimax;
import com.zivlazarov.chessengine.model.board.*;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.move.MoveLabel;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.ui.game.ChessGame;
import com.zivlazarov.chessengine.ui.utils.Utilities;
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
import java.sql.SQLException;
import java.util.List;
import java.util.*;

import static com.zivlazarov.chessengine.model.move.MoveLabel.EN_PASSANT;
import static com.zivlazarov.chessengine.ui.utils.Utilities.createImageIcon;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class BoardFrame {

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final SidePanel whiteSidePanel;
    private final SidePanel blackSidePanel;
    private final SidePanel movesSidePanel;
    private final SidePanel capturedPiecesPanel;

    private final Map<Player, SidePanel> playerSidePanelMap;

    private static Board board;

    private static final Map<PieceColor, GameSituation> checkSituations = Map.of(
            PieceColor.WHITE, GameSituation.WHITE_IN_CHECK,
            PieceColor.BLACK, GameSituation.BLACK_IN_CHECK
    );
    private static final Map<PieceColor, GameSituation> checkmateSituations = Map.of(
            PieceColor.WHITE, GameSituation.WHITE_CHECKMATED,
            PieceColor.BLACK, GameSituation.BLACK_CHECKMATED
    );

    private static Player whitePlayer;
    private static Player blackPlayer;

    private static Minimax minimax;

    private static Tile sourceTile;
    private static Tile destinationTile;
    private static Piece playerPiece;

    public BoardFrame() {
        initGame();

        PlayerDao playerDao = new PlayerDao();
        MoveDao moveDao = new MoveDao();
        PieceDao pieceDao = new PieceDao();
        TileDao tileDao = new TileDao();

        gameFrame = new JFrame("Chess");
        gameFrame.setUndecorated(true);
        gameFrame.setLayout(new BorderLayout());
        boardPanel = new BoardPanel(board);
        whiteSidePanel = new SidePanel();
        blackSidePanel = new SidePanel();

        movesSidePanel = new SidePanel();
        movesSidePanel.setLayout(new GridLayout(50, 1));

        capturedPiecesPanel = new SidePanel();
        capturedPiecesPanel.setLayout(new GridLayout(8, 2));

        playerSidePanelMap  = Map.of(
                whitePlayer, whiteSidePanel,
                blackPlayer, blackSidePanel
        );

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
            try {
                // saving tiles
                for (Tile[] tiles : board.getBoard()) {
                    for (Tile tile : tiles) {
                        tileDao.insertTile(tile);
                    }
                }

                // saving players
                playerDao.insertPlayer(whitePlayer);
                playerDao.insertPlayer(blackPlayer);

                // saving pieces
                for (Piece piece : whitePlayer.getAlivePieces()) {
                    pieceDao.insertPiece(piece);
                }
                for (Piece piece : whitePlayer.getDeadPieces()) {
                    pieceDao.insertPiece(piece);
                }
                for (Piece piece : blackPlayer.getAlivePieces()) {
                    pieceDao.insertPiece(piece);
                }

                for (Piece piece : blackPlayer.getDeadPieces()) {
                    pieceDao.insertPiece(piece);
                }

                // saving moves
                for (Move move : whitePlayer.getMoves()) {
                    moveDao.insertMove(move);
                }
                for (Move move : blackPlayer.getMoves()) {
                    moveDao.insertMove(move);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
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

        gameFrame.setSize(850, 600);
        gameFrame.add(boardPanel, BorderLayout.CENTER);
//        gameFrame.add(whiteSidePanel, BorderLayout.WEST);
//        gameFrame.add(blackSidePanel, BorderLayout.EAST);
        gameFrame.add(movesSidePanel, BorderLayout.WEST);
        gameFrame.add(capturedPiecesPanel, BorderLayout.EAST);
        gameFrame.setVisible(true);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setting window in center
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        gameFrame.setLocation(dim.width / 2 - gameFrame.getSize().width / 2, dim.height / 2 - gameFrame.getSize().height / 2);

        /*  depth 3: 8902

            depth 4: 197281

            depth 5: 4865609  */

//        System.out.println(ChessGame.generatedMove(board, 4));

//        playRandomly(100);
//        playMinimax();
    }

    public static void initGame() {
        whitePlayer = new Player(PieceColor.WHITE);
        blackPlayer = new Player(PieceColor.BLACK);

        minimax = new Minimax();

        whitePlayer.setName("Ziv");
        blackPlayer.setName("Guy");

        whitePlayer.setAI(false);
        blackPlayer.setAI(false);

        minimax = new Minimax();

        whitePlayer.setOpponent(blackPlayer);

        board = new Board();

        board.setWhitePlayer(whitePlayer);
        board.setBlackPlayer(blackPlayer);

        whitePlayer.setBoard(board);
        blackPlayer.setBoard(board);

        board.setCurrentPlayer(whitePlayer);

        board.initBoard();
        board.checkBoard();
    }

    public void playMinimax() {
        while (board.canContinueGame()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean isMax = board.getCurrentPlayer().getColor() == PieceColor.WHITE;
            Move move = minimax.findBestMove(board, 3, blackPlayer);
            move.makeMove(true);

            SwingUtilities.invokeLater(() -> boardPanel.drawBoard(false));
        }
    }

    public void playRandomly(long milliseconds) {
        while (board.canContinueGame()) {
            try {
                Thread.sleep(milliseconds);

                Iterator<Move> iterator = board.getCurrentPlayer().getMoves().iterator();
                Move move = iterator.next();

                move.makeMove(true);

                System.out.println(board.evaluateBoard());
                System.out.println(board.getGameSituation());

                if (!board.canContinueGame()) {
                    board.printBoard();
                        Thread.currentThread().interrupt();
                        ImageIcon icon = null;
                        try {
                            BufferedImage image = ImageIO.read(new File(Utilities.currentPath +
                                    "/src/" + whitePlayer.getKing().getImageName()));
                            icon = new ImageIcon(image);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String[] options = { "No", "Yes" };
                        int answer = JOptionPane.showOptionDialog(gameFrame, "Checkmate! Restart game? ", "End Game",
                                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, icon, options, options[1]);
                        if (answer == 1) {
                            restartGame();
                        } else System.exit(1);
                    break;
                };

                SwingUtilities.invokeLater(() -> boardPanel.drawBoard(false));
//                SwingUtilities.invokeLater(boardPanel::drawBoard);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void restartGame() {
        gameFrame.dispose();
//        board.resetBoard();
        new BoardFrame();
    }

    private class SidePanel extends JPanel {

        SidePanel() {
//            super(new GridLayout(50, 1));

//            setBackground(Color.BLACK);

            setPreferredSize(new Dimension(125, 350));
            validate();
        }

        private void drawMoves() {
            removeAll();

            validate();
            repaint();

            List<JLabel> labels = new ArrayList<>();

            for (Move move : board.getMatchPlays()) {
                labels.add(new JLabel(move.toString()));
            }

            for (JLabel label : labels) {
                add(label);
            }

            validate();
            repaint();
        }

        private void drawPiece() {
            removeAll();
            validate();
            repaint();

            List<JLabel> whiteLabels = new ArrayList<>();
            List<JLabel> blackLabels = new ArrayList<>();

            for (Piece piece : board.getWhitePlayer().getDeadPieces()) {
                whiteLabels.add(new JLabel(createImageIcon(piece)));
            }
            for (Piece piece : board.getBlackPlayer().getDeadPieces()) {
                blackLabels.add(new JLabel(createImageIcon(piece)));
            }

            whiteLabels.forEach(this::add);
            blackLabels.forEach(this::add);

            validate();
            repaint();
        }
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

        public void drawBoard(boolean showDialog) {
            removeAll();

            for (TilePanel tilePanel : tilePanels) {
                tilePanel.drawTile();
                add(tilePanel);
            }
            // redraw side panels
            validate();
            repaint();

            if (showDialog) {
                showDialogInSituation();
            }
        }

        public void showDialogInSituation() {
            if (board.getGameSituation() == GameSituation.BLACK_CHECKMATED ||
                    board.getGameSituation() == GameSituation.WHITE_CHECKMATED) {
                ImageIcon icon = null;
                try {
                    BufferedImage image = ImageIO.read(new File(Utilities.currentPath + "/src/main/java/"
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
//                    board.resetBoard();
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
                                if (sourceTile.getPiece().getPieceColor() == board.getCurrentPlayer().getColor()) {
                                    if (!sourceTile.getPiece().canMove()) {
                                        System.out.println(sourceTile.getPiece().getName() + " can't move!");
                                        sourceTile = null;
                                        return;
                                    }
                                    playerPiece = sourceTile.getPiece();
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

                                    if (move.getLabel() == MoveLabel.REGULAR) {
                                        System.out.println(move.getPlayer().getName() + " has executed a regular move");
                                    } else if (move.getLabel() == EN_PASSANT) {
                                        System.out.println(move.getPlayer().getName() + " has executed an " + move.getLabel());
                                    } else {
                                        System.out.println(move.getPlayer().getName() + " has executed a " + move.getLabel());
                                    }
                                }

                                System.out.println(board.evaluateBoard());

                                sourceTile = null;
                                playerPiece = null;
                                destinationTile = null;

                                for (Tile markedTile : markedTiles) {
                                    boardPanel.tilePanelMap.get(markedTile).setBackground(tileColorMap.get(markedTile.getTileColor()));
                                }

                                SwingUtilities.invokeLater(() -> boardPanel.drawBoard(true));
                                SwingUtilities.invokeLater(movesSidePanel::drawMoves);
                                SwingUtilities.invokeLater(capturedPiecesPanel::drawPiece);
//                                SwingUtilities.invokeLater(boardPanel::drawBoard);
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

            validate();
        }

        public void drawTile() {

            removeAll();

            setBackground(tileColorMap.get(tile.getTileColor()));

            validate();
            repaint();

            removeAll();

            if (!tile.isEmpty()) {
                if (tile.getPiece().isAlive())
                    add(new JLabel(createImageIcon(tile.getPiece())));
            }

            validate();
            repaint();
        }
    }
}