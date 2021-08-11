package com.zivlazarov.chessengine.model.pieces;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;

// worst case scenario, revert this back to an interface and apply it on all pieces
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "piece")
public abstract class Piece implements Cloneable, Serializable {

    @Serial
    static final long serialVersionUID = 2L;

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    protected int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    protected Player player;

    protected String name;

    protected boolean isAlive;

    protected PieceColor pieceColor;

//    @OneToMany(targetEntity = Tile.class, mappedBy = "piece")
    // TODO: change type to List
    @Transient
    protected final Stack<Tile> historyMoves;

    protected int value;

    protected int pieceCounter;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_tile_id", referencedColumnName = "id")
    protected Tile currentTile;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "last_tile_id", referencedColumnName = "id")
    @Transient
    protected Tile lastTile;

    protected String imageName;

    protected PieceType pieceType;

    protected boolean hasMoved;

    @Transient
    protected Board board;
    @Transient
    protected boolean isInDanger;
    @Transient
    protected final Set<Move> moves;
    @Transient
    protected final ArrayList<Tile> possibleMoves;
    @Transient
    protected final ArrayList<Piece> piecesUnderThreat;
    @Transient
    protected final Stack<Piece> capturedPieces;

    public Piece() {
        moves = new HashSet<>();
        possibleMoves = new ArrayList<>();
        piecesUnderThreat = new ArrayList<>();
        capturedPieces = new Stack<>();
        historyMoves = new Stack<>();

        isAlive = true;
        isInDanger = false;
        hasMoved = false;
    }

    public void refresh() {
        reset();
        generateMoves();
    }

    public abstract void generateMoves();

    public int getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setIsAlive(boolean alive) {
        isAlive = alive;
    }

    public PieceColor getPieceColor() {
        return pieceColor;
    }

    public void setPieceColor(PieceColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    public Stack<Tile> getHistoryMoves() {
        return historyMoves;
    }

    public Tile getLastMove() {
        return historyMoves.peek();
    }

    public Piece getLastCapturedPiece() {
        return capturedPieces.peek();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getPieceCounter() {
        return pieceCounter;
    }

    public void setPieceCounter(int pieceCounter) {
        this.pieceCounter = pieceCounter;
    }

    public Tile getCurrentTile() {
        return currentTile;
    }

    public void setCurrentTile(Tile currentTile) {
        this.currentTile = currentTile;
        if (currentTile == null) return;
        currentTile.setPiece(this);
    }

    public Tile getLastTile() {
        return lastTile;
    }

    public void setLastTile(Tile lastTile) {
        this.lastTile = lastTile;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public Set<Move> getMoves() {
        return moves;
    }

    public boolean isInDanger() {
        return isInDanger;
    }

    public void setIsInDanger(boolean inDanger) {
        isInDanger = inDanger;
    }

    public ArrayList<Tile> getPossibleMoves() {
        return possibleMoves;
    }

    public ArrayList<Piece> getPiecesUnderThreat() {
        return piecesUnderThreat;
    }

    public Stack<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public boolean isTileAvailable(Tile tile) {
        if (tile.isEmpty()) {
            return true;
        } else return tile.getPiece().getPieceColor() != pieceColor;
    }

    public boolean canMove() {
        return moves.size() > 0;
    }

    public void reset() {
        if (possibleMoves.size() != 0) {
            possibleMoves.clear();
        }
        if (piecesUnderThreat.size() != 0) {
            piecesUnderThreat.clear();
        }
        if (moves.size() != 0) {
            moves.clear();
        }
    }
}

//    public abstract int getId();
//    public abstract void setId(int id);
//
//    public abstract String getName();
//    public abstract boolean isAlive();
//    public abstract boolean getIsInDanger();
//    public abstract List<Tile> getPossibleMoves();
//    public abstract PieceColor getPieceColor();
//    public abstract Tile getCurrentTile();
//    public abstract Stack<Tile> getHistoryMoves();
//    public abstract Tile getLastMove();
//    public abstract List<Piece> getPiecesUnderThreat();
//    public abstract String getImageName();
//    public abstract int getPieceCounter();
//
//    public abstract Tile getLastTile();
//
//    public abstract Set<Move> getMoves();
//
//    public abstract boolean canMove();
//
//    public abstract void setName(String name);
//    public abstract void setIsAlive(boolean isAlive);
//    public abstract void setIsInDanger(boolean isInDanger);
//    public abstract void setPieceColor(PieceColor pieceColor);
//    public abstract void setCurrentTile(Tile tile);
//
//    public abstract void setPieceType(PieceType pieceType);
//    public abstract PieceType getPieceType();
//
//    public abstract void setLastTile(Tile tile);
//
//    public abstract Piece getLastPieceEaten();
//
//    public abstract Stack<Piece> getCapturedPieces();
//
//    public abstract void generateMoves();
//
//    public abstract boolean isThreatenedAtTile(Tile tile);
//
//    public abstract boolean isTileAvailable(Tile tile);
//
//    public abstract void refresh();
//
//    public abstract int getValue();
//
//    public abstract boolean hasMoved();
//
//    public abstract boolean equals(Piece piece);
//
//    public abstract void setPlayer(Player player);
//    public abstract Player getPlayer();
