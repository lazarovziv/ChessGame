package com.zivlazarov.chessengine.model.pieces;
import com.zivlazarov.chessengine.model.utils.Pair;
import com.zivlazarov.chessengine.model.utils.board.Board;
import com.zivlazarov.chessengine.model.utils.player.Piece;
import com.zivlazarov.chessengine.model.utils.board.PieceColor;
import com.zivlazarov.chessengine.model.utils.board.Tile;
import com.zivlazarov.chessengine.model.utils.player.Player;
//import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Stack;

//import static com.zivlazarov.chessengine.ui.Game.createImageView;

public class KingPiece implements Piece {

    private Player player;

    private final ArrayList<Tile> tilesToMoveTo;
    private final ArrayList<Piece> piecesUnderThreat;
    private final Stack<Pair<Tile, Tile>> historyMoves;
    private Stack<Pair<Piece, Tile>> piecesEaten;
    private final Board board;
    private String name;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private boolean hasMoved;
    private Tile currentTile;
    private PieceColor pieceColor;
//    private ImageView imageIcon;

    public KingPiece(Player player, Board board, PieceColor pc, Tile initTile) {
        this.player = player;
        this.board = board;

//        name = 'K';
        pieceColor = pc;
        tilesToMoveTo = new ArrayList<>();
        piecesUnderThreat = new ArrayList<>();
        historyMoves = new Stack<>();
        piecesEaten = new Stack<>();

        hasMoved = false;

        currentTile = initTile;
        if (pieceColor == PieceColor.BLACK) {
            name = "bK";
            board.getBlackAlivePieces().put(name, this);
        }
        if (pieceColor == PieceColor.WHITE) {
            name = "wK";
            board.getWhiteAlivePieces().put(name, this);
        }
        player.addPieceToAlive(this);

        currentTile.setPiece(this);

//        generateTilesToMoveTo();
    }

//    public KingPiece(Board board, PieceColor pc, Tile initTile, ImageView imageView) {
//        this.board = board;
//
////        name = 'K';
//        pieceColor = pc;
//        tilesToMoveTo = new ArrayList<>();
//
//        currentTile = initTile;
//        if (pieceColor == PieceColor.BLACK) {
//            name = "bK";
//            board.getBlackAlivePieces().put(name, this);
//        }
//        if (pieceColor == PieceColor.WHITE) {
//            name = "wK";
//            board.getWhiteAlivePieces().put(name, this);
//        }
//
//        currentTile.setPiece(this);
//        imageIcon = imageView;
//        currentTile.setPieceImageView(imageIcon);
//
////        generateTilesToMoveTo();
//    }

    @Override
    public void refresh() {
        if (tilesToMoveTo.size() != 0) {
            tilesToMoveTo.clear();
        }
        generateTilesToMoveTo();
    }

    @Override
    public void generateTilesToMoveTo() {
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
                if (pieceColor == PieceColor.WHITE) {
                    if (!targetTile.isThreatenedByBlack()) tilesToMoveTo.add(targetTile);
                } else {
                    if (!targetTile.isThreatenedByWhite()) tilesToMoveTo.add(targetTile);
                }
            }
        }
        // adding castling to tilesToMoveTo
        int whiteRookKingSideColumn = 0;
        int whiteRookQueenSideColumn = 7;
        int blackRookKingSideColumn = 7;
        int blackRookQueenSideColumn = 0;

        if (pieceColor == PieceColor.WHITE) {
            if (canKingSideCastle()) {
                tilesToMoveTo.add(board.getBoard()[x][whiteRookKingSideColumn+1]);
            }
            if (canQueenSideCastle()) {
                tilesToMoveTo.add(board.getBoard()[x][whiteRookQueenSideColumn-2]);
            }
        } else if (pieceColor == PieceColor.BLACK) {
            if (canKingSideCastle()) {
                tilesToMoveTo.add(board.getBoard()[x][blackRookKingSideColumn-1]);
            }
            if (canQueenSideCastle()) {
                tilesToMoveTo.add(board.getBoard()[x][blackRookQueenSideColumn+2]);
            }
        }

        for (Tile tile : tilesToMoveTo) {
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
            // 0 is white king side rook column
            for (int i = 1; y-i > 0; i++) {
                if (board.getBoard()[x][0].getPiece() == null) return false;
                if (!board.getBoard()[x][y-i].isEmpty() || hasMoved || board.getBoard()[x][0].getPiece().hasMoved()
                        || isInDanger || board.getBoard()[x][y-i].isThreatenedByBlack()) return false;
            }
        } else {
            // 7 is black king side rook column
            for (int i = 1; y+i < 7; i++) {
                if (board.getBoard()[x][7].getPiece() == null) return false;
                if (!board.getBoard()[x][y+i].isEmpty() || hasMoved || board.getBoard()[x][7].getPiece().hasMoved()
                        || isInDanger || board.getBoard()[x][y+i].isThreatenedByWhite()) return false;
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
            // 0 is black queen side rook column
            for (int i = 1; y-i > 0; i++) {
                if (board.getBoard()[x][0].getPiece() == null) return false;
                if (!board.getBoard()[x][y-i].isEmpty() || hasMoved || board.getBoard()[x][0].getPiece().hasMoved()
                        || isInDanger || board.getBoard()[x][y-i].isThreatenedByWhite()) return false;
            }
        } else {
            // 7 is white queen side rook column
            for (int i = 1; y+i < 7; i++) {
                if (board.getBoard()[x][7].getPiece() == null) return false;
                if (!board.getBoard()[x][y+i].isEmpty() || hasMoved || board.getBoard()[x][7].getPiece().hasMoved()
                        || isInDanger || board.getBoard()[x][y+i].isThreatenedByBlack()) return false;
            }
        }
        // just move the 2 pieces
        // logic will be handled on generateTilesToMoveTo() method
        return true;
//        tilesToMoveTo.add(board.getBoard()[x][y+2]);
//        moveToTile(board.getBoard()[x][y-2]);
//        rookPiece.moveToTile(board.getBoard()[x][rookPiece.getCurrentTile().getCol() + 3]);
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
        return isInDanger;
    }

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

