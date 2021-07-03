package com.zivlazarov.chessengine.model.pieces;

import com.zivlazarov.chessengine.model.utils.Pair;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.player.Player;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

//import static com.zivlazarov.chessengine.ui.Game.createImageView;

public class PawnPiece implements Piece, Cloneable {

    private final ArrayList<Tile> possibleMoves;
    private final ArrayList<Piece> piecesUnderThreat;
    private final Stack<Tile> historyMoves;
    private Tile lastTile;
    private Stack<Piece> piecesEaten;
    private final Board board;
    private Player player;
    private String name;
    private int pieceCounter;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private Tile currentTile;
    private PieceColor pieceColor;
    private String imageName;
    private boolean hasMoved = false;

    private Tile enPassantTile;
    private Icon imageIcon;

    public PawnPiece(Player player, Board board, PieceColor pc, Tile initTile, int pieceCounter) {
        this.player = player;
        this.board = board;

//        name = 'P';
        pieceColor = pc;
        possibleMoves = new ArrayList<Tile>();
        piecesUnderThreat = new ArrayList<>();
        historyMoves = new Stack<>();
        piecesEaten = new Stack<>();

        currentTile = initTile;
        lastTile = currentTile;

        this.pieceCounter = pieceCounter;

        if (pieceColor == PieceColor.BLACK) {
            name = "bP";
            imageName = "blackPawn.png";
        }
        if (pieceColor == PieceColor.WHITE) {
            name = "wP";
            imageName = "whitePawn.png";
        }
        player.addPieceToAlive(this);

        currentTile.setPiece(this);
//        generateTilesToMoveTo();
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

    public boolean canEnPassant(int eatingDirection) {
        int x = currentTile.getRow();
        int y = currentTile.getCol();

        // checking borders of board
        if (y + eatingDirection < 0 || y + eatingDirection > board.getBoard().length - 1
        || x - 2 * player.getOpponentPlayer().getPlayerDirection() < 0) return false;

        // checking if piece next to pawn is of type pawn and is opponent's piece
        if (board.getBoard()[x][y + eatingDirection].getPiece() instanceof PawnPiece &&
                board.getBoard()[x][y + eatingDirection].getPiece().getPieceColor() != pieceColor) {
            PawnPiece pawn = (PawnPiece) board.getBoard()[x][y + eatingDirection].getPiece();
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
        return false;
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
    public Stack<Tile> getHistoryMoves() {
        return historyMoves;
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

    public void setHasMoved(boolean moved) {
        hasMoved = moved;
    }

    public String getImageName() {
        return imageName;
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
            if (enPassantTile != null) {
                if (tile.equals(enPassantTile)) {
                    historyMoves.push(enPassantTile);
                    currentTile = tile;
                    currentTile.setPiece(this);
                    possibleMoves.clear();
                    piecesEaten.push(
                            board.getBoard()[enPassantTile.getRow() - player.getPlayerDirection()][enPassantTile.getCol()].getPiece());
                    player.getOpponentPlayer().addPieceToDead(
                            board.getBoard()[enPassantTile.getRow() - player.getPlayerDirection()][enPassantTile.getCol()].getPiece());
                    generateMoves();
                    return;
                }
            }

            // check if tile has opponent's piece and if so, mark as not alive
            if (!tile.isEmpty()) {
                piecesEaten.push(tile.getPiece());
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
            // add target tile to history of moves

            if (!hasMoved) hasMoved = true;
            generateMoves();
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
        possibleMoves.clear();
        generateMoves();
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


    public Tile getEnPassantTile() {
        return enPassantTile;
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
        if (piecesEaten.size() == 0) return null;
        return piecesEaten.peek();
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
    public Stack<Piece> getPiecesEaten() {
        return piecesEaten;
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
}
