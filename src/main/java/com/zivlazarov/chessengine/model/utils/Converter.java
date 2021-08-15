package com.zivlazarov.chessengine.model.utils;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.player.Player;

public class Converter {

    /*
    * function board_to_fen(board)
{
    let result = "";
    for(let y = 0; y < board.length; y++)
    {
        let empty = 0;
        for(let x = 0; x < board[y].length; x++)
        {
            let c = board[y][x][0];  // Fixed
            if(c == 'w' || c == 'b') {
                if(empty > 0)
                {
                    result += empty.toString();
                    empty = 0;
                }
                if(c == 'w')
                {
                    result += board[y][x][1].toUpperCase();  // Fixed
                } else {
                    result += board[y][x][1].toLowerCase();  // Fixed
                }
            } else {
                empty += 1;
            }
        }
        if(empty > 0)   // Fixed
        {
            result += empty.toString();
        }
        if(y < board.length - 1)  // Added to eliminate last '/'
        {
          result += '/';
        }
    }
    result += ' w KQkq - 0 1';
    return result;
}*/

    private static final char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    private static final int[] nums = {1, 2, 3, 4, 5, 6, 7, 8};

    public static String convertBoardToFENString(Board board) {
        StringBuilder builder = new StringBuilder();
        for (int row = board.getBoard().length - 1; row >= 0; row--) {
            int numOfEmptyTiles = 0;
            for (int col = 0; col < board.getBoard().length; col++) {
                Tile tile = board.getBoard()[row][col];
                if (!tile.isEmpty()) {
                    if (numOfEmptyTiles > 0) builder.append(numOfEmptyTiles);
                    numOfEmptyTiles = 0;
                    if (tile.getPiece().getPieceColor() == PieceColor.WHITE) {
                        builder.append(tile.getPiece().getName().substring(1).toUpperCase());
                    } else builder.append(tile.getPiece().getName().substring(1).toLowerCase());
                } else {
                    numOfEmptyTiles += 1;
                }
            }
            if (numOfEmptyTiles == 8) builder.append(8);
            else if (numOfEmptyTiles > 0) builder.append(numOfEmptyTiles);
            if (row > 0)
                builder.append("/");
        }

        char currentTurn = board.getCurrentPlayer().getColor() == PieceColor.WHITE ? 'w' : 'b';
        builder.append(" ").append(currentTurn).append(" ");

        int numOfCantCastle = 0;

        if (!board.getWhitePlayer().getKing().hasExecutedKingSideCastle()) {
            builder.append("K");
        } else numOfCantCastle++;
        if (!board.getWhitePlayer().getKing().hasExecutedQueenSideCastle()) {
            builder.append("Q");
        } else numOfCantCastle++;
        if (!board.getBlackPlayer().getKing().hasExecutedKingSideCastle()) {
            builder.append("k");
        } else numOfCantCastle++;
        if (!board.getBlackPlayer().getKing().hasExecutedQueenSideCastle()) {
            builder.append("q");
        } else numOfCantCastle++;

        if (numOfCantCastle == 4) builder.append("-").append(" ");

        if (board.getMatchPlays().size() > 0) {
            Piece piece = board.getMatchPlays().peek().getMovingPiece();
            if (piece instanceof PawnPiece) {
                if (((PawnPiece) piece).hasMovedLong()) {
                    Tile pieceTile = piece.getCurrentTile();
                    char row = letters[pieceTile.getRow() - piece.getPlayer().getPlayerDirection()];
                    int column = nums[pieceTile.getCol()];
                    builder.append(" ").append(row).append(column);
                }
            }
        }

        builder.append(" ")
                .append(board.getWhitePlayer().getTurnsPlayed() + board.getBlackPlayer().getTurnsPlayed())
                .append(" ")
                .append(board.getCurrentPlayer().getOpponent().getTurnsPlayed());

        return builder.toString();
    }

    // rnbqkbnr/ppppppp1/8/7p/7P/8/PPPPPPP1/RNBQKBNR w - f8 2 1

