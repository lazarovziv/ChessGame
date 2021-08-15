package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.pieces.RookPiece;
import com.zivlazarov.chessengine.model.player.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MoveTest {

    private static Board board;

    private static Player player;
    private static Player opponent;

    private static Piece pawnPiece;
    private static Piece opponentPawnPiece;

    @Test
    public void testMove() {
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);
        Piece king = new KingPiece(player, board, board.getBoard()[5][5]);
        Piece oKing = new KingPiece(opponent, board, board.getBoard()[7][5]);
        pawnPiece = new PawnPiece(player, board, board.getBoard()[4][2], 0);
        opponentPawnPiece = new PawnPiece(opponent, board, board.getBoard()[6][3], 0);

        board.checkBoard();

        Move move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawnPiece)
                .targetTile(board.getBoard()[pawnPiece.getCurrentTile().getRow()+player.getPlayerDirection()][pawnPiece.getCurrentTile().getCol()])
                .build();

        board.printBoard();
        move.makeMove(true);
        board.printBoard();
    }

    @Test
    public void testUnmakeMove() {
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);
        Piece king = new KingPiece(player, board, board.getBoard()[5][5]);
        Piece oKing = new KingPiece(opponent, board, board.getBoard()[7][5]);
        pawnPiece = new PawnPiece(player, board, board.getBoard()[4][2], 0);
        opponentPawnPiece = new PawnPiece(opponent, board, board.getBoard()[6][3], 0);

        board.checkBoard();

        Move move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawnPiece)
                .targetTile(board.getBoard()[pawnPiece.getCurrentTile().getRow() + player.getPlayerDirection()][pawnPiece.getCurrentTile().getCol()])
                .build();

        board.printBoard();
        move.makeMove(true);
        board.printBoard();
        move.unmakeMove(true);
        board.printBoard();

        Assertions.assertTrue(pawnPiece.getCurrentTile().equals(board.getBoard()[4][2]));

        move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(player.getKing())
                .targetTile(board.getBoard()[4][4])
                .build();

        move.makeMove(true);
        board.printBoard();
        move.unmakeMove(true);
        board.printBoard();

        Assertions.assertTrue(king.getCurrentTile().equals(board.getBoard()[5][5]));
    }

    @Test
    public void testUnmakeMoveEnPassant() {
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        Piece king = new KingPiece(player, board, board.getBoard()[5][5]);
        Piece oKing = new KingPiece(opponent, board, board.getBoard()[7][5]);
        pawnPiece = new PawnPiece(player, board, board.getBoard()[3][2], 0);
        opponentPawnPiece = new PawnPiece(opponent, board, board.getBoard()[6][3], 0);

        board.checkBoard();

        board.printBoard();

        Move move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawnPiece)
                .targetTile(board.getBoard()[4][2])
                .build();

        move.makeMove(true);
        board.printBoard();

        Move oMove = new Move.Builder()
                .board(board)
                .player(opponent)
                .movingPiece(opponentPawnPiece)
                .targetTile(board.getBoard()[4][3])
                .build();

        oMove.makeMove(true);
        board.printBoard();

        pawnPiece.getMoves().forEach(System.out::println);

        move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawnPiece)
                .targetTile(((PawnPiece) pawnPiece).getEnPassantTile())
                .build();

        move.makeMove(true);
        board.printBoard();

        move.unmakeMove(true);
        board.printBoard();

        Assertions.assertTrue(opponentPawnPiece.isAlive() && opponentPawnPiece.getCurrentTile().equals(board.getBoard()[4][3])
        && pawnPiece.getCurrentTile().equals(board.getBoard()[4][2]));
    }

    @Test
    public void testUnmakePawnPromotion() {
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);
        Piece king = new KingPiece(player, board, board.getBoard()[5][5]);
        Piece oKing = new KingPiece(opponent, board, board.getBoard()[7][5]);
        pawnPiece = new PawnPiece(player, board, board.getBoard()[4][2], 0);
        opponentPawnPiece = new PawnPiece(opponent, board, board.getBoard()[6][3], 0);

        board.checkBoard();

        Move move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawnPiece)
                .targetTile(board.getBoard()[pawnPiece.getCurrentTile().getRow() + 1][pawnPiece.getCurrentTile().getCol()])
                .build();

        board.printBoard();
        move.makeMove(true);
        board.printBoard();

        move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawnPiece)
                .targetTile(board.getBoard()[pawnPiece.getCurrentTile().getRow() + 1][pawnPiece.getCurrentTile().getCol()])
                .build();

        move.makeMove(true);
        board.printBoard();

        player.getAlivePieces().forEach(System.out::println);

        move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawnPiece)
                .targetTile(board.getBoard()[pawnPiece.getCurrentTile().getRow() + 1][pawnPiece.getCurrentTile().getCol()])
                .build();

        move.makeMove(true);
        board.printBoard();

