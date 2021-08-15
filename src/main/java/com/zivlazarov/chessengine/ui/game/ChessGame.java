package com.zivlazarov.chessengine.ui.game;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.ui.components.BoardFrame;

import java.util.ArrayList;
import java.util.List;

public class ChessGame {

    public static void main(String[] args) {
        new BoardFrame();
    }

    /*
    depth 1: 20

    depth 2: 400

    depth 3: 8902

    depth 4: 197281

    depth 5: 4865609
    */

    public static int generatedMove(Board board, int depth) {
        if (depth == 0) return 1;

        int numOfPositions = 0;

        List<Move> moves = new ArrayList<>(board.getCurrentPlayer().getMoves());

        for (Move move : moves) {
            if (!move.getTargetTile().isEmpty()) {
                if (move.getTargetTile().getPiece() instanceof KingPiece) {
                    System.out.println(move);
                }
            }
            move.makeMove(true);
            numOfPositions += generatedMove(board, depth - 1);
            move.unmakeMove(false);
        }

        return numOfPositions;
    }
}
