package com.zivlazarov.chessengine.model.pieces;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

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
    @Transient
    protected final Stack<Tile> historyMoves;

    protected int value;

    protected int pieceCounter;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_tile_id", referencedColumnName = "id")
    protected Tile currentTile;

    protected int currentRow;
    protected int currentCol;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "last_tile_id", referencedColumnName = "id")
    @Transient
    protected Tile lastTile;

    protected int lastRow;
    protected int lastCol;

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

    protected double[][] strongTiles;

    protected int pieceIndex;

    public Piece() {
        moves = new HashSet<>();
        possibleMoves = new ArrayList<>();
        piecesUnderThreat = new ArrayList<>();
        capturedPieces = new Stack<>();
        historyMoves = new Stack<>();

        isAlive = true;
        isInDanger = false;
        hasMoved = false;

//        strongTiles = new double[8][8];
    }

    public void refresh() {
        reset();
        generateMoves();
    }

    public abstract void generateMoves();

    public abstract Piece clone(Board newBoard, Player player);

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
        if (currentTile == null) return; /*throw new NullPointerException("Tile in null"); */
        currentTile.setPiece(this);
        currentRow = currentTile.getRow();
        currentCol = currentTile.getCol();
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }

    public int getCurrentCol() {
        return currentCol;
    }

    public void setCurrentCol(int currentCol) {
        this.currentCol = currentCol;
    }

    public Tile getLastTile() {
        return lastTile;
    }

    public void setLastTile(Tile lastTile) {
        this.lastTile = lastTile;
        lastRow = lastTile.getRow();
        lastCol = lastTile.getCol();
    }

    public int getLastRow() {
        return lastRow;
    }

    public void setLastRow(int lastRow) {
        this.lastRow = lastRow;
    }

    public int getLastCol() {
        return lastCol;
    }

    public void setLastCol(int lastCol) {
        this.lastCol = lastCol;
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

    public int getRow() {
        return currentRow;
    }

    public int getCol() {
        return currentCol;
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

    public double[][] getStrongTiles() {
        return strongTiles;
    }

    public int getPieceIndex() {
        return pieceIndex;
    }

    public void setPieceIndex(int pieceIndex) {
        this.pieceIndex = pieceIndex;
    }

    public boolean isTileAvailable(Tile tile) {
        if (tile.isEmpty()) {
            return true;
        } else return tile.getPiece().getPieceColor() != pieceColor;
    }

    public void setBoard(Board newBoard) {
        this.board = newBoard;
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

    public double[][] revertStrongTiles(double[][] tiles) {
        double[][] newTiles = new double[8][8];

        int rowIndex = 0;

        for (int r = tiles.length - 1; r >= 0; r--) {
            for (int c = 0; c < tiles.length; c++) {
                newTiles[rowIndex][c] = -1 * tiles[r][c];
            }
            rowIndex++;
        }
        return newTiles;
    }
}