package com.zivlazarov.test.client.pieces;

import com.google.gson.Gson;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.PipedDeepCopy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

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
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);
        board.initBoard();
        board.setCurrentPlayer(opponent);
//        rookPiece = new RookPiece(player, board, PieceColor.WHITE, board.getBoard()[0][7], false, 0);
//        rookPiece1 = new RookPiece(player, board, PieceColor.WHITE, board.getBoard()[0][0], true, 1);
//        kingPiece = new KingPiece(player, board, PieceColor.WHITE, board.getBoard()[0][4]);
//        knightPiece = new KnightPiece(player, board, PieceColor.WHITE, board.getBoard()[1][4], 0);
//        opponentPawnPiece = new PawnPiece(opponent, board, PieceColor.BLACK, board.getBoard()[3][4], 0);
//        opponentPawnPiece1 = new PawnPiece(opponent, board, PieceColor.BLACK, board.getBoard()[5][0], 1);
//        opponentBishopPiece = new BishopPiece(opponent, board, PieceColor.BLACK, board.getBoard()[4][0], 0);
//        board.initBoard();
//        board.setCurrentPlayer(player);
//        board.checkBoard(board.getCurrentPlayer());
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
        board.checkBoard();
        System.out.println(board.getGameSituation());
    }

    @Test
    public void testCheckSituation() {
        board.printBoard();
        if (player.isInCheck()) System.out.println("Check!");
        else System.out.println("Normal");
        player.movePiece(kingPiece, board.getBoard()[0][3]);
        board.printBoard();
        board.checkBoard();
        if (player.isInCheck()) System.out.println("Check!");
        else System.out.println("Normal");
    }

    @Test
    public void testSaveAndLoadState() {
        board.printBoard();
        board.saveState();
        for (Tile tile : rookPiece.getPossibleMoves()) {
            player.movePiece(rookPiece, tile);
            break;
        }
        board.printBoard();

        Board loadedBoard = board.loadState();
        loadedBoard.printBoard();
    }

    @Test
    public void testCalculatePotentialDangerForKing() {
        Piece kingPiece = new KingPiece(player, board, board.getBoard()[3][3]);
        Piece pawnPiece = new PawnPiece(player, board, board.getBoard()[3][4], 0);
        Piece pawnPiece1 = new PawnPiece(player, board, board.getBoard()[4][2], 1);
        Piece opponentQueenPiece = new QueenPiece(opponent, board, board.getBoard()[3][7]);
        Piece opponentBishopPiece = new BishopPiece(opponent, board, board.getBoard()[5][1], 0);
        board.checkBoard();
        board.printBoard();

//        board.canKingBeInDanger(player);
//        if (board.canKingBeInDanger(player)) System.out.println("TRUE");

        Map<Piece, List<Tile>> map = board.calculatePotentialDangerForKing(player);
        for (Piece piece : map.keySet()) {
            System.out.println(piece.getName() + ": ");
            map.get(piece).forEach(System.out::println);
        }
//        player.getMoves().forEach(System.out::println);
    }

    @Test
    public void testSaveBoard() {
        Gson gson = new Gson();
        board.printBoard();
        Board boardCopy = gson.fromJson(gson.toJson(board), Board.class);
        Assertions.assertNotSame(board, boardCopy);
    }

    @Test
    public void testPipedDeepCopy() {
        Board copy = (Board) PipedDeepCopy.copy(board);
        board.printBoard();
        copy.printBoard();

        Assertions.assertNotEquals(copy, board);
    }

    @Test
    public void testDraw() {
        Board b = new Board();
        Player whitePlayer;
        Player blackPlayer;

        whitePlayer = new Player(PieceColor.WHITE);
        blackPlayer = new Player(PieceColor.BLACK);

        whitePlayer.setName("Ziv");
        blackPlayer.setName("Guy");

        whitePlayer.setAI(false);
        blackPlayer.setAI(false);

        whitePlayer.setOpponentPlayer(blackPlayer);

        b.setWhitePlayer(whitePlayer);
        b.setBlackPlayer(blackPlayer);

        whitePlayer.setBoard(b);
        blackPlayer.setBoard(b);

        b.setCurrentPlayer(whitePlayer);

        Piece whiteKing = new KingPiece(whitePlayer, b, b.getBoard()[0][4]);
        Piece blackKing = new KingPiece(blackPlayer, b, b.getBoard()[7][4]);
        Piece whiteBishop = new BishopPiece(whitePlayer, b, b.getBoard()[0][2], 0);
        Piece blackBishop = new BishopPiece(blackPlayer, b, b.getBoard()[7][2], 0);

        b.checkBoard();

        b.printBoard();

        System.out.println(b.getGameSituation());
    }

    @Test
    public void testStalemateCase0() {
        Board b = new Board();
        Player whitePlayer;
        Player blackPlayer;

        whitePlayer = new Player(PieceColor.WHITE);
        blackPlayer = new Player(PieceColor.BLACK);

        whitePlayer.setName("Ziv");
        blackPlayer.setName("Guy");

        whitePlayer.setAI(false);
        blackPlayer.setAI(false);

        whitePlayer.setOpponentPlayer(blackPlayer);

        b.setWhitePlayer(whitePlayer);
        b.setBlackPlayer(blackPlayer);

        whitePlayer.setBoard(b);
        blackPlayer.setBoard(b);

        b.setCurrentPlayer(whitePlayer);

        Piece whiteKing = new KingPiece(whitePlayer, b, b.getBoard()[0][0]);
        Piece blackKing = new KingPiece(blackPlayer, b, b.getBoard()[2][2]);
        Piece blackRook = new RookPiece(blackPlayer, b, b.getBoard()[1][1], 0);

        b.checkBoard();

        b.printBoard();

        System.out.println(b.getGameSituation());
    }

    @Test
    public void testStalemateCase1() {
        Board b = new Board();
        Player whitePlayer;
        Player blackPlayer;

        whitePlayer = new Player(PieceColor.WHITE);
        blackPlayer = new Player(PieceColor.BLACK);

        whitePlayer.setName("Ziv");
        blackPlayer.setName("Guy");

        whitePlayer.setAI(false);
        blackPlayer.setAI(false);

        whitePlayer.setOpponentPlayer(blackPlayer);

        b.setWhitePlayer(whitePlayer);
        b.setBlackPlayer(blackPlayer);

        whitePlayer.setBoard(b);
        blackPlayer.setBoard(b);

        b.setCurrentPlayer(whitePlayer);

        Piece whiteKing = new KingPiece(whitePlayer, b, b.getBoard()[0][5]);
        Piece blackKing = new KingPiece(blackPlayer, b, b.getBoard()[2][5]);
        Piece blackRook = new PawnPiece(blackPlayer, b, b.getBoard()[1][5], 0);

        b.checkBoard();

        b.printBoard();

        System.out.println(b.getGameSituation());
    }
}
