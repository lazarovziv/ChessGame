package com.zivlazarov.chessengine.model.pieces;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
//import javafx.scene.image.ImageView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

//import static com.zivlazarov.chessengine.ui.Game.createImageView;

public class RookPiece implements Piece, Cloneable {

    private Player player;

    private ObjectProperty<Tile> currentTileProperty;

    private PieceType pieceType;

    private final ArrayList<Move> moves;
    private final ArrayList<Tile> possibleMoves;
    private final ArrayList<Piece> piecesUnderThreat;
    private final Stack<Tile> historyMoves;
    private Tile lastTile;
    private final Stack<Piece> capturedPieces;
    private final Board board;
    private String name;
    private final int pieceCounter;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private boolean hasMoved;
    private Tile currentTile;
    private PieceColor pieceColor;
    private String imageName;

    private Tile kingSideCastlingTile = null;
    private Tile queenSideCastlingTile = null;
    private final boolean isKingSide;
    private final boolean isQueenSide;
    private Icon imageIcon;

    private int value = 5;

    private final Object[] allFields;

    public RookPiece(Player player, Board board, PieceColor pc, Tile initTile, boolean isKingSide, int pieceCounter) {
        this.player = player;
        this.board = board;

//        name = 'R';
        pieceColor = pc;
        possibleMoves = new ArrayList<Tile>();
        piecesUnderThreat = new ArrayList<>();
        historyMoves = new Stack<>();
        capturedPieces = new Stack<>();
        moves = new ArrayList<>();

        hasMoved = false;

        currentTile = initTile;
        lastTile = currentTile;

        this.pieceCounter = pieceCounter;
        this.isKingSide = isKingSide;
        this.isQueenSide = !isKingSide;

        if (pieceColor == PieceColor.BLACK) {
            name = "bR";
            imageName = "blackRook.png";
        }
        if (pieceColor == PieceColor.WHITE) {
            name = "wR";
            imageName = "whiteRook.png";
        }

        if (isKingSide) {
            kingSideCastlingTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() - 2];
        } else queenSideCastlingTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() + 3];

        player.addPieceToAlive(this);

        currentTile.setPiece(this);

        currentTileProperty = new SimpleObjectProperty<>(this, "currentTile", currentTile);
//        generateTilesToMoveTo();
        allFields = new Object[] {player, pieceType, possibleMoves, piecesUnderThreat,
                historyMoves, lastTile, capturedPieces,
                name, pieceCounter, isAlive, isInDanger, currentTile,
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
        if (moves.size() != 0) {
            moves.clear();
        }
        generateMoves();
    }

    @Override
    public void generateMoves() {
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
                    Move move = new Move.Builder()
                            .board(board)
                            .player(player)
                            .movingPiece(this)
                            .targetTile(targetTile)
                            .build();
                    moves.add(move);
                    possibleMoves.add(targetTile);
                } else if (targetTile.getPiece().getPieceColor() != pieceColor) {
                    Move move = new Move.Builder()
                            .board(board)
                            .player(player)
                            .movingPiece(this)
                            .targetTile(targetTile)
                            .build();
                    moves.add(move);
                    possibleMoves.add(targetTile);
                    piecesUnderThreat.add(targetTile.getPiece());
                    break;
                }
                if (!targetTile.isEmpty() && targetTile.getPiece().getPieceColor() == pieceColor) break;
            }
        }
        player.getLegalMoves().addAll(possibleMoves);
        player.getMoves().addAll(moves);
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
        return isInDanger;
    }

//    @Override
//    public ImageView getImageIcon() {
//        return imageIcon;
//    }

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
    public void setPieceColor(PieceColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    @Override
    public Stack<Tile> getHistoryMoves() {
        return historyMoves;
    }

    @Override
    public ArrayList<Piece> getPiecesUnderThreat() {
        return piecesUnderThreat;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void setCurrentTile(Tile currentTile) {
        this.currentTile = currentTile;
        if (currentTile == null) return;
        currentTile.setPiece(this);
    }

    public Tile getKingSideCastlingTile() {
        return kingSideCastlingTile;
    }

    public Tile getQueenSideCastlingTile() {
        return queenSideCastlingTile;
    }

    public void setHasMoved(boolean moved) {
        hasMoved = moved;
    }

    @Override
    public Tile getCurrentTile() {
        return currentTile;
    }

    public int getPieceCounter() {
        return pieceCounter;
    }

    public String getImageName() {
        return imageName;
    }

    @Override
    public int getValue() {
        return value;
    }

    public boolean isKingSide() {
        return isKingSide;
    }

    public boolean isQueenSide() {
        return isQueenSide;
    }

    @Override
    public boolean isThreatenedAtTile(Tile tile) {
        if (pieceColor == PieceColor.WHITE) {
            if (tile.isThreatenedByBlack()) return true;
            else return false;
        }
        if (pieceColor == PieceColor.BLACK) {
            if (tile.isThreatenedByWhite()) return true;
            else return false;
        }
        return false;
    }

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
            // add tile to history of moves

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
    public Tile getLastMove() {
        if (historyMoves.size() == 0) return null;
        return historyMoves.peek();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Piece piece) {
        return currentTile.getRow() == piece.getCurrentTile().getRow() &&
                currentTile.getCol() == piece.getCurrentTile().getCol() &&
                (name + pieceCounter).equals(piece.getName() + pieceCounter);
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

    @Override
    public List<Move> getMoves() {
        return moves;
    }
}
