package com.zivlazarov.test.db;

import com.zivlazarov.chessengine.db.PlayerDao;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;

public class PlayerDaoTest {

    private static PlayerDao playerDao;
    private static Board board;
    private static Player player;
    private static Player opponent;

    @BeforeAll
    public static void setup() {
        playerDao = new PlayerDao();
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
    public void testInsertPlayer() {
        long id = 0;
        try {
            id = playerDao.insertPlayer(player);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(id);

        Player player = null;

        try {
            player = playerDao.findPlayerByID(id);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        if (player != null)
            System.out.println(player.getPlayerColor());
    }


    @Test
    public void testDeletePlayer() {
        long id = 0;
        try {
            id = playerDao.deletePlayer(player);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(id);
    }
}
