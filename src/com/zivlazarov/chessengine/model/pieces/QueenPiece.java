package com.zivlazarov.chessengine.model.pieces;
import com.zivlazarov.chessengine.model.Pair;
import com.zivlazarov.chessengine.model.utils.Board;
import com.zivlazarov.chessengine.model.utils.Piece;
import com.zivlazarov.chessengine.model.utils.PieceColor;
import com.zivlazarov.chessengine.model.utils.Tile;
//import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Stack;

//import static com.zivlazarov.chessengine.ui.Game.createImageView;

public class QueenPiece implements Piece {

    private final ArrayList<Tile> tilesToMoveTo;
    private final ArrayList<Piece> piecesUnderThreat;
    private final Stack<Pair<Tile, Tile>> historyMoves;
    private final Board board;
    private String name;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private Tile currentTile;
    private PieceColor pieceColor;
//    private ImageView imageIcon;

    public QueenPiece(Board board, PieceColor pc, Tile initTile) {
        this.board = board;

//        name = 'Q';
        pieceColor = pc;
        tilesToMoveTo = new ArrayList<Tile>();
        piecesUnderThreat = new ArrayList<>();
        historyMoves = new Stack<>();

        currentTile = initTile;
        if (pieceColor == PieceColor.BLACK) {
            name = "bQ";
            board.getBlackAlivePieces().put(name, this);
        }
        if (pieceColor == PieceColor.WHITE) {
            name = "wQ";
            board.getWhiteAlivePieces().put(name, this);
        }

        currentTile.setPiece(this);

//        generateTilesToMoveTo();
    }

//    public QueenPiece(Board board, PieceColor pc, Tile initTile, ImageView imageView) {
//        this.board = board;
//
////        name = 'Q';
//        pieceColor = pc;
//        tilesToMoveTo = new ArrayList<Tile>();
//
//        currentTile = initTile;
//        if (pieceColor == PieceColor.BLACK) {
//            name = "bQ";
//            board.getBlackAlivePieces().put(name, this);
//        }
//        if (pieceColor == PieceColor.WHITE) {
//            name = "wQ";
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
                    tilesToMoveTo.add(targetTile);
                } else if (targetTile.getPiece().getPieceColor() != pieceColor) {
                    tilesToMoveTo.add(targetTile);
                    break;
                }
                if (!targetTile.isEmpty() && targetTile.getPiece().getPieceColor() == pieceColor) break;
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

//    @Override
    public void asfsagenerateTilesToMoveTo() {
        // queen's moves are bishop's and rook's moves, together
        // diagonal moves
        int x = currentTile.getRow();
        int y = currentTile.getCol();

        // TODO: use 1 loop in each iteration, maybe use a local variable outside of loop and zero it right before each one executes

        // "going down and right diagonally"
        for (int i = x + 1, j = y + 1; i < board.getBoard().length && j < board.getBoard().length; i++, j++) {
            if (board.getBoard()[i][j].isEmpty()) {
                tilesToMoveTo.add(board.getBoard()[i][j]);
            } else if (board.getBoard()[i][j].getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(board.getBoard()[i][j]);
            } else break;
        }
        // "going up and left diagonally"
        for (int i = x - 1, j = y - 1; i >= 0 && j >=0; i--, j--) {
            if (board.getBoard()[i][j].isEmpty()) {
                tilesToMoveTo.add(board.getBoard()[i][j]);
            } else if (board.getBoard()[i][j].getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(board.getBoard()[i][j]);
            } else break;
        }

        // "going up and right diagonally"
        for (int i = x + 1, j = y - 1; i < board.getBoard().length && j >= 0; i++, j--) {
            if (board.getBoard()[i][j].isEmpty()) {
                tilesToMoveTo.add(board.getBoard()[i][j]);
            } else if (board.getBoard()[i][j].getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(board.getBoard()[i][j]);
            } else break;
        }

        // "going down and left diagonally"
        for (int i = x - 1, j = y + 1; i >= 0 && j < board.getBoard().length; i--, j++) {
            if (board.getBoard()[i][j].isEmpty()) {
                tilesToMoveTo.add(board.getBoard()[i][j]);
            } else if (board.getBoard()[i][j].getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(board.getBoard()[i][j]);
            } else break;
        }

        // straight line moves
        // checking the board for threats before adding moves to tilesToMoveTo
        // TODO: checking the board after every turn instead of every generation of moves to each piece to save memory
        //board.checkBoard();

        Tile[] currentRow = board.getBoard()[x];
        Tile[] currentCol = new Tile[8];
        for (int i = 0; i < board.getBoard().length; i++) {
            currentCol[i] = board.getBoard()[i][y];
        }

        // TODO: add possible moves at "Check" situation

        // CHECKING ROW
        // checking if Rook is at position 0, need to check only one direction instead of 2
        if (y == 0) {
            checkTilesTowardsEndFromIndex(y, currentRow);
            // checking other way around, instead of position 0, last position 7
        } else if (y == board.getBoard().length - 1) {
            checkTilesTowardsStartFromIndex(y, currentRow);
            // checking if Rook is not on edge of any of board's rows
        } else {
            // "going right"
            checkTilesTowardsEndFromIndex(y, currentRow);
            // "going left"
            checkTilesTowardsStartFromIndex(y, currentRow);
        }


        // CHECKING COLUMN
        if (x == 0) {
            checkTilesTowardsEndFromIndex(x, currentCol);
        } else if (x == board.getBoard().length - 1) {
            checkTilesTowardsStartFromIndex(x, currentCol);
        } else {
            checkTilesTowardsEndFromIndex(x, currentCol);
            checkTilesTowardsStartFromIndex(x, currentCol);
        }
    }

    private void checkTilesTowardsEndFromIndex(int index, Tile[] tiles) {
        for (int i = index + 1; i < board.getBoard().length; i++) {
            if (tiles[i].isEmpty()) {
                tilesToMoveTo.add(tiles[i]);
                // if not empty but contains opponent's piece, can be added to tilesToMoveTo but after that need to break loop
            } else if (tiles[i].getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(tiles[i]);
                break;
                // if not empty but contains same piece color, can't be moved there and must break loop
            } else break;
        }
    }

    private void checkTilesTowardsStartFromIndex(int index, Tile[] tiles) {
        for (int i = index - 1; i >= 0; i--) {
            if (tiles[i].isEmpty()) {
                tilesToMoveTo.add(tiles[i]);
                // if not empty but contains opponent's piece, can be added to tilesToMoveTo but after that need to break loop
            } else if (tiles[i].getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(tiles[i]);
                break;
                // if not empty but contains same piece color, can't be moved there and must break loop
            } else break;
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
        return isInDanger;
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
            // check if tile has opponent's piece and if so, mark as not alive
            if (!tile.isEmpty()) {
                tile.getPiece().setIsAlive(false);
                if (pieceColor == PieceColor.BLACK) {
                    board.getWhiteAlivePieces().remove(tile.getPiece().getName());
                } else if (pieceColor == PieceColor.WHITE) {
                    board.getBlackAlivePieces().remove(tile.getPiece().getName());
                }
                tile.setPiece(null);
                tilesPair = new Pair<>(currentTile, tile);
            }
            // change to selected tile
            currentTile = tile;
            // set the piece at selected tile
            currentTile.setPiece(this);
            tilesToMoveTo.clear();
            historyMoves.add(tilesPair);
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
