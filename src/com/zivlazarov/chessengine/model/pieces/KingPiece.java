package com.zivlazarov.chessengine.model.pieces;
import com.zivlazarov.chessengine.model.utils.Board;
import com.zivlazarov.chessengine.model.utils.Piece;
import com.zivlazarov.chessengine.model.utils.PieceColor;
import com.zivlazarov.chessengine.model.utils.Tile;
//import javafx.scene.image.ImageView;

import java.util.ArrayList;

//import static com.zivlazarov.chessengine.ui.Game.createImageView;

public class KingPiece implements Piece {

    private final ArrayList<Tile> tilesToMoveTo;
    private final Board board;
    private String name;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private boolean hasMoved = false;
    private Tile currentTile;
    private PieceColor pieceColor;
//    private ImageView imageIcon;

    public KingPiece(Board board, PieceColor pc, Tile initTile) {
        this.board = board;

//        name = 'K';
        pieceColor = pc;
        tilesToMoveTo = new ArrayList<>();

        currentTile = initTile;
        if (pieceColor == PieceColor.BLACK) {
            name = "bK";
            board.getBlackAlivePieces().put(name, this);
        }
        if (pieceColor == PieceColor.WHITE) {
            name = "wK";
            board.getWhiteAlivePieces().put(name, this);
        }

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

        boolean canCastle = !hasMoved;

        for (int[] direction : directions) {
            int r = direction[0];
            int c = direction[1];
            if (x+r > board.getBoard().length - 1 || x+r < 0 || y+c > board.getBoard().length - 1 || y+c < 0) continue;
            Tile targetTile = board.getBoard()[x+r][y+c];
            if (targetTile.isEmpty() || targetTile.getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(targetTile);
            }
        }
    }

//    @Override
//    public void assadgenerateTilesToMoveTo() {
//        int x = currentTile.getRow();
//        int y = currentTile.getCol();
//
//        // checking the board for threats before adding moves to tilesToMoveTo
//        // TODO: checking the board after every turn instead of every generation of moves to each piece to save memory
//        // board.checkBoard();
//        // checking if King's legal game steps are possible and adding it to tilesToMoveTo
//        if (x + 1 < board.getBoard().length) {
//            if (board.getBoard()[x + 1][y].isEmpty()) {
//                if (!isThreatenedAtTile(board.getBoard()[x + 1][y])) {
//                    tilesToMoveTo.add(board.getBoard()[x + 1][y]);
//                }
//            } else {
//                if (board.getBoard()[x+1][y].getPiece().getPieceColor() != pieceColor) {
//                    if (!isThreatenedAtTile(board.getBoard()[x + 1][y])) {
//                        tilesToMoveTo.add(board.getBoard()[x + 1][y]);
//                    }
//                }
//            }
//        }
//        if (x - 1 >= 0) {
//            if (board.getBoard()[x - 1][y].isEmpty()) {
//                if (!isThreatenedAtTile(board.getBoard()[x - 1][y])) {
//                    tilesToMoveTo.add(board.getBoard()[x - 1][y]);
//                }
//            } else {
//                if (board.getBoard()[x - 1][y].getPiece().getPieceColor() != pieceColor) {
//                    if (!isThreatenedAtTile(board.getBoard()[x - 1][y])) {
//                        tilesToMoveTo.add(board.getBoard()[x - 1][y]);
//                    }
//                }
//            }
//        }
//        if (y + 1 < board.getBoard().length) {
//            if (board.getBoard()[x][y + 1].isEmpty()) {
//                if (!isThreatenedAtTile(board.getBoard()[x][y + 1])) {
//                    tilesToMoveTo.add(board.getBoard()[x][y + 1]);
//                }
//            } else {
//                if (board.getBoard()[x][y + 1].getPiece().getPieceColor() != pieceColor) {
//                    if (!isThreatenedAtTile(board.getBoard()[x][y + 1])) {
//                        tilesToMoveTo.add(board.getBoard()[x][y + 1]);
//                    }
//                }
//            }
//        }
//        if (y - 1 >= 0) {
//            if (board.getBoard()[x][y - 1].isEmpty()) {
//                if (!isThreatenedAtTile(board.getBoard()[x][y - 1])) {
//                    tilesToMoveTo.add(board.getBoard()[x][y - 1]);
//                }
//            } else {
//                if (board.getBoard()[x][y - 1].getPiece().getPieceColor() != pieceColor) {
//                    if (!isThreatenedAtTile(board.getBoard()[x][y - 1])) {
//                        tilesToMoveTo.add(board.getBoard()[x][y - 1]);
//                    }
//                }
//            }
//        }
//        if (x + 1 < board.getBoard().length && y + 1 < board.getBoard().length) {
//            if (board.getBoard()[x + 1][y + 1].isEmpty()) {
//                if (!isThreatenedAtTile(board.getBoard()[x + 1][y + 1])) {
//                    tilesToMoveTo.add(board.getBoard()[x + 1][y + 1]);
//                }
//            } else {
//                if (board.getBoard()[x + 1][y + 1].getPiece().getPieceColor() != pieceColor) {
//                    if (!isThreatenedAtTile(board.getBoard()[x + 1][y + 1])) {
//                        tilesToMoveTo.add(board.getBoard()[x + 1][y + 1]);
//                    }
//                }
//            }
//        }
//        if (x + 1 < board.getBoard().length && y - 1 >= 0) {
//            if (board.getBoard()[x + 1][y - 1].isEmpty()) {
//                if (!isThreatenedAtTile(board.getBoard()[x + 1][y - 1])) {
//                    tilesToMoveTo.add(board.getBoard()[x + 1][y - 1]);
//                }
//            } else {
//                if (board.getBoard()[x + 1][y - 1].getPiece().getPieceColor() != pieceColor) {
//                    if (!isThreatenedAtTile(board.getBoard()[x + 1][y - 1])) {
//                        tilesToMoveTo.add(board.getBoard()[x + 1][y - 1]);
//                    }
//                }
//            }
//        }
//        if (x - 1 >= 0 && y + 1 < board.getBoard().length) {
//            if (board.getBoard()[x - 1][y + 1].isEmpty()) {
//                if (!isThreatenedAtTile(board.getBoard()[x - 1][y + 1])) {
//                    tilesToMoveTo.add(board.getBoard()[x - 1][y + 1]);
//                }
//            } else {
//                if (board.getBoard()[x - 1][y + 1].getPiece().getPieceColor() != pieceColor) {
//                    if (!isThreatenedAtTile(board.getBoard()[x - 1][y + 1])) {
//                        tilesToMoveTo.add(board.getBoard()[x - 1][y + 1]);
//                    }
//                }
//            }
//        }
//        if (x - 1 >= 0 && y - 1 >= 0) {
//            if (board.getBoard()[x - 1][y - 1].isEmpty()) {
//                if (!isThreatenedAtTile(board.getBoard()[x - 1][y - 1])) {
//                    tilesToMoveTo.add(board.getBoard()[x - 1][y - 1]);
//                }
//            } else {
//                if (board.getBoard()[x - 1][y - 1].getPiece().getPieceColor() != pieceColor) {
//                    if (!isThreatenedAtTile(board.getBoard()[x - 1][y - 1])) {
//                        tilesToMoveTo.add(board.getBoard()[x - 1][y - 1]);
//                    }
//                }
//            }
//        }
//        /*
//        for (Tile tile : tilesToMoveTo) {
//            // setting every possible move for king as tile threatened by king's piece color
//            if (pieceColor == PieceColor.WHITE) {
//                if (tile.isThreatenedByWhite()) return; else tile.setThreatenedByWhite(true);
//            } else if (pieceColor == PieceColor.BLACK) {
//                if (tile.isThreatenedByBlack()) return; else tile.setThreatenedByBlack(true);
//            }
//        }
//        */
//    }

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

    @Override
    public void setPieceColor(PieceColor pieceColor) {
        this.pieceColor = pieceColor;
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
            }
            // change to selected tile
            currentTile = tile;
            // set the piece at selected tile
            currentTile.setPiece(this);
            tilesToMoveTo.clear();
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
//            }
//        });
//    }

    @Override
    public boolean canMove() {
        return tilesToMoveTo.size() != 0;
    }
}
