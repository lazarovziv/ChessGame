package com.zivlazarov.chessengine.model.pieces;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.Pair;

public class ChessPiece extends Piece implements Cloneable {

    // PAWN
    private Tile enPassantTile;
    private boolean movedLong = false;
    private boolean executedEnPassant = false;

    // ROOK
    private Tile kingSideCastlingTile = null;
    private Tile queenSideCastlingTile = null;
    private boolean isKingSide = false;
    private boolean isQueenSide = false;

    // KING
    private Tile kingSideCastleTile;
    private Tile queenSideCastleTile;
    private boolean executedKingSideCastle = false;
    private boolean executedQueenSideCastle = false;

    public ChessPiece(Player player, Board board, PieceType pieceType, Tile initTile) {
        super();

        this.player = player;
        this.board = board;
        this.pieceType = pieceType;
        this.pieceColor = player.getColor();
        currentTile = initTile;
        lastTile = currentTile;

        switch (pieceType) {
            case PAWN -> {
                if (pieceColor == PieceColor.BLACK) {
                    name = "bP";
                    imageName = "blackPawn.png";
                }
                if (pieceColor == PieceColor.WHITE) {
                    name = "wP";
                    imageName = "whitePawn.png";
                }
                value = 1;
                pieceCounter = 0;
                for (Piece piece : player.getAlivePieces()) {
                    if (piece.getPieceType() == PieceType.PAWN) {
                        pieceCounter += 1;
                    }
                }
            }
            case ROOK -> {
                if (pieceColor == PieceColor.BLACK) {
                    name = "bR";
                    imageName = "blackRook.png";
                }
                if (pieceColor == PieceColor.WHITE) {
                    name = "wR";
                    imageName = "whiteRook.png";
                }

                isKingSide = initTile.getCol() == 7;
                isQueenSide = !isKingSide;

                if (isKingSide) {
                    kingSideCastlingTile = board.getBoard()[currentTile.getRow()][5];
                    board.getKingSideChessRooksMap().put(player, this);
                } else {
                    queenSideCastlingTile = board.getBoard()[currentTile.getRow()][3];
                    board.getQueenSideChessRooksMap().put(player, this);
                }

                value = 5;
            }
            case KNIGHT -> {
                if (pieceColor == PieceColor.BLACK) {
                    name = "bN";
                    imageName = "blackKnight.png";
                }
                if (pieceColor == PieceColor.WHITE) {
                    name = "wN";
                    imageName = "whiteKnight.png";
                }
                value = 3;
            }
            case BISHOP -> {
                if (pieceColor == PieceColor.BLACK) {
                    name = "bB";
                    imageName = "blackBishop.png";
                }
                if (pieceColor == PieceColor.WHITE) {
                    name = "wB";
                    imageName = "whiteBishop.png";
                }
                value = 3;
            }
            case QUEEN -> {
                if (pieceColor == PieceColor.BLACK) {
                    name = "bQ";
                    imageName = "blackQueen.png";
                }
                if (pieceColor == PieceColor.WHITE) {
                    name = "wQ";
                    imageName = "whiteQueen.png";
                }
                value = 9;
            }
            case KING -> {
                if (pieceColor == PieceColor.BLACK) {
                    name = "bK";
                    imageName = "blackKing.png";
                }
                if (pieceColor == PieceColor.WHITE) {
                    name = "wK";
                    imageName = "whiteKing.png";
                }

                if (currentTile.getCol() + 2 <= 7 && currentTile.getCol() - 2 >= 0) {
                    kingSideCastleTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() + 2];
                    queenSideCastleTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() - 2];
                }

                board.getKingsChessMap().put(player, this);

                value = 100;
            }
        }
        player.addPieceToAlive(this);

        currentTile.setPiece(this);
    }

    @Override
    public void generateMoves() {
        if (!isAlive) return;

        switch (pieceType) {
            case PAWN -> {
                int x = currentTile.getRow();
                int y = currentTile.getCol();

                int[] eatingDirections = {-1, 1};

                boolean canMoveFurther;

                if (pieceColor == PieceColor.WHITE) {
                    canMoveFurther = !hasMoved && x == 1;
                } else {
                    canMoveFurther = !hasMoved && x == 6;
                }

                int direction = player.getPlayerDirection();
                int longDirection = direction * 2;

                if (x + player.getPlayerDirection() > board.getBoard().length - 1 || x + player.getPlayerDirection() < 0) return;

                if (board.getBoard()[x+direction][y].isEmpty()) {
                    possibleMoves.add(board.getBoard()[x + direction][y]);
                    Move move = new Move.Builder()
                            .board(board)
                            .player(player)
                            .movingPiece(this)
                            .targetTile(board.getBoard()[x+direction][y])
                            .build();
                    moves.add(move);
                    if (canMoveFurther) {
                        if (x + longDirection < 0 || x + longDirection > board.getBoard().length - 1) return;
                        if (board.getBoard()[x+longDirection][y].isEmpty()) {
                            possibleMoves.add(board.getBoard()[x+longDirection][y]);
                            Move move1 = new Move.Builder()
                                    .board(board)
                                    .player(player)
                                    .movingPiece(this)
                                    .targetTile(board.getBoard()[x+longDirection][y])
                                    .build();
                            moves.add(move1);
                        }
                    }
                }
                for (int d : eatingDirections) {
                    if (y + d > board.getBoard().length - 1 || y + d < 0) continue;
                    if (!board.getBoard()[x+direction][y+d].isEmpty() &&
                            board.getBoard()[x+direction][y+d].getPiece().getPieceColor() != pieceColor) {
                        possibleMoves.add(board.getBoard()[x+direction][y+d]);
                        piecesUnderThreat.add(board.getBoard()[x+direction][y+d].getPiece());
                        Move move = new Move.Builder()
                                .board(board)
                                .player(player)
                                .movingPiece(this)
                                .targetTile(board.getBoard()[x+direction][y+d])
                                .build();
                        moves.add(move);
                    }
                    // setting potential capturing tiles as threats
                    board.getBoard()[x+direction][y+d].setThreatenedByColor(pieceColor, true);
                    // insert en passant
                    if (canEnPassant(d)) {
                        possibleMoves.add(enPassantTile);
                        Move move = new Move.Builder()
                                .board(board)
                                .player(player)
                                .movingPiece(this)
                                .targetTile(enPassantTile)
                                .build();
                        moves.add(move);
                        // setting the adjacent pawn piece as under threat
                        // only move in chess where piece can be eaten without moving to it's tile
                        piecesUnderThreat.add(board.getBoard()[x][y+d].getPiece());
                    }
                }
                player.getLegalMoves().addAll(possibleMoves);
                player.getMoves().addAll(moves);
            }
            case ROOK -> {
                int[][] directions = {
                        {1, 0},
                        {-1, 0},
                        {0, 1},
                        {0, -1}
                };

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
                            Move move = new Move.Builder()
                                    .board(board)
                                    .player(player)
                                    .movingPiece(this)
                                    .targetTile(targetTile)
                                    .build();
                            moves.add(move);
                            break;
                        }
                        if (!targetTile.isEmpty() && targetTile.getPiece().getPieceColor() == pieceColor) {
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
            case KNIGHT -> {
                int[][] directions ={
                        {1, 2},
                        {1, -2},
                        {-1, 2},
                        {-1, -2},
                        {2, 1},
                        {2, -1},
                        {-2, 1},
                        {-2 ,-1}
                };

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
                            if (targetTile.getPiece().getPieceColor() != pieceColor) piecesUnderThreat.add(targetTile.getPiece());
                        }
                    }
                }
                possibleMoves.forEach(tile -> tile.setThreatenedByColor(pieceColor, true));
                player.getLegalMoves().addAll(possibleMoves);
                player.getMoves().addAll(moves);
            }
            case BISHOP -> {
                if (!isAlive) return;
                int[][] directions = {
                        {1, 1},
                        {1, -1},
                        {-1, -1},
                        {-1, 1}
                };

                int x = currentTile.getRow();
                int y = currentTile.getCol();

                for (int[] direction : directions) {
                    int r = direction[0];
                    int c = direction[1];

                    if (x + r > board.getBoard().length - 1 || x + r < 0 || y + c > board.getBoard().length - 1 || y + c < 0)
                        continue;

                    for (int i = 1; i < board.getBoard().length; i++) {
                        if (x + r * i > board.getBoard().length - 1 || x + r * i < 0 || y + c * i > board.getBoard().length - 1 || y + c * i < 0)
                            break;
                        Tile targetTile = board.getBoard()[x + r * i][y + c * i];
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
                            Move move = new Move.Builder()
                                    .board(board)
                                    .player(player)
                                    .movingPiece(this)
                                    .targetTile(targetTile)
                                    .build();
                            moves.add(move);
                            break;
                        }
                        if (!targetTile.isEmpty() && targetTile.getPiece().getPieceColor() == pieceColor) {
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
            case QUEEN -> {
                int[][] directions = {
                        {1, 0},
                        {-1, 0},
                        {0, 1},
                        {0, -1},
                        {1, 1},
                        {1, -1},
                        {-1, -1},
                        {-1, 1}
                };

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
                            Move move = new Move.Builder()
                                    .board(board)
                                    .player(player)
                                    .movingPiece(this)
                                    .targetTile(targetTile)
                                    .build();
                            moves.add(move);
                            break;
                        } else {
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
            case KING -> {
                int[][] directions = {
                        {1, 0},
                        {1, 1},
                        {1, -1},
                        {0, 1},
                        {0, -1},
                        {-1, 0},
                        {-1, 1},
                        {-1, -1}
                };

                int x = currentTile.getRow();
                int y = currentTile.getCol();

                for (int[] direction : directions) {
                    int r = direction[0];
                    int c = direction[1];
                    if (x + r > board.getBoard().length - 1 || x + r < 0 || y + c > board.getBoard().length - 1 || y + c < 0)
                        continue;
                    Tile targetTile = board.getBoard()[x + r][y + c];
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
                                }
                            }
                        }
                    }
                    if (!targetTile.isEmpty()) {
                        if (targetTile.getPiece().getPieceColor() == pieceColor)
                            targetTile.setThreatenedByColor(pieceColor, true);
                    }
                }

                if (y + 2 <= 7) {
                    if (canKingSideCastle()) {
                        possibleMoves.add(board.getBoard()[x][y + 2]);
                        Move move = new Move.Builder()
                                .board(board)
                                .player(player)
                                .movingPiece(this)
                                .targetTile(board.getBoard()[x][y + 2])
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
                                .targetTile(board.getBoard()[x][y - 2])
                                .build();
                        moves.add(move);
                    }
                }

                for (Tile tile : possibleMoves) {
                    if (!tile.isEmpty()) {
                        if (tile.getPiece().getPieceColor() != pieceColor) {
                            piecesUnderThreat.add(tile.getPiece());
                        }
                    }
                }
                possibleMoves.forEach(tile -> tile.setThreatenedByColor(pieceColor, true));
                player.getLegalMoves().addAll(possibleMoves);
                player.getMoves().addAll(moves);
            }
        }
    }

    // KING
    // king moves 2 tiles rook moves 2 tiles
    public boolean canKingSideCastle() {
        int x = currentTile.getRow();
        int y = currentTile.getCol();

        for (int i = 1; y+i < 7; i++) {
            if (board.getBoard()[x][7].getPiece() == null || hasMoved || isInDanger
                    || board.getBoard()[x][7].getPiece().hasMoved()
                    || board.getBoard()[x][7].isThreatenedByColor(player.getOpponent().getColor())
                    || player.isInCheck()) return false;

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
                    || player.isInCheck()) return false;

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

    // PAWN
    public boolean canEnPassant(int eatingDirection) {
        int x = currentTile.getRow();
        int y = currentTile.getCol();

        // checking borders of board
        if (y + eatingDirection < 0 || y + eatingDirection > board.getBoard().length - 1
                || x - 2 * player.getOpponent().getPlayerDirection() < 0) return false;

        // checking if piece next to pawn is of type pawn and is opponent's piece
        if (board.getBoard()[x][y + eatingDirection].getPiece() instanceof PawnPiece pawn &&
                pawn.getPieceColor() != pieceColor && !pawn.hasExecutedEnPassant() && board.getGameHistoryMoves().size() > 1) {
            // checking to see if opponent's last move is pawn's move 2 tiles forward
            if (board.getGameHistoryMoves().lastElement()/*.getSecond()*/.equals(new Pair<>(
//                    board.getBoard()[x - 2 * pawn.getPlayer().getPlayerDirection()][y+eatingDirection],
                    pawn,
                    pawn.getCurrentTile()))) {
                if (board.getBoard()[x+player.getPlayerDirection()][y+eatingDirection].isEmpty()) {
                    enPassantTile = board.getBoard()[x+player.getPlayerDirection()][y+eatingDirection];
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasExecutedEnPassant() {
        return executedEnPassant;
    }

    public void setExecutedEnPassant(boolean executedEnPassant) {
        this.executedEnPassant = executedEnPassant;
    }

    public Tile getEnPassantTile() {
        return enPassantTile;
    }

    public void setEnPassantTile(Tile enPassantTile) {
        this.enPassantTile = enPassantTile;
    }

    public boolean hasMovedLong() {
        return movedLong;
    }

    public void setMovedLong(boolean movedLong) {
        this.movedLong = movedLong;
    }

    // ROOK
    public Tile getKingSideCastlingTile() {
        return kingSideCastlingTile;
    }

    public Tile getQueenSideCastlingTile() {
        return queenSideCastlingTile;
    }
}
