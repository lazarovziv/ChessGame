package com.zivlazarov.test.db;

import com.zivlazarov.chessengine.db.dao.MoveDao;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

public class MoveDaoTest {

    private static MoveDao moveDao;
    private static Board board;
    private static Player player;
    private static Player opponent;

    @BeforeAll
    public static void setup() {
        moveDao = new MoveDao();
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
    public void testInsertMove() {
        List<Move> moves = opponent.getMoves().stream().filter(m -> m.getMovingPiece().getCurrentTile().getCol() == 4).toList();
        Move move = moves.get(0);
        try {
            int id = moveDao.insertMove(move);
            System.out.println(id);
            System.out.println(opponent.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
