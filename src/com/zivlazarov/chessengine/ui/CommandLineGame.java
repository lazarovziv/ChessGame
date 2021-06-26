package com.zivlazarov.chessengine.ui;

import com.zivlazarov.chessengine.controllers.BoardController;
import com.zivlazarov.chessengine.controllers.PlayerController;
import com.zivlazarov.chessengine.logs.MovesLog;
import com.zivlazarov.chessengine.model.utils.MyObserver;
import com.zivlazarov.chessengine.model.utils.Pair;
import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.GameSituation;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.player.Piece;
import com.zivlazarov.chessengine.model.player.Player;

import java.util.Scanner;
import java.util.Stack;

public class CommandLineGame {

    private static Board board = new Board();
    private static Piece[] allPieces = new Piece[32];
    private static Stack<Pair<Pair<Player, Piece>, Pair<Tile, Tile>>> movesLog;

    public static void main(String[] args) {

        boolean gameStarted = false;

        String whitePlayerName = "";
        String blackPlayerName = "";

        Player whitePlayer = new Player(board, PieceColor.WHITE);
        Player blackPlayer = new Player(board, PieceColor.BLACK);

        MovesLog log = MovesLog.getInstance();
        movesLog = log.getMovesLog();

        PlayerController playerController = new PlayerController();

        BoardController boardController = new BoardController();
        boardController.setBoard(board);
        boardController.setWhitePlayer(whitePlayer);
        boardController.setBlackPlayer(blackPlayer);

        PieceColor[] playersColors = {PieceColor.WHITE, PieceColor.BLACK};

        playerController.setPlayer(whitePlayer);
        playerController.setOpponentPlayer(blackPlayer);

        String answer = "";
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("Would you like to start a game? (y/n)");

            answer = scanner.nextLine();
            answer = answer.toLowerCase();

            if (answer.equals("y")) gameStarted = true;
            else if (answer.equals("n")) System.exit(0);

        } while (!gameStarted);

        do {
            System.out.println("Who plays white? ");

            whitePlayerName = scanner.nextLine();
            playerController.setPlayerName(whitePlayerName);

            System.out.println("Who plays black? ");

            blackPlayerName = scanner.nextLine();
            playerController.setOpponentPlayerName(blackPlayerName);

            System.out.println();
            System.out.println("It's a " + playerController.getPlayer().getName() + " vs. " + playerController.getOpponentPlayer().getName() + " SHOWDOWN!");
            System.out.println();

        } while (whitePlayerName.equals("") || blackPlayerName.equals(""));

        // initializing all pieces
        initPieces(whitePlayer, blackPlayer);

        // adding players' alive pieces
//        playerController.addAlivePieces(allPieces);
//        playerController.addAlivePiecesToOpponent(allPieces);

        // white always starts first
        int turn = 0;

        while (gameStarted) {

            if (movesLog.size() != 0) {
                System.out.println(movesLog.peek().getFirst() + " played: " + movesLog.peek().getSecond());
                System.out.println();
            }

            PieceColor currentTurn = playersColors[(turn + playersColors.length) % 2];

            Player currentPlayer = playerController.getPlayer();

            if (turn != 0) {
                if (currentTurn == whitePlayer.getPlayerColor()) {
                    currentPlayer = whitePlayer;
                    playerController.setPlayer(currentPlayer);
                    playerController.setOpponentPlayer(blackPlayer);
                } else {
                    currentPlayer = blackPlayer;
                    playerController.setPlayer(currentPlayer);
                    playerController.setOpponentPlayer(whitePlayer);
                }
            }

            playerController.setHasPlayerPlayedThisTurn(false);

            boardController.checkBoard(currentPlayer);

            // handle game situations
            handleGameSituations(currentPlayer, turn);

            // show board to player from his side of view
            if (turn % 2 == 0) boardController.printBoardUpsideDown();
            else boardController.printBoard();

            int rowChosen;
            int colChosen;
            Tile tileChosen;

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

                tileChosen = board.getBoard()[rowChosen-1][colChosen-1];

                if (tileChosen.isEmpty()) {
                    System.out.println("This tile is empty! Please choose another tile: ");
                } else if (tileChosen.getPiece().getPieceColor() != currentPlayer.getPlayerColor()) {
                    System.out.println("Please choose a " + currentPlayer.getPlayerColor() + " piece!");
                } else if (!currentPlayer.getPiecesCanMove().contains(tileChosen.getPiece())) {
                    System.out.println("This piece can't move!");
                }

            } while (rowChosen < 1 || rowChosen > 8 || colChosen < 1 || colChosen > 8 ||
                    tileChosen.isEmpty() || tileChosen.getPiece().getPieceColor() != currentTurn
                    || !currentPlayer.getPiecesCanMove().contains(tileChosen.getPiece()));

