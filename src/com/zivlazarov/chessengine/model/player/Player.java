package com.zivlazarov.chessengine.model.player;

import com.zivlazarov.chessengine.controllers.PlayerController;
import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.utils.MyObservable;
import com.zivlazarov.chessengine.model.utils.MyObserver;
import com.zivlazarov.chessengine.model.utils.Pair;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Player implements MyObserver, Serializable {

    private Player opponentPlayer;

    private final Board board;

    private MyObservable observable;

    private final PieceColor playerColor;
    private String name;
    private final List<Piece> alivePieces;
    private final List<Piece> deadPieces;
    private final List<Tile> legalMoves;

    private final int playerDirection;

    private Pair<Piece, Tile> lastMove;

    public Player(Board b, PieceColor pc) {
        board = b;
        playerColor = pc;
        alivePieces = new ArrayList<Piece>();
        deadPieces = new ArrayList<Piece>();
        legalMoves = new ArrayList<>();

        // setting player direction, white goes up the board, black goes down (specifically to pawn pieces and for checking pawn promotion)
        if (playerColor == PieceColor.WHITE) {
            playerDirection = 1;
        } else playerDirection = -1;
    }

    public void refreshPieces() {
        legalMoves.clear();
        for (Piece piece : alivePieces) {
            piece.refresh();
            legalMoves.addAll(piece.getPossibleMoves());
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
        Tile pieceTile = piece.getCurrentTile();
        clearTileFromPiece(pieceTile);

        // checking if an en passant or castling has been made to know if can continue to the rest of method's statements
        boolean isSpecialMove = false;

        if (piece instanceof PawnPiece) {
            isSpecialMove = handleEnPassantMove(piece, targetTile);
            ((PawnPiece) piece).setHasMoved(true);
            handlePawnPromotion(piece);
        }
        if (piece instanceof KingPiece) {
            isSpecialMove = handleKingSideCastling(piece, targetTile);
            if (!isSpecialMove) isSpecialMove = handleQueenSideCastling(piece, targetTile);
            ((KingPiece) piece).setHasMoved(true);
        }
        // eat move
        if (!isSpecialMove && !targetTile.isEmpty() && targetTile.getPiece().getPieceColor() != playerColor) {
            opponentPlayer.addPieceToDead(targetTile.getPiece());
        }

        // placing piece in target tile
        piece.setCurrentTile(targetTile);
        // pushing move to log
        piece.getHistoryMoves().push(pieceTile);
        board.getGameHistoryMoves().push(new Pair<>(piece, targetTile));

        lastMove = null;
        lastMove = new Pair<>(piece, targetTile);

        update();

        return true;
    }

    public void handlePawnPromotion(Piece piece) {
        if (piece.getCurrentTile().getRow() == playerDirection * (board.getBoard().length - 1)) {
            // setting it as dead and adding it to deadPieces list
            piece.setIsAlive(false);
            addPieceToDead(piece);
            Tile targetTile = piece.getCurrentTile();
            Piece convertedPiece = null;
            // clearing piece from it's tile to set a new piece
            targetTile.setPiece(null);

            char chosenPiece = PlayerController.receivePawnPromotionChoice();

            switch (chosenPiece) {
                case 'q' -> convertedPiece = new QueenPiece(this, board, playerColor, targetTile);
                case 'b' -> convertedPiece = new BishopPiece(this, board, playerColor, targetTile, 3);
                case 'n' -> convertedPiece = new KnightPiece(this, board, playerColor, targetTile, 3);
                case 'r' -> convertedPiece = new RookPiece(this, board, playerColor, targetTile, false, 3);

            }
            if (convertedPiece != null) addPieceToAlive(convertedPiece);
        }
    }

    public boolean handleEnPassantMove(Piece piece, Tile tile) {
        if (((PawnPiece) piece).getEnPassantTile() != null) {
            if (tile.equals(((PawnPiece) piece).getEnPassantTile())) {
                opponentPlayer.addPieceToDead(
                        board.getBoard()[((PawnPiece)piece).getEnPassantTile().getRow()-playerDirection]
                                [((PawnPiece)piece).getEnPassantTile().getCol()]
                                .getPiece());
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
//            piece.setCurrentTile(piece.getCurrentTile());
        }
    }

    public void addPieceToDead(Piece piece) {
        if (piece.getPieceColor() == playerColor) {
            deadPieces.add(piece);
            alivePieces.remove(piece);
            clearTileFromPiece(piece.getCurrentTile());
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
        for (Piece piece : alivePieces) {
            if (piece instanceof KingPiece) return (KingPiece) piece;
        }
        return null;
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
//        if (opponentPlayer.getOpponentPlayer() != null) {
//            opponentPlayer.setOpponentPlayer(this);
//        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Player other) {
        return playerColor == other.getPlayerColor();
    }

    @Override
    public String toString() {
        return name;
    }

    public Pair<Piece, Tile> getLastMove() {
        return lastMove;
    }

    public int getPlayerDirection() {
        return playerDirection;
    }

    public List<Tile> getLegalMoves() {
        return legalMoves;
    }

    public void updateLegalMoves() {
        for (Piece piece : alivePieces) {
            legalMoves.addAll(piece.getPossibleMoves());
        }
    }

    public boolean isInCheck() {
        return getKing().getIsInDanger();
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

    @Override
    public void update() {
        refreshPieces();
    }

    @Override
    public void setObservable(MyObservable observable) {
        this.observable = observable;
    }
}