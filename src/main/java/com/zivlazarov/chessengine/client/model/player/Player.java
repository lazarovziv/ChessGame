package com.zivlazarov.chessengine.client.model.player;

import com.zivlazarov.chessengine.client.model.board.Board;
import com.zivlazarov.chessengine.client.model.board.PieceColor;
import com.zivlazarov.chessengine.client.model.board.Tile;
import com.zivlazarov.chessengine.client.model.move.Move;
import com.zivlazarov.chessengine.client.model.pieces.*;
import com.zivlazarov.chessengine.client.model.utils.Memento;
import com.zivlazarov.chessengine.client.model.utils.MyObservable;
import com.zivlazarov.chessengine.client.model.utils.MyObserver;
import com.zivlazarov.chessengine.client.model.utils.Pair;

import javax.persistence.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "player")
public class Player implements MyObserver, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue
    private int id;

    @Column(name = "isAI")
    private boolean isAI;

    @Column(name = "isCurrentPlayer")
    private boolean isCurrentPlayer;

    @Column(name = "name")
    private String name;

    @Column(name = "playerDirection")
    private int playerDirection;

    @Column(name = "playerScore")
    private int playerScore = 0;

    @Column(name = "playerColor")
    private PieceColor playerColor;

//    @OneToMany(targetEntity = Piece.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "player")
    @OneToMany(targetEntity = Piece.class, mappedBy = "player")
    private final List<Piece> alivePieces;

