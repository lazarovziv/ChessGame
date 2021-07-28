package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.BishopPiece;
import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PlayerTest {

    private static Board board;
    private static PawnPiece pawnPiece;
    private static BishopPiece bishopPiece;
    private static KingPiece opponentKingPiece;
    private static PawnPiece opponentPawnPiece;
    private static Player player;
    private static Player opponent;

    @BeforeAll
    public static void setup() {
        board = Board.getInstance();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        player.setOpponentPlayer(opponent);
        opponent.setOpponentPlayer(player);
        pawnPiece = new PawnPiece(player, board, player.getPlayerColor(), board.getBoard()[5][3], 0);
        bishopPiece = new BishopPiece(player, board, player.getPlayerColor(), board.getBoard()[1][0], 0);
        opponentKingPiece = new KingPiece(opponent, board, opponent.getPlayerColor(), board.getBoard()[7][4], false);
        opponentPawnPiece = new PawnPiece(opponent, board, opponent.getPlayerColor(), board.getBoard()[6][2], 0);
        board.checkBoard();
    }

    @Test
    public void testMove() {
        board.printBoard();
        pawnPiece.refresh();
        if (player.movePiece(pawnPiece, board.getBoard()[1][3])) board.printBoard();
        bishopPiece.refresh();
        if (player.movePiece(bishopPiece, board.getBoard()[7][6])) board.printBoard();
    }

    @Test
    public void testSaveAndLoadState() {
        KingPiece kingPiece = new KingPiece(player, board, player.getPlayerColor(), board.getBoard()[1][4], false);
        System.out.println("Player pieces: ");
        for (Piece piece : player.getAlivePieces()) {
            System.out.println(piece.getName() + " - " + piece.getCurrentTile());
        }
        System.out.println("Opponent pieces: ");
        for (Piece piece : opponent.getAlivePieces())
            System.out.println(piece.getName() + " - " + piece.getCurrentTile());

        player.saveState();
        player.getOpponentPlayer().saveState();

        player.movePiece(pawnPiece, board.getBoard()[1][3]);

        board.checkBoard();

        Player loadedPlayer = player.loadState();
        Player loadedOpponent = player.getOpponentPlayer().loadState();

        System.out.println("Loaded player pieces: ");
        for (Piece piece : loadedPlayer.getAlivePieces()) {
            System.out.println(piece.getName() + " - " + piece.getCurrentTile());
        }
        System.out.println("Loaded opponent pieces: ");
        for (Piece piece : loadedOpponent.getAlivePieces()) {
            System.out.println(piece.getName() + " - " + piece.getCurrentTile());
        }
    }

    @Test
    public void testUndoMove() {
        board.printBoard();
        pawnPiece.getPossibleMoves().forEach(System.out::println);
        if (player.movePiece(pawnPiece, board.getBoard()[6][2])) {
            board.checkBoard();
            board.printBoard();
            player.undoLastMove();
            board.printBoard();
        }
        opponentPawnPiece.getPossibleMoves().forEach(System.out::println);
    }

    @Test
    public void testUnmakeMove() {
        Move move = player.getMoves().get(0);
        System.out.println(move);
        move.makeMove(true);
        board.printBoard();
        move.unmakeMove(true);
        board.printBoard();
    }
}
