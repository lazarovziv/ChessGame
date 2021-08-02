package com.zivlazarov.test.db;

import com.zivlazarov.chessengine.db.PieceDao;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class PieceDaoTest {

    private static PieceDao pieceDao;
    private static Board board;
    private static Player player;
    private static Player opponent;

    @BeforeAll
    public static void setup() {
        pieceDao = new PieceDao();
        board = Board.getInstance();

        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        player.setOpponentPlayer(opponent);

        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        board.initBoard();

        board.checkBoard();
    }

    @Test
    public void testInsertPiece() {
        Piece piece = board.getKingsMap().get(player);
        try {
            int id = pieceDao.insertPiece(piece);
            System.out.println(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
