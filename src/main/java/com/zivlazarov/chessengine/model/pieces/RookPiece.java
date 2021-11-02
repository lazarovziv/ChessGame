package com.zivlazarov.chessengine.model.pieces;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;

import javax.persistence.Entity;

@Entity
public class RookPiece extends Piece implements Cloneable {

    private Tile kingSideCastlingTile;
    private Tile queenSideCastlingTile;

    private final boolean isKingSide;
    private final boolean isQueenSide;

    private static final int[][] directions = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
    };

    public RookPiece(Player player, Board board, Tile initTile, int pieceCounter) {
        super();

        this.player = player;
        this.board = board;
        this.pieceColor = player.getColor();
        this.currentTile = initTile;
        this.lastTile = currentTile;
        this.pieceCounter = pieceCounter;

        this.value = 50;

        if (this.pieceColor == PieceColor.BLACK) {
            this.name = "bR";
            this.imageName = "blackRook.png";
        }
        if (this.pieceColor == PieceColor.WHITE) {
            this.name = "wR";
            this.imageName = "whiteRook.png";
        }

        this.player.addPieceToAlive(this);
        this.currentTile.setPiece(this);
        this.pieceType = PieceType.ROOK;

        isKingSide = initTile.getCol() == 7;
        isQueenSide = !isKingSide;

        if (isKingSide) {
            kingSideCastlingTile = board.getBoard()[currentTile.getRow()][5];
            board.getKingSideRooksMap().put(player, this);
        } else {
            queenSideCastlingTile = board.getBoard()[currentTile.getRow()][3];
            board.getQueenSideRooksMap().put(player, this);
        }

        strongTiles = new double[][] {
                {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                {0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5},
                {-0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5},
                {-0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5},
                {-0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5},
                {-0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5},
                {-0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5},
                {0.0, 0.0, 0.0, 0.5, 0.5, 0.0, 0.0, 0.0}
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

    public Tile getKingSideCastlingTile() {
        return kingSideCastlingTile;
    }

    public void setKingSideCastlingTile(Tile kingSideCastlingTile) {
        this.kingSideCastlingTile = kingSideCastlingTile;
    }

    public Tile getQueenSideCastlingTile() {
        return queenSideCastlingTile;
    }

    public void setQueenSideCastlingTile(Tile queenSideCastlingTile) {
        this.queenSideCastlingTile = queenSideCastlingTile;
    }

    public boolean isKingSide() {
        return isKingSide;
    }

    public boolean isQueenSide() {
        return isQueenSide;
    }

    public Piece clone(Board newBoard, Player player) {
        RookPiece newRook = new RookPiece(player, newBoard, newBoard.getBoard()[currentTile.getRow()][currentTile.getCol()], pieceCounter);
        newRook.setLastTile(newBoard.getBoard()[lastRow][lastCol]);
        newRook.setPieceIndex(pieceIndex);
        return newRook;
    }
}
