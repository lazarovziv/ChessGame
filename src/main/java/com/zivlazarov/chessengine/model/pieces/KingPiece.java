package com.zivlazarov.chessengine.model.pieces;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;

import javax.persistence.Entity;

@Entity
public class KingPiece extends Piece implements Cloneable {

    private Tile kingSideCastleTile;
    private Tile queenSideCastleTile;

    private boolean executedKingSideCastle = false;
    private boolean executedQueenSideCastle = false;

    public KingPiece(Player player, Board board, Tile initTile) {
        super();

        this.player = player;
        this.board = board;
        this.pieceColor = player.getColor();
        this.currentTile = initTile;
        this.lastTile = currentTile;
        this.pieceCounter = -1;

        this.value = 900;

        if (this.pieceColor == PieceColor.BLACK) {
            this.name = "bK";
            this.imageName = "blackKing.png";
        }
        if (this.pieceColor == PieceColor.WHITE) {
            this.name = "wK";
            this.imageName = "whiteKing.png";
        }

        this.player.addPieceToAlive(this);
        this.currentTile.setPiece(this);
        this.pieceType = PieceType.KING;

        if (currentTile.getCol() + 2 <= 7 && currentTile.getCol() - 2 >= 0) {
            kingSideCastleTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() + 2];
            queenSideCastleTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() - 2];
        }

        board.getKingsMap().put(player, this);

        strongTiles = new double[][] {
                {-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
                {-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
                {-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
                {-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
                {-2.0, -3.0, -3.0, -4.0 ,-4.0, -3.0, -3.0, -2.0},
                {-1.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -1.0},
                {2.0, 2.0, 0.0, 0.0, 0.0, 0.0, 2.0, 2.0},
                {2.0, 3.0, 1.0, 0.0, 0.0, 1.0, 3.0, 2.0}
        };

        if (pieceColor == PieceColor.BLACK) {
            strongTiles = revertStrongTiles(strongTiles);
        }
    }

    @Override
    public void generateMoves() {
        if (!isAlive) return;
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

        for (int[] direction : directions) {
            int r = direction[0];
            int c = direction[1];
            if (x+r > board.getBoard().length - 1 || x+r < 0 || y+c > board.getBoard().length - 1 || y+c < 0) continue;
            Tile targetTile = board.getBoard()[x+r][y+c];
            if (!targetTile.isThreatenedByColor(player.getOpponent().getColor())) {
                if (targetTile.isEmpty() || targetTile.getPiece().getPieceColor() != pieceColor) {
                    // calling possible moves first because of the target tile check condition in Move.Builder class
                    possibleMoves.add(targetTile);
                    Move move = new Move.Builder()
                            .board(board)
                            .player(player)
                            .movingPiece(this)
                            .targetTile(targetTile)
                            .build();
                    moves.add(move);
                    if (!targetTile.isEmpty()) {
                        if (targetTile.getPiece().getPieceColor() != pieceColor) {
                            piecesUnderThreat.add(targetTile.getPiece());
                        }
                    }
                }
            }
            if (!targetTile.isEmpty()) {
                if (targetTile.getPiece().getPieceColor() == pieceColor) targetTile.setThreatenedByColor(pieceColor, true);
            }
        }

        if (y + 2 <= 7) {
            if (canKingSideCastle()) {
                possibleMoves.add(board.getBoard()[x][y + 2]);
                Move move = new Move.Builder()
                        .board(board)
                        .player(player)
                        .movingPiece(this)
                        .targetTile(board.getBoard()[x][y+2])
                        .build();
                moves.add(move);
            }
        }

        if (y - 2 >= 0) {
            if (canQueenSideCastle()) {
                possibleMoves.add(board.getBoard()[x][y - 2]);
                Move move = new Move.Builder()
                        .board(board)
                        .player(player)
                        .movingPiece(this)
                        .targetTile(board.getBoard()[x][y-2])
                        .build();
                moves.add(move);
            }
        }

        for (Tile tile : possibleMoves) {
            if (!tile.isEmpty()) {
                if (tile.getPiece().getPieceColor() != pieceColor) {
                    piecesUnderThreat.add(tile.getPiece());
                    tile.getPiece().setIsInDanger(true);
                }
            }
        }
        possibleMoves.forEach(tile -> tile.setThreatenedByColor(pieceColor, true));
        player.getLegalMoves().addAll(possibleMoves);
        player.getMoves().addAll(moves);
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

        for (int i = 1; y+i < 7; i++) {
            if (board.getBoard()[x][7].getPiece() == null || hasMoved || isInDanger
                    || board.getBoard()[x][7].getPiece().hasMoved()
                    || board.getBoard()[x][7].isThreatenedByColor(player.getOpponent().getColor())
                    || player.isInCheck()) return false;

            if (!board.getBoard()[x][y+i].isEmpty()
                    || board.getBoard()[x][y+i].isThreatenedByColor(player.getOpponent().getColor())) return false;
        }
        return true;
    }

    // king moves 2 tiles rook moves 3 tiles
    public boolean canQueenSideCastle() {
        int x = currentTile.getRow();
        int y = currentTile.getCol();

        for (int i = 1; y-i > 0; i++) {
            if (board.getBoard()[x][0].getPiece() == null || hasMoved || isInDanger
                    || board.getBoard()[x][0].getPiece().hasMoved()
                    || board.getBoard()[x][0].isThreatenedByColor(player.getOpponent().getColor())
                    || player.isInCheck()) return false;

            if (!board.getBoard()[x][y-i].isEmpty()
                    || board.getBoard()[x][y-i].isThreatenedByColor(player.getOpponent().getColor())) return false;
        }
        return true;
    }

    public Tile getKingSideCastleTile() {
        return kingSideCastleTile;
    }

    public Tile getQueenSideCastleTile() {
        return queenSideCastleTile;
    }

    public boolean hasExecutedKingSideCastle() {
        return executedKingSideCastle;
    }

    public void setExecutedKingSideCastle(boolean executedKingSideCastle) {
        this.executedKingSideCastle = executedKingSideCastle;
    }

    public boolean hasExecutedQueenSideCastle() {
        return executedQueenSideCastle;
    }

    public void setExecutedQueenSideCastle(boolean executedQueenSideCastle) {
        this.executedQueenSideCastle = executedQueenSideCastle;
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
//    private final Stack<Piece> capturedPieces;
//    private final Board board;
//
//    private String name;
//
//    private boolean isAlive = true;
//    private boolean isInDanger = false;
//    private boolean hasMoved = false;
//    private Tile currentTile;
//    private PieceColor pieceColor;
//    private String imageName;
//    private Icon imageIcon;
//
//    private Tile kingSideCastleTile;
//    private Tile queenSideCastleTile;
//
//
//    private final int value = 0;
//
//    private final Object[] allFields;
//
//    public KingPiece(Player player, Board board, PieceColor pc, Tile initTile, boolean test) {
//        this.player = player;
//        this.board = board;
//
////        name = 'K';
//        pieceColor = pc;
//        possibleMoves = new ArrayList<>();
//        piecesUnderThreat = new ArrayList<>();
//        historyMoves = new Stack<>();
//        capturedPieces = new Stack<>();
//        moves = new HashSet<>();
//
//        currentTile = initTile;
//        lastTile = currentTile;
//
//        if (pieceColor == PieceColor.BLACK) {
//            name = "bK";
//            imageName = "blackKing.png";
//        }
//        if (pieceColor == PieceColor.WHITE) {
//            name = "wK";
//            imageName = "whiteKing.png";
//        }
//
//        kingSideCastleTile = null;
//        queenSideCastleTile = null;
//
//        if (!test)
//            kingSideCastleTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() + 2];
//            queenSideCastleTile = board.getBoard()[currentTile.getRow()][currentTile.getCol() - 2];
//
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
//        pieceType = PieceType.KING;
//
//        board.getKingsMap().put(player, this);
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
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    @Override
//    public boolean isAlive() {
//        return !isAlive;
//    }
//
//    @Override
//    public void setIsAlive(boolean isAlive) {
//        this.isAlive = isAlive;
//    }
//
//    @Override
//    public boolean getIsInDanger() {
//        return isThreatenedAtTile(currentTile);
//    }
//
//    @Override
//    public void setIsInDanger(boolean isInDanger) {
//        this.isInDanger = isInDanger;
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
//    public void setPieceColor(PieceColor pieceColor) {
//        this.pieceColor = pieceColor;
//    }
//
//    public void setHasMoved(boolean moved) {
//        hasMoved = moved;
//    }
//
//    @Override
//    public Stack<Tile> getHistoryMoves() {
//        return historyMoves;
//    }
//
//    public Tile getKingSideCastleTile() {
//        return kingSideCastleTile;
//    }
//
//    public Tile getQueenSideCastleTile() {
//        return queenSideCastleTile;
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
//            return tile.isThreatenedByBlack();
//        }
//        if (pieceColor == PieceColor.BLACK) {
//            return tile.isThreatenedByWhite();
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
//        return hasMoved;
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
//        return -1;
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
