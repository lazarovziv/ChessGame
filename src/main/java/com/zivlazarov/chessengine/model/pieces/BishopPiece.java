package com.zivlazarov.chessengine.model.pieces;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.MyObservable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

//import static com.zivlazarov.chessengine.ui.Game.createImageView;

public class BishopPiece implements Piece, Cloneable {

    private Player player;

    private MyObservable observable;

    private ObjectProperty<Tile> currentTileProperty;

    private PieceType pieceType;

    private final ArrayList<Move> moves;
    private final ArrayList<Tile> possibleMoves;
    private final ArrayList<Piece> piecesUnderThreat;
    private final Stack<Tile> historyMoves;
    private Tile lastTile;
    private Stack<Piece> capturedPieces;
    private final Board board;
    private String name;
    private final int pieceCounter;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private Tile currentTile;
    private PieceColor pieceColor;
    private String imageName;
    private Icon imageIcon;

    private int value = 3;

    private final Object[] allFields;

    public BishopPiece(Player player, Board board, PieceColor pc, Tile initTile, int pieceCounter) {
        this.player = player;
        this.board = board;

//        name = "B";
        pieceColor = pc;
        possibleMoves = new ArrayList<>();
        piecesUnderThreat = new ArrayList<>();
        historyMoves = new Stack<Tile>();
        capturedPieces = new Stack<>();
        moves = new ArrayList<>();

        currentTile = initTile;
        lastTile = currentTile;

        this.pieceCounter = pieceCounter;

        if (pieceColor == PieceColor.BLACK) {
            name = "bB";
            imageName = "blackBishop.png";
        }
        if (pieceColor == PieceColor.WHITE) {
            name = "wB";
            imageName = "whiteBishop.png";
        }
        player.addPieceToAlive(this);

        currentTile.setPiece(this);

        currentTileProperty = new SimpleObjectProperty<>(this, "currentTile", currentTile);

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

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void setId(int id) {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isAlive() {
        return !isAlive;
    }

    @Override
    public boolean getIsInDanger() {
        return false;
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

    public int getPieceCounter() {
        return pieceCounter;
    }

    @Override
    public void setName(String name) {
        this.name = name;
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

    public String getImageName() {
        return imageName;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setCurrentTile(Tile currentTile) {
        this.currentTile = currentTile;
        if (currentTile == null) return;
        currentTile.setPiece(this);
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
    public boolean canMove() {
        return possibleMoves.size() != 0;
    }

    @Override
    public boolean hasMoved() {
        return false;
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

    public Tile getCurrentTileProperty() {
        return currentTileProperty.get();
    }

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