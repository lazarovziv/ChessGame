package com.zivlazarov.chessengine.model.pieces;
import com.zivlazarov.chessengine.model.utils.Board;
import com.zivlazarov.chessengine.model.utils.Piece;
import com.zivlazarov.chessengine.model.utils.PieceColor;
import com.zivlazarov.chessengine.model.utils.Tile;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

import static com.zivlazarov.chessengine.ui.Game.createImageView;

public class KnightPiece implements Piece {

    private final ArrayList<Tile> tilesToMoveTo;
    private final Board board;
    private String name;
    private int pieceCounter;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private Tile currentTile;
    private PieceColor pieceColor;
    private ImageView imageIcon;

    public KnightPiece(Board board, PieceColor pc, Tile initTile, int pieceCounter) {
        this.board = board;

//        name = 'N';
        pieceColor = pc;
        tilesToMoveTo = new ArrayList<Tile>();

        currentTile = initTile;
        this.pieceCounter = pieceCounter;
        if (pieceColor == PieceColor.BLACK) {
            name = "bN";
            board.getBlackAlivePieces().put(name + pieceCounter, this);
        }
        if (pieceColor == PieceColor.WHITE) {
            name = "wN";
            board.getWhiteAlivePieces().put(name + pieceCounter, this);
        }

        currentTile.setPiece(this);

//        generateTilesToMoveTo();
    }

    public KnightPiece(Board board, PieceColor pc, Tile initTile, ImageView imageView) {
        this.board = board;

//        name = 'N';
        pieceColor = pc;
        tilesToMoveTo = new ArrayList<Tile>();

        currentTile = initTile;
        if (pieceColor == PieceColor.BLACK) {
            name = "bN";
            board.getBlackAlivePieces().put(name, this);
        }
        if (pieceColor == PieceColor.WHITE) {
            name = "wN";
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
        int[][] directions ={
                {1, 2},
                {1, -2},
                {-1, 2},
                {-1, -2},
                {2, 1},
                {2, -1},
                {-2, 1},
                {-2 ,-1}
        };

        int x = currentTile.getRow();
        int y = currentTile.getCol();

        for (int[] direction : directions) {
            int r = direction[0];
            int c = direction[1];

            if (x+r > board.getBoard().length - 1  || x+r < 0 || y+c > board.getBoard().length - 1 || y+c < 0) continue;
            Tile targetTile = board.getBoard()[x+r][y+c];
            if (targetTile.isEmpty() || targetTile.getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(targetTile);
            }
        }
    }

//    @Override
//    public void asdagenerateTilesToMoveTo() {
//        int x = currentTile.getRow();
//        int y = currentTile.getCol();
//
//        // 1 right 2 up
//        if (x + 2 < board.getBoard().length && y + 1 < board.getBoard().length) {
//            if (isTileAvailable(board.getBoard()[x+2][y+1])) {
//                tilesToMoveTo.add(board.getBoard()[x+2][y+1]);
//            }
//        }
//        // 1 right 2 down
//        if (x - 2 >= 0 && y + 1 < board.getBoard().length) {
//            if (isTileAvailable(board.getBoard()[x-2][y+1])) {
//                tilesToMoveTo.add(board.getBoard()[x-2][y+1]);
//            }
//        }
//        // 1 left 2 up
//        if (x + 2 < board.getBoard().length && y - 1 >= 0) {
//            if (isTileAvailable(board.getBoard()[x+2][y-1])) {
//                tilesToMoveTo.add(board.getBoard()[x+2][y-1]);
//            }
//        }
//        // 1 left 2 down
//        if (x - 2 >= 0 && y - 1 >= 0) {
//            if (isTileAvailable(board.getBoard()[x-2][y-1])) {
//                tilesToMoveTo.add(board.getBoard()[x-2][y-1]);
//            }
//        }
//        // 2 right 1 up
//        if (x + 1 < board.getBoard().length && y + 2 < board.getBoard().length) {
//            if (isTileAvailable(board.getBoard()[x+1][y+2])) {
//                tilesToMoveTo.add(board.getBoard()[x+1][y+2]);
//            }
//        }
//        // 2 right 1 down
//        if (x - 1 >= 0 && y + 2 < board.getBoard().length) {
//            if (isTileAvailable(board.getBoard()[x-1][y+2])) {
//                tilesToMoveTo.add(board.getBoard()[x-1][y+2]);
//            }
//        }
//        // 2 left 1 up
//        if (x + 1 < board.getBoard().length && y - 2 >= 0) {
//            if (isTileAvailable(board.getBoard()[x+1][y-2])) {
//                tilesToMoveTo.add(board.getBoard()[x+1][y-2]);
//            }
//        }
//        // 2 left 1 down
//        if (x - 1 >= 0 && y - 2 >= 0) {
//            if (isTileAvailable(board.getBoard()[x-1][y-2])) {
//                tilesToMoveTo.add(board.getBoard()[x-1][y-2]);
//            }
//        }
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
    public ImageView getImageIcon() {
        return imageIcon;
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

    @Override
    public void setPieceColor(PieceColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    @Override
    public void setImageIcon(ImageView imageIcon) {
        this.imageIcon = imageIcon;
    }

    @Override
    public Tile getCurrentTile() {
        return currentTile;
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
            }
        });
    }

    @Override
    public boolean canMove() {
        return tilesToMoveTo.size() != 0;
    }
}
