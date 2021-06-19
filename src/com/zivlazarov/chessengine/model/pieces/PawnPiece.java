package com.zivlazarov.chessengine.model.pieces;

import com.zivlazarov.chessengine.logs.MovesLog;
import com.zivlazarov.chessengine.model.utils.Pair;
import com.zivlazarov.chessengine.model.utils.board.Board;
import com.zivlazarov.chessengine.model.utils.player.Piece;
import com.zivlazarov.chessengine.model.utils.board.PieceColor;
import com.zivlazarov.chessengine.model.utils.board.Tile;
import com.zivlazarov.chessengine.model.utils.player.Player;
//import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

//import static com.zivlazarov.chessengine.ui.Game.createImageView;

public class PawnPiece implements Piece {

    private final ArrayList<Tile> tilesToMoveTo;
    private final ArrayList<Piece> piecesUnderThreat;
    private final Stack<Pair<Tile, Tile>> historyMoves;
    private final Board board;
    private Player player;
    private String name;
    private int pieceCounter;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private Tile currentTile;
    private PieceColor pieceColor;
    private boolean hasMoved = false;

    private Tile enPassantTile;
//    private ImageView imageIcon;

    public PawnPiece(Player player, Board board, PieceColor pc, Tile initTile, int pieceCounter) {
        this.player = player;
        this.board = board;

//        name = 'P';
        pieceColor = pc;
        tilesToMoveTo = new ArrayList<Tile>();
        piecesUnderThreat = new ArrayList<>();
        historyMoves = new Stack<>();

        currentTile = initTile;
        this.pieceCounter = pieceCounter;

        if (pieceColor == PieceColor.BLACK) {
            name = "bP";
            board.getBlackAlivePieces().put(name + pieceCounter, this);
        }
        if (pieceColor == PieceColor.WHITE) {
            name = "wP";
            board.getWhiteAlivePieces().put(name + pieceCounter, this);
        }

        currentTile.setPiece(this);

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
        Map<PieceColor, Integer> map = new HashMap<>();
        map.put(PieceColor.WHITE, 1);
        map.put(PieceColor.BLACK, -1);
        int[] eatingDirections = new int[]{-1, 1};

        int x = currentTile.getRow();
        int y = currentTile.getCol();

        boolean canMoveFurther = !hasMoved;

        int direction = map.get(pieceColor);
        int longDirection = direction * 2;

        if (x + map.get(pieceColor) > board.getBoard().length - 1 || x + map.get(pieceColor) < 0) return;

        if (board.getBoard()[x + direction][y].isEmpty()) {
            tilesToMoveTo.add(board.getBoard()[x + direction][y]);
            if (canMoveFurther) {
                if (x + longDirection < 0 || x + longDirection > board.getBoard().length - 1) return;
                if (board.getBoard()[x + longDirection][y].isEmpty()) {
                    tilesToMoveTo.add(board.getBoard()[x + longDirection][y]);
                }
            }
        }
        for (int d : eatingDirections) {
            if (y + d > board.getBoard().length - 1 || y + d < 0) return;
            if (!board.getBoard()[x + direction][y + d].isEmpty() &&
                    board.getBoard()[x + direction][y + d].getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(board.getBoard()[x + direction][y + d]);
                piecesUnderThreat.add(board.getBoard()[x + direction][y + d].getPiece());
            }

            // insert en passant
            if (canEnPassant(d)) {
                tilesToMoveTo.add(board.getBoard()[x+player.getPlayerDirection()][y+d]);
                // setting the adjacent pawn piece as under threat
                // only move in chess where piece can be eaten without moving to it's tile
                piecesUnderThreat.add(board.getBoard()[x][y+d].getPiece());
            }
        }

    }