//        Assertions.assertTrue(pawnPiece instanceof QueenPiece);

        move.unmakeMove(true);
        board.printBoard();

        Assertions.assertTrue(pawnPiece.isAlive() && pawnPiece.getCurrentTile().equals(board.getBoard()[6][2]));
    }

    @Test
    public void testUnmakeCastling() {
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);
        Piece king = new KingPiece(player, board, board.getBoard()[0][4]);
        Piece oKing = new KingPiece(opponent, board, board.getBoard()[7][5]);
        Piece kingSide = new RookPiece(player, board, board.getBoard()[0][7], 0);
        Piece queenSide = new RookPiece(player, board, board.getBoard()[0][0], 0);

        board.checkBoard();
        board.printBoard();

        kingSide.getMoves().forEach(System.out::println);

        Move kingSideCastle = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(king)
                .targetTile(board.getBoard()[0][6])
                .build();
        kingSideCastle.makeMove(true);
        board.printBoard();

        kingSideCastle.unmakeMove(true);
        board.printBoard();

        Assertions.assertTrue(king.getCurrentTile().equals(board.getBoard()[0][4])
                && kingSide.getCurrentTile().equals(board.getBoard()[0][7]));

        Move queenSideCastle = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(king)
                .targetTile(board.getBoard()[0][2])
                .build();
        queenSideCastle.makeMove(true);
        board.printBoard();

        queenSideCastle.unmakeMove(true);
        board.printBoard();

        Assertions.assertTrue(king.getCurrentTile().equals(board.getBoard()[0][4])
                && queenSide.getCurrentTile().equals(board.getBoard()[0][0]));
    }

    @Test
    public void testMoveEquals() {
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);
        Piece king = new KingPiece(player, board, board.getBoard()[5][5]);
        Piece oKing = new KingPiece(opponent, board, board.getBoard()[7][5]);
        pawnPiece = new PawnPiece(player, board, board.getBoard()[4][2], 0);
        opponentPawnPiece = new PawnPiece(opponent, board, board.getBoard()[6][3], 0);

        board.checkBoard();

        Move move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(king)
                .targetTile(board.getBoard()[4][5])
                .build();

        Move move1 = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(king)
                .targetTile(board.getBoard()[4][5])
                .build();

        Assertions.assertTrue(move.equals(move1));
    }

    @Test
    public void testMovesGenerated(int depth) {
        System.out.println(generatedMove(3));
    }

    public int generatedMove(int depth) {
        Board board = new Board();
        Player player = new Player(board, PieceColor.WHITE);
        Player opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        board.initBoard();
        board.checkBoard();

        if (depth == 0) return 1;

        List<Move> moves = board.getCurrentPlayer().getMoves().stream().toList();
        int numOfPositions = 0;

        for (Move move : moves) {
            move.makeMove(true);
            numOfPositions += generatedMove(depth - 1);
            move.unmakeMove(true);
        }

        return numOfPositions;
    }
}
