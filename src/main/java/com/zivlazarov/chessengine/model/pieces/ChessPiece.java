package com.zivlazarov.chessengine.model.pieces;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.MyObservable;
import com.zivlazarov.chessengine.model.utils.Pair;
import javafx.beans.property.ObjectProperty;

import javax.swing.*;
import java.util.*;

public class ChessPiece implements Piece {

    private Player player;

    private MyObservable observable;

    private ObjectProperty<Tile> currentTileProperty;

    private final ArrayList<Tile> possibleMoves;
    private final ArrayList<Piece> piecesUnderThreat;
    private final Stack<Tile> historyMoves;
    private Tile lastTile;
    private Stack<Piece> capturedPieces;
    private final Board board;
    private PieceType pieceType;
    private String name;
    private int pieceCounter;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private Tile currentTile;
    private PieceColor pieceColor;
    private String imageName;
    private Icon imageIcon;
    private boolean hasMoved = false;

    // PAWN
    private Tile enPassantTile;

    // ROOK
    private Tile kingSideCastlingTile = null;
    private Tile queenSideCastlingTile = null;
    private boolean isKingSide = false;
    private boolean isQueenSide = false;

    // KING
    private Tile kingSideCastleTile;
    private Tile queenSideCastleTile;

    private int value;

    private final Object[] allFields;

    public ChessPiece(Player player, Board board, PieceType pieceType, PieceColor pieceColor, Tile initTile) {
        this.player = player;
        this.board = board;
        this.pieceType = pieceType;
        this.pieceColor = pieceColor;
        currentTile = initTile;
        lastTile = currentTile;

        possibleMoves = new ArrayList<>();
        piecesUnderThreat = new ArrayList<>();
        historyMoves = new Stack<>();

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
                if (currentTile.getCol() == board.getBoard().length - 1) {
                    isKingSide = true;
                    isQueenSide = false;
                    kingSideCastlingTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() - 2];
                    queenSideCastlingTile = null;
                }
                else if (currentTile.getCol() == 0) {
                    isQueenSide = true;
                    isKingSide = false;
                    queenSideCastlingTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() + 3];
                    kingSideCastlingTile = null;
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

                kingSideCastleTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() + 2];
                queenSideCastleTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() - 2];

                value = 100;
            }
        }
        player.addPieceToAlive(this);

        currentTile.setPiece(this);

        allFields = new Object[] {player, pieceType, possibleMoves, piecesUnderThreat,
                historyMoves, lastTile, capturedPieces,
                name, pieceCounter, isAlive, isInDanger, currentTile,
                pieceColor, imageName, imageIcon};
    }

    @Override
    public void generateMoves() {
        switch (pieceType) {
            case PAWN -> {
                if (!isAlive) return;
                Map<PieceColor, Integer> map = new HashMap<>();
                map.put(PieceColor.WHITE, 1);
                map.put(PieceColor.BLACK, -1);
                int[] eatingDirections = new int[]{-1, 1};

                int x = currentTile.getRow();
                int y = currentTile.getCol();

                boolean canMoveFurther = !hasMoved;

                int direction = player.getPlayerDirection();
                int longDirection = direction * 2;

                if (x + map.get(pieceColor) > board.getBoard().length - 1 || x + map.get(pieceColor) < 0) return;

                if (board.getBoard()[x+direction][y].isEmpty()) {
                    possibleMoves.add(board.getBoard()[x + direction][y]);
                    if (canMoveFurther) {
                        if (x + longDirection < 0 || x + longDirection > board.getBoard().length - 1) return;
                        if (board.getBoard()[x+longDirection][y].isEmpty()) {
                            possibleMoves.add(board.getBoard()[x+longDirection][y]);
                        }
                    }
                }
                for (int d : eatingDirections) {
                    if (y + d > board.getBoard().length - 1 || y + d < 0) continue;
                    if (!board.getBoard()[x+direction][y+d].isEmpty() &&
                            board.getBoard()[x+direction][y+d].getPiece().getPieceColor() != pieceColor) {
                        possibleMoves.add(board.getBoard()[x+direction][y+d]);
                        piecesUnderThreat.add(board.getBoard()[x+direction][y+d].getPiece());
                    }
                    // insert en passant
                    if (canEnPassant(d)) {
                        possibleMoves.add(enPassantTile);
                        // setting the adjacent pawn piece as under threat
                        // only move in chess where piece can be eaten without moving to it's tile
                        piecesUnderThreat.add(board.getBoard()[x][y+d].getPiece());
                    }
                }

                player.getLegalMoves().addAll(possibleMoves);
            }
            case ROOK -> {
                if (!isAlive) return;
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
                        } else if (targetTile.getPiece().getPieceColor() != pieceColor) {
                            possibleMoves.add(targetTile);
                            piecesUnderThreat.add(targetTile.getPiece());
                            break;
                        }
                        if (!targetTile.isEmpty() && targetTile.getPiece().getPieceColor() == pieceColor) break;
                    }
                }
            }
            case KNIGHT -> {
                if (!isAlive) return;
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
                        if (!targetTile.isEmpty()) {
                            if (targetTile.getPiece().getPieceColor() != pieceColor) piecesUnderThreat.add(targetTile.getPiece());
                        }
                    }
                }
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

                    if (x+r > board.getBoard().length - 1  || x+r < 0 || y+c > board.getBoard().length - 1 || y+c < 0) continue;

                    for (int i = 1; i < board.getBoard().length; i++) {
                        if (x+r*i > board.getBoard().length - 1 || x+r*i < 0 || y+c*i > board.getBoard().length - 1 || y+c*i < 0) break;
                        Tile targetTile = board.getBoard()[x+r*i][y+c*i];
                        if (targetTile.isEmpty()) {
                            possibleMoves.add(targetTile);
                        } else if (targetTile.getPiece().getPieceColor() != pieceColor) {
                            possibleMoves.add(targetTile);
                            piecesUnderThreat.add(targetTile.getPiece());
                            break;
                        }
                        if (!targetTile.isEmpty() && targetTile.getPiece().getPieceColor() == pieceColor) break;
                    }
                }
            }
            case QUEEN -> {
                if (!isAlive) return;
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
                        } else if (targetTile.getPiece().getPieceColor() != pieceColor) {
                            possibleMoves.add(targetTile);
                            piecesUnderThreat.add(targetTile.getPiece());
                            break;
                        }
                        if (!targetTile.isEmpty() && targetTile.getPiece().getPieceColor() == pieceColor) break;
                    }
                }
            }
            case KING -> {
                if (!isAlive) return;
                int[][] directions = {
                        {1,0},
                        {1,1},
                        {1,-1},
                        {0,1},
                        {0,-1},
                        {-1,0},
                        {-1,1},
                        {-1,-1}
                };

                int x = currentTile.getRow();
                int y = currentTile.getCol();

//        boolean canCastle = !hasMoved;

                for (int[] direction : directions) {
                    int r = direction[0];
                    int c = direction[1];
                    if (x+r > board.getBoard().length - 1 || x+r < 0 || y+c > board.getBoard().length - 1 || y+c < 0) continue;
                    Tile targetTile = board.getBoard()[x+r][y+c];
                    if (targetTile.isEmpty() || targetTile.getPiece().getPieceColor() != pieceColor) {
                        if (!isThreatenedAtTile(targetTile)) {
                            possibleMoves.add(targetTile);
                            if (!targetTile.isEmpty()) {
                                if (targetTile.getPiece().getPieceColor() != pieceColor) piecesUnderThreat.add(targetTile.getPiece());
                            }
                        }
                    }
                }
                if (y + 2 <= 7) {
                    if (canKingSideCastle()) possibleMoves.add(board.getBoard()[x][y+2]);
                }
                if (y - 2 >= 0) {
                    if (canQueenSideCastle()) possibleMoves.add(board.getBoard()[x][y-2]);
                }
                for (Tile tile : possibleMoves) {
                    if (!tile.isEmpty()) {
                        if (tile.getPiece().getPieceColor() != pieceColor) {
                            piecesUnderThreat.add(tile.getPiece());
                        }
                    }
                }
            }
        }
    }

    // PAWN
    public boolean canEnPassant(int eatingDirection) {
        int x = currentTile.getRow();
        int y = currentTile.getCol();

        // checking borders of board
        if (y + eatingDirection < 0 || y + eatingDirection > board.getBoard().length - 1
                || x - 2 * player.getOpponentPlayer().getPlayerDirection() < 0) return false;

        // checking if piece next to pawn is of type pawn and is opponent's piece
        if (board.getBoard()[x][y + eatingDirection].getPiece() instanceof PawnPiece pawn &&
                pawn.getPieceColor() != pieceColor && !pawn.hasExecutedEnPassant()) {
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

    // KING
    // king moves 2 tiles rook moves 2 tiles
    public boolean canKingSideCastle() {
        // castling rules
// The king has not previously moved;
// Your chosen rook has not previously moved;
// There must be no pieces between the king and the chosen rook;
// The king is not currently in check;
// Your king must not pass through a square that is under attack by enemy pieces;
// The king must not end up in check.
        int x = currentTile.getRow();
        int y = currentTile.getCol();

        if (pieceColor == PieceColor.WHITE) {
            for (int i = 1; y+i < 7; i++) {
                if (board.getBoard()[x][7].getPiece() == null) return false;
                if (!board.getBoard()[x][y+i].isEmpty() || hasMoved || board.getBoard()[x][7].getPiece().hasMoved()
                        || isInDanger || board.getBoard()[x][y+i].isThreatenedByBlack()
                        || board.getBoard()[x][7].isThreatenedByBlack()) return false;
            }
//            // 0 is white king side rook column
//            for (int i = 1; y-i > 0; i++) {
//                if (board.getBoard()[x][0].getPiece() == null) return false;
//                if (!board.getBoard()[x][y-i].isEmpty() || hasMoved || board.getBoard()[x][0].getPiece().hasMoved()
//                        || isInDanger || board.getBoard()[x][y-i].isThreatenedByBlack()) return false;
//            }
        } else {
            for (int i = 1; y+i < 7; i++) {
                if (board.getBoard()[x][7].getPiece() == null) return false;
                if (!board.getBoard()[x][y+i].isEmpty() || hasMoved || board.getBoard()[x][7].getPiece().hasMoved()
                        || isInDanger || board.getBoard()[x][y+i].isThreatenedByWhite()
                        || board.getBoard()[x][7].isThreatenedByWhite()) return false;
            }
            // 7 is black king side rook column
//            for (int i = 1; y+i < 7; i++) {
//                if (board.getBoard()[x][7].getPiece() == null) return false;
//                if (!board.getBoard()[x][y+i].isEmpty() || hasMoved || board.getBoard()[x][7].getPiece().hasMoved()
//                        || isInDanger || board.getBoard()[x][y+i].isThreatenedByWhite()) return false;
//            }
        }
        // just move the 2 pieces
        // logic will be handled on generateTilesToMoveTo() method
        return true;
//        tilesToMoveTo.add(board.getBoard()[x][y+2]);
//        moveToTile(board.getBoard()[x][y+2]);
//        rookPiece.moveToTile(board.getBoard()[x][rookPiece.getCurrentTile().getCol() - 2]);
    }

    // king moves 2 tiles rook moves 3 tiles
    public boolean canQueenSideCastle() {
        // castling rules
// The king has not previously moved;
// Your chosen rook has not previously moved;
// There must be no pieces between the king and the chosen rook;
// The king is not currently in check;
// Your king must not pass through a square that is under attack by enemy pieces;
// The king must not end up in check.
        int x = currentTile.getRow();
        int y = currentTile.getCol();

        if (pieceColor == PieceColor.BLACK) {
            for (int i = 1; y-i > 0; i++) {
                if (board.getBoard()[x][0].getPiece() == null) return false;
                if (!board.getBoard()[x][y-i].isEmpty() || hasMoved || board.getBoard()[x][0].getPiece().hasMoved()
                        || isInDanger || board.getBoard()[x][y-i].isThreatenedByBlack()) return false;
            }
            // 0 is black queen side rook column
            for (int i = 1; y-i > 0; i++) {
                if (board.getBoard()[x][0].getPiece() == null) return false;
                if (!board.getBoard()[x][y-i].isEmpty() || hasMoved || board.getBoard()[x][0].getPiece().hasMoved()
                        || isInDanger || board.getBoard()[x][y-i].isThreatenedByWhite()) return false;
            }
        } else {
            for (int i = 1; y-i > 0; i++) {
                if (board.getBoard()[x][0].getPiece() == null) return false;
                if (!board.getBoard()[x][y-i].isEmpty() || hasMoved || board.getBoard()[x][0].getPiece().hasMoved()
                        || isInDanger || board.getBoard()[x][y-i].isThreatenedByWhite()) return false;
            }
//            // 7 is white queen side rook column
//            for (int i = 1; y+i < 7; i++) {
//                if (board.getBoard()[x][7].getPiece() == null) return false;
//                if (!board.getBoard()[x][y+i].isEmpty() || hasMoved || board.getBoard()[x][7].getPiece().hasMoved()
//                        || isInDanger || board.getBoard()[x][y+i].isThreatenedByBlack()) return false;
//            }
        }
        // just move the 2 pieces
        // logic will be handled on generateTilesToMoveTo() method
        return true;
//        tilesToMoveTo.add(board.getBoard()[x][y+2]);
//        moveToTile(board.getBoard()[x][y-2]);
//        rookPiece.moveToTile(board.getBoard()[x][rookPiece.getCurrentTile().getCol() + 3]);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public ArrayList<Tile> getPossibleMoves() {
        return possibleMoves;
    }

    @Override
    public ArrayList<Piece> getPiecesUnderThreat() {
        return piecesUnderThreat;
    }

    @Override
    public Stack<Tile> getHistoryMoves() {
        return historyMoves;
    }

    @Override
    public Tile getLastTile() {
        return lastTile;
    }

    @Override
    public void setLastTile(Tile lastTile) {
        this.lastTile = lastTile;
    }

    @Override
    public Stack<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public void setCapturedPieces(Stack<Piece> capturedPieces) {
        this.capturedPieces = capturedPieces;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public int getPieceCounter() {
        return pieceCounter;
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean isInDanger() {
        return isInDanger;
    }

    public void setInDanger(boolean inDanger) {
        isInDanger = inDanger;
    }

    @Override
    public Tile getCurrentTile() {
        return currentTile;
    }

    @Override
    public void setCurrentTile(Tile currentTile) {
        this.currentTile = currentTile;
    }

    @Override
    public PieceColor getPieceColor() {
        return pieceColor;
    }

    @Override
    public void setPieceColor(PieceColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    @Override
    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @Override
    public Icon getImageIcon() {
        return imageIcon;
    }

    @Override
    public void setImageIcon(Icon imageIcon) {
        this.imageIcon = imageIcon;
    }

    @Override
    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public Tile getEnPassantTile() {
        return enPassantTile;
    }

    public void setEnPassantTile(Tile enPassantTile) {
        this.enPassantTile = enPassantTile;
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

    public void setKingSide(boolean kingSide) {
        isKingSide = kingSide;
    }

    public boolean isQueenSide() {
        return isQueenSide;
    }

    public void setQueenSide(boolean queenSide) {
        isQueenSide = queenSide;
    }

    public Tile getKingSideCastleTile() {
        return kingSideCastleTile;
    }

    public void setKingSideCastleTile(Tile kingSideCastleTile) {
        this.kingSideCastleTile = kingSideCastleTile;
    }

    public Tile getQueenSideCastleTile() {
        return queenSideCastleTile;
    }

    public void setQueenSideCastleTile(Tile queenSideCastleTile) {
        this.queenSideCastleTile = queenSideCastleTile;
    }

    @Override
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean getIsInDanger() {
        return isInDanger;
    }

    @Override
    public Tile getLastMove() {
        if (historyMoves.size() == 0) return null;
        return historyMoves.peek();
    }

    @Override
    public boolean canMove() {
        return possibleMoves.size() != 0;
    }

    @Override
    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    @Override
    public void setIsInDanger(boolean isInDanger) {
        this.isInDanger = isInDanger;
    }

    @Override
    public Tile getCurrentTileProperty() {
        return null;
    }

    @Override
    public ObjectProperty<Tile> currentTilePropertyProperty() {
        return null;
    }

    @Override
    public void setCurrentTileProperty(Tile currentTileProperty) {
        this.currentTileProperty.setValue(currentTileProperty);
    }

    @Override
    public Piece getLastPieceEaten() {
        if (capturedPieces.size() == 0) return null;
        return capturedPieces.peek();
    }

    @Override
    public boolean isThreatenedAtTile(Tile tile) {
        if (pieceColor == PieceColor.WHITE) {
            return tile.isThreatenedByBlack();
        }
        if (pieceColor == PieceColor.BLACK) {
            return tile.isThreatenedByWhite();
        }
        return false;
    }

    @Override
    public boolean isTileAvailable(Tile tile) {
        if (tile.isEmpty()) {
            return true;
        } else return tile.getPiece().getPieceColor() != pieceColor;
    }

    @Override
    public void refresh() {
        if (possibleMoves.size() != 0) {
            possibleMoves.clear();
        }
        if (piecesUnderThreat.size() != 0) {
            piecesUnderThreat.clear();
        }
        generateMoves();
    }

    @Override
    public boolean equals(Piece piece) {
        return piece.getPieceType() == pieceType && piece.getCurrentTile() == currentTile;
    }

    @Override
    public Object[] getAllFields() {
        return allFields;
    }

    @Override
    public List<Move> getMoves() {
        return null;
    }
}
