package com.zivlazarov.chessengine.model.pieces;

import com.zivlazarov.chessengine.model.utils.Pair;
import com.zivlazarov.chessengine.model.utils.board.Board;
import com.zivlazarov.chessengine.model.utils.player.Piece;
import com.zivlazarov.chessengine.model.utils.board.PieceColor;
import com.zivlazarov.chessengine.model.utils.board.Tile;
import com.zivlazarov.chessengine.model.utils.player.Player;
//import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

//import static com.zivlazarov.chessengine.ui.Game.createImageView;

public class BishopPiece implements Piece, Observer, Cloneable {

    private Player player;

    private final ArrayList<Tile> tilesToMoveTo;
    private final ArrayList<Piece> piecesUnderThreat;
    private final Stack<Tile> historyMoves;
    private Stack<Piece> piecesEaten;
    private final Board board;
    private String name;
    private int pieceCounter;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private Tile currentTile;
    private PieceColor pieceColor;
//    private ImageView imageIcon;

    public BishopPiece(Player player, Board board, PieceColor pc, Tile initTile, int pieceCounter) {
        this.player = player;
        this.board = board;

//        name = "B";
        pieceColor = pc;
        tilesToMoveTo = new ArrayList<>();
        piecesUnderThreat = new ArrayList<>();
        historyMoves = new Stack<>();
        piecesEaten = new Stack<>();

        currentTile = initTile;
        this.pieceCounter = pieceCounter;

        if (pieceColor == PieceColor.BLACK) {
            name = "bB";
            board.getBlackAlivePieces().put(name + pieceCounter, this);
        }
        if (pieceColor == PieceColor.WHITE) {
            name = "wB";
            board.getWhiteAlivePieces().put(name + pieceCounter, this);
        }
        player.addPieceToAlive(this);

        currentTile.setPiece(this);
        historyMoves.push(currentTile);
        // need to be called after all pieces have been initialized
//        generateTilesToMoveTo();
    }

    @Override
    public void refresh() {
        if (tilesToMoveTo.size() != 0) {
            tilesToMoveTo.clear();
        }
        generateTilesToMoveTo();
    }

    @Override
    public void generateTilesToMoveTo() {
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
                    tilesToMoveTo.add(targetTile);
                } else if (targetTile.getPiece().getPieceColor() != pieceColor) {
                    tilesToMoveTo.add(targetTile);
                    piecesUnderThreat.add(targetTile.getPiece());
                    break;
                }
                if (!targetTile.isEmpty() && targetTile.getPiece().getPieceColor() == pieceColor) break;
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean getIsAlive() {
        return isAlive;
    }

    @Override
    public boolean getIsInDanger() {
        return false;
    }

    @Override
    public ArrayList<Tile> getTilesToMoveTo() {
        return tilesToMoveTo;
    }

    @Override
    public PieceColor getPieceColor() {
        return pieceColor;
    }

//    @Override
//    public ImageView getImageIcon() {
//        return imageIcon;
//    }

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

    @Override
    public void setCurrentTile(Tile currentTile) {
        this.currentTile = currentTile;
        if (currentTile == null) return;
        currentTile.setPiece(this);
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
        if (tilesToMoveTo.contains(tile)) {
            // clear current tile
            currentTile.setPiece(null);
            // check if tile has opponent's piece and if so, mark as not alive
            if (!tile.isEmpty()) {
                piecesEaten.push(tile.getPiece());
                tile.getPiece().setIsAlive(false);
                if (pieceColor == PieceColor.BLACK) {
                    board.getWhiteAlivePieces().remove(tile.getPiece().getName() + pieceCounter);
                } else if (pieceColor == PieceColor.WHITE) {
                    board.getBlackAlivePieces().remove(tile.getPiece().getName() + pieceCounter);
                }
                player.getOpponentPlayer().addPieceToDead(tile.getPiece());
                tile.setPiece(null);
            }
            // change to selected tile
            currentTile = tile;
            // set the piece at selected tile
            currentTile.setPiece(this);
            tilesToMoveTo.clear();
            // add target tile to history of moves
            historyMoves.push(currentTile);

            generateTilesToMoveTo();
        }
    }

    @Override
    public void unmakeLastMove() {
        if (historyMoves.size() == 0) return;
        Tile previousTile = historyMoves.pop();

        if (piecesEaten.size() > 0) {
            if (piecesEaten.peek().getHistoryMoves().peek().equals(currentTile)) {
                Piece piece = piecesEaten.pop();
                currentTile.setPiece(piece);
                piece.setIsAlive(true);
                player.getOpponentPlayer().addPieceToAlive(piece);
            }
        } else currentTile.setPiece(null);

        currentTile = previousTile;
        currentTile.setPiece(this);
        tilesToMoveTo.clear();
        generateTilesToMoveTo();
    }

    @Override
    public boolean isTileAvailable(Tile tile) {
        if (tile.isEmpty()) {
            return true;
        } else return tile.getPiece().getPieceColor() != pieceColor;
    }

    @Override
    public boolean canMove() {
        return tilesToMoveTo.size() != 0;
    }

    @Override
    public boolean hasMoved() {
        return false;
    }

    @Override
    public Piece getLastPieceEaten() {
        if (piecesEaten.size() == 0) return null;
        return piecesEaten.peek();
    }

    @Override
    public Pair<Tile, Tile> getLastMove() {
        if (historyMoves.size() == 0) return null;
        return new Pair<Tile, Tile>(historyMoves.peek(), currentTile);
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
