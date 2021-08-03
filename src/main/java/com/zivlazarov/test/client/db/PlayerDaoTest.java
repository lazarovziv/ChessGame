package com.zivlazarov.test.client.db;

import com.zivlazarov.chessengine.client.db.PlayerDao;
import com.zivlazarov.chessengine.client.model.board.Board;
import com.zivlazarov.chessengine.client.model.board.PieceColor;
import com.zivlazarov.chessengine.client.model.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

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
        int id = 0;
        try {
            id = playerDao.insertPlayer(opponent);
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
        int id = 0;
        try {
            id = playerDao.deletePlayer(player);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(id);
    }
}
