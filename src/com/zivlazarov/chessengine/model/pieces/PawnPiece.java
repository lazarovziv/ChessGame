package com.zivlazarov.chessengine.model.pieces;
import com.zivlazarov.chessengine.model.utils.Board;
import com.zivlazarov.chessengine.model.utils.Piece;
import com.zivlazarov.chessengine.model.utils.PieceColor;
import com.zivlazarov.chessengine.model.utils.Tile;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.zivlazarov.chessengine.ui.Game.createImageView;

public class PawnPiece implements Piece {

    private final ArrayList<Tile> tilesToMoveTo;
    private final Board board;
    private String name;
    private int pieceCounter;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private Tile currentTile;
    private PieceColor pieceColor;
    private boolean hasMoved = false;
    private ImageView imageIcon;

    public PawnPiece(Board board, PieceColor pc, Tile initTile, int pieceCounter) {
        this.board = board;

//        name = 'P';
        pieceColor = pc;
        tilesToMoveTo = new ArrayList<Tile>();

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

    public PawnPiece(Board board, PieceColor pc, Tile initTile, ImageView imageView) {
        this.board = board;

//        name = 'P';
        pieceColor = pc;
        tilesToMoveTo = new ArrayList<Tile>();

        currentTile = initTile;
        if (pieceColor == PieceColor.BLACK) {
            name = "bP";
            board.getBlackAlivePieces().put(name, this);
        }
        if (pieceColor == PieceColor.WHITE) {
            name = "wP";
            board.getWhiteAlivePieces().put(name, this);
        }

        currentTile.setPiece(this);
        imageIcon = imageView;
        currentTile.setPieceImageView(imageIcon);

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
        int[] eatingDirections = new int[] {-1, 1};

        int x = currentTile.getRow();
        int y = currentTile.getCol();

        boolean canMoveFurther = !hasMoved;

        int direction = map.get(pieceColor);
        int longDirection = direction * 2;

        if (x + map.get(pieceColor) > board.getBoard().length - 1 ||  x + map.get(pieceColor) < 0) return;

        if (board.getBoard()[x+direction][y].isEmpty()) {
            tilesToMoveTo.add(board.getBoard()[x+direction][y]);
            if (canMoveFurther) {
                if (x + longDirection < 0 || x + longDirection > board.getBoard().length - 1) return;
                if (board.getBoard()[x+longDirection][y].isEmpty()) {
                    tilesToMoveTo.add(board.getBoard()[x+longDirection][y]);
                }
            }
        }
        for (int d : eatingDirections) {
            if (y + d > board.getBoard().length - 1 || y + d < 0) return;
            if (!board.getBoard()[x+direction][y+d].isEmpty() &&
                    board.getBoard()[x+direction][y+d].getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(board.getBoard()[x+direction][y+d]);
            }
        }
    }

//    @Override
//    public void asdgenerateTilesToMoveTo() {
//        int x = currentTile.getRow();
//        int y = currentTile.getCol();
//
//        boolean canMoveFurther = !hasMoved;
//
//        // if it's white, it's only way forward is "down the matrix" which is using a lower x value
//        if (pieceColor == PieceColor.BLACK) {
//            if (x - 1 < 0) return;
//            if (board.getBoard()[x-1][y].isEmpty()) {
//                tilesToMoveTo.add(board.getBoard()[x-1][y]);
//                // checking canMoveFurther for another step
//                if (canMoveFurther) {
//                    if (x - 2 < 0) return;
//                    if (board.getBoard()[x-2][y].isEmpty()) {
//                        tilesToMoveTo.add(board.getBoard()[x-2][y]);
//                    } else if (board.getBoard()[x-2][y].getPiece().getPieceColor() != pieceColor) {
//                        tilesToMoveTo.add(board.getBoard()[x-2][y]);
//                    }
//                }
//            } else if (board.getBoard()[x-1][y].getPiece().getPieceColor() != pieceColor) {
//                if (board.getBoard()[x-1][y+1].getPiece() != null) {
//                    if (board.getBoard()[x-1][y+1].getPiece().getPieceColor() != pieceColor) {
//                        tilesToMoveTo.add(board.getBoard()[x-1][y+1]);
//                    }
//                }
//                if (board.getBoard()[x-1][y-1].getPiece() != null) {
//                    if (board.getBoard()[x-1][y-1].getPiece().getPieceColor() != pieceColor) {
//                        tilesToMoveTo.add(board.getBoard()[x-1][y-1]);
//                    }
//                }
//            }
//        }
//        // if black, forward means "going up the matrix" which is using a higher x value
//        if (pieceColor == PieceColor.WHITE) {
//            if (x + 1 > 7) return;
//            if (board.getBoard()[x+1][y].isEmpty()) {
//                tilesToMoveTo.add(board.getBoard()[x+1][y]);
//                if (canMoveFurther) {
//                    if (x + 2 > 7) return;
//                    if (board.getBoard()[x+2][y].isEmpty()) {
//                        tilesToMoveTo.add(board.getBoard()[x+2][y]);
//                    } else if (board.getBoard()[x+2][y].getPiece().getPieceColor() != pieceColor) {
//                        tilesToMoveTo.add(board.getBoard()[x+2][y]);
//                    }
//                }
//                if (board.getBoard()[x+1][y].getPiece().getPieceColor() != pieceColor) {
//                    if (board.getBoard()[x+1][y+1].getPiece() != null) {
//                        if (board.getBoard()[x+1][y+1].getPiece().getPieceColor() != pieceColor) {
//                            tilesToMoveTo.add(board.getBoard()[x-1][y+1]);
//                        }
//                    }
//                    if (board.getBoard()[x+1][y-1].getPiece() != null) {
//                        if (board.getBoard()[x+1][y-1].getPiece().getPieceColor() != pieceColor) {
//                            tilesToMoveTo.add(board.getBoard()[x-1][y-1]);
//                        }
//                    }
//                }
//            } else if (board.getBoard()[x+1][y].getPiece().getPieceColor() != pieceColor) {
//                if (board.getBoard()[x+1][y+1].getPiece() != null) {
//                    if (board.getBoard()[x+1][y+1].getPiece().getPieceColor() != pieceColor) {
//                        tilesToMoveTo.add(board.getBoard()[x-1][y+1]);
//                    }
//                }
//                if (board.getBoard()[x+1][y-1].getPiece() != null) {
//                    if (board.getBoard()[x+1][y-1].getPiece().getPieceColor() != pieceColor) {
//                        tilesToMoveTo.add(board.getBoard()[x-1][y-1]);
//                    }
//                }
//            }
//        }
//    }

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

    @Override
    public ImageView getImageIcon() {
        return imageIcon;
    }

    @Override
    public Tile getCurrentTile() {
        return currentTile;
    }
    
    @Override
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
    public void setImageIcon(ImageView imageIcon) {
        this.imageIcon = imageIcon;
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
                tile.getPiece().setIsAlive(false);
                if (pieceColor == PieceColor.BLACK) {
                    board.getWhiteAlivePieces().remove(tile.getPiece().getName() + pieceCounter);
                } else if (pieceColor == PieceColor.WHITE) {
                    board.getBlackAlivePieces().remove(tile.getPiece().getName() + pieceCounter);
                }
            }
            // change to selected tile
            currentTile = tile;
            // set the piece at selected tile
            currentTile.setPiece(this);
            tilesToMoveTo.clear();
            hasMoved = true;
            generateTilesToMoveTo();
        }
    }

    @Override
    public boolean isTileAvailable(Tile tile) {
        if (tile.isEmpty()) {
            return true;
        } else return tile.getPiece().getPieceColor() != pieceColor;
    }

    @Override
    public void setOnClickListener() {
//        if (!isAlive) return;
        if (imageIcon == null) return;
        imageIcon.setOnMouseClicked(mouseEvent -> {
            if (tilesToMoveTo.size() == 0) return;
            for (Tile tile : tilesToMoveTo) {
                tile.setTileImageView(createImageView("redTile"));
                System.out.println("[" + tile.getRow() + ", " + tile.getCol() + "]");
            }
        });
    }

    @Override
    public boolean canMove() {
        return tilesToMoveTo.size() != 0;
    }
}
