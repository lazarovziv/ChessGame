package com.zivlazarov.test.client.utils;

import com.zivlazarov.chessengine.model.board.Zobrist;
import org.junit.jupiter.api.Test;

public class ZobristTest {

    @Test
    public void testRandom64() {
        System.out.println(Zobrist.random64());
    }

    @Test
    public void testDistribution() {
        int sampleSize = 2000;
        int sampleSeconds = 3;

        long startTime = System.currentTimeMillis();
        long endTime = startTime + sampleSeconds * 1000;

        int[] distArray = new int[sampleSize];

        while (System.currentTimeMillis() <= endTime) {
            for (int i = 0; i < 10000; i++) {
                distArray[(int) (Zobrist.random64() % (sampleSize / 2)) + (sampleSize / 2)]++;
            }
        }

        for (int number : distArray) {
            System.out.println(number);
        }
    }

    @Test
    public void testZobristFillArray() {
        Zobrist.zobristFillArray();
        for (long[][] one : Zobrist.zobristArray) {
            System.out.println();
            for (long[] two : one) {
                System.out.println();
                for (long num : two) System.out.print(num + ", ");
            }
        }
    }
}
