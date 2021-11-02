package com.zivlazarov.chessengine.model.pieces;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;

import javax.persistence.Entity;

@Entity
public class KnightPiece extends Piece implements Cloneable {

    private static final int[][] directions ={
            {1, 2},
            {1, -2},
            {-1, 2},
            {-1, -2},
            {2, 1},
            {2, -1},
            {-2, 1},
            {-2 ,-1}
    };

    public KnightPiece(Player player, Board board, Tile initTile, int pieceCounter) {
        super();

        this.player = player;
        this.board = board;
        this.pieceColor = player.getColor();
        this.currentTile = initTile;
        this.lastTile = currentTile;
        this.pieceCounter = pieceCounter;

        this.value = 30;

        if (this.pieceColor == PieceColor.BLACK) {
            this.name = "bN";
            this.imageName = "blackKnight.png";
        }
        if (this.pieceColor == PieceColor.WHITE) {
            this.name = "wN";
            this.imageName = "whiteKnight.png";
        }

        this.player.addPieceToAlive(this);
        this.currentTile.setPiece(this);
        this.pieceType = PieceType.KNIGHT;

        strongTiles = new double[][] {
                {-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0},
                {-4.0, -2.0, 0.0, 0.0, 0.0, 0.0, -2.0, -4.0},
                {-3.0, 0.0, 1.0, 1.5, 1.5, 1.0, 0.0, -3.0},
                {-3.0, 0.5, 1.5, 1.5, 1.5, 1.5, 0.5, -3.0},
                {-3.0, 0.0, 1.5, 1.5, 1.5, 1.5, 0.0, -3.0},
                {-3.0, 0.5, 1.0, 1.5, 1.5, 1.0, 0.5, -3.0},
                {-4.0, -2.0, 0.0, 0.5, 0.5, 0.0, -2.0, -4.0},
                {-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0}
        };

        if (pieceColor == PieceColor.BLACK) {
            strongTiles = revertStrongTiles(strongTiles);
        }

        pieceIndex = player.getAlivePieces().size() - 1;
    }

    @Override
    public void generateMoves() {
        if (!isAlive) return;

        int x = currentTile.getRow();
        int y = currentTile.getCol();

        for (int[] direction : directions) {
            int r = direction[0];
            int c = direction[1];

            if (x+r > board.getBoard().length - 1  || x+r < 0 || y+c > board.getBoard().length - 1 || y+c < 0) continue;
            Tile targetTile = board.getBoard()[x+r][y+c];
            if (targetTile.isEmpty() || targetTile.getPiece().getPieceColor() != pieceColor) {
                possibleMoves.add(targetTile);
                Move move = new Move.Builder()
                        .board(board)
                        .player(player)
                        .movingPiece(this)
                        .targetTile(targetTile)
                        .build();
                moves.add(move);
                if (!targetTile.isEmpty()) {
                    if (targetTile.getPiece().getPieceColor() != pieceColor) {
                        piecesUnderThreat.add(targetTile.getPiece());
                        targetTile.getPiece().setIsInDanger(true);
                    }
                }
            } else if (!targetTile.isEmpty() && targetTile.getPiece().getPieceColor() == pieceColor) {
                targetTile.setThreatenedByColor(pieceColor, true);
            }
        }
        possibleMoves.forEach(tile -> tile.setThreatenedByColor(pieceColor, true));
        player.getLegalMoves().addAll(possibleMoves);
        player.getMoves().addAll(moves);
    }

    public Piece clone(Board newBoard, Player player) {
        KnightPiece newKnight = new KnightPiece(player, newBoard, newBoard.getBoard()[currentTile.getRow()][currentTile.getCol()], pieceCounter);
        newKnight.setLastTile(newBoard.getBoard()[lastRow][lastCol]);
        newKnight.setPieceIndex(pieceIndex);
        return newKnight;
    }
}
