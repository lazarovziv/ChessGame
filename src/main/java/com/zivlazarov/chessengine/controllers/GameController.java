package com.zivlazarov.chessengine.controllers;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.board.TileColor;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.ui.components.BoardFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameController {

    private Board board;
    private BoardFrame frame;

    final Map<Tile, BoardFrame.TilePanel> tilePanelMap;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece playerPiece;

    public GameController(Board board, BoardFrame frame) {
        this.board = board;
        this.frame = frame;

        tilePanelMap = new HashMap<>();

        frame.addMouseListener(new MoveListener());
    }

    private class MoveListener implements MouseListener {

        Tile tile;

        private static final ArrayList<Tile> markedTiles = new ArrayList<>();
        private static final Color whiteTileColor = Color.decode("#FFFACD");
        private static final Color blackTileColor = Color.decode("#593E1A");

        private static final Map<TileColor, Color> tileColorMap = Map.of(TileColor.WHITE, whiteTileColor,
                TileColor.BLACK, blackTileColor);

        MoveListener() {}

        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                // first mouse click
                if (sourceTile == null) {
                    sourceTile = tile;
                    if (!sourceTile.isEmpty()) {
                        if (sourceTile.getPiece().getPieceColor() == board.getCurrentPlayer().getColor()) {
                            playerPiece = sourceTile.getPiece();
                            if (!playerPiece.canMove()) {
                                sourceTile = null;
                                playerPiece = null;
                            } else {
                                for (Tile tile : playerPiece.getPossibleMoves()) {
                                    markedTiles.add(tile);
                                    markTile(tilePanelMap.get(tile));
                                }
                            }
                        }
                    } else sourceTile = null;

                } else {
                    if (playerPiece != null) {
                        destinationTile = tile;
                        // checking if tile clicked is any of the chosen piece's possible moves
                        boolean noneMatch = playerPiece.getMoves().stream().noneMatch(move -> move.getTargetTile().equals(tile));
                        // if it does not included in possible moves, destination tile needs to be reset
                        if (noneMatch) {
                            System.out.println("Can't move to " + destinationTile + " !");
                            // if it matches, make move
                        } else {
                            Move move = new Move.Builder()
                                    .board(board)
                                    .player(board.getCurrentPlayer())
                                    .movingPiece(playerPiece)
                                    .targetTile(destinationTile)
                                    .build();

                            move.makeMove(true, true);

                            System.out.println(move);
                        }

                        System.out.println(board.evaluateBoard());

                        sourceTile = null;
                        playerPiece = null;
                        destinationTile = null;

                        // set marked tiles to original colors
                        markedTiles.forEach(tile -> tilePanelMap.get(tile).setBackground(tileColorMap.get(tile.getTileColor())));

//                        SwingUtilities.invokeLater(() -> boardPanel.drawBoard());
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

        public void markTile(BoardFrame.TilePanel panel) {
            frame.markTile(panel);
        }
    }
}
