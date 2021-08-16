package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.BishopPiece;
import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

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
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);
        player.setOpponent(opponent);
        KingPiece kingPiece = new KingPiece(player, board, board.getBoard()[1][4]);
        pawnPiece = new PawnPiece(player, board, board.getBoard()[5][3], 0);
        bishopPiece = new BishopPiece(player, board, board.getBoard()[1][0], 0);
        opponentKingPiece = new KingPiece(opponent, board, board.getBoard()[7][4]);
        opponentPawnPiece = new PawnPiece(opponent, board, board.getBoard()[6][2], 0);
        board.setCurrentPlayer(player);
        board.checkBoard();
    }

    @Test
    public void testSaveAndLoadState() {
        System.out.println("Player pieces: ");
        for (Piece piece : player.getAlivePieces()) {
//            System.out.println(piece.getName() + " - " + piece.getCurrentTile());
            piece.getMoves().forEach(System.out::println);
        }
        System.out.println("Opponent pieces: ");
        for (Piece piece : opponent.getAlivePieces()) {
//            System.out.println(piece.getName() + " - " + piece.getCurrentTile());
            piece.getMoves().forEach(System.out::println);
        }

        player.saveState();
        player.getOpponent().saveState();

        Move move = (Move) player.getMoves().toArray()[0];
        move.makeMove(true, true);

        System.out.println();

        Player loadedPlayer = player.loadState();
        Player loadedOpponent = player.getOpponent().loadState();

        System.out.println("Loaded player pieces: ");
        for (Piece piece : loadedPlayer.getAlivePieces()) {
//            System.out.println(piece.getName() + " - " + piece.getCurrentTile());
            piece.getMoves().forEach(System.out::println);
        }
        System.out.println("Loaded opponent pieces: ");
        for (Piece piece : loadedOpponent.getAlivePieces()) {
//            System.out.println(piece.getName() + " - " + piece.getCurrentTile());
            piece.getMoves().forEach(System.out::println);
        }
    }
}
