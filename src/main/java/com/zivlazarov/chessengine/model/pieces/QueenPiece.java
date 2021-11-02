package com.zivlazarov.chessengine.model.pieces;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;

import javax.persistence.Entity;

@Entity
public class QueenPiece extends Piece implements Cloneable {

    private static final int[][] directions = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1},
            {1, 1},
            {1, -1},
            {-1, -1},
            {-1, 1}
    };

    public QueenPiece(Player player, Board board, Tile initTile) {
        super();

        this.player = player;
        this.board = board;
        this.pieceColor = player.getColor();
        this.currentTile = initTile;
        this.lastTile = currentTile;
        this.pieceCounter = -2;

        this.value = 90;

        if (this.pieceColor == PieceColor.BLACK) {
            this.name = "bQ";
            this.imageName = "blackQueen.png";
        }
        if (this.pieceColor == PieceColor.WHITE) {
            this.name = "wQ";
            this.imageName = "whiteQueen.png";
        }

        this.player.addPieceToAlive(this);
        this.currentTile.setPiece(this);
        this.pieceType = PieceType.QUEEN;

        strongTiles = new double[][] {
                {-2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0},
                {-1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -1.0},
                {-1.0, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -1.0},
                {-0.5, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -0.5},
                {0.0, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -0.5},
                {-1.0, 0.5, 0.5, 0.5, 0.5, 0.5, 0.0, -1.0},
                {-1.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, -1.0},
                {-2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0}
        };

        if (pieceColor == PieceColor.BLACK) {
            strongTiles = revertStrongTiles(strongTiles);
        }

        pieceIndex = player.getAlivePieces().size() - 1;
    }

    @Override
    public void generateMoves() {
        if (!isAlive) return;

        if (currentTile == null) return;

        int x = currentTile.getRow();
        int y = currentTile.getCol();

        for (int[] direction : directions) {
            int r = direction[0];
            int c = direction[1];

            if (x+r > board.getBoard().length - 1  || x+r < 0 || y+c > board.getBoard().length - 1 || y+c < 0) continue;

            for (int i = 1; i < board.getBoard().length; i++) {
                if (x + i*r > board.getBoard().length - 1 || x+r*i < 0 || y+c*i > board.getBoard().length - 1 || y+c*i < 0) break;
                Tile targetTile = board.getBoard()[x+r*i][y+c*i];
                if (targetTile.isEmpty()) {
                    possibleMoves.add(targetTile);
                    Move move = new Move.Builder()
                            .board(board)
                            .player(player)
                            .movingPiece(this)
                            .targetTile(targetTile)
                            .build();
                    moves.add(move);
                } else if (targetTile.getPiece().getPieceColor() != pieceColor) {
                    possibleMoves.add(targetTile);
                    piecesUnderThreat.add(targetTile.getPiece());
                    targetTile.getPiece().setIsInDanger(true);
                    Move move = new Move.Builder()
                            .board(board)
                            .player(player)
                            .movingPiece(this)
                            .targetTile(targetTile)
                            .build();
                    moves.add(move);
                    break;
                } else if (!targetTile.isEmpty() && targetTile.getPiece().getPieceColor() == pieceColor) {
                    // setting it as threatened in the case of the piece on the tile will be captured
                    targetTile.setThreatenedByColor(pieceColor, true);
                    break;
                }
            }
        }
        possibleMoves.forEach(tile -> tile.setThreatenedByColor(pieceColor, true));
        player.getLegalMoves().addAll(possibleMoves);
        player.getMoves().addAll(moves);
    }

    public Piece clone(Board newBoard, Player player) {
        QueenPiece newQueen = new QueenPiece(player, newBoard, newBoard.getBoard()[currentTile.getRow()][currentTile.getCol()]);
        newQueen.setLastTile(newBoard.getBoard()[lastRow][lastCol]);
        newQueen.setPieceIndex(pieceIndex);
        return newQueen;
    }
}
