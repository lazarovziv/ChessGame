package com.zivlazarov.test.db;

import com.zivlazarov.chessengine.db.PlayerDao;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PlayerDaoTest {

    private static PlayerDao playerDao;
    private static Board board;

    @BeforeAll
    public static void setup() {
        playerDao = new PlayerDao();
        board = Board.getInstance();
    }

    @Test
    public void testInsertPlayer() {
        Player player = new Player(board, PieceColor.WHITE);
        player.setName("Ziv");
        Player opponent = new Player(board, PieceColor.BLACK);
        opponent.setName("Opponent");

        player.setOpponentPlayer(opponent);

        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);
        board.initBoard();

        int id = playerDao.insertPlayer(player);

        System.out.println(id);
    }
}
