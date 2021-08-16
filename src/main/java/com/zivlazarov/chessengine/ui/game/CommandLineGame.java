package com.zivlazarov.chessengine.ui.game;

import com.zivlazarov.chessengine.model.ai.Minimax;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.GameSituation;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandLineGame {

    private static Board board;

    private static Player whitePlayer;
    private static Player blackPlayer;

    private static Minimax minimax;

    private static GameSituation gameSituation;

    public static void main(String[] args) {

        board = new Board();

        whitePlayer = new Player(board, PieceColor.WHITE);
        blackPlayer = new Player(board, PieceColor.BLACK);

        minimax = new Minimax();

        whitePlayer.setName("Ziv");
        blackPlayer.setName("Guy");

        whitePlayer.setAI(false);
        blackPlayer.setAI(true);

        Map<GameSituation, String> gameSituationsMap = new HashMap<>();

        gameSituationsMap.put(GameSituation.WHITE_IN_CHECK, whitePlayer.getName() + " is in check!");
        gameSituationsMap.put(GameSituation.BLACK_IN_CHECK, blackPlayer.getName() + " is in check!)");
        gameSituationsMap.put(GameSituation.WHITE_CHECKMATED, "Checkmate! " + whitePlayer.getName() + " lost!");
        gameSituationsMap.put(GameSituation.BLACK_CHECKMATED, "Checkmate! " + blackPlayer.getName() + " lost!");
        gameSituationsMap.put(GameSituation.DRAW, "It's a draw!");

        whitePlayer.setOpponent(blackPlayer);

        board.setWhitePlayer(whitePlayer);
        board.setBlackPlayer(blackPlayer);

        board.initBoard();

        board.setCurrentPlayer(whitePlayer);
        board.checkBoard();

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
//        board.initBoard();

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

            // showing current board situation and exiting program if it's checkmate
            if (board.getGameSituation() != GameSituation.NORMAL) {
                System.out.println(gameSituationsMap.get(board.getGameSituation()));
                if (board.getGameSituation() == GameSituation.WHITE_CHECKMATED) System.exit(1);
                if (board.getGameSituation() == GameSituation.BLACK_CHECKMATED) System.exit(1);
                if (board.getGameSituation() == GameSituation.DRAW) System.exit(1);
                if (board.getGameSituation() == GameSituation.STALEMATE) System.exit(1);
            }

            if (!board.getCurrentPlayer().isAI()) {
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
                    else if (pieceTile.getPiece().getPieceColor() != currentPlayer.getColor())
                        System.out.println("Please choose a " + currentPlayer.getColor() + " piece.");
//                else if (currentPlayer.isInCheck()) {
//                    Player finalCurrentPlayer = currentPlayer;
//                    if (!pieceTile.getPiece().getPossibleMoves().stream().anyMatch(tile -> finalCurrentPlayer.getLegalMoves().contains(tile))) {
//                        System.out.println();
//                    }
//                }

                } while (pieceRowChosen < 1 || pieceRowChosen > 8 || pieceColChosen < 1 || pieceColChosen > 8
                        || pieceTile.isEmpty() || !pieceTile.getPiece().canMove()
                        || pieceTile.getPiece().getPieceColor() != currentPlayer.getColor());

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

//                currentPlayer.movePiece(pieceChosen, targetTile);

                Move move = new Move.Builder()
                        .board(board)
                        .player(currentPlayer)
                        .movingPiece(pieceChosen)
                        .targetTile(targetTile)
                        .build();

                move.makeMove(true, true);

                // incrementing the turn
                turn++;

                System.out.println();
            } else {
//                Move move = minimax.calculateBestMove(board, 3, false);
//                move.makeMove(true);
            }

        }
    }
}