            // show board to player from his side of view
            if (turn % 2 == 0) boardController.printBoardUpsideDown(tileChosen);
            else boardController.printBoard(tileChosen);

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

            boolean legalMoveWhenInCheck = true;

            do {
                System.out.print("Row: ");
                rowToMoveChosen = scanner.nextInt();
                System.out.print("Column: ");
                colToMoveChosen = scanner.nextInt();

                tileToMoveChosen = boardController.getBoard().getBoard()[rowToMoveChosen-1][colToMoveChosen-1];

                if (!pieceChosen.getTilesToMoveTo().contains(tileToMoveChosen)) {
                    System.out.println("Piece cannot move to " + tileToMoveChosen + " !");
                }


                if (currentPlayer.isInCheck()) {
                    if (!currentPlayer.getLegalMoves().contains(tileToMoveChosen)) legalMoveWhenInCheck = false;
                }

            } while (!pieceChosen.getTilesToMoveTo().contains(tileToMoveChosen) && legalMoveWhenInCheck);

            // handle castling
            handleCastling(currentPlayer, whitePlayer, blackPlayer, pieceChosen, tileToMoveChosen, playerController);

            // handle pawn promotion
            handlePawnPromotion(pieceChosen, currentPlayer, playerController, scanner);

            if (!playerController.hasPlayerPlayedThisTurn()) {
                playerController.movePiece(pieceChosen, tileToMoveChosen);
            }

            Pair<Pair<Player, Piece>, Pair<Tile, Tile>> lastMove =
                    new Pair<Pair<Player, Piece>, Pair<Tile, Tile>>(new Pair<Player, Piece>(playerController.getPlayer(), pieceChosen),
                            playerController.getPlayer().getLastMove());
            movesLog.push(lastMove);

