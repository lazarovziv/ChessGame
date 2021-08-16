package com.zivlazarov.chessengine.model.pieces;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.Pair;

import javax.persistence.Entity;

@Entity
public class PawnPiece extends Piece implements Cloneable {

    private boolean executedEnPassant = false;
    private boolean movedLong = false;

    private Tile enPassantTile;

    private static final int[] eatingDirections = new int[]{-1, 1};

    public PawnPiece(Player player, Board board, Tile initTile, int pieceCounter) {
        super();

        this.player = player;
        this.board = board;
        this.pieceColor = player.getColor();
        this.currentTile = initTile;
        this.lastTile = currentTile;
        this.pieceCounter = pieceCounter;

        this.value = 10;

        if (this.pieceColor == PieceColor.BLACK) {
            this.name = "bP";
            this.imageName = "blackPawn.png";
        }
        if (this.pieceColor == PieceColor.WHITE) {
            this.name = "wP";
            this.imageName = "whitePawn.png";
        }

        this.player.addPieceToAlive(this);
        this.currentTile.setPiece(this);
        this.pieceType = PieceType.PAWN;

        strongTiles = new double[][] {
                {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                {5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0},
                {1.0, 1.0, 2.0, 3.0, 3.0, 2.0, 1.0, 1.0},
                {0.5, 0.5, 1.0, 2.5, 2.5, 1.0, 0.5, 0.5},
                {0.0, 0.0, 0.0, 2.0, 2.0, 0.0, 0.0, 0.0},
                {0.5, -0.5, -1.0, 0.0, 0.0, -1.0, -0.5, 0.5},
                {0.5, 1.0, 1.0, -2.0, -2.0, 1.0, 1.0, 0.5},
                {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}
        };

        if (pieceColor == PieceColor.BLACK) {
            strongTiles = revertStrongTiles(strongTiles);
        }
    }

    @Override
    public void generateMoves() {
        if (!isAlive) return;

        int x = currentTile.getRow();
        int y = currentTile.getCol();

        boolean canMoveFurther;

        if (pieceColor == PieceColor.WHITE) {
            canMoveFurther = !hasMoved && x == 1;
        } else {
            canMoveFurther = !hasMoved && x == 6;
        }

        int direction = player.getPlayerDirection();
        int longDirection = direction * 2;

        if (x + player.getPlayerDirection() > board.getBoard().length - 1 || x + player.getPlayerDirection() < 0) return;

        if (board.getBoard()[x+direction][y].isEmpty()) {
            possibleMoves.add(board.getBoard()[x + direction][y]);
            Move move = new Move.Builder()
                    .board(board)
                    .player(player)
                    .movingPiece(this)
                    .targetTile(board.getBoard()[x+direction][y])
                    .build();
            moves.add(move);
            if (canMoveFurther) {
                if (x + longDirection < 0 || x + longDirection > board.getBoard().length - 1) return;
                if (board.getBoard()[x+longDirection][y].isEmpty()) {
                    possibleMoves.add(board.getBoard()[x+longDirection][y]);
                    Move move1 = new Move.Builder()
                            .board(board)
                            .player(player)
                            .movingPiece(this)
                            .targetTile(board.getBoard()[x+longDirection][y])
                            .build();
                    moves.add(move1);
                }
            }
        }
        for (int d : eatingDirections) {
            if (y + d > board.getBoard().length - 1 || y + d < 0) continue;
            if (!board.getBoard()[x+direction][y+d].isEmpty() &&
                    board.getBoard()[x+direction][y+d].getPiece().getPieceColor() != pieceColor) {

                possibleMoves.add(board.getBoard()[x+direction][y+d]);
                piecesUnderThreat.add(board.getBoard()[x+direction][y+d].getPiece());
                board.getBoard()[x+direction][y+d].getPiece().setIsInDanger(true);

                Move move = new Move.Builder()
                        .board(board)
                        .player(player)
                        .movingPiece(this)
                        .targetTile(board.getBoard()[x+direction][y+d])
                        .build();
                moves.add(move);
            }
            // setting potential capturing tiles as threats
            board.getBoard()[x+direction][y+d].setThreatenedByColor(pieceColor, true);
            // en passant
            if (canEnPassant(d)) {
                possibleMoves.add(enPassantTile);
                Move move = new Move.Builder()
                        .board(board)
                        .player(player)
                        .movingPiece(this)
                        .targetTile(enPassantTile)
                        .build();
                moves.add(move);
                // setting the adjacent pawn piece as under threat
                // only move in chess where piece can be eaten without moving to it's tile
                piecesUnderThreat.add(board.getBoard()[x][y+d].getPiece());
                enPassantTile.setThreatenedByColor(pieceColor, true);
            }
        }
        player.getLegalMoves().addAll(possibleMoves);
        player.getMoves().addAll(moves);
    }

    public boolean canEnPassant(int eatingDirection) {
        int x = currentTile.getRow();
        int y = currentTile.getCol();

        // checking borders of board
        if (y + eatingDirection < 0 || y + eatingDirection > board.getBoard().length - 1
        || x - 2 * player.getOpponent().getPlayerDirection() < 0) return false;

        // checking if piece next to pawn is of type pawn and is opponent's piece
        if (board.getBoard()[x][y + eatingDirection].getPiece() instanceof PawnPiece pawn &&
               pawn.getPieceColor() != pieceColor && !pawn.hasExecutedEnPassant() &&
                board.getGameHistoryMoves().size() > 1 && pawn.hasMovedLong()) {
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

    public boolean hasExecutedEnPassant() {
        return executedEnPassant;
    }

    public void setExecutedEnPassant(boolean executedEnPassant) {
        this.executedEnPassant = executedEnPassant;
    }

    public Tile getEnPassantTile() {
        return enPassantTile;
    }

    public void setEnPassantTile(Tile enPassantTile) {
        this.enPassantTile = enPassantTile;
    }

    public boolean hasMovedLong() {
        return movedLong;
    }

    public void setMovedLong(boolean movedLong) {
        this.movedLong = movedLong;
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
//    private int pieceCounter;
//
//    private boolean isAlive = true;
//    private boolean isInDanger = false;
//    private Tile currentTile;
//    private PieceColor pieceColor;
//    private String imageName;
//    private boolean hasMoved = false;
//    private boolean executedEnPassant = false;
//
//    private Tile enPassantTile;
//    private Icon imageIcon;
//
//    private int value = 1;
//
//    private final Object[] allFields;
//
//    public PawnPiece(Player player, Board board, PieceColor pc, Tile initTile, int pieceCounter) {
//        this.player = player;
//        this.board = board;
//
////        name = 'P';
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
//        this.pieceCounter = pieceCounter;
//
//        if (pieceColor == PieceColor.BLACK) {
//            name = "bP";
//            imageName = "blackPawn.png";
//        }
//        if (pieceColor == PieceColor.WHITE) {
//            name = "wP";
//            imageName = "whitePawn.png";
//        }
//        player.addPieceToAlive(this);
//
//        currentTile.setPiece(this);
//
//        currentTileProperty = new SimpleObjectProperty<>(this, "currentTile", currentTile);
////        generateTilesToMoveTo();
//        allFields = new Object[] {player, pieceType, possibleMoves, piecesUnderThreat,
//                historyMoves, lastTile, capturedPieces,
//                name, pieceCounter, isAlive, isInDanger, currentTile,
//                pieceColor, imageName, imageIcon};
//
//        pieceType = PieceType.PAWN;
//
//        id = 100 * value * player.getPlayerDirection() + player.getId() + pieceCounter;
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
//        return false;
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
//    public void setPieceColor(PieceColor pieceColor) {
//        this.pieceColor = pieceColor;
//    }
//
//    @Override
//    public Tile getCurrentTile() {
//        return currentTile;
//    }
//
//    public int getPieceCounter() {
//        return pieceCounter;
//    }
//
//    public boolean hasExecutedEnPassant() {
//        return executedEnPassant;
//    }
//
//    public void setExecutedEnPassant(boolean executedEnPassant) {
//        this.executedEnPassant = executedEnPassant;
//    }
//
//    public Player getPlayer() {
//        return player;
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
//    public void setHasMoved(boolean moved) {
//        hasMoved = moved;
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
//    public Tile getEnPassantTile() {
//        return enPassantTile;
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
//                (name + pieceCounter).equals(piece.getName() + pieceCounter);
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
