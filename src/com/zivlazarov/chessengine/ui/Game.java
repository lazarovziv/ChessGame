package com.zivlazarov.chessengine.ui;

import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.player.Player;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class Game {}

//    private final Board board;
//    private final GridPane gridPane;
//
//    private final String[] colors = {"brownTile", "whiteTile"};
//
//    private static Player whitePlayer;
//    private static Player blackPlayer;
//
//    public Game() {
//        board = Board.getInstance();
//        gridPane = new GridPane();
//    }
//
//    @Override
//    public void start(Stage stage) {
////        String[] whitePieces = {"whiteRook", "whiteKnight", "whiteBishop", "whiteQueen", "whiteKing", "whiteBishop", "whiteKnight", "whiteRook",
////                "whitePawn", "whitePawn", "whitePawn", "whitePawn", "whitePawn", "whitePawn", "whitePawn", "whitePawn"};
////        String[] blackPieces = {"blackRook", "blackKnight", "blackBishop", "blackKing", "blackQueen", "blackBishop", "blackKnight", "blackRook",
////                "blackPawn", "blackPawn", "blackPawn", "blackPawn", "blackPawn", "blackPawn", "blackPawn", "blackPawn"};
//
//        boolean gameStarted = false;
//
//        whitePlayer = new Player(board, PieceColor.WHITE);
//        blackPlayer = new Player(board, PieceColor.BLACK);
//
//        whitePlayer.setOpponentPlayer(blackPlayer);
//        blackPlayer.setOpponentPlayer(whitePlayer);
//
//
//        String answer = "";
//
//        do {
//            System.out.println("Would you like to start a game? (y/n)");
//
//            Scanner scanner = new Scanner(System.in);
//            answer = scanner.nextLine();
//            answer = answer.toLowerCase();
//
//            if (answer.equals("y")) gameStarted = true;
//            else if (answer.equals("n")) System.exit(0);
//
//        } while (!answer.equals("y"));
//
//        for (int i = 0; i < board.getBoard().length; i++) {
//            for (int j = 0; j < board.getBoard().length; j++) {
//                // associating image tiles to board tiles
//                ImageView tileImageView = createImageView(colors[(i+j) % colors.length]);
//                board.getBoard()[i][j].setTileImageView(tileImageView);
//                gridPane.add(tileImageView, i, j);
//            }
//        }
//
//        CommandLineGame.initPieces(whitePlayer, blackPlayer);
//
//        board.printBoard();
//
//        // when calling the init() method for every piece, first call for the pieces on the back row!!! because tiles in front of them aren't empty
//
//        // !!!!!!!!!!!!!!!!!!!!!
//        for (Tile[] tiles : board.getBoard()) {
//            for (Tile tile : tiles) {
//                if (tile.getPieceImageView() == null) continue;
//                gridPane.add(tile.getPieceImageView(), tile.getCol(), tile.getCol());
//            }
//        }
//
//        Scene scene = new Scene(gridPane, 225*8,225*8);
//        stage.setTitle("Chess");
//        stage.setScene(scene);
//        stage.setResizable(false);
//        stage.show();
//
//        int turn = 0;
//
//        Player currentPlayer;
//        boolean printForWhite;
//
////        while (gameStarted) {
////
////
////        }
//    }
//
//    public static ImageView createImageView(String fileName) {
//        InputStream stream = null;
//        try {
//            stream = new FileInputStream("//Users/zivlazarov/Projects/Java/ChessGame/src/" + fileName + ".png");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        Image image = null;
//        if (stream != null) {
//            image = new Image(stream);
//        }
//
//        ImageView imageView = new ImageView();
//        imageView.setImage(image);
//        imageView.setFitWidth(225);
//        imageView.setFitHeight(225);
//        imageView.setPreserveRatio(true);
//
//        return imageView;
//    }
//
//    private void updateBoard(Player currentPlayer) {
//        board.checkBoard(currentPlayer);
//        gridPane.getChildren().clear();
//        for (int r = 0; r < board.getBoard().length; r++) {
//            for (int c = 0; c < board.getBoard().length; c++) {
//                ImageView tileImageView = createImageView(colors[(r+c) % colors.length]);
//                board.getBoard()[r][c].setTileImageView(tileImageView);
//                gridPane.add(tileImageView, r, c);
//            }
//        }
//        for (Piece piece : currentPlayer.getAlivePieces()) {
//            if (piece.getImageIcon() == null) continue;
//            // col first, row second
//            gridPane.add(piece.getImageIcon(), piece.getCurrentTile().getCol(), piece.getCurrentTile().getRow());
//        }
//    }
//
//    private void movePieceToTile(Piece piece, Tile tile) {
//        if (!tile.isEmpty()) {
//            tile.getPiece().setImageIcon(null);
//        }
//        piece.moveToTile(tile);
//        tile.setPieceImageView(piece.getImageIcon());
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