//    @Override
//    public ImageView getImageIcon() { return imageIcon; }

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

    @Override
    public void setCurrentTile(Tile currentTile) {
        this.currentTile = currentTile;
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

//    @Override
//    public void setImageIcon(ImageView imageIcon) {
//        this.imageIcon = imageIcon;
//    }

    @Override
    public void moveToTile(Tile tile) {
        Pair<Tile, Tile> tilesPair = null;
        if (tilesToMoveTo.contains(tile)) {
            // clear current tile
            currentTile.setPiece(null);
            // check if tile has opponent's piece and if so, mark as not alive
            if (!tile.isEmpty()) {
                piecesEaten.push(new Pair<Piece, Tile>(tile.getPiece(), tile));
                tile.getPiece().setIsAlive(false);
                if (pieceColor == PieceColor.BLACK) {
                    board.getWhiteAlivePieces().remove(tile.getPiece().getName());
                } else if (pieceColor == PieceColor.WHITE) {
                    board.getBlackAlivePieces().remove(tile.getPiece().getName());
                }
                player.getOpponentPlayer().addPieceToDead(tile.getPiece());
                tile.setPiece(null);
            }
            // change to selected tile
            currentTile = tile;
            // set the piece at selected tile
            currentTile.setPiece(this);
            tilesToMoveTo.clear();
            tilesPair = new Pair<>(currentTile, tile);
            historyMoves.add(tilesPair);
            if (!hasMoved) hasMoved = true;

            generateTilesToMoveTo();
        }
    }

    @Override
    public void unmakeLastMove() {
        if (historyMoves.size() == 0) return;
        Tile previousTile = historyMoves.pop().getFirst();
        // checking if piece really ate opponent's piece last turn
        if (piecesEaten.size() != 0) {
            Pair<Piece, Tile> lastPair = piecesEaten.pop();
            if (lastPair.getSecond().equals(currentTile)) {
                // if so, setting the eaten piece at this piece's current tile and this piece at it's previous tile
                currentTile.setPiece(lastPair.getFirst());
                lastPair.getFirst().setIsAlive(true);
            }
        } else {
            currentTile.setPiece(null);
        }
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

//    @Override
//    public void setOnClickListener() {
////        if (!isAlive) return;
//        if (imageIcon == null) return;
//        imageIcon.setOnMouseClicked(mouseEvent -> {
//            if (tilesToMoveTo.size() == 0) return;
//            for (Tile tile : tilesToMoveTo) {
//                tile.setTileImageView(createImageView("redTile"));
//            }
//        });
//    }

    @Override
    public boolean canMove() {
        return tilesToMoveTo.size() != 0;
    }

    @Override
    public boolean hasMoved() {
        return hasMoved;
    }

    @Override
    public Piece lastPieceEaten() {
        return piecesEaten.pop().getFirst();
    }
}
