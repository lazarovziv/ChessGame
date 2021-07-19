package com.zivlazarov.chessengine.model.pieces;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.player.Player;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
//import javafx.scene.image.ImageView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Stack;

//import static com.zivlazarov.chessengine.ui.Game.createImageView;

public class KingPiece implements Piece, Cloneable {

    private Player player;

    private ObjectProperty<Tile> currentTileProperty;

    private PieceType pieceType;

    private final ArrayList<Tile> possibleMoves;
    private final ArrayList<Piece> piecesUnderThreat;
    private final Stack<Tile> historyMoves;
    private Tile lastTile;
    private Stack<Piece> capturedPieces;
    private final Board board;
    private String name;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private boolean hasMoved;
    private Tile currentTile;
    private PieceColor pieceColor;
    private String imageName;
    private Icon imageIcon;

    private Tile kingSideCastleTile;
    private Tile queenSideCastleTile;

    private int value = 0;

    private final Object[] allFields;

    public KingPiece(Player player, Board board, PieceColor pc, Tile initTile) {
        this.player = player;
        this.board = board;

//        name = 'K';
        pieceColor = pc;
        possibleMoves = new ArrayList<>();
        piecesUnderThreat = new ArrayList<>();
        historyMoves = new Stack<>();
        capturedPieces = new Stack<>();

        hasMoved = false;

        currentTile = initTile;
        lastTile = currentTile;

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

        player.addPieceToAlive(this);

        currentTile.setPiece(this);

        currentTileProperty = new SimpleObjectProperty<>(this, "currentTile", currentTile);
//        generateTilesToMoveTo();
        allFields = new Object[] {player, pieceType, possibleMoves, piecesUnderThreat,
                historyMoves, lastTile, capturedPieces,
                name, isAlive, isInDanger, currentTile,
                pieceColor, imageName, imageIcon};
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
    public void generateMoves() {
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

        if (pieceColor == PieceColor.WHITE) {
            for (int i = 1; y+i < 7; i++) {
                if (board.getBoard()[x][7].getPiece() == null) return false;
                if (!board.getBoard()[x][y+i].isEmpty() || hasMoved || board.getBoard()[x][7].getPiece().hasMoved()
                || isInDanger || board.getBoard()[x][y+i].isThreatenedByBlack()
                || board.getBoard()[x][7].isThreatenedByBlack()) return false;
            }
        } else {
            for (int i = 1; y+i < 7; i++) {
                if (board.getBoard()[x][7].getPiece() == null) return false;
                if (!board.getBoard()[x][y+i].isEmpty() || hasMoved || board.getBoard()[x][7].getPiece().hasMoved()
                        || isInDanger || board.getBoard()[x][y+i].isThreatenedByWhite()
                        || board.getBoard()[x][7].isThreatenedByWhite()) return false;
            }
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
        }
        // just move the 2 pieces
        // logic will be handled on generateMoves() method
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isAlive() {
        return !isAlive;
    }

    @Override
    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    @Override
    public boolean getIsInDanger() {
        return isThreatenedAtTile(currentTile);
    }

    @Override
    public void setIsInDanger(boolean isInDanger) {
        this.isInDanger = isInDanger;
    }

    @Override
    public ArrayList<Tile> getPossibleMoves() {
        return possibleMoves;
    }

    @Override
    public PieceColor getPieceColor() {
        return pieceColor;
    }

    @Override
    public Tile getCurrentTile() {
        return currentTile;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void setPieceColor(PieceColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    public void setHasMoved(boolean moved) {
        hasMoved = moved;
    }

    @Override
    public Stack<Tile> getHistoryMoves() {
        return historyMoves;
    }

    public Tile getKingSideCastleTile() {
        return kingSideCastleTile;
    }

    public Tile getQueenSideCastleTile() {
        return queenSideCastleTile;
    }

    @Override
    public Tile getLastMove() {
        if (historyMoves.size() == 0) return null;
        return historyMoves.peek();
    }

    @Override
    public ArrayList<Piece> getPiecesUnderThreat() {
        return piecesUnderThreat;
    }

    @Override
    public void setCurrentTile(Tile currentTile) {
        this.currentTile = currentTile;
        if (currentTile == null) return;
        currentTile.setPiece(this);
    }

    public String getImageName() {
        return imageName;
    }

    @Override
    public int getValue() {
        return value;
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

//    @Override
//    public void setImageIcon(ImageView imageIcon) {
//        this.imageIcon = imageIcon;
//    }

    @Override
    public void moveToTile(Tile tile) {
        if (!player.getLegalMoves().contains(tile)) return;
        if (possibleMoves.contains(tile)) {
            // clear current tile
            currentTile.setPiece(null);
            // check if tile has opponent's piece and if so, mark as not alive
            if (!tile.isEmpty()) {
                capturedPieces.push(tile.getPiece());
                tile.getPiece().setIsAlive(false);
                player.getOpponentPlayer().addPieceToDead(tile.getPiece());
                tile.setPiece(null);
            }
            historyMoves.push(tile);
            // change to selected tile
            currentTile = tile;
            // set the piece at selected tile
            currentTile.setPiece(this);
            possibleMoves.clear();
            if (!hasMoved) hasMoved = true;

            generateMoves();
        }
    }

    @Override
    public void unmakeLastMove() {
        if (historyMoves.size() == 0) return;
        Tile previousTile = historyMoves.pop();

        if (capturedPieces.size() > 0) {
            if (capturedPieces.peek().getHistoryMoves().peek().equals(currentTile)) {
                Piece piece = capturedPieces.pop();
                currentTile.setPiece(piece);
                piece.setIsAlive(true);
                player.getOpponentPlayer().addPieceToAlive(piece);
            }
        } else currentTile.setPiece(null);

        currentTile = previousTile;
        currentTile.setPiece(this);
        possibleMoves.clear();
        generateMoves();
    }

    @Override
    public boolean isTileAvailable(Tile tile) {
        if (tile.isEmpty()) {
            return true;
        } else return tile.getPiece().getPieceColor() != pieceColor;
    }

    @Override
    public boolean canMove() {
        return possibleMoves.size() != 0;
    }

    @Override
    public boolean hasMoved() {
        return hasMoved;
    }

    @Override
    public Piece getLastPieceEaten() {
        if (capturedPieces.size() == 0) return null;
        return capturedPieces.peek();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Piece piece) {
        return currentTile.getRow() == piece.getCurrentTile().getRow() &&
                currentTile.getCol() == piece.getCurrentTile().getCol() &&
                name.equals(piece.getName());
    }

    @Override
    public Stack<Piece> getCapturedPieces() {
        return capturedPieces;
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
    public Icon getImageIcon() {
        return imageIcon;
    }

    @Override
    public void setImageIcon(Icon imageIcon) {
        this.imageIcon = imageIcon;
    }

    @Override
    public Tile getCurrentTileProperty() {
        return currentTileProperty.get();
    }

    @Override
    public ObjectProperty<Tile> currentTilePropertyProperty() {
        return currentTileProperty;
    }

    public void setCurrentTileProperty(Tile currentTileProperty) {
        this.currentTileProperty.set(currentTileProperty);
    }

    @Override
    public PieceType getPieceType() {
        return pieceType;
    }

    @Override
    public void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
    }

    @Override
    public Object[] getAllFields() {
        return allFields;
    }

    @Override
    public void setPlayer(Player player)  {
        this.player = player;
    }
}
