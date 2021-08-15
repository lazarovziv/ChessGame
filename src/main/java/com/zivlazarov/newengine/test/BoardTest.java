package com.zivlazarov.newengine.test;

import com.zivlazarov.newengine.Board;
import com.zivlazarov.newengine.Move;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BoardTest {

    @Test
    public void testBoardInit() {
        Board.init();
        Board.printBoardWithLetters();

        System.out.println();
        System.out.println();

        List<Move> moves = Board.generateMoves();

        System.out.println(moves.stream().filter(move -> move.getSourceSquare() >= 8 && move.getSourceSquare() <= 15).toList().size());
        moves.stream().filter(move -> move.getSourceSquare() >= 8 && move.getSourceSquare() <= 15).forEach(System.out::println);
    }
}
