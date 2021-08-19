package com.zivlazarov.chessengine.model.pieces;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;

import javax.persistence.Entity;

@Entity
public class KingPiece extends Piece implements Cloneable {

    private Tile kingSideCastleTile;
    private Tile queenSideCastleTile;

    private boolean executedKingSideCastle = false;
    private boolean executedQueenSideCastle = false;

    private int[][] directions = {
            {1,0},
            {1,1},
            {1,-1},
            {0,1},
            {0,-1},
            {-1,0},
            {-1,1},
            {-1,-1}
    };

    public KingPiece(Player player, Board board, Tile initTile) {
        super();

        this.player = player;
        this.board = board;
        this.pieceColor = player.getColor();
        this.currentTile = initTile;
        this.lastTile = currentTile;
        this.pieceCounter = -1;

        this.value = 900;

        if (this.pieceColor == PieceColor.BLACK) {
            this.name = "bK";
            this.imageName = "blackKing.png";
        }
        if (this.pieceColor == PieceColor.WHITE) {
            this.name = "wK";
            this.imageName = "whiteKing.png";
        }

        this.player.addPieceToAlive(this);
        this.currentTile.setPiece(this);
        this.pieceType = PieceType.KING;

        if (currentTile.getCol() + 2 <= 7 && currentTile.getCol() - 2 >= 0) {
            kingSideCastleTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() + 2];
            queenSideCastleTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() - 2];
        }

        board.getKingsMap().put(player, this);

        strongTiles = new double[][] {
                {-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
                {-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
                {-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
                {-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
                {-2.0, -3.0, -3.0, -4.0 ,-4.0, -3.0, -3.0, -2.0},
                {-1.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -1.0},
                {2.0, 2.0, 0.0, 0.0, 0.0, 0.0, 2.0, 2.0},
                {2.0, 3.0, 1.0, 0.0, 0.0, 1.0, 3.0, 2.0}
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
            if (x+r > board.getBoard().length - 1 || x+r < 0 || y+c > board.getBoard().length - 1 || y+c < 0) continue;
            Tile targetTile = board.getBoard()[x+r][y+c];
            if (!targetTile.isThreatenedByColor(player.getOpponent().getColor())) {
                if (targetTile.isEmpty() || targetTile.getPiece().getPieceColor() != pieceColor) {
                    // calling possible moves first because of the target tile check condition in Move.Builder class
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
                        } else targetTile.setThreatenedByColor(pieceColor, true);
                    }
                }
            }
        }

        if (y + 2 <= 7) {
            if (canKingSideCastle()) {
                possibleMoves.add(board.getBoard()[x][y + 2]);
                Move move = new Move.Builder()
                        .board(board)
                        .player(player)
                        .movingPiece(this)
                        .targetTile(board.getBoard()[x][y+2])
                        .build();
                moves.add(move);
            }
        }

        if (y - 2 >= 0) {
            if (canQueenSideCastle()) {
                possibleMoves.add(board.getBoard()[x][y - 2]);
                Move move = new Move.Builder()
                        .board(board)
                        .player(player)
                        .movingPiece(this)
                        .targetTile(board.getBoard()[x][y-2])
                        .build();
                moves.add(move);
            }
        }

        for (Tile tile : possibleMoves) {
            if (!tile.isEmpty()) {
                if (tile.getPiece().getPieceColor() != pieceColor) {
                    piecesUnderThreat.add(tile.getPiece());
                    tile.getPiece().setIsInDanger(true);
                }
            }
        }
        possibleMoves.forEach(tile -> tile.setThreatenedByColor(pieceColor, true));
        player.getLegalMoves().addAll(possibleMoves);
        player.getMoves().addAll(moves);
    }

// castling rules
// The king has not previously moved;
// Your chosen rook has not previously moved;
// There must be no pieces between the king and the chosen rook;
// The king is not currently in check;
// Your king must not pass through a square that is under attack by enemy pieces;
// The king must not end up in check.

    // king moves 2 tiles rook moves 2 tiles
    public boolean canKingSideCastle() {
        int x = currentTile.getRow();
        int y = currentTile.getCol();

        for (int i = 1; y+i < 7; i++) {
            if (board.getBoard()[x][7].getPiece() == null || hasMoved || isInDanger
                    || board.getBoard()[x][7].getPiece().hasMoved()
                    || board.getBoard()[x][7].isThreatenedByColor(player.getOpponent().getColor())
                    || player.isInCheck()
                    || player.getKingSideRookPiece() == null
                    || player.getKingSideRookPiece().hasMoved()) return false;

            if (!board.getBoard()[x][y+i].isEmpty()
                    || board.getBoard()[x][y+i].isThreatenedByColor(player.getOpponent().getColor())) return false;
        }
        return true;
    }

    // king moves 2 tiles rook moves 3 tiles
    public boolean canQueenSideCastle() {
        int x = currentTile.getRow();
        int y = currentTile.getCol();

        for (int i = 1; y-i > 0; i++) {
            if (board.getBoard()[x][0].getPiece() == null || hasMoved || isInDanger
                    || board.getBoard()[x][0].getPiece().hasMoved()
                    || board.getBoard()[x][0].isThreatenedByColor(player.getOpponent().getColor())
                    || player.isInCheck()
                    || player.getQueenSideRookPiece() == null
                    || player.getQueenSideRookPiece().hasMoved()) return false;

            if (!board.getBoard()[x][y-i].isEmpty()
                    || board.getBoard()[x][y-i].isThreatenedByColor(player.getOpponent().getColor())) return false;
        }
        return true;
    }

    public Tile getKingSideCastleTile() {
        return kingSideCastleTile;
    }

    public Tile getQueenSideCastleTile() {
        return queenSideCastleTile;
    }

    public boolean hasExecutedKingSideCastle() {
        return executedKingSideCastle;
    }

    public void setExecutedKingSideCastle(boolean executedKingSideCastle) {
        this.executedKingSideCastle = executedKingSideCastle;
    }

    public boolean hasExecutedQueenSideCastle() {
        return executedQueenSideCastle;
    }

    public void setExecutedQueenSideCastle(boolean executedQueenSideCastle) {
        this.executedQueenSideCastle = executedQueenSideCastle;
    }

    public Piece clone(Board newBoard, Player player) {
        KingPiece newKing = new KingPiece(player, newBoard, newBoard.getBoard()[currentTile.getRow()][currentTile.getCol()]);
        newKing.setLastTile(newBoard.getBoard()[lastRow][lastCol]);
        if (executedKingSideCastle) newKing.setExecutedKingSideCastle(true);
        if (executedQueenSideCastle) newKing.setExecutedQueenSideCastle(true);
        if (hasMoved) newKing.setHasMoved(true);
        newKing.setPieceIndex(pieceIndex);
        return newKing;
    }
}