//    @OneToMany(targetEntity = Piece.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "player")
    @OneToMany(targetEntity = Piece.class, mappedBy = "player")
    private final List<Piece> deadPieces;

    @Transient
    private final Set<Move> moves;

    @Transient
    private final List<Tile> legalMoves;

    @Transient
    private final Map<Piece, Pair<Tile, Tile>> lastMove;

    @Transient
    private transient Board board;

    @EmbeddedId
    private transient Player opponentPlayer;

    @Transient
    private transient MyObservable observable;

    public Player() {
        alivePieces = new ArrayList<>();
        deadPieces = new ArrayList<>();
        legalMoves = new ArrayList<>();
        lastMove = new HashMap<>();

        moves = new HashSet<>();
    }

    public Player(Board b, PieceColor pc) {
        board = b;
        playerColor = pc;
        alivePieces = new ArrayList<>();
        deadPieces = new ArrayList<>();
        legalMoves = new ArrayList<>();
        lastMove = new HashMap<>();

        moves = new HashSet<>();

        // setting player direction, white goes up the board, black goes down (specifically to pawn pieces and for checking pawn promotion)
        if (playerColor == PieceColor.WHITE) {
            playerDirection = 1;
        } else playerDirection = -1;

        board.addObserver(this);
    }

    public void refreshPieces() {
        if (legalMoves.size() != 0) legalMoves.clear();
        if (moves.size() != 0) moves.clear();

        for (Piece piece : alivePieces) {
            piece.setIsInDanger(false);
            piece.refresh();
        }
    }

    public void updatePieceAsDead(Piece piece) {
        piece.setIsAlive(false);
        opponentPlayer.addPieceToDead(piece);
    }

    public void updatePieceAsAlive(Piece piece) {
        piece.setIsAlive(true);
        addPieceToAlive(piece);
    }

    public boolean movePiece(Piece piece, Tile targetTile) {
        if (!piece.getPossibleMoves().contains(targetTile)) return false;
        if (!legalMoves.contains(targetTile)) return false;
//        if (!piece.isAlive()) return false;

        // clearing lastMove
        lastMove.clear();

        Tile pieceTile = piece.getCurrentTile();
        if (pieceTile != null) {
            clearTileFromPiece(pieceTile);
        }

        // checking if an en passant or castling has been made to know if can continue to the rest of method's statements
        boolean isSpecialMove = false;

        if (piece instanceof PawnPiece) {
            isSpecialMove = handleEnPassantMove(piece, targetTile);
            ((PawnPiece) piece).setHasMoved(true);
            piece = handlePawnPromotion(piece, targetTile);
        }
        if (piece instanceof KingPiece) {
            isSpecialMove = handleKingSideCastling(piece, targetTile);
            if (!isSpecialMove) isSpecialMove = handleQueenSideCastling(piece, targetTile);
            ((KingPiece) piece).setHasMoved(true);
        }
        // eat move
        if (!isSpecialMove && !targetTile.isEmpty() && targetTile.getPiece().getPieceColor() != playerColor) {
            piece.getCapturedPieces().push(targetTile.getPiece());
            opponentPlayer.addPieceToDead(targetTile.getPiece());
        }

        // placing piece in target tile
        piece.setLastTile(pieceTile);
        piece.setCurrentTile(targetTile);

        // pushing move to log
        piece.getHistoryMoves().push(targetTile);
        board.getGameHistoryMoves().push(new Pair<>(piece, targetTile));

        lastMove.put(piece, new Pair<>(pieceTile, targetTile));

//        board.checkBoard(opponentPlayer);

        resetPlayerScore();
        evaluatePlayerScore();
        opponentPlayer.resetPlayerScore();
        opponentPlayer.evaluatePlayerScore();

//        board.setCurrentPlayer(opponentPlayer);
//
//        board.checkBoard(board.getCurrentPlayer());
//        board.setCurrentPlayer(opponentPlayer);
//        board.checkBoard(board.getCurrentPlayer());
        // set board's current node the node the player made
//        for (BoardNode node : board.getCurrentNode().getChildren()) {
//            if (node.getBoard().isSameBoard(board)) board.setCurrentNode(node);
//        }

        return true;
    }

    public Piece handlePawnPromotion(Piece piece, Tile tile) {
        if (piece.getPieceColor() == PieceColor.WHITE) {
            if (tile.getRow() == 7) {
                addPieceToDead(piece);
                piece = new QueenPiece(this, board, tile);
            }
        } else {
            if (tile.getRow() == 0) {
                addPieceToDead(piece);
                piece = new QueenPiece(this, board, tile);
            }
        }
        return piece;
    }

    public boolean handleEnPassantMove(Piece piece, Tile tile) {
        if (((PawnPiece) piece).getEnPassantTile() != null) {
            if (tile.equals(((PawnPiece) piece).getEnPassantTile())) {
                opponentPlayer.addPieceToDead(
                        board.getBoard()[((PawnPiece)piece).getEnPassantTile().getRow()-playerDirection]
                                [((PawnPiece)piece).getEnPassantTile().getCol()]
                                .getPiece());
                ((PawnPiece) piece).setExecutedEnPassant(true);
                return true;
            }
        }
        return false;
    }

    public boolean handleKingSideCastling(Piece piece, Tile tile) {
        if (/*!piece.hasMoved() && */tile.equals(((KingPiece) piece).getKingSideCastleTile())
                && getKingSideRookPiece() != null
                && !getKingSideRookPiece().hasMoved()) {
            // logging rook move
            RookPiece kingSideRookPiece = getKingSideRookPiece();
            kingSideRookPiece.getHistoryMoves().push(kingSideRookPiece.getKingSideCastlingTile());
            // setting rook tile to it's king side castling tile
            kingSideRookPiece.getCurrentTile().setPiece(null);
            kingSideRookPiece.setCurrentTile(kingSideRookPiece.getKingSideCastlingTile());
            kingSideRookPiece.setHasMoved(true);
            // adding castling to game history moves
            board.getGameHistoryMoves().push(new Pair<>(kingSideRookPiece, kingSideRookPiece.getLastMove()));
            return true;
        }
        return false;
    }

    public boolean handleQueenSideCastling(Piece piece, Tile tile) {
        if (/*!piece.hasMoved() && */tile.equals(((KingPiece) piece).getQueenSideCastleTile())
                && getQueenSideRookPiece() != null
                && !getQueenSideRookPiece().hasMoved()) {
            // logging rook move
            RookPiece queenSideRookPiece = getQueenSideRookPiece();
            queenSideRookPiece.getHistoryMoves().push(queenSideRookPiece.getQueenSideCastlingTile());
            // setting rook tile to it's queen side castling tile
            queenSideRookPiece.getCurrentTile().setPiece(null);
            queenSideRookPiece.setCurrentTile(queenSideRookPiece.getQueenSideCastlingTile());
            queenSideRookPiece.setHasMoved(true);
            // adding castling to game history moves
            board.getGameHistoryMoves().push(new Pair<>(queenSideRookPiece, queenSideRookPiece.getLastMove()));

            return true;
        }
        return false;
    }

    public RookPiece getKingSideRookPiece() {
        return board.getKingSideRooksMap().get(this);
//        for (Piece piece : alivePieces) {
//            if (piece instanceof RookPiece) {
//                if (((RookPiece) piece).isKingSide()) return (RookPiece) piece;
//            }
//        }
//        return null;
    }

    public RookPiece getQueenSideRookPiece() {
        return board.getQueenSideRooksMap().get(this);
//        for (Piece piece : alivePieces) {
//            if (piece instanceof RookPiece) {
//                if (((RookPiece) piece).isQueenSide()) return (RookPiece) piece;
//            }
//        }
//        return null;
    }

    public void addPieceToAlive(Piece piece) {
        if (piece.getPieceColor() == playerColor) {
            alivePieces.add(piece);
            deadPieces.remove(piece);
            piece.setIsAlive(true);
//            piece.setCurrentTile(piece.getCurrentTile());
        }
    }

    public void addPieceToDead(Piece piece) {
        if (piece == null) return;
        if (piece.getPieceColor() == playerColor) {
            deadPieces.add(piece);
            alivePieces.remove(piece);
            clearTileFromPiece(piece.getCurrentTile());
            piece.setLastTile(piece.getCurrentTile());
            piece.setCurrentTile(null);
            piece.setIsAlive(false);
        }
    }

    public void addAlivePieces(Piece[] pieces) {
        alivePieces.addAll(Arrays.stream(pieces)
                .filter(piece -> piece.getPieceColor() == playerColor)
                .collect(Collectors.toList()));
    }

    public void clearTileFromPiece(Tile tile) {
        tile.setPiece(null);
    }

    public KingPiece getKing() {
        return board.getKingsMap().get(this);
    }

    public PieceColor getPlayerColor() {
        return playerColor;
    }

    public List<Piece> getAlivePieces() {
        return alivePieces;
    }

    public List<Piece> getDeadPieces() {
        return deadPieces;
    }

    public String getName() {
        return name;
    }

    public Player getOpponentPlayer() {
        return opponentPlayer;
    }

    public void setOpponentPlayer(Player opponent) {
        opponentPlayer = opponent;
        if (opponentPlayer.getOpponentPlayer() == null) {
            opponentPlayer.setOpponentPlayer(this);
        }
    }

    public void setPlayerColor(PieceColor playerColor) {
        this.playerColor = playerColor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int value) {
        id = value;
    }

    public boolean equals(Player other) {
        return playerColor == other.getPlayerColor();
    }

    public Set<Move> getMoves() {
        return moves;
    }

    @Override
    public String toString() {
        return name;
    }

    public Map<Piece, Pair<Tile, Tile>> getLastMove() {
        return lastMove;
    }

    public int getPlayerDirection() {
        return playerDirection;
    }

    public List<Tile> getLegalMoves() {
        return legalMoves;
    }

    public void evaluatePlayerScore() {
        for (Piece piece : alivePieces) {
            playerScore += playerDirection * 100 * piece.getValue();
            for (Piece threatenedPiece : piece.getPiecesUnderThreat()) {
                playerScore += playerDirection * 12 * threatenedPiece.getValue();
            }
        }
    }

    public void resetPlayerScore() {
        playerScore = 0;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public boolean isAI() {
        return isAI;
    }

    public void setAI(boolean AI) {
        isAI = AI;
    }

    public void updateLegalMoves() {
        for (Piece piece : alivePieces) {
            legalMoves.addAll(piece.getPossibleMoves());
            moves.addAll(piece.getMoves());
        }
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public void setPlayerDirection(int playerDirection) {
        this.playerDirection = playerDirection;
    }

    public boolean isInCheck() {
        for (Piece piece : opponentPlayer.getAlivePieces()) {
            if (piece.getPiecesUnderThreat().contains(getKing())) {
                return true;
            }
        }
        return false;
    }

    public void saveState() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(playerColor + "_player.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();
            objectOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Player loadState() {
        Player loadedPlayer = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(playerColor + "_player.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            loadedPlayer = (Player) objectInputStream.readObject();
            objectInputStream.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return loadedPlayer;
    }

    public Memento<Player> saveToMemento() {
        return new Memento<Player>(this);
    }

    public void restoreFromMemento(Memento<Board> memento) {
        board = memento.getSavedState();
    }

    @Override
    public void update() {
        refreshPieces();
    }

    @Override
    public void setObservable(MyObservable observable) {
        this.observable = observable;
    }

    public boolean isCurrentPlayer() {
        return isCurrentPlayer;
    }

    public void setIsCurrentPlayer(boolean isCurrentPlayer) {
        this.isCurrentPlayer = isCurrentPlayer;
    }
}