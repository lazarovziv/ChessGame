package com.zivlazarov.chessengine.model.pieces;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;

import javax.persistence.Entity;

@Entity
public class QueenPiece extends Piece implements Cloneable {

    public QueenPiece(Player player, Board board, Tile initTile) {
        super();

        this.player = player;
        this.board = board;
        this.pieceColor = player.getColor();
        this.currentTile = initTile;
        this.lastTile = currentTile;
        this.pieceCounter = -2;

        this.value = 90;

        if (this.pieceColor == PieceColor.BLACK) {
            this.name = "bQ";
            this.imageName = "blackQueen.png";
        }
        if (this.pieceColor == PieceColor.WHITE) {
            this.name = "wQ";
            this.imageName = "whiteQueen.png";
        }

        this.player.addPieceToAlive(this);
        this.currentTile.setPiece(this);
        this.pieceType = PieceType.QUEEN;

        strongTiles = new double[][] {
                {-2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0},
                {-1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -1.0},
                {-1.0, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -1.0},
                {-0.5, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -0.5},
                {0.0, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -0.5},
                {-1.0, 0.5, 0.5, 0.5, 0.5, 0.5, 0.0, -1.0},
                {-1.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, -1.0},
                {-2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0}
        };

        if (pieceColor == PieceColor.BLACK) {
            strongTiles = revertStrongTiles(strongTiles);
        }
    }

    @Override
    public void generateMoves() {
        if (!isAlive) return;
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

        if (currentTile == null) return;

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
                    Move move = new Move.Builder()
                            .board(board)
                            .player(player)
                            .movingPiece(this)
                            .targetTile(targetTile)
                            .build();
                    moves.add(move);
                } else if (targetTile.getPiece().getPieceColor() != pieceColor) {
                    possibleMoves.add(targetTile);
                    piecesUnderThreat.add(targetTile.getPiece());
                    targetTile.getPiece().setIsInDanger(true);
                    Move move = new Move.Builder()
                            .board(board)
                            .player(player)
                            .movingPiece(this)
                            .targetTile(targetTile)
                            .build();
                    moves.add(move);
                    break;
                } else {
                    // setting it as threatened in the case of the piece on the tile will be captured
                    targetTile.setThreatenedByColor(pieceColor, true);
                    break;
                }
            }
        }
        possibleMoves.forEach(tile -> tile.setThreatenedByColor(pieceColor, true));
        player.getLegalMoves().addAll(possibleMoves);
        player.getMoves().addAll(moves);
    }

    //
