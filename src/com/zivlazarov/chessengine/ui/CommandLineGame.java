package com.zivlazarov.chessengine.ui;

import com.zivlazarov.chessengine.controllers.PlayerController;
import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.utils.*;

import java.util.Scanner;

public class CommandLineGame {

    private static Board board = new Board();
    private static Piece[] allPieces = new Piece[32];

    public static void main(String[] args) {

        boolean gameStarted = false;

        String whitePlayerName = "";
        String blackPlayerName = "";

        Player whitePlayer = new Player(board, PieceColor.WHITE, true);
        Player blackPlayer = new Player(board, PieceColor.BLACK, false);

//        PlayerController controller = new PlayerController(whitePlayer, blackPlayer);
        PlayerController whiteController = new PlayerController(whitePlayer);
        PlayerController blackController = new PlayerController(blackPlayer);

        PieceColor[] playersColors = {PieceColor.WHITE, PieceColor.BLACK};

        String answer = "";
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("Would you like to start a game? (y/n)");

            answer = scanner.nextLine();

            if (answer.equals("y") || answer.equals("Y")) gameStarted = true;
            else if (answer.equals("n") || answer.equals("N")) System.exit(0);

        } while (!gameStarted);

        do {
            System.out.println("Who plays white? ");

            whitePlayerName = scanner.nextLine();
            whiteController.setPlayerName(whitePlayerName);
//            whitePlayer.setName(whitePlayerName);

            System.out.println("Who plays black? ");

            blackPlayerName = scanner.nextLine();
            blackController.setPlayerName(blackPlayerName);
//            blackPlayer.setName(blackPlayerName);
            System.out.println();

        } while (whitePlayerName.equals("") || blackPlayerName.equals(""));

        RookPiece whiteRook0 = new RookPiece(board, PieceColor.WHITE, board.getBoard()[0][0], 0);
//        whiteRook0.setImageIcon(createImageView("whiteRook"));
        RookPiece whiteRook1 = new RookPiece(board, PieceColor.WHITE, board.getBoard()[0][7], 1);
//        whiteRook1.setImageIcon(createImageView("whiteRook"));
        RookPiece blackRook0 = new RookPiece(board, PieceColor.BLACK, board.getBoard()[7][0], 0);
//        blackRook0.setImageIcon(createImageView("blackRook"));
        RookPiece blackRook1 = new RookPiece(board, PieceColor.BLACK, board.getBoard()[7][7], 1);
//        blackRook1.setImageIcon(createImageView("blackRook"));

        KnightPiece whiteKnight0 = new KnightPiece(board, PieceColor.WHITE, board.getBoard()[0][1], 0);
//        whiteKnight0.setImageIcon(createImageView("whiteKnight"));
        KnightPiece whiteKnight1 = new KnightPiece(board, PieceColor.WHITE, board.getBoard()[0][6], 1);
//        whiteKnight1.setImageIcon(createImageView("whiteKnight"));
        KnightPiece blackKnight0 = new KnightPiece(board, PieceColor.BLACK, board.getBoard()[7][1], 0);
//        blackKnight0.setImageIcon(createImageView("blackKnight"));
        KnightPiece blackKnight1 = new KnightPiece(board, PieceColor.BLACK, board.getBoard()[7][6], 1);
//        blackKnight1.setImageIcon(createImageView("blackKnight"));

        BishopPiece whiteBishop0 = new BishopPiece(board, PieceColor.WHITE, board.getBoard()[0][2], 0);
//        whiteBishop0.setImageIcon(createImageView("whiteBishop"));
        BishopPiece whiteBishop1 = new BishopPiece(board, PieceColor.WHITE, board.getBoard()[0][5], 1);
//        whiteBishop1.setImageIcon(createImageView("whiteBishop"));
        BishopPiece blackBishop0 = new BishopPiece(board, PieceColor.BLACK, board.getBoard()[7][2], 0);
//        blackBishop0.setImageIcon(createImageView("blackBishop"));
        BishopPiece blackBishop1 = new BishopPiece(board, PieceColor.BLACK, board.getBoard()[7][5], 1);
//        blackBishop1.setImageIcon(createImageView("blackBishop"));

        QueenPiece whiteQueen = new QueenPiece(board, PieceColor.WHITE, board.getBoard()[0][4]);
//        whiteQueen.setImageIcon(createImageView("whiteQueen"));
        QueenPiece blackQueen = new QueenPiece(board, PieceColor.BLACK, board.getBoard()[7][3]);
//        blackQueen.setImageIcon(createImageView("blackQueen"));

        KingPiece whiteKing = new KingPiece(board, PieceColor.WHITE, board.getBoard()[0][3]);
//        whiteKing.setImageIcon(createImageView("whiteKing"));
        KingPiece blackKing = new KingPiece(board, PieceColor.BLACK, board.getBoard()[7][4]);
//        blackKing.setImageIcon(createImageView("blackKing"));
        PawnPiece whitePawn0 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][0], 0);
//        whitePawn0.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn1 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][1], 1);
//        whitePawn1.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn2 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][2], 2);
//        whitePawn2.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn3 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][3], 3);
//        whitePawn3.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn4 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][4], 4);
//        whitePawn4.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn5 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][5], 5);
//        whitePawn5.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn6 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][6], 6);
//        whitePawn6.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn7 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][7], 7);
//        whitePawn7.setImageIcon(createImageView("whitePawn"));

        PawnPiece blackPawn0 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][0], 0);
//        blackPawn0.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn1 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][1], 1);
//        blackPawn1.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn2 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][2], 2);
//        blackPawn2.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn3 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][3], 3);
//        blackPawn3.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn4 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][4], 4);
//        blackPawn4.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn5 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][5], 5);
//        blackPawn5.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn6 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][6], 6);
//        blackPawn6.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn7 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][7], 7);
//        blackPawn7.setImageIcon(createImageView("blackPawn"));

//        board.printBoard();

        // when calling the refresh() method for every piece, first call for the pieces on the back row!!! because tiles in front of them aren't empty

        allPieces = new Piece[] {whiteRook0, whiteKnight0, whiteBishop0, whiteQueen, whiteKing, whiteBishop1, whiteKnight1, whiteRook1,
                whitePawn0, whitePawn1, whitePawn2, whitePawn3, whitePawn4, whitePawn5, whitePawn6, whitePawn7,
                blackRook0, blackKnight0, blackBishop0, blackQueen, blackKing, blackBishop1, blackKnight1, blackRook1,
                blackPawn0, blackPawn1, blackPawn2, blackPawn3, blackPawn4, blackPawn5, blackPawn6, blackPawn7};

//        String[] pn = new String[] {"wR", "wN", "wB", "wK", "wQ", "wP", "bR", "bN", "bK", "bQ", "bP"};
//        ArrayList<String> piecesNames = new ArrayList<>();
//        Collections.addAll(piecesNames, pn);

        // adding players' alive pieces
        whiteController.addAlivePieces(allPieces, piece -> piece.getPieceColor() == whiteController.getPlayer().getPlayerColor());
        blackController.addAlivePieces(allPieces, piece -> piece.getPieceColor() == blackController.getPlayer().getPlayerColor());
//        whiteController.addAlivePieces(allPieces);
//        blackController.addAlivePieces(allPieces);

//        whitePlayer.getAlivePieces().addAll(
//                Arrays.stream(allPieces)
//                .filter(piece -> piece.getPieceColor() == PieceColor.WHITE)
//                .collect(Collectors.toList()));
//
//        blackPlayer.getAlivePieces().addAll(
//                Arrays.stream(allPieces)
//                .filter(piece -> piece.getPieceColor() == PieceColor.BLACK)
//                .collect(Collectors.toList()));

        // white always starts first
        int turn = 0;

        while (gameStarted) {
            board.checkBoard();
//            board.printBoard();

            PieceColor currentTurn = playersColors[(turn + playersColors.length) % 2];

            Player currentPlayer = null;
            if (currentTurn == whitePlayer.getPlayerColor()) {
                currentPlayer = whitePlayer;
            } else currentPlayer = blackPlayer;

            if (board.getGameSituation() == GameSituation.CHECKMATE) {
                System.out.println("Checkmate! " + currentPlayer.getName() + " wins!");
                break;
            } else if (board.getGameSituation() == GameSituation.DRAW) {
                System.out.println("Draw! ");
                break;
            } else if (board.getGameSituation() == GameSituation.CHECK) {
                System.out.println("Check! " + currentPlayer.getName() + "'s King is in danger!");
                System.out.println(currentPlayer.getName() + "'s turn: ");
            } else {
                if (turn == 0) {
                    System.out.println(currentPlayer.getName() + " starts! ");
                } else {
                    System.out.println(currentPlayer.getName() + "'s turn: ");
                }
            }

            // show board to player from his side of view
            if (turn % 2 == 0) board.printBoardUpsideDown();
            else board.printBoard();

            int rowChosen;
            int colChosen;
            Tile tileChosen = null;

            System.out.println("Choose a piece from tile: (row, column)");

            do {
                // getting row input
                System.out.print("Row: ");
                rowChosen = scanner.nextInt();

                while (rowChosen < 1 || rowChosen > 8) {
                    System.out.println("Please enter a value from 1 to 8: ");
                    System.out.print("Row: ");
                    rowChosen = scanner.nextInt();
                }

                // getting column input
                System.out.print("Column: ");
                colChosen = scanner.nextInt();

                while (colChosen < 1 || colChosen > 8) {
                    System.out.println("Please enter a value from 1 to 8: ");
                    System.out.print("Column: ");
                    colChosen = scanner.nextInt();
                }

//                if (rowChosen < 1 || rowChosen > 8) {
//                    System.out.println("Please enter a value from 1 to 8: ");
//                    break;
//                } else if (colChosen < 1 || colChosen > 8) {
//                    System.out.println("Please enter a value from 1 to 8: ");
//                    break;
//                }

                tileChosen = board.getBoard()[rowChosen-1][colChosen-1];

                if (tileChosen.isEmpty()) {
                    System.out.println("This tile is empty! Please choose another tile: ");
                } else if (tileChosen.getPiece().getPieceColor() != currentPlayer.getPlayerColor()) {
                    System.out.println("Please choose a " + currentPlayer.getPlayerColor() + " piece!");
                } else if (!tileChosen.getPiece().canMove()) {
                    System.out.println("This piece can't move!");
                }

            } while (rowChosen < 1 || rowChosen > 8 || colChosen < 1 || colChosen > 8 ||
                    tileChosen.isEmpty() || tileChosen.getPiece().getPieceColor() != currentTurn
                    || !tileChosen.getPiece().canMove());

            // show board to player from his side of view
            if (turn % 2 == 0) board.printBoardUpsideDown(tileChosen);
            else board.printBoard(tileChosen);

            Piece pieceChosen = null;

            if (tileChosen.getPiece() != null) {
                pieceChosen = tileChosen.getPiece();
            }

            int rowToMoveChosen;
            int colToMoveChosen;
            Tile tileToMoveChosen;

            System.out.println("Please choose a tile to move to: (row, column)");
            for (int i = 0; i < pieceChosen.getTilesToMoveTo().size(); i++) {
                if (i == pieceChosen.getTilesToMoveTo().size() - 1) {
                    System.out.print(pieceChosen.getTilesToMoveTo().get(i) + " ");
                } else System.out.print(pieceChosen.getTilesToMoveTo().get(i) + ", ");
            }

            System.out.println();

            do {
                System.out.print("Row: ");
                rowToMoveChosen = scanner.nextInt();
                System.out.print("Column: ");
                colToMoveChosen = scanner.nextInt();

                tileToMoveChosen = board.getBoard()[rowToMoveChosen-1][colToMoveChosen-1];

                if (!pieceChosen.getTilesToMoveTo().contains(tileToMoveChosen)) {
                    System.out.println("Piece cannot move to " + tileToMoveChosen + " !");
                }

            } while (!pieceChosen.getTilesToMoveTo().contains(tileToMoveChosen));

            currentPlayer.movePiece(pieceChosen, tileToMoveChosen);
//            pieceChosen.moveToTile(tileToMoveChosen);

            turn = turn + 1;
            System.out.println();
        }
    }

}
