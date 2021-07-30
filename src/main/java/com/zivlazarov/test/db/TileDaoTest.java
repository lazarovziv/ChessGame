package com.zivlazarov.test.db;

import com.zivlazarov.chessengine.db.TileDao;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.Tile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TileDaoTest {

    private static TileDao tileDao;
    private static Board board;

    @BeforeAll
    public static void setup() {
        tileDao = new TileDao();
        board = Board.getInstance();
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
        int id = tileDao.insertTile(tile);
        System.out.println(id);
    }
}
