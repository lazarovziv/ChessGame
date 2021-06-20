package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.utils.board.Board;
import com.zivlazarov.chessengine.model.utils.board.PieceColor;
import com.zivlazarov.chessengine.model.utils.player.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BoardTest {

    private static Board board;
    private static KingPiece kingPiece;
    private static KnightPiece knightPiece;
    private static PawnPiece opponentPawnPiece;
    private static PawnPiece opponentPawnPiece1;
    private static BishopPiece opponentBishopPiece;
    private static Player player;
    private static Player opponent;
    private static RookPiece rookPiece;
    private static RookPiece rookPiece1;

    @BeforeAll
    public static void setup() {
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        player.setOpponentPlayer(opponent);
        opponent.setOpponentPlayer(player);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);
        rookPiece = new RookPiece(player, board, PieceColor.WHITE, board.getBoard()[0][7], 0);
        rookPiece1 = new RookPiece(player, board, PieceColor.WHITE, board.getBoard()[0][0], 1);
        kingPiece = new KingPiece(player, board, PieceColor.WHITE, board.getBoard()[0][4]);
        knightPiece = new KnightPiece(player, board, PieceColor.WHITE, board.getBoard()[1][4], 0);
        opponentPawnPiece = new PawnPiece(opponent, board, PieceColor.BLACK, board.getBoard()[3][4], 0);
        opponentPawnPiece1 = new PawnPiece(opponent, board, PieceColor.BLACK, board.getBoard()[5][0], 1);
        opponentBishopPiece = new BishopPiece(opponent, board, PieceColor.BLACK, board.getBoard()[4][0], 0);
        board.checkBoard();
    }

    @Test
    public void testDistanceBetweenPieces() {
        board.printBoard();
        int distance = board.distanceBetweenPieces(kingPiece, opponentPawnPiece);
        int distance1 = board.distanceBetweenPieces(kingPiece, opponentPawnPiece1);
        System.out.println(distance);
        System.out.println(distance1);
        Assertions.assertEquals(2, distance);
        Assertions.assertEquals(4, distance1);
    }

    @Test
    public void testLegalMovesInitialization() {
        board.printBoard();
        board.checkBoard(player);
        System.out.println(board.getGameSituation());
    }

    @Test
    public void testUnmakeLastMove() {
        board.printBoard();
        rookPiece1.moveToTile(board.getBoard()[4][0]);
        board.printBoard();
        board.checkBoard();
        board.unmakeLastMove(rookPiece1);
        board.printBoard();
    }
}
