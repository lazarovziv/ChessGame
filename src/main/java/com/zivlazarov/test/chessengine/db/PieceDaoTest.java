package com.zivlazarov.test.chessengine.db;

import com.zivlazarov.chessengine.db.dao.PieceDao;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

public class PieceDaoTest {

    private static PieceDao pieceDao;
    private static Board board;
    private static Player player;
    private static Player opponent;

    @BeforeAll
    public static void setup() {
        pieceDao = new PieceDao();
        board = new Board();

        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);

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

    @Test
    public void testInsertAllPieces() {
        List<Piece> pieces = player.getAlivePieces();
        int [] ids = pieceDao.insertAllPieces(pieces);
        for (int id : ids) System.out.println(id);
    }
}
