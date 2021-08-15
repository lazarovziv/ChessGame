package com.zivlazarov.chessengine.model.move;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {

    private Board board;
    private Player player;

    public MoveGenerator(Board board, Player player) {
        this.board = board;
        this.player = player;
    }

    public List<Move> generate() {
        List<Move> moves = new ArrayList<>();
        for (Piece piece : player.getAlivePieces()) {
            moves.addAll(piece.getMoves());
        }
        return moves;
    }
}
