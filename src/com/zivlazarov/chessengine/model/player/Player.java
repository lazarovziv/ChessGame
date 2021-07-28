package com.zivlazarov.chessengine.model.player;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.utils.Memento;
import com.zivlazarov.chessengine.model.utils.MyObservable;
import com.zivlazarov.chessengine.model.utils.MyObserver;
import com.zivlazarov.chessengine.model.utils.Pair;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "players")
public class Player implements MyObserver, Serializable {

    @Id
    private UUID id;

    private Player opponentPlayer;

    private boolean isAI;
    private boolean isCurrentPlayer;

    private final Board board;

    private MyObservable observable;

    private final PieceColor playerColor;
    private String name;
    private final List<Piece> alivePieces;
    private final List<Piece> deadPieces;
    private final List<Tile> legalMoves;

    private final int playerDirection;

    private final List<Move> moves;

    private Map<Piece, Pair<Tile, Tile>> lastMove;

    private int playerScore = 0;

    public Player(Board b, PieceColor pc) {
        id = UUID.randomUUID();

        board = b;
        playerColor = pc;
        alivePieces = new ArrayList<Piece>();
        deadPieces = new ArrayList<Piece>();
        legalMoves = new ArrayList<>();
        lastMove = new HashMap<>();

        moves = new ArrayList<>();

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

    public void undoLastMove() {
        Piece piece = new ArrayList<>(lastMove.keySet()).get(0);
        Tile previousTile = lastMove.get(piece).getFirst();
        Tile currentTile = lastMove.get(piece).getSecond();

        Piece capturedPiece = null;

        // getting last eaten piece
        if (piece.getCapturedPieces().size() != 0) {
            capturedPiece = piece.getLastPieceEaten();

            // if eaten piece's last tile is the last move's previous tile, return the eaten piece to the game
            // and place eaten piece in that tile while clearing the current piece from there
            if (currentTile.equals(capturedPiece.getLastTile())) {
                opponentPlayer.addPieceToAlive(capturedPiece);
                piece.getCapturedPieces().remove(piece.getCapturedPieces().size() - 1);
                clearTileFromPiece(currentTile);
                capturedPiece.setCurrentTile(currentTile);
            }
        }
        // if last tile is not empty then clear it and set piece to it's previous tile
        if (!currentTile.isEmpty()) clearTileFromPiece(currentTile);
        piece.setCurrentTile(previousTile);

        board.setCurrentPlayer(this);

        board.checkBoard();
    }

    public Piece handlePawnPromotion(Piece piece, Tile tile) {
        if (piece.getPieceColor() == PieceColor.WHITE) {
            if (tile.getRow() == 7) {
                addPieceToDead(piece);
                piece = new QueenPiece(this, board, playerColor, tile);
            }
        } else {
            if (tile.getRow() == 0) {
                addPieceToDead(piece);
                piece = new QueenPiece(this, board, playerColor, tile);
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
        if (!piece.hasMoved() && tile.equals(((KingPiece) piece).getKingSideCastleTile())
                && getKingSideRookPiece() != null
                && !getKingSideRookPiece().hasMoved()) {
//                player.kingSideCastle((KingPiece) piece, (RookPiece) ((KingPiece) piece).getKingSideCastleTile().getPiece());
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
        if (!piece.hasMoved() && tile.equals(((KingPiece) piece).getQueenSideCastleTile())
                && getQueenSideRookPiece() != null
                && !getQueenSideRookPiece().hasMoved()) {
//                player.queenSideCastle((KingPiece) piece, (RookPiece) ((KingPiece) piece).getQueenSideCastleTile().getPiece());
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
        for (Piece piece : alivePieces) {
            if (piece instanceof RookPiece) {
                if (((RookPiece) piece).isKingSide()) return (RookPiece) piece;
            }
        }
        return null;
    }

    public RookPiece getQueenSideRookPiece() {
        for (Piece piece : alivePieces) {
            if (piece instanceof RookPiece) {
                if (((RookPiece) piece).isQueenSide()) return (RookPiece) piece;
            }
        }
        return null;
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
//        for (Piece piece : alivePieces) {
//            if (piece instanceof KingPiece) return (KingPiece) piece;
//        }
//        return null;
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

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public boolean equals(Player other) {
        return playerColor == other.getPlayerColor();
    }

    public List<Move> getMoves() {
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

    public boolean isInCheck() {
        for (Piece piece : opponentPlayer.getAlivePieces()) {
            if (piece.getPiecesUnderThreat().contains(getKing())) {
                return true;
            }
        }
        return false;
//        return getKing().getIsInDanger();
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