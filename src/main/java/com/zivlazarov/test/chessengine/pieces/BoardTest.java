package com.zivlazarov.test.chessengine.pieces;

import com.google.gson.Gson;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.Converter;
import com.zivlazarov.chessengine.model.utils.PipedDeepCopy;
import com.zivlazarov.chessengine.ui.components.BoardFrame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoardTest {

    private static Board board;
    private static KingPiece kingPiece;
    private static KnightPiece knightPiece;
    private static PawnPiece opponentPawnPiece;
    private static PawnPiece opponentPawnPiece1;
    private static BishopPiece opponentBishopPiece;
    private static Player player;
    private static Player opponent;
    private static RookPiece rookPiece;
    private static RookPiece rookPiece1;

    private static BoardFrame frame;

    @BeforeAll
    public static void setup() {
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        player.setName("Ziv");
        opponent.setName("Guy");
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);
        board.initBoard();
        board.setCurrentPlayer(opponent);
        board.checkBoard();
//        rookPiece = new RookPiece(player, board, PieceColor.WHITE, board.getBoard()[0][7], false, 0);
//        rookPiece1 = new RookPiece(player, board, PieceColor.WHITE, board.getBoard()[0][0], true, 1);
//        kingPiece = new KingPiece(player, board, PieceColor.WHITE, board.getBoard()[0][4]);
//        knightPiece = new KnightPiece(player, board, PieceColor.WHITE, board.getBoard()[1][4], 0);
//        opponentPawnPiece = new PawnPiece(opponent, board, PieceColor.BLACK, board.getBoard()[3][4], 0);
//        opponentPawnPiece1 = new PawnPiece(opponent, board, PieceColor.BLACK, board.getBoard()[5][0], 1);
//        opponentBishopPiece = new BishopPiece(opponent, board, PieceColor.BLACK, board.getBoard()[4][0], 0);
//        board.initBoard();
//        board.setCurrentPlayer(player);
//        board.checkBoard(board.getCurrentPlayer());
    }

    @Test
    public void testDistanceBetweenPieces() {
        board.printBoard();
        int distance = board.distanceBetweenPieces(kingPiece, opponentPawnPiece);
        int distance1 = board.distanceBetweenPieces(kingPiece, opponentPawnPiece1);
        System.out.println(distance);
        System.out.println(distance1);
        Assertions.assertEquals(2, distance);
        Assertions.assertEquals(4, distance1);
    }

    @Test
    public void testLegalMovesInitialization() {
        board.printBoard();
        board.checkBoard();
        System.out.println(board.getGameSituation());
    }

    @Test
    public void testCalculatePotentialDangerForKing() {
        Piece kingPiece = new KingPiece(player, board, board.getBoard()[3][3]);
        Piece pawnPiece = new PawnPiece(player, board, board.getBoard()[3][4], 0);
        Piece pawnPiece1 = new PawnPiece(player, board, board.getBoard()[4][2], 1);
        Piece opponentQueenPiece = new QueenPiece(opponent, board, board.getBoard()[3][7]);
        Piece opponentBishopPiece = new BishopPiece(opponent, board, board.getBoard()[5][1], 0);
        board.checkBoard();
        board.printBoard();

//        board.canKingBeInDanger(player);
//        if (board.canKingBeInDanger(player)) System.out.println("TRUE");

        Map<Piece, List<Tile>> map = board.calculatePotentialThreatsForKing(player);
        for (Piece piece : map.keySet()) {
            System.out.println(piece.getName() + ": ");
            map.get(piece).forEach(System.out::println);
        }
//        player.getMoves().forEach(System.out::println);
    }

    @Test
    public void testSaveBoard() {
        Gson gson = new Gson();
        board.printBoard();
        Board boardCopy = gson.fromJson(gson.toJson(board), Board.class);
        Assertions.assertNotSame(board, boardCopy);
    }

    @Test
    public void testPipedDeepCopy() {
        Board copy = (Board) PipedDeepCopy.copy(board);

        Move move = (Move) copy.getCurrentPlayer().getMoves().toArray()[0];
        move.makeMove(true, true);

        board.printBoard();
        copy.printBoard();

        Assertions.assertNotEquals(copy, board);
    }

    @Test
    public void testDraw() {
        Board b = new Board();
        Player whitePlayer;
        Player blackPlayer;

        whitePlayer = new Player(PieceColor.WHITE);
        blackPlayer = new Player(PieceColor.BLACK);

        whitePlayer.setName("Ziv");
        blackPlayer.setName("Guy");

        whitePlayer.setAI(false);
        blackPlayer.setAI(false);

        whitePlayer.setOpponent(blackPlayer);

        b.setWhitePlayer(whitePlayer);
        b.setBlackPlayer(blackPlayer);

        whitePlayer.setBoard(b);
        blackPlayer.setBoard(b);

        b.setCurrentPlayer(whitePlayer);

        Piece whiteKing = new KingPiece(whitePlayer, b, b.getBoard()[0][4]);
        Piece blackKing = new KingPiece(blackPlayer, b, b.getBoard()[7][4]);
        Piece whiteBishop = new BishopPiece(whitePlayer, b, b.getBoard()[0][2], 0);
        Piece blackBishop = new BishopPiece(blackPlayer, b, b.getBoard()[7][2], 0);

        b.checkBoard();

        b.printBoard();

        System.out.println(b.getGameSituation());
    }

    @Test
    public void testStalemateCase0() {
        Board b = new Board();
        Player whitePlayer;
        Player blackPlayer;

        whitePlayer = new Player(PieceColor.WHITE);
        blackPlayer = new Player(PieceColor.BLACK);

        whitePlayer.setName("Ziv");
        blackPlayer.setName("Guy");

        whitePlayer.setAI(false);
        blackPlayer.setAI(false);

        whitePlayer.setOpponent(blackPlayer);

        b.setWhitePlayer(whitePlayer);
        b.setBlackPlayer(blackPlayer);

        whitePlayer.setBoard(b);
        blackPlayer.setBoard(b);

        b.setCurrentPlayer(whitePlayer);

        Piece whiteKing = new KingPiece(whitePlayer, b, b.getBoard()[0][0]);
        Piece blackKing = new KingPiece(blackPlayer, b, b.getBoard()[2][2]);
        Piece blackRook = new RookPiece(blackPlayer, b, b.getBoard()[1][1], 0);

        b.checkBoard();

        b.printBoard();

        System.out.println(b.getGameSituation());
    }

    @Test
    public void testStalemateCase1() {
        Board b = new Board();
        Player whitePlayer;
        Player blackPlayer;

        whitePlayer = new Player(PieceColor.WHITE);
        blackPlayer = new Player(PieceColor.BLACK);

        whitePlayer.setName("Ziv");
        blackPlayer.setName("Guy");

        whitePlayer.setAI(false);
        blackPlayer.setAI(false);

        whitePlayer.setOpponent(blackPlayer);

        b.setWhitePlayer(whitePlayer);
        b.setBlackPlayer(blackPlayer);

        whitePlayer.setBoard(b);
        blackPlayer.setBoard(b);

        b.setCurrentPlayer(whitePlayer);

        Piece whiteKing = new KingPiece(whitePlayer, b, b.getBoard()[0][5]);
        Piece blackKing = new KingPiece(blackPlayer, b, b.getBoard()[2][5]);
        Piece blackRook = new PawnPiece(blackPlayer, b, b.getBoard()[1][5], 0);

        b.checkBoard();

        b.printBoard();

        System.out.println(b.getGameSituation());
    }

    @Test
    public void testConvertToFENString() {
        Board board = new Board();
        Player player = new Player(board, PieceColor.WHITE);
        Player opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        board.initBoard();
        board.checkBoard();

        Piece pawn = null;
        for (Piece piece : board.getCurrentPlayer().getAlivePieces()) {
            if (piece instanceof PawnPiece) {
                pawn = piece;
            }
        }

        Move move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawn)
                .targetTile(board.getBoard()[pawn.getCurrentTile().getRow() + 2* player.getPlayerDirection()][pawn.getCurrentTile().getCol()])
                .build();

        move.makeMove(true, true);
        board.printBoard();

        Move oMove = (Move) board.getCurrentPlayer().getMoves().toArray()[0];
        oMove.makeMove(true, true);
        board.printBoard();

        System.out.println(Converter.convertBoardToFENString(board));
    }

    public String convertToFENStringSample() {
        Board board = new Board();
        Player player = new Player(board, PieceColor.WHITE);
        Player opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        board.initBoard();
        board.checkBoard();

        return Converter.convertBoardToFENString(board);
    }

    @Test
    public void testConvertFENStringToBoard() {
        Board board = Converter.convertFENStringToBoard(convertToFENStringSample());
        board.printBoard();
    }

    @Test
    public void testZobristHash() {
        Board board = new Board();
        Player player = new Player(board, PieceColor.WHITE);
        Player opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        board.initBoard();
        board.checkBoard();
    }

    @Test
    public void testPossibleMovesWhenInPotentialDanger() {
        Board board = new Board();
        Player player = new Player(board, PieceColor.WHITE);
        Player opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        frame = new BoardFrame();


        Piece whiteKing = new KingPiece(player, board, board.getBoard()[4][2]);
        Piece whitePawn = new PawnPiece(player, board, board.getBoard()[3][3], 0);
        Piece blackPawn = new PawnPiece(opponent, board, board.getBoard()[6][4], 0);
        Piece blackRook = new RookPiece(opponent, board, board.getBoard()[4][6], 0);
        Piece blackKing = new KingPiece(opponent, board, board.getBoard()[7][7]);

        board.printBoard();
        board.checkBoard();

        List<Move> wPawn = whitePawn.getMoves().stream().toList();
        System.out.println(wPawn.get(0));
        wPawn.get(0).makeMove(true, true);
        board.printBoard();

        List<Move> oPawnMoves = blackPawn.getMoves().stream().filter(move -> move.getTargetTile().equals(
                board.getBoard()[4][4]
        )).toList();
        Move blackPawnLongMove = oPawnMoves.get(0);
        System.out.println(blackPawnLongMove);
        blackPawnLongMove.makeMove(true, true);
        board.printBoard();

        whitePawn.getMoves().forEach(System.out::println);
        board.printBoard();

        // asserting one move because "there should be an en passant move" but the king prevents it
        Assertions.assertEquals(1, whitePawn.getMoves().size());
    }

    @Test
    public void testStates() {
        Board board = new Board();
        Player player = new Player(board, PieceColor.WHITE);
        Player opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        board.initBoard();
        board.checkBoard();

        Move move = (Move) board.getCurrentPlayer().getMoves().toArray()[0];

        move.makeMove(true, true);
        board.printBoard();

        move = (Move) board.getCurrentPlayer().getMoves().toArray()[0];

        move.makeMove(true, true);
        board.printBoard();

        Tile[][] boardTiles = board.getBoardStates().firstElement();
        board.setBoard(boardTiles);
        board.printBoard();
    }

    @Test
    public void testGeneratedMovesDepth1() {
        Board board = new Board();
        Player player = new Player(board, PieceColor.WHITE);
        Player opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        board.initBoard();
        board.checkBoard();

        Assertions.assertEquals(20, generatedMove(board, 1));
    }

    @Test
    public void testGeneratedMovesDepth2() {
        Board board = new Board();
        Player player = new Player(board, PieceColor.WHITE);
        Player opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        board.initBoard();
        board.checkBoard();

        Assertions.assertEquals(400, generatedMove(board, 2));
    }

    @Test
    public void testGeneratedMovesDepth3() {
        Board board = new Board();
        Player player = new Player(board, PieceColor.WHITE);
        Player opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        board.initBoard();
        board.checkBoard();

        Assertions.assertEquals(8902, generatedMove(board, 3));
    }

    @Test
    public void testGenerateMovesDepth4() {
        Board board = new Board();
        Player player = new Player(board, PieceColor.WHITE);
        Player opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        board.initBoard();
        board.checkBoard();

        Assertions.assertEquals(197281, generatedMove(board, 4));
    }

    @Test
    public void testGenerateMovesDepth5() {
        Board board = new Board();
        Player player = new Player(board, PieceColor.WHITE);
        Player opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        board.initBoard();
        board.checkBoard();

        Assertions.assertEquals(4865609, generatedMove(board, 5));
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
            move.makeMove(true, true);
            numOfPositions += generatedMove(board, depth - 1);
            move.unmakeMove(false);
        }

        return numOfPositions;
    }

    @Test
    public void testMoveOnNewBoard() {
        Board board = new Board();
        Player player = new Player(board, PieceColor.WHITE);
        Player opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);

        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        player.setBoard(board);
        opponent.setBoard(board);

        board.setCurrentPlayer(player);

        board.initBoard();
        board.checkBoard();

        board.printBoard();

        Board newBoard = new Board();
        newBoard.setWhitePlayer(player);
        newBoard.setBlackPlayer(opponent);
        newBoard.setCurrentPlayer(player);

        player.setBoard(newBoard);
        opponent.setBoard(newBoard);

        for (Piece piece : player.getAlivePieces()) {
            int row = piece.getRow();
            int col = piece.getCol();

            newBoard.getBoard()[row][col].setPiece(piece);
        }

        for (Piece piece : opponent.getAlivePieces()) {
            int row = piece.getRow();
            int col = piece.getCol();

            newBoard.getBoard()[row][col].setPiece(piece);
        }

        System.out.println();

        newBoard.printBoard();

        Piece piece = player.getAlivePieces().get(2);

    }
}
