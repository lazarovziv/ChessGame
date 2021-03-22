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

    private final Board board;

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

        GridPane gridPane = new GridPane();

        for (int i = 0; i < board.getBoard().length; i++) {
            for (int j = 0; j < board.getBoard().length; j++) {
                // associating image tiles to board tiles
                ImageView tileImageView = createImageView(colors[(i+j) % colors.length]);
                board.getBoard()[i][j].setImageView(tileImageView);
                gridPane.add(tileImageView, i, j);
            }
        }

        RookPiece whiteRook0 = new RookPiece(board, PieceColor.WHITE, board.getBoard()[0][0], createImageView("whiteRook"));
//        whiteRook0.setImageIcon(createImageView("whiteRook"));
        RookPiece whiteRook1 = new RookPiece(board, PieceColor.WHITE, board.getBoard()[0][7], createImageView("whiteRook"));
//        whiteRook1.setImageIcon(createImageView("whiteRook"));
        RookPiece blackRook0 = new RookPiece(board, PieceColor.BLACK, board.getBoard()[7][0], createImageView("blackRook"));
//        blackRook0.setImageIcon(createImageView("blackRook"));
        RookPiece blackRook1 = new RookPiece(board, PieceColor.BLACK, board.getBoard()[7][7], createImageView("blackRook"));
//        blackRook1.setImageIcon(createImageView("blackRook"));

        KnightPiece whiteKnight0 = new KnightPiece(board, PieceColor.WHITE, board.getBoard()[0][1], createImageView("whiteKnight"));
//        whiteKnight0.setImageIcon(createImageView("whiteKnight"));
        KnightPiece whiteKnight1 = new KnightPiece(board, PieceColor.WHITE, board.getBoard()[0][6], createImageView("whiteKnight"));
//        whiteKnight1.setImageIcon(createImageView("whiteKnight"));
        KnightPiece blackKnight0 = new KnightPiece(board, PieceColor.BLACK, board.getBoard()[7][1], createImageView("blackKnight"));
//        blackKnight0.setImageIcon(createImageView("blackKnight"));
        KnightPiece blackKnight1 = new KnightPiece(board, PieceColor.BLACK, board.getBoard()[7][6], createImageView("blackKnight"));
//        blackKnight1.setImageIcon(createImageView("blackKnight"));

        BishopPiece whiteBishop0 = new BishopPiece(board, PieceColor.WHITE, board.getBoard()[0][2], createImageView("whiteBishop"));
//        whiteBishop0.setImageIcon(createImageView("whiteBishop"));
        BishopPiece whiteBishop1 = new BishopPiece(board, PieceColor.WHITE, board.getBoard()[0][5], createImageView("whiteBishop"));
//        whiteBishop1.setImageIcon(createImageView("whiteBishop"));
        BishopPiece blackBishop0 = new BishopPiece(board, PieceColor.BLACK, board.getBoard()[7][2], createImageView("blackBishop"));
//        blackBishop0.setImageIcon(createImageView("blackBishop"));
        BishopPiece blackBishop1 = new BishopPiece(board, PieceColor.BLACK, board.getBoard()[7][5], createImageView("blackBishop"));
//        blackBishop1.setImageIcon(createImageView("blackBishop"));

        QueenPiece whiteQueen = new QueenPiece(board, PieceColor.WHITE, board.getBoard()[0][4], createImageView("whiteQueen"));
//        whiteQueen.setImageIcon(createImageView("whiteQueen"));
        QueenPiece blackQueen = new QueenPiece(board, PieceColor.BLACK, board.getBoard()[7][3], createImageView("blackQueen"));
//        blackQueen.setImageIcon(createImageView("blackQueen"));

        KingPiece whiteKing = new KingPiece(board, PieceColor.BLACK, board.getBoard()[0][3], createImageView("whiteKing"));
//        whiteKing.setImageIcon(createImageView("whiteKing"));
        KingPiece blackKing = new KingPiece(board, PieceColor.WHITE, board.getBoard()[7][4], createImageView("blackKing"));
//        blackKing.setImageIcon(createImageView("blackKing"));
        PawnPiece whitePawn0 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][0], createImageView("whitePawn"));
//        whitePawn0.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn1 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][1], createImageView("whitePawn"));
//        whitePawn1.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn2 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][2], createImageView("whitePawn"));
//        whitePawn2.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn3 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][3], createImageView("whitePawn"));
//        whitePawn3.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn4 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][4], createImageView("whitePawn"));
//        whitePawn4.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn5 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][5], createImageView("whitePawn"));
//        whitePawn5.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn6 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][6], createImageView("whitePawn"));
//        whitePawn6.setImageIcon(createImageView("whitePawn"));
        PawnPiece whitePawn7 = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][7], createImageView("whitePawn"));
//        whitePawn7.setImageIcon(createImageView("whitePawn"));

        PawnPiece blackPawn0 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][0], createImageView("blackPawn"));
//        blackPawn0.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn1 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][1], createImageView("blackPawn"));
//        blackPawn1.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn2 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][2], createImageView("blackPawn"));
//        blackPawn2.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn3 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][3], createImageView("blackPawn"));
//        blackPawn3.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn4 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][4], createImageView("blackPawn"));
//        blackPawn4.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn5 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][5], createImageView("blackPawn"));
//        blackPawn5.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn6 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][6], createImageView("blackPawn"));
//        blackPawn6.setImageIcon(createImageView("blackPawn"));
        PawnPiece blackPawn7 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[6][7], createImageView("blackPawn"));
//        blackPawn7.setImageIcon(createImageView("blackPawn"));

        board.printBoard();

        Piece[] allPieces = {whiteRook0, whiteRook1, whiteKnight0, whiteKnight1, whiteBishop0, whiteBishop1, whiteQueen, whiteKing, whitePawn0, whitePawn1,
                whitePawn2, whitePawn3, whitePawn4, whitePawn5, whitePawn6, whitePawn7,
        blackRook0, blackRook1, blackKnight0, blackKnight1, blackBishop0, blackBishop1, blackQueen, blackKing, blackPawn0, blackPawn1, blackPawn2,
                blackPawn3, blackPawn4, blackPawn5, blackPawn6, blackPawn7};

        for (Piece piece : allPieces) {
            gridPane.add(piece.getImageIcon(), piece.getCurrentTile().getY(), piece.getCurrentTile().getX());
        }

        whitePawn0.moveToTile(board.getBoard()[2][0]);
        blackKnight0.moveToTile(board.getBoard()[5][0]);
        board.printBoard();

        Scene scene = new Scene(gridPane, 225*8,225*8);
        stage.setTitle("Chess");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
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