//    private Player player;
//
//    private ObjectProperty<Tile> currentTileProperty;
//
//    private PieceType pieceType;
//
//    private int id;
//
//    private final Set<Move> moves;
//    private final List<Tile> possibleMoves;
//    private final List<Piece> piecesUnderThreat;
//    private final Stack<Tile> historyMoves;
//    private Tile lastTile;
//    private Stack<Piece> capturedPieces;
//    private final Board board;
//
//    private String name;
//
//    private boolean isAlive = true;
//    private boolean isInDanger = false;
//    private Tile currentTile;
//    private PieceColor pieceColor;
//    private String imageName;
//    private Icon imageIcon;
//
//    private int value = 9;
//
//    private final Object[] allFields;
//
//    public QueenPiece(Player player, Board board, PieceColor pc, Tile initTile) {
//        this.player = player;
//        this.board = board;
//
////        name = 'Q';
//        pieceColor = pc;
//        possibleMoves = new ArrayList<Tile>();
//        piecesUnderThreat = new ArrayList<>();
//        historyMoves = new Stack<>();
//        capturedPieces = new Stack<>();
//        moves = new HashSet<>();
//
//        currentTile = initTile;
//        lastTile = currentTile;
//
//        if (pieceColor == PieceColor.BLACK) {
//            name = "bQ";
//            imageName = "blackQueen.png";
//        }
//        if (pieceColor == PieceColor.WHITE) {
//            name = "wQ";
//            imageName = "whiteQueen.png";
//        }
//        player.addPieceToAlive(this);
//
//        currentTile.setPiece(this);
//
//        currentTileProperty = new SimpleObjectProperty<>(this, "currentTile", currentTile);
////        generateTilesToMoveTo();
//        allFields = new Object[] {player, pieceType, possibleMoves, piecesUnderThreat,
//                historyMoves, lastTile, capturedPieces,
//                name, isAlive, isInDanger, currentTile,
//                pieceColor, imageName, imageIcon};
//
//        pieceType = PieceType.QUEEN;
//
//        id = 100 * value * player.getPlayerDirection() + player.getId() - 2;
//    }
//
//    @Override
//    public int getId() {
//        return id;
//    }
//
//    @Override
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    @Override
//    public String getName() {
//        return name;
//    }
//
//    @Override
//    public boolean isAlive() {
//        return !isAlive;
//    }
//
//    @Override
//    public boolean getIsInDanger() {
//        return isInDanger;
//    }
//
//    @Override
//    public List<Tile> getPossibleMoves() {
//        return possibleMoves;
//    }
//
//    @Override
//    public PieceColor getPieceColor() {
//        return pieceColor;
//    }
//
//    @Override
//    public Tile getCurrentTile() {
//        return currentTile;
//    }
//
//    public Player getPlayer() {
//        return player;
//    }
//
//    @Override
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    @Override
//    public void setIsAlive(boolean isAlive) {
//        this.isAlive = isAlive;
//    }
//
//    @Override
//    public void setIsInDanger(boolean isInDanger) {
//        this.isInDanger = isInDanger;
//    }
//
//    @Override
//    public void setPieceColor(PieceColor pieceColor) {
//        this.pieceColor = pieceColor;
//    }
//
//    @Override
//    public Stack<Tile> getHistoryMoves() {
//        return historyMoves;
//    }
//
//    @Override
//    public Tile getLastMove() {
//        if (historyMoves.size() == 0) return null;
//        return historyMoves.peek();
//    }
//
//    @Override
//    public List<Piece> getPiecesUnderThreat() {
//        return piecesUnderThreat;
//    }
//
//    @Override
//    public void setCurrentTile(Tile currentTile) {
//        this.currentTile = currentTile;
//        if (currentTile == null) return;
//        currentTile.setPiece(this);
//    }
//
//    public String getImageName() {
//        return imageName;
//    }
//
//    @Override
//    public int getValue() {
//        return value;
//    }
//
//    @Override
//    public boolean isThreatenedAtTile(Tile tile) {
//        if (pieceColor == PieceColor.WHITE) {
//            if (tile.isThreatenedByBlack()) return true;
//            else return false;
//        }
//        if (pieceColor == PieceColor.BLACK) {
//            if (tile.isThreatenedByWhite()) return true;
//            else return false;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean isTileAvailable(Tile tile) {
//        if (tile.isEmpty()) {
//            return true;
//        } else return tile.getPiece().getPieceColor() != pieceColor;
//    }
//
//    @Override
//    public boolean canMove() {
//        return possibleMoves.size() != 0;
//    }
//
//    @Override
//    public boolean hasMoved() {
//        return false;
//    }
//
//    @Override
//    public Piece getLastPieceEaten() {
//        if (capturedPieces.size() == 0) return null;
//        return capturedPieces.peek();
//    }
//
//    @Override
//    public Object clone() throws CloneNotSupportedException {
//        return super.clone();
//    }
//
//    @Override
//    public boolean equals(Piece piece) {
//        return currentTile.getRow() == piece.getCurrentTile().getRow() &&
//                currentTile.getCol() == piece.getCurrentTile().getCol() &&
//                name.equals(piece.getName());
//    }
//
//    @Override
//    public Stack<Piece> getCapturedPieces() {
//        return capturedPieces;
//    }
//
//    @Override
//    public Tile getLastTile() {
//        return lastTile;
//    }
//
//    @Override
//    public void setLastTile(Tile lastTile) {
//        this.lastTile = lastTile;
//    }
//
//    @Override
//    public PieceType getPieceType() {
//        return pieceType;
//    }
//
//    @Override
//    public void setPieceType(PieceType pieceType) {
//        this.pieceType = pieceType;
//    }
//
//    @Override
//    public void setPlayer(Player player)  {
//        this.player = player;
//    }
//
//    @Override
//    public Set<Move> getMoves() {
//        return moves;
//    }
//
//    @Override
//    public int getPieceCounter() {
//        return -2;
//    }
//
//    @Override
//    public void reset() {
//        if (possibleMoves.size() != 0) {
//            possibleMoves.clear();
//        }
//        if (piecesUnderThreat.size() != 0) {
//            piecesUnderThreat.clear();
//        }
//        if (moves.size() != 0) {
//            moves.clear();
//        }
//    }
}
