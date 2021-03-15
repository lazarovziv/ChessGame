package com.zivlazarov.chessengine.ui;

import com.zivlazarov.chessengine.utils.Board;
import com.zivlazarov.chessengine.utils.Tile;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class Game extends Application {

    private Board board;

    public Game() {
        board = new Board();
    }

    @Override
    public void start(Stage stage) throws Exception {
        String[] whitePieces = {"whiteRook", "whiteKnight", "whiteBishop", "whiteQueen", "whiteKing", "whiteBishop", "whiteKnight", "whiteRook",
                "whitePawn", "whitePawn", "whitePawn", "whitePawn", "whitePawn", "whitePawn", "whitePawn", "whitePawn"};
        String[] blackPieces = {"blackRook", "blackKnight", "blackBishop", "blackKing", "blackQueen", "blackBishop", "blackKnight", "blackRook",
                "blackPawn", "blackPawn", "blackPawn", "blackPawn", "blackPawn", "blackPawn", "blackPawn", "blackPawn"};

        ArrayList<ImageView> imageViews = new ArrayList<>();

        ImageView boardImageView = new ImageView(new Image(new FileInputStream("/home/ziv/IdeaProjects/ChessGame/src/board.png")));

        imageViews.add(boardImageView);

        int colCount = 0;
        int row = 1;
        for (int i = 0; i < whitePieces.length; i++) {
            if (colCount == 8) {
                colCount = 0;
                row = 2;
            }
            imageViews.add(createImageView(whitePieces[i], -3 + 59 * colCount, -59 + 59 * row));
            colCount++;
        }

        colCount = 0;
        row = 7;
        for (int i = 0; i < blackPieces.length; i++) {
            if (colCount == 8) {
                colCount = 0;
                row = 6;
            }
            imageViews.add(createImageView(blackPieces[i], -3 + 59 * colCount, 59 * row));
            colCount++;
        }

        // TODO: make icons bigger and adjust dimensions accordingly.

        Group root = new Group();

        boardImageView.fitWidthProperty().bind(stage.widthProperty());

        root.getChildren().addAll(imageViews);

        Scene scene = new Scene(root, 472, 472);
        stage.setTitle("Chess");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // TODO: assign every tile in board to tile in boardImageView.
        for (int r = 0; r < board.getBoard().length; r++) {
            for (int c = 0; c < board.getBoard().length; c++) {
                Tile tile = board.getBoard()[r][c];

            }
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

    public static void main(String[] args) {
        launch(args);
    }
}
