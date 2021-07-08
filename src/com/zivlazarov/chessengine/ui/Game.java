package com.zivlazarov.chessengine.ui;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Game extends Application {

    private final Board board;
    private final GridPane gridPane;

    private final String[] colors = {"brownTile", "whiteTile"};

    private static Player whitePlayer;
    private static Player blackPlayer;

    private static final Path path = Paths.get("");
    private static final String currentPath = path.toAbsolutePath().toString();

    public Game() {
        board = Board.getInstance();
        gridPane = new GridPane();
//        gridPane.setMaxWidth(800);
//        gridPane.setMaxHeight(600);
    }

    @Override
    public void start(Stage stage) {
//        String[] whitePieces = {"whiteRook", "whiteKnight", "whiteBishop", "whiteQueen", "whiteKing", "whiteBishop", "whiteKnight", "whiteRook",
//                "whitePawn", "whitePawn", "whitePawn", "whitePawn", "whitePawn", "whitePawn", "whitePawn", "whitePawn"};
//        String[] blackPieces = {"blackRook", "blackKnight", "blackBishop", "blackKing", "blackQueen", "blackBishop", "blackKnight", "blackRook",
//                "blackPawn", "blackPawn", "blackPawn", "blackPawn", "blackPawn", "blackPawn", "blackPawn", "blackPawn"};

        List<Node> nodes = new ArrayList<>();

        boolean gameStarted = false;

        whitePlayer = new Player(board, PieceColor.WHITE);
        blackPlayer = new Player(board, PieceColor.BLACK);

        whitePlayer.setOpponentPlayer(blackPlayer);
        blackPlayer.setOpponentPlayer(whitePlayer);

        CommandLineGame.initPieces(whitePlayer, blackPlayer);

        board.checkBoard(whitePlayer);

        Alert alertDialog = new Alert(Alert.AlertType.NONE,
                "Would you like to start a game?",
                ButtonType.YES,
                ButtonType.NO);
        alertDialog.showAndWait();

        if (alertDialog.getResult() == ButtonType.NO) System.exit(1);

        for (int i = 0; i < board.getBoard().length; i++) {
            for (int j = 0; j < board.getBoard().length; j++) {
                Property<Tile> tileProperty = new SimpleObjectProperty<>();
                tileProperty.setValue(board.getBoard()[i][j]);

                // associating image tiles to board tiles
                ImageView tileImageView = createImageView(colors[(i+j) % colors.length]);
                tileImageView.setPreserveRatio(true);

                board.getBoard()[i][j].setTileImageView(tileImageView);
                GridPane.setConstraints(tileImageView, j, i);

//                gridPane.add(tileImageView, i, j);
                nodes.add(tileImageView);

                if (!board.getBoard()[i][j].isEmpty()) {
                    Piece piece = board.getBoard()[i][j].getPiece();
                    Image pieceImage = createImage(piece.getImageName());
                    ImageView pieceImageView = new ImageView(pieceImage);
                    pieceImageView.setPreserveRatio(true);

                    System.out.println(pieceImageView.getBoundsInLocal());
                    // binding piece object to a property
                    Property<Number> rowNumberProperty = new SimpleObjectProperty<>(piece, "row", piece.getCurrentTile().getRow());
                    Property<Number> colNumberProperty = new SimpleObjectProperty<>(piece, "col", piece.getCurrentTile().getCol());
                    /*
//                    Property<Piece> pieceObjectProperty = new SimpleObjectProperty<Piece>();
//                    pieceObjectProperty.setValue(piece);
//
//                    // binding piece's row and column (position on board) to different properties
//                    Property<Number> rowNumberProperty = new SimpleObjectProperty<>();
//                    rowNumberProperty.setValue(pieceObjectProperty.getValue().getCurrentTile().getRow());
//
//                    Property<Number> colNumberProperty = new SimpleObjectProperty<>();
//                    colNumberProperty.setValue(piece.getCurrentTile().getCol());
//
//                    pieceObjectProperty.addListener((observable, oldValue, newValue) -> {
//                        if (oldValue.getCurrentTile() != newValue.getCurrentTile()) {
//                            rowNumberProperty.setValue(newValue.getCurrentTile().getRow());
//                            colNumberProperty.setValue(newValue.getCurrentTile().getCol());
//                        }
//                    });
//
//                    pieceImageView.xProperty().bindBidirectional(rowNumberProperty);
//                    pieceImageView.yProperty().bindBidirectional(colNumberProperty);
*/
//                    pieceImageView.setOnMouseClicked(event -> System.out.println(pieceImageView.getLayoutX() + ", " + pieceImageView.getLayoutY()));
                    pieceImageView.layoutXProperty().bindBidirectional(rowNumberProperty);
                    pieceImageView.layoutYProperty().bindBidirectional(colNumberProperty);

                    GridPane.setConstraints(pieceImageView, j, i);
                    nodes.add(pieceImageView);
                }
            }
        }

        gridPane.getChildren().addAll(nodes);

        whitePlayer.movePiece(board.getBoard()[1][2].getPiece(), board.getBoard()[3][2]);

        Scene scene = new Scene(gridPane, 225*8,225*8);
        stage.setTitle("Chess");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(800);
        stage.setHeight(600);
        stage.show();

        int turn = 0;

        Player currentPlayer;
        boolean printForWhite;

//        while (gameStarted) {
//
//
//        }
    }

    public static ImageView createImageView(String fileName) {
        InputStream stream = null;
        try {
            stream = new FileInputStream(currentPath + "/src/" + fileName + ".png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Image image = null;
        if (stream != null) {
            image = new Image(stream);
        }

        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(112);
        imageView.setFitHeight(72);
//        imageView.setPreserveRatio(true);

        return imageView;
    }

    public static Image createImage(String fileName) {
        InputStream stream = null;
        try {
            stream = new FileInputStream(currentPath + "/src/" + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Image image = null;
        if (stream != null) {
            image = new Image(stream);
        }

        return image;
    }

    private void updateBoard(Player currentPlayer) {
        board.checkBoard(currentPlayer);
        gridPane.getChildren().clear();
        for (int r = 0; r < board.getBoard().length; r++) {
            for (int c = 0; c < board.getBoard().length; c++) {
                ImageView tileImageView = createImageView(colors[(r+c) % colors.length]);
                board.getBoard()[r][c].setTileImageView(tileImageView);
                gridPane.add(tileImageView, r, c);
            }
        }
//        for (Piece piece : currentPlayer.getAlivePieces()) {
//            if (piece.getImageIcon() == null) continue;
//            // col first, row second
//            gridPane.add(piece.getImageIcon(), piece.getCurrentTile().getCol(), piece.getCurrentTile().getRow());
//        }
    }

    private void movePieceToTile(Piece piece, Tile tile) {
        if (!tile.isEmpty()) {
            tile.getPiece().setImageIcon(null);
        }
        piece.moveToTile(tile);
//        tile.setPieceImageView(piece.getImageIcon());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
