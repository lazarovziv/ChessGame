package com.zivlazarov.chessengine.ui;

import com.zivlazarov.chessengine.controllers.BoardController;
import com.zivlazarov.chessengine.controllers.PlayerController;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.GameSituation;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandLineGame {

    private final static Board board = Board.getInstance();
    private static BoardController boardController;

    private static PlayerController playerController;
    private static Player whitePlayer;
    private static Player blackPlayer;

    private static GameSituation gameSituation;

    public static void main(String[] args) {

        whitePlayer = new Player(board, PieceColor.WHITE);
        blackPlayer = new Player(board, PieceColor.BLACK);

        whitePlayer.setName("Ziv");
        blackPlayer.setName("Guy");

        Map<GameSituation, String> gameSituationsMap = new HashMap<>();

        gameSituationsMap.put(GameSituation.WHITE_IN_CHECK, whitePlayer.getName() + " is in check!");
        gameSituationsMap.put(GameSituation.BLACK_IN_CHECK, blackPlayer.getName() + " is in check!)");
        gameSituationsMap.put(GameSituation.WHITE_CHECKMATED, "Checkmate! " + whitePlayer.getName() + " lost!");
        gameSituationsMap.put(GameSituation.BLACK_CHECKMATED, "Checkmate! " + blackPlayer.getName() + " lost!");
        gameSituationsMap.put(GameSituation.DRAW, "It's a draw!");

        whitePlayer.setOpponentPlayer(blackPlayer);
        blackPlayer.setOpponentPlayer(whitePlayer);

        board.setWhitePlayer(whitePlayer);
        board.setBlackPlayer(blackPlayer);

        boardController = new BoardController(board);

//        boardController.addObserver(whitePlayer);
//        boardController.addObserver(blackPlayer);

        boolean gameStarted = false;

        Scanner scanner = new Scanner(System.in);

        String answer = "";

        do {
            System.out.print("Would you like to start a new game? (y/n) ");
            answer = scanner.nextLine();
            answer = answer.toLowerCase();

        } while (!answer.equals("y") && !answer.equals("n"));

        if (answer.equals("n")) System.exit(1);
        else gameStarted = true;

        int turn = 0;

        Player currentPlayer;
        boolean printForWhite;

        System.out.println("\nIt's a " + whitePlayer.getName() + " vs. " + blackPlayer.getName() + " SHOWDOWN!");

        // initializing all pieces
        board.initBoard();

        // game loop
        while (gameStarted) {

            if (turn % 2 == 0) {
                currentPlayer = whitePlayer;
                printForWhite = true;
            } else {
                currentPlayer = blackPlayer;
                printForWhite = false;
            }

            if (printForWhite) {
                board.printBoardUpsideDown();
            } else board.printBoard();

            // checking the board to see what situation the current player is in
            boardController.checkBoard(currentPlayer);

            // showing current board situation and exiting program if it's checkmate
            if (board.getGameSituation() != GameSituation.NORMAL) {
                System.out.println(gameSituationsMap.get(board.getGameSituation()));
                if (board.getGameSituation() == GameSituation.WHITE_CHECKMATED) System.exit(1);
                if (board.getGameSituation() == GameSituation.BLACK_CHECKMATED) System.exit(1);
                if (board.getGameSituation() == GameSituation.DRAW) System.exit(1);
            }

            // choosing a piece to move with
            int pieceRowChosen;
            int pieceColChosen;
            Tile pieceTile;

            System.out.println("Choose a piece: (row/column)");

            do {
                System.out.print("Row: ");
                pieceRowChosen = scanner.nextInt();

                while (pieceRowChosen < 1 || pieceRowChosen > 8) {
                    System.out.print("Please enter a valid number: ");
                    pieceRowChosen = scanner.nextInt();
                }

                System.out.print("Column: ");
                pieceColChosen = scanner.nextInt();

                while (pieceColChosen < 1 || pieceColChosen > 8) {
                    System.out.print("Please enter a valid number: ");
                    pieceColChosen = scanner.nextInt();
                }

                pieceTile = board.getBoard()[pieceRowChosen-1][pieceColChosen-1];

                if (pieceTile.isEmpty()) System.out.println("This tile is empty!");
                else if (!pieceTile.getPiece().canMove()) System.out.println("This piece can't move!");
                else if (pieceTile.getPiece().getPieceColor() != currentPlayer.getPlayerColor())
                    System.out.println("Please choose a " + currentPlayer.getPlayerColor() + " piece.");
//                else if (currentPlayer.isInCheck()) {
//                    Player finalCurrentPlayer = currentPlayer;
//                    if (!pieceTile.getPiece().getPossibleMoves().stream().anyMatch(tile -> finalCurrentPlayer.getLegalMoves().contains(tile))) {
//                        System.out.println();
//                    }
//                }

            } while (pieceRowChosen < 1 || pieceRowChosen > 8 || pieceColChosen < 1 || pieceColChosen > 8
                    || pieceTile.isEmpty() || !pieceTile.getPiece().canMove()
                    || pieceTile.getPiece().getPieceColor() != currentPlayer.getPlayerColor());

            Piece pieceChosen = pieceTile.getPiece();

            System.out.println();

            if (printForWhite) {
                board.printBoardUpsideDown();
            } else board.printBoard();

            // showing possible moves to make
            System.out.println("Possible moves: ");
            for (int i = 0; i < pieceChosen.getPossibleMoves().size(); i++) {
                if (i != pieceChosen.getPossibleMoves().size() - 1) {
                    System.out.print(pieceChosen.getPossibleMoves().get(i) + ", ");
                } else System.out.print(pieceChosen.getPossibleMoves().get(i));
            }

            System.out.println();
            System.out.println();

            // getting a move input
            int targetTileRow;
            int targetTileCol;
            Tile targetTile;

            System.out.println("Please choose a move: (row/column)");
            do {
                System.out.print("Row: ");
                targetTileRow = scanner.nextInt();

                while (targetTileRow < 1 || targetTileRow > 8) {
                    System.out.print("Please choose a valid number: ");
                    targetTileRow = scanner.nextInt();
                }

                System.out.print("Column: ");
                targetTileCol = scanner.nextInt();

                while (targetTileCol < 1 || targetTileCol > 8) {
                    System.out.print("Please choose a valid number: ");
                    targetTileCol = scanner.nextInt();
                }

                targetTile = board.getBoard()[targetTileRow-1][targetTileCol-1];

                if (!pieceChosen.getPossibleMoves().contains(targetTile)) {
                    System.out.println("This piece can't move there!");
                }
            } while (targetTileRow < 1 || targetTileRow > 8 || targetTileCol < 1 || targetTileCol > 8
            || !pieceChosen.getPossibleMoves().contains(targetTile) /* insert check situation ?*/);

            currentPlayer.movePiece(pieceChosen, targetTile);

//            Move move = new Move.Builder()
//                    .board(board)
//                    .player(currentPlayer)
//                    .movingPiece(pieceChosen)
//                    .targetTile(targetTile)
//                    .build();
//
//            move.makeMove();

            // incrementing the turn
            turn++;
            System.out.println();
            System.out.println(
                    board.getGameHistoryMoves().lastElement().getFirst().getName()
                            + " -> " + board.getGameHistoryMoves().lastElement().getSecond());
            System.out.println();
        }
    }

    public static void initPieces(Player whitePlayer, Player blackPlayer) {
        RookPiece whiteRookKingSide = new RookPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[0][0], true, 0);
        RookPiece whiteRookQueenSide = new RookPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[0][7], false, 1);

        RookPiece blackRookQueenSide = new RookPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[7][0], false, 0);
        RookPiece blackRookKingSide = new RookPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[7][7], true, 1);

        KnightPiece whiteKnightKingSide = new KnightPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[0][1], 0);
        KnightPiece whiteKnightQueenSide = new KnightPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[0][6], 1);

        KnightPiece blackKnightKingSide = new KnightPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[7][1], 0);
        KnightPiece blackKnightQueenSide = new KnightPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[7][6], 1);

        BishopPiece whiteBishopKingSide = new BishopPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[0][2], 0);
        BishopPiece whiteBishopQueenSide = new BishopPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[0][5], 1);

        BishopPiece blackBishopKingSide = new BishopPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[7][5], 0);
        BishopPiece blackBishopQueenSide = new BishopPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[7][2], 1);

        QueenPiece whiteQueen = new QueenPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[0][4]);
        QueenPiece blackQueen = new QueenPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[7][3]);

        KingPiece whiteKing = new KingPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[0][3]);
        KingPiece blackKing = new KingPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[7][4]);

        PawnPiece whitePawn0 = new PawnPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[1][0], 0);
        PawnPiece whitePawn1 = new PawnPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[1][1], 1);
        PawnPiece whitePawn2 = new PawnPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[1][2], 2);
        PawnPiece whitePawn3 = new PawnPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[1][3], 3);
        PawnPiece whitePawn4 = new PawnPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[1][4], 4);
        PawnPiece whitePawn5 = new PawnPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[1][5], 5);
        PawnPiece whitePawn6 = new PawnPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[1][6], 6);
        PawnPiece whitePawn7 = new PawnPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[1][7], 7);

        PawnPiece blackPawn0 = new PawnPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[6][0], 0);
        PawnPiece blackPawn1 = new PawnPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[6][1], 1);
        PawnPiece blackPawn2 = new PawnPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[6][2], 2);
        PawnPiece blackPawn3 = new PawnPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[6][3], 3);
        PawnPiece blackPawn4 = new PawnPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[6][4], 4);
        PawnPiece blackPawn5 = new PawnPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[6][5], 5);
        PawnPiece blackPawn6 = new PawnPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[6][6], 6);
        PawnPiece blackPawn7 = new PawnPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[6][7], 7);

        // when calling the refresh() method for every piece, first call for the pieces on the back row!!! because tiles in front of them aren't empty

//        Piece[] allPieces = new Piece[]{whiteRookKingSide, whiteKnightKingSide, whiteBishopKingSide, whiteQueen, whiteKing, whiteBishopQueenSide, whiteKnightQueenSide, whiteRookQueenSide,
//                whitePawn0, whitePawn1, whitePawn2, whitePawn3, whitePawn4, whitePawn5, whitePawn6, whitePawn7,
//                blackRookQueenSide, blackKnightKingSide, blackBishopQueenSide, blackQueen, blackKing, blackBishopKingSide, blackKnightQueenSide, blackRookKingSide,
//                blackPawn0, blackPawn1, blackPawn2, blackPawn3, blackPawn4, blackPawn5, blackPawn6, blackPawn7};
    }
}