    public static Board convertFENStringToBoard(String fen) {
        System.out.println(fen);

        Board board = new Board();
        Player player = new Player(board, PieceColor.WHITE);
        Player opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        int whitePawnCounter = 0;
        int whiteKnightCounter = 0;
        int whiteBishopCounter = 0;
        int whiteRookCounter = 0;
        int blackPawnCounter = 0;
        int blackKnightCounter = 0;
        int blackBishopCounter = 0;
        int blackRookCounter = 0;

        int tileNum = 0;

        for (int i = 0; i < fen.length(); i++) {
            char c = fen.charAt(i);

            if (c == '/') continue;
//            if (c == 'w') break;

            System.out.println(tileNum);

            if (!Character.isLetter(c)) {
                if (Character.isDigit(c)) {
                    // if gone to a digit, skip to the tile where it's not a digit by the digit number
                    tileNum += c - 1;
                    continue;
                } else if (Character.isWhitespace(c)) break;
            }

            switch (c) {
                case 'p' -> {
                    Piece pawnPiece = new PawnPiece(opponent, board, board.getTileByNumberTo63(tileNum), blackPawnCounter);
                    blackPawnCounter++;
                    System.out.println(pawnPiece.getCurrentTile());
                }
                case 'P' -> {
                    Piece pawnPiece = new PawnPiece(player, board, board.getTileByNumberTo63(tileNum), whitePawnCounter);
                    whitePawnCounter++;
                    System.out.println(pawnPiece.getCurrentTile());
                }
                case 'n' -> {
                    Piece knightPiece = new KnightPiece(opponent, board, board.getTileByNumberTo63(tileNum), blackKnightCounter);
                    blackKnightCounter++;
                    System.out.println(knightPiece.getCurrentTile());
                }
                case 'N' -> {
                    Piece knightPiece = new KnightPiece(player, board, board.getTileByNumberTo63(tileNum), whiteKnightCounter);
                    whiteKnightCounter++;
                    System.out.println(knightPiece.getCurrentTile());
                }
                case 'b' -> {
                    Piece bishopPiece = new BishopPiece(opponent, board, board.getTileByNumberTo63(tileNum), blackBishopCounter);
                    blackBishopCounter++;
                    System.out.println(bishopPiece.getCurrentTile());
                }
                case 'B' -> {
                    Piece bishopPiece = new BishopPiece(player, board, board.getTileByNumberTo63(tileNum), whiteBishopCounter);
                    whiteBishopCounter++;
                    System.out.println(bishopPiece.getCurrentTile());
                }
                case 'r' -> {
                    Piece rookPiece = new RookPiece(opponent, board, board.getTileByNumberTo63(tileNum), blackRookCounter);
                    blackRookCounter++;
                    System.out.println(rookPiece.getCurrentTile());
                }
                case 'R' -> {
                    Piece rookPiece = new RookPiece(player, board, board.getTileByNumberTo63(tileNum), whiteRookCounter);
                    whiteRookCounter++;
                    System.out.println(rookPiece.getCurrentTile());
                }
                case 'q' -> {
                    Piece queenPiece = new QueenPiece(opponent, board, board.getTileByNumberTo63(tileNum));
                    System.out.println(queenPiece.getCurrentTile());
                }
                case 'Q' -> {
                    Piece queenPiece = new QueenPiece(player, board, board.getTileByNumberTo63(tileNum));
                    System.out.println(queenPiece.getCurrentTile());
                }
                case 'k' -> {
                    Piece kingPiece = new KingPiece(opponent, board, board.getTileByNumberTo63(tileNum));
                    System.out.println(kingPiece.getCurrentTile());
                }
                case 'K' -> {
                    Piece kingPiece = new KingPiece(player, board, board.getTileByNumberTo63(tileNum));
                    System.out.println(kingPiece.getCurrentTile());
                }
            }
            tileNum++;
        }

        return board;
    }
}