    public boolean canEnPassant(int eatingDirection) {
        int x = currentTile.getRow();
        int y = currentTile.getCol();

        if (y + eatingDirection < 0 || y + eatingDirection > board.getBoard().length - 1) return false;
        // checking if piece next to pawn is of type pawn and is opponent's piece
        if (board.getBoard()[x][y + eatingDirection].getPiece() instanceof PawnPiece &&
                board.getBoard()[x][y + eatingDirection].getPiece().getPieceColor() != pieceColor) {
            PawnPiece pawn = (PawnPiece) board.getBoard()[x][y + eatingDirection].getPiece();
            // checking to see if opponent's last move is pawn's move 2 tiles forward
            if (pawn.getPlayer().getLastMove().equals(new Pair<Tile, Tile>(
                    board.getBoard()[x - 2 * pawn.getPlayer().getPlayerDirection()][y+eatingDirection],
                    pawn.getCurrentTile()))) {
                enPassantTile = board.getBoard()[x + player.getPlayerDirection()][y + eatingDirection];
                return board.getBoard()[x + player.getPlayerDirection()][y + eatingDirection].isEmpty();
            }
        }
        return false;
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
    public boolean getIsAlive() {
        return isAlive;
    }

    @Override
    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    @Override
    public boolean getIsInDanger() {
        return false;
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
    public ArrayList<Tile> getTilesToMoveTo() {
        return tilesToMoveTo;
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
    public Tile getCurrentTile() {
        return currentTile;
    }

    public int getPieceCounter() {
        return pieceCounter;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public Stack<Pair<Tile, Tile>> getHistoryMoves() {
        return historyMoves;
    }

    @Override
    public Pair<Tile, Tile> getLastMove() {
        return historyMoves.peek();
    }

    @Override
    public ArrayList<Piece> getPiecesUnderThreat() {
        return piecesUnderThreat;
    }

//    @Override
//    public void setImageIcon(ImageView imageIcon) {
//        this.imageIcon = imageIcon;
//    }

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
        Pair<Tile, Tile> tilesPair = null;
        if (tilesToMoveTo.contains(tile)) {
            // clear current tile
            currentTile.setPiece(null);

            if (enPassantTile != null) {
                if (tile.equals(enPassantTile)) {
                    tilesPair = new Pair<>(currentTile, tile);
                    currentTile = tile;
                    currentTile.setPiece(this);
                    tilesToMoveTo.clear();
                    historyMoves.add(tilesPair);
                    board.getBoard()[enPassantTile.getRow() - player.getPlayerDirection()][enPassantTile.getCol()].setPiece(null);
                    generateTilesToMoveTo();
                    return;
                }
            }

            // check if tile has opponent's piece and if so, mark as not alive
            if (!tile.isEmpty()) {
                tile.getPiece().setIsAlive(false);
                if (pieceColor == PieceColor.BLACK) {
                    board.getWhiteAlivePieces().remove(tile.getPiece().getName() + pieceCounter);
                } else if (pieceColor == PieceColor.WHITE) {
                    board.getBlackAlivePieces().remove(tile.getPiece().getName() + pieceCounter);
                }
                tile.setPiece(null);
                tilesPair = new Pair<>(currentTile, tile);
            }
            // change to selected tile
            currentTile = tile;
            // set the piece at selected tile
            currentTile.setPiece(this);
            tilesToMoveTo.clear();
            // add target tile to history of moves
            historyMoves.add(tilesPair);

            if (!hasMoved) hasMoved = true;
            generateTilesToMoveTo();
        }
    }

    @Override
    public boolean isTileAvailable(Tile tile) {
        if (tile.isEmpty()) {
            return true;
        } else return tile.getPiece().getPieceColor() != pieceColor;
    }

//    @Override
//    public void setOnClickListener() {
////        if (!isAlive) return;
//        if (imageIcon == null) return;
//        imageIcon.setOnMouseClicked(mouseEvent -> {
//            if (tilesToMoveTo.size() == 0) return;
//            for (Tile tile : tilesToMoveTo) {
//                tile.setTileImageView(createImageView("redTile"));
//                System.out.println("[" + tile.getRow() + ", " + tile.getCol() + "]");
//            }
//        });
//    }

    @Override
    public boolean canMove() {
        return tilesToMoveTo.size() != 0;
    }

    @Override
    public boolean hasMoved() {
        return false;
    }
}
