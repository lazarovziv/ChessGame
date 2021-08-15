package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.ChessPiece;
import com.zivlazarov.chessengine.model.pieces.PieceType;
import com.zivlazarov.chessengine.model.player.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestChessPiece {

    @Test
    public void testPieceType() {
        Board board = new Board();
        Player player = new Player(board, PieceColor.WHITE);
        Player opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        ChessPiece whiteRook = new ChessPiece(player, board, PieceType.ROOK, board.getBoard()[0][0]);
        ChessPiece whiteKing = new ChessPiece(player, board, PieceType.KING, board.getBoard()[0][4]);
        ChessPiece blackKing = new ChessPiece(opponent, board, PieceType.KING, board.getBoard()[0][4]);

        Assertions.assertEquals(whiteRook.getPieceType(), PieceType.ROOK);
    }

    @Test
    public void testPawnPromotion() {
        Board board = new Board();
        Player player = new Player(board, PieceColor.WHITE);
        Player opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);
        ChessPiece whiteKing = new ChessPiece(player, board, PieceType.KING, board.getBoard()[0][4]);
        ChessPiece blackKing = new ChessPiece(opponent, board, PieceType.KING, board.getBoard()[0][4]);

        ChessPiece pawnPiece = new ChessPiece(player, board, PieceType.PAWN, board.getBoard()[6][1]);
        pawnPiece.refresh();
        board.printBoard();
        Move move = (Move) pawnPiece.getMoves().toArray()[0];
        move.makeMove(true);
    }
}
