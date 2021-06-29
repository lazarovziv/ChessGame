package com.zivlazarov.chessengine.model.pieces;
import com.zivlazarov.chessengine.model.utils.Pair;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.player.Player;
//import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Stack;

//import static com.zivlazarov.chessengine.ui.Game.createImageView;

public class RookPiece implements Piece, Cloneable {

    private final Player player;

    private final ArrayList<Tile> possibleMoves;
    private final ArrayList<Piece> piecesUnderThreat;
    private final Stack<Pair<Tile, Tile>> historyMoves;
    private Stack<Piece> piecesEaten;
    private final Board board;
    private String name;
    private int pieceCounter;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private boolean hasMoved;
    private Tile currentTile;
    private PieceColor pieceColor;
    private String imageName;

    private Tile kingSideCastlingTile = null;
    private Tile queenSideCastlingTile = null;
    private boolean isKingSide;
    private boolean isQueenSide;
//    private ImageView imageIcon;

    public RookPiece(Player player, Board board, PieceColor pc, Tile initTile, boolean isKingSide, int pieceCounter) {
        this.player = player;
        this.board = board;

//        name = 'R';
        pieceColor = pc;
        possibleMoves = new ArrayList<Tile>();
        piecesUnderThreat = new ArrayList<>();
        historyMoves = new Stack<>();
        piecesEaten = new Stack<>();

        hasMoved = false;

        currentTile = initTile;
        this.pieceCounter = pieceCounter;
        this.isKingSide = isKingSide;
        this.isQueenSide = !isKingSide;

        if (pieceColor == PieceColor.BLACK) {
            name = "bR";
            board.getBlackAlivePieces().put(name + pieceCounter, this);
            imageName = "blackRook.png";

            if (isKingSide) {
                kingSideCastlingTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() - 2];
            } else queenSideCastlingTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() + 3];
        }
        if (pieceColor == PieceColor.WHITE) {
            name = "wR";
            board.getWhiteAlivePieces().put(name + pieceCounter, this);
            imageName = "whiteRook.png";

            if (isKingSide) {
                kingSideCastlingTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() + 2];
            } else queenSideCastlingTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() - 3];
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
        generateMoves();
    }

    @Override
    public void generateMoves() {
        if (!isAlive) return;
        int[][] directions = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
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
                    possibleMoves.add(targetTile);
                } else if (targetTile.getPiece().getPieceColor() != pieceColor) {
                    possibleMoves.add(targetTile);
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
        return isInDanger;
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
    public Stack<Pair<Tile, Tile>> getHistoryMoves() {
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

    public Tile getKingSideCastlingTile() {
        return kingSideCastlingTile;
    }

    public Tile getQueenSideCastlingTile() {
        return queenSideCastlingTile;
    }

    public void setHasMoved(boolean moved) {
        hasMoved = moved;
    }

    @Override
    public Tile getCurrentTile() {
        return currentTile;
    }

    public int getPieceCounter() {
        return pieceCounter;
    }

    public String getImageName() {
        return imageName;
    }

    public boolean isKingSide() {
        return isKingSide;
    }

    public boolean isQueenSide() {
        return isQueenSide;
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
        if (!player.getPiecesCanMove().contains(this)) return;
        if (possibleMoves.contains(tile)) {
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
            historyMoves.push(new Pair<Tile, Tile>(currentTile, tile));
            // change to selected tile
            currentTile = tile;
            // set the piece at selected tile
            currentTile.setPiece(this);
            possibleMoves.clear();
            // add tile to history of moves

            if (!hasMoved) hasMoved = true;

            generateMoves();
        }
    }

    @Override
    public void unmakeLastMove() {
        if (historyMoves.size() == 0) return;
        Tile previousTile = historyMoves.pop().getFirst();

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

    @Override
    public boolean canMove() {
        return possibleMoves.size() != 0;
    }

    @Override
    public boolean hasMoved() {
        return hasMoved;
    }

    @Override
    public Piece getLastPieceEaten() {
        if (piecesEaten.size() == 0) return null;
        return piecesEaten.peek();
    }

    @Override
    public Pair<Tile, Tile> getLastMove() {
        if (historyMoves.size() == 0) return null;
        return new Pair<Tile, Tile>(historyMoves.peek().getFirst(), currentTile);
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
}
