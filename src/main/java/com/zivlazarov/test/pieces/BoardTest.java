package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.Memento;
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
        board = Board.getInstance();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        player.setOpponentPlayer(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);
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
    public void testUnmakeLastMove() {
        System.out.println(rookPiece1.getCurrentTile());
        board.printBoard();
//        rookPiece1.moveToTile(board.getBoard()[0][0]);
        board.printBoard();
        board.checkBoard();
        board.unmakeLastMove(rookPiece1);
        board.printBoard();
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
    public void testMemento() {
        board.printBoard();
        Memento<Board> boardMemento = board.saveToMemento();
        Memento<Player> playerMemento = player.saveToMemento();
        player.movePiece(kingPiece, board.getBoard()[0][3]);
        board.printBoard();
        board.restoreFromMemento(boardMemento);
        board.printBoard();

    }

    @Test
    public void testStalemate() {
        Piece kingPiece = new KingPiece(player, board, player.getPlayerColor(), board.getBoard()[7][7], true);
        Piece opponentKingPiece = new KingPiece(opponent, board, opponent.getPlayerColor(), board.getBoard()[6][4], true);
        Piece opponentQueenPiece = new QueenPiece(opponent, board, opponent.getPlayerColor(), board.getBoard()[5][6]);
        board.printBoard();
        board.checkBoard();
//        opponentKingPiece.getMoves().get(3).makeMove(true);
        board.printBoard();
        System.out.println(board.getGameSituation());
        player.getMoves().forEach(System.out::println);
    }

    @Test
    public void testCalculatePotentialDangerForKing() {
        Piece kingPiece = new KingPiece(player, board, player.getPlayerColor(), board.getBoard()[3][3], true);
        Piece pawnPiece = new PawnPiece(player, board, player.getPlayerColor(), board.getBoard()[3][4], 0);
        Piece pawnPiece1 = new PawnPiece(player, board, player.getPlayerColor(), board.getBoard()[4][2], 1);
        Piece opponentQueenPiece = new QueenPiece(opponent, board, opponent.getPlayerColor(), board.getBoard()[3][7]);
        Piece opponentBishopPiece = new BishopPiece(opponent, board, opponent.getPlayerColor(), board.getBoard()[5][1], 0);
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
}