            turn = turn + 1;
            System.out.println();
        }
    }

    public static void initPieces(Player whitePlayer, Player blackPlayer) {
        RookPiece whiteRookKingSide = new RookPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[0][0], 0);
        RookPiece whiteRookQueenSide = new RookPiece(whitePlayer, board, PieceColor.WHITE, board.getBoard()[0][7], 1);

        RookPiece blackRookQueenSide = new RookPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[7][0], 0);
        RookPiece blackRookKingSide = new RookPiece(blackPlayer, board, PieceColor.BLACK, board.getBoard()[7][7], 1);

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

        allPieces = new Piece[] {whiteRookKingSide, whiteKnightKingSide, whiteBishopKingSide, whiteQueen, whiteKing, whiteBishopQueenSide, whiteKnightQueenSide, whiteRookQueenSide,
                whitePawn0, whitePawn1, whitePawn2, whitePawn3, whitePawn4, whitePawn5, whitePawn6, whitePawn7,
                blackRookQueenSide, blackKnightKingSide, blackBishopQueenSide, blackQueen, blackKing, blackBishopKingSide, blackKnightQueenSide, blackRookKingSide,
                blackPawn0, blackPawn1, blackPawn2, blackPawn3, blackPawn4, blackPawn5, blackPawn6, blackPawn7};

        board.addAllObservers(allPieces);
    }

    private static void handlePawnPromotion(Piece pieceChosen, Player currentPlayer, PlayerController playerController, Scanner scanner) {
        if (pieceChosen instanceof PawnPiece) {
            if (pieceChosen.getCurrentTile().getRow() == currentPlayer.getPlayerDirection() * (board.getBoard().length - 1)) {
                String promotionAnswer = "";
                boolean answeredCorrect = false;

                do {
                    System.out.println("To which piece would you like to convert your pawn? (Q/R/B/N)");
                    promotionAnswer = scanner.nextLine();

                    switch (promotionAnswer) {
                        case "Q", "q", "R", "r", "B", "b", "N", "n" -> {
                            answeredCorrect = true;
                        }
                        default -> answeredCorrect = false;
                    }
                } while (!answeredCorrect);

                playerController.promotePawn((PawnPiece) pieceChosen, promotionAnswer);
            }
        }
    }

    private static void handleCastling(Player currentPlayer, Player whitePlayer, Player blackPlayer, Piece pieceChosen,
                                       Tile tileToMoveChosen, PlayerController playerController) {
        if (currentPlayer.equals(whitePlayer)) {
            if (pieceChosen.getName().equals("wK") && tileToMoveChosen.equals(board.getBoard()[0][1])) {
                playerController.kingSideCastle((KingPiece) pieceChosen, (RookPiece) board.getBoard()[0][0].getPiece());
            } else if (pieceChosen.getName().equals("wK") && tileToMoveChosen.equals(board.getBoard()[0][5])) {
                playerController.queenSideCastle((KingPiece) pieceChosen, (RookPiece) board.getBoard()[0][7].getPiece());
            }
        } else if (currentPlayer.equals(blackPlayer)) {
            if (pieceChosen.getName().equals("bK") && tileToMoveChosen.equals(board.getBoard()[7][2])) {
                playerController.kingSideCastle((KingPiece) pieceChosen, (RookPiece) board.getBoard()[7][0].getPiece());
            } else if (pieceChosen.getName().equals("bK") && tileToMoveChosen.equals(board.getBoard()[7][6])) {
                playerController.queenSideCastle((KingPiece) pieceChosen, (RookPiece) board.getBoard()[7][7].getPiece());
            }
        }
    }

    private static void handleGameSituations(Player currentPlayer, int turn) {
        if (currentPlayer.getPlayerColor() == PieceColor.WHITE) {
            if (board.getGameSituation() == GameSituation.WHITE_CHECKMATED) {
                System.out.println("Checkmate! " + currentPlayer.getOpponentPlayer().getName() + " wins!");
                currentPlayer.getOpponentPlayer().setHasWonGame(true);
            } else if (board.getGameSituation() == GameSituation.WHITE_IN_CHECK) {
                System.out.println("Check! " + currentPlayer.getName() + "'s King is in danger!");
                System.out.println(currentPlayer.getName() + "'s turn: ");
            } else if (board.getGameSituation() == GameSituation.DRAW) {
                System.out.println("Draw! ");
            } else {
                if (turn == 0) {
                    System.out.println(currentPlayer.getName() + " starts! ");
                } else {
                    System.out.println(currentPlayer.getName() + "'s turn: ");
                }
            }
        } else if (currentPlayer.getPlayerColor() == PieceColor.BLACK) {
            if (board.getGameSituation() == GameSituation.BLACK_CHECKMATED) {
                System.out.println("Checkmate! " + currentPlayer.getOpponentPlayer().getName() + " wins!");
                currentPlayer.getOpponentPlayer().setHasWonGame(true);
                System.out.println("Moves from the match: ");
            } else if (board.getGameSituation() == GameSituation.BLACK_IN_CHECK) {
                System.out.println("Check! " + currentPlayer.getName() + "'s King is in danger!");
                System.out.println(currentPlayer.getName() + "'s turn: ");
            } else if (board.getGameSituation() == GameSituation.DRAW) {
                System.out.println("Draw! ");
            } else {
                if (turn == 0) {
                    System.out.println(currentPlayer.getName() + " starts! ");
                } else {
                    System.out.println(currentPlayer.getName() + "'s turn: ");
                }
            }
        }
    }
}
