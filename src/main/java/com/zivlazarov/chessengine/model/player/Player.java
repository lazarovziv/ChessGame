package com.zivlazarov.chessengine.model.player;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.utils.MyObservable;
import com.zivlazarov.chessengine.model.utils.MyObserver;
import com.zivlazarov.chessengine.model.utils.Pair;
import javafx.collections.*;

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

    private int turnsPlayed = 0;

    @Transient
    private final Set<Move> moves;

    @Transient
    private final List<Tile> legalMoves;

    @Transient
    private final Map<Piece, Pair<Tile, Tile>> lastMove;

    @Transient
    private transient Board board;

    @OneToOne
    private transient Player opponent;

    @Transient
    private transient MyObservable observable;

    private boolean isInCheck = false;

    public Player(PieceColor pieceColor) {
        playerColor = pieceColor;
        alivePieces = new ArrayList<>();
        deadPieces = new ArrayList<>();
        legalMoves = new ArrayList<>();
        lastMove = new HashMap<>();

        moves = new HashSet<>();

        ObservableSet<Move> oMoves = FXCollections.observableSet(moves);
        ObservableList<Piece> oAlivePieces = FXCollections.observableList(alivePieces);
        ObservableList<Piece> oCapturedPieces = FXCollections.observableList(deadPieces);
        ObservableMap<Piece, Pair<Tile, Tile>> oLastMove = FXCollections.observableMap(lastMove);
        ObservableList<Tile> oLegalMoves = FXCollections.observableList(legalMoves);

        oMoves.addListener((SetChangeListener<Move>) change -> {

        });

        // setting player direction, white goes up the board, black goes down (specifically to pawn pieces and for checking pawn promotion)
        if (playerColor == PieceColor.WHITE) {
            playerDirection = 1;
        } else playerDirection = -1;
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

    public Player() {
        alivePieces = new ArrayList<>();
        deadPieces = new ArrayList<>();
        legalMoves = new ArrayList<>();
        lastMove = new HashMap<>();

        moves = new HashSet<>();
    }

    public Player(Board b, Player player) {
        board = b;
        playerColor = player.getColor();
        alivePieces = player.getAlivePieces();
        deadPieces = player.getDeadPieces();
        legalMoves = player.getLegalMoves();
        lastMove = player.getLastMove();

        moves = new HashSet<>();
        playerDirection = player.getPlayerDirection();
        board.addObserver(this);
    }

    public void refreshPieces() {
        if (legalMoves.size() != 0) legalMoves.clear();
        if (moves.size() != 0) moves.clear();

        for (Piece piece : alivePieces) {
//            piece.setIsInDanger(false);
            piece.refresh();
        }
    }

    public void updatePieceAsDead(Piece piece) {
        piece.setIsAlive(false);
        opponent.addPieceToDead(piece);
    }

    public void updatePieceAsAlive(Piece piece) {
        piece.setIsAlive(true);
        piece.setIsInDanger(false);
        addPieceToAlive(piece);
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

    public RookPiece getKingSideRookPiece() {
        return board.getKingSideRooksMap().get(this);
    }

    public RookPiece getQueenSideRookPiece() {
        return board.getQueenSideRooksMap().get(this);
    }

    public void addPieceToAlive(Piece piece) {
        if (piece.getPieceColor() == playerColor) {
            alivePieces.add(piece);
            deadPieces.remove(piece);
            piece.setIsAlive(true);
            piece.setIsInDanger(false);
//            piece.setCurrentTile(piece.getCurrentTile());
        }
    }

    public void addPieceToDead(Piece piece) {
        if (piece == null) return;
        if (piece.getPieceColor() == playerColor) {
            deadPieces.add(piece);
            alivePieces.remove(piece);
            piece.setLastTile(piece.getCurrentTile());
            clearPieceFromTile(piece.getCurrentTile());
            piece.setCurrentTile(null);
            piece.setIsAlive(false);
        }
    }

    public void addAlivePieces(Piece[] pieces) {
        alivePieces.addAll(Arrays.stream(pieces)
                .filter(piece -> piece.getPieceColor() == playerColor)
                .collect(Collectors.toList()));
    }

    public void clearPieceFromTile(Tile tile) {
//        if (tile != null) {
//            tile.setPiece(null);
//        }
        tile.getPiece().getCurrentTile().setPiece(null);
    }

    public KingPiece getKing() {
        return board.getKingsMap().get(this);
    }

    public PieceColor getColor() {
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

    public Player getOpponent() {
        return opponent;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
        if (this.opponent.getOpponent() == null) {
            this.opponent.setOpponent(this);
        }
    }

    public void setColor(PieceColor playerColor) {
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
        return playerColor == other.getColor();
    }

    public Set<Move> getMoves() {
        return moves;
    }

    @Override
    public String toString() {
        return name + ": " + playerColor;
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

    public void incrementTurn() {
        turnsPlayed++;
    }

    public int getTurnsPlayed() {
        return turnsPlayed;
    }

    public double evaluatePlayerScore() {
        resetPlayerScore();
        for (Piece piece : alivePieces) {
            playerScore += playerDirection * piece.getValue();
            for (Piece threatenedPiece : piece.getPiecesUnderThreat()) {
                playerScore += playerDirection * 0.12 * threatenedPiece.getValue();
            }

            // adding
            playerScore += playerDirection * piece.getStrongTiles()[piece.getRow()][piece.getCol()];

            if (piece.isInDanger()) {
                if (piece instanceof KingPiece) continue;
                playerScore -= playerDirection * 0.12 * piece.getValue();
            }

            if (piece instanceof KingPiece) {
                if (((KingPiece) piece).canKingSideCastle() || ((KingPiece) piece).canQueenSideCastle()) {
                    playerScore += playerDirection * 30;
                }
                if (((KingPiece) piece).hasExecutedKingSideCastle()) {
                    playerScore += playerDirection * 70;
                } else if (((KingPiece) piece).hasExecutedQueenSideCastle()) {
                    playerScore += playerDirection * 60;
                }
            }
        }
        for (Piece piece : deadPieces) {
            playerScore -= playerDirection * piece.getValue();
        }
        if (opponent.isInCheck()) playerScore += playerDirection * 35;

        return playerScore;
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

    public void setIsInCheck(boolean isInCheck) {
        this.isInCheck = isInCheck;
    }

    public boolean isInCheck() {
//        return isInCheck;
        return getKing().isInDanger();
//        for (Piece piece : opponent.getAlivePieces()) {
//            if (piece.getPiecesUnderThreat().contains(getKing())) {
//                return true;
//            }
//        }
//        return false;
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

    public Player clone() {
//        Player newPlayer = new Player(playerColor);
//        newPlayer.setBoard(newBoard);
        return new Player(playerColor);
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

    public void setBoard(Board board) {
        this.board = board;
        board.addObserver(this);
    }

    public void addToScore(double addition) {
        playerScore += addition;
    }

    public Piece getPieceByIndex(int index) {
        return alivePieces.stream().filter(piece -> piece.getPieceIndex() == index).toList().get(0);
//        for (Piece piece : alivePieces) {
//            if (piece.getPieceIndex() == index) return piece;
//        }
//        return null;
    }
}