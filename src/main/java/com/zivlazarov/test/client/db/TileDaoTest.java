package com.zivlazarov.test.client.db;

import com.zivlazarov.chessengine.db.dao.TileDao;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.Tile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class TileDaoTest {

    private static TileDao tileDao;
    private static Board board;

    @BeforeAll
    public static void setup() {
        tileDao = new TileDao();
        board = new Board();
    }

//    @Test
//    public void testInsertTile() {
//        Tile tile = board.getBoard()[4][5];
//        tileDao.insertTile(tile);
//    }
//
//    @Test
//    public void testFindTileByRowAndCol() {
//        int row = 4;
//        int col = 5;
//
//        Tile tile = tileDao.findTileByRowAndCol(row, col);
//    }
//
//    @Test
//    public void testDeleteTile() {
//        Tile tile = board.getBoard()[4][5];
//        tileDao.deleteTile(tile);
//    }

    @Test
    public void testInsertTile() {
        Tile tile = board.getBoard()[4][7];
        int id = 0;
        try {
            id = tileDao.insertTile(tile);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(id);
    }

    @Test
    public void testDeleteTile() {
        Tile tile = board.getBoard()[4][7];
        int id = 0;
        try {
            id = tileDao.deleteTile(tile);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(id);
    }
}
