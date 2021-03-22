package com.zivlazarov.chessengine.ui;

import com.zivlazarov.chessengine.pieces.*;
import com.zivlazarov.chessengine.utils.Board;
import com.zivlazarov.chessengine.utils.Piece;
import com.zivlazarov.chessengine.utils.PieceColor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Game extends Application {

    private Board board;

    public Game() {
        board = new Board();
    }

    @Override
    public void start(Stage stage) throws Exception {
//        String[] whitePieces = {"whiteRook", "whiteKnight", "whiteBishop", "whiteQueen", "whiteKing", "whiteBishop", "whiteKnight", "whiteRook",
//                "whitePawn", "whitePawn", "whitePawn", "whitePawn", "whitePawn", "whitePawn", "whitePawn", "whitePawn"};
//        String[] blackPieces = {"blackRook", "blackKnight", "blackBishop", "blackKing", "blackQueen", "blackBishop", "blackKnight", "blackRook",
//                "blackPawn", "blackPawn", "blackPawn", "blackPawn", "blackPawn", "blackPawn", "blackPawn", "blackPawn"};

        String[] colors = {"brownTile", "whiteTile"};

        // TODO: make icons bigger and adjust dimensions accordingly. done!!

        GridPane gridPane = new GridPane();

        for (int i = 0; i < board.getBoard().length; i++) {
            for (int j = 0; j < board.getBoard().length; j++) {
                // associating image tiles to board tiles
                ImageView imageView = createImageView(colors[(i+j) % colors.length]);
                board.getBoard()[i][j].setImageView(imageView);
                gridPane.add(imageView, i, j);
            }
        }

        Scene scene = new Scene(gridPane, 225*8,225*8);
        stage.setTitle("Chess");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        RookPiece whiteRook0 = new RookPiece(board, PieceColor.WHITE, board.getBoard()[0][0]);
        whiteRook0.setImageIcon(createImageView("whiteRook"));
        RookPiece whiteRook1 = new RookPiece(board, PieceColor.WHITE, board.getBoard()[0][7]);
        whiteRook1.setImageIcon(createImageView("whiteRook"));
        RookPiece blackRook0 = new RookPiece(board, PieceColor.BLACK, board.getBoard()[7][0]);
        blackRook0.setImageIcon(createImageView("blackRook"));
        RookPiece blackRook1 = new RookPiece(board, PieceColor.BLACK, board.getBoard()[7][7]);
        blackRook1.setImageIcon(createImageView("blackRook"));

        KnightPiece whiteKnight0 = new KnightPiece(board, PieceColor.WHITE, board.getBoard()[0][1]);
        whiteKnight0.setImageIcon(createImageView("whiteKnight"));
        KnightPiece whiteKnight1 = new KnightPiece(board, PieceColor.WHITE, board.getBoard()[0][6]);
        whiteKnight1.setImageIcon(createImageView("whiteKnight"));
        KnightPiece blackKnight0 = new KnightPiece(board, PieceColor.BLACK, board.getBoard()[7][1]);
        blackKnight0.setImageIcon(createImageView("blackKnight"));
        KnightPiece blackKnight1 = new KnightPiece(board, PieceColor.BLACK, board.getBoard()[7][6]);
        blackKnight1.setImageIcon(createImageView("blackKnight"));

        BishopPiece whiteBishop0 = new BishopPiece(board, PieceColor.WHITE, board.getBoard()[0][2]);
        whiteBishop0.setImageIcon(createImageView("whiteBishop"));
        BishopPiece whiteBishop1 = new BishopPiece(board, PieceColor.WHITE, board.getBoard()[0][5]);
        whiteBishop1.setImageIcon(createImageView("whiteBishop"));
        BishopPiece blackBishop0 = new BishopPiece(board, PieceColor.BLACK, board.getBoard()[7][2]);
        blackBishop0.setImageIcon(createImageView("blackBishop"));
        BishopPiece blackBishop1 = new BishopPiece(board, PieceColor.BLACK, board.getBoard()[7][5]);
        blackBishop1.setImageIcon(createImageView("blackBishop"));

        QueenPiece whiteQueen = new QueenPiece(board, PieceColor.WHITE, board.getBoard()[0][4]);
        whiteQueen.setImageIcon(createImageView("whiteQueen"));
        QueenPiece blackQueen = new QueenPiece(board, PieceColor.BLACK, board.getBoard()[7][3]);
        blackQueen.setImageIcon(createImageView("blackQueen"));

        KingPiece whiteKing = new KingPiece(board, PieceColor.BLACK, board.getBoard()[0][3]);
        whiteKing.setImageIcon(createImageView("whiteKing"));
        KingPiece blackKing = new KingPiece(board, PieceColor.WHITE, board.getBoard()[7][4]);
        blackKing.setImageIcon(createImageView("blackKing"));

        PawnPiece whitePawn0 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][0]);
        whitePawn0.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn1 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][1]);
        whitePawn1.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn2 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][2]);
        whitePawn2.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn3 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][3]);
        whitePawn3.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn4 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][4]);
        whitePawn4.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn5 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][5]);
        whitePawn5.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn6 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][6]);
        whitePawn6.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn7 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][7]);
        whitePawn7.setImageIcon(createImageView("whitePawn"));

        PawnPiece blackPawn0 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][0]);
        blackPawn0.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn1 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][1]);
        blackPawn1.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn2 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][2]);
        blackPawn2.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn3 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][3]);
        blackPawn3.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn4 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][4]);
        blackPawn4.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn5 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][5]);
        blackPawn5.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn6 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][6]);
        blackPawn6.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn7 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][7]);
        blackPawn7.setImageIcon(createImageView("blackPawn"));

        board.printBoard();

        Piece[] allPieces = {whiteRook0, whiteRook1, whiteKnight0, whiteKnight1, whiteBishop0, whiteBishop1, whiteQueen, whiteKing, whitePawn0, whitePawn1,
                whitePawn2, whitePawn3, whitePawn4, whitePawn5, whitePawn6, whitePawn7,
        blackRook0, blackRook1, blackKnight0, blackKnight1, blackBishop0, blackBishop1, blackQueen, blackKing, blackPawn0, blackPawn1, blackPawn2,
                blackPawn3, blackPawn4, blackPawn5, blackPawn6, blackPawn7};

        for (Piece piece : allPieces) {
            gridPane.add(piece.getImageIcon(), piece.getCurrentTile().getY(), piece.getCurrentTile().getX());
        }
    }

    private ImageView createImageView(String fileName, double x, double y) throws FileNotFoundException {
        InputStream stream = new FileInputStream("/home/ziv/IdeaProjects/ChessGame/src/" + fileName + ".png");
        Image image = new Image(stream);

        ImageView imageView = new ImageView();
        imageView.setImage(image);

        imageView.setX(x);
        imageView.setY(y);
//        imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);

        return imageView;
    }

    private ImageView createImageView(String fileName) throws FileNotFoundException {
        InputStream stream = new FileInputStream("/home/ziv/IdeaProjects/ChessGame/src/" + fileName + ".png");
        Image image = new Image(stream);

        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(225);
        imageView.setFitHeight(225);
        imageView.setPreserveRatio(true);

        return imageView;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
