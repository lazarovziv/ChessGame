package com.zivlazarov.chessengine.model.utils.player;

//import javafx.scene.image.ImageView;

import com.zivlazarov.chessengine.model.utils.Pair;
import com.zivlazarov.chessengine.model.utils.board.PieceColor;
import com.zivlazarov.chessengine.model.utils.board.Tile;

import java.util.ArrayList;
import java.util.Stack;

public interface Piece {

    String name = "";
    boolean isAlive = true;
    boolean isInDanger = false;
    final ArrayList<Tile> tilesToMoveTo = new ArrayList<>();
    PieceColor pieceColor = PieceColor.WHITE;
    final ArrayList<Piece> piecesUnderThreat = new ArrayList<>();
    final Stack<Pair<Tile, Tile>> historyMoves = new Stack<>();
//    ImageView imageIcon = null;

    String getName();
    boolean getIsAlive();
    boolean getIsInDanger();
    ArrayList<Tile> getTilesToMoveTo();
    PieceColor getPieceColor();
//    ImageView getImageIcon();
    Tile getCurrentTile();
    Stack<Pair<Tile, Tile>> getHistoryMoves();
    Pair<Tile, Tile> getLastMove();
    ArrayList<Piece> getPiecesUnderThreat();
    Player getPlayer();
    boolean canMove();

    void setName(String name);
    void setIsAlive(boolean isAlive);
    void setIsInDanger(boolean isInDanger);
    void setPieceColor(PieceColor pieceColor);
    void setCurrentTile(Tile tile);
//    void setImageIcon(ImageView imageView);

    void moveToTile(Tile tile);

    void unmakeLastMove();

    Piece lastPieceEaten();

    void generateTilesToMoveTo();

    boolean isThreatenedAtTile(Tile tile);

    boolean isTileAvailable(Tile tile);

//    void setOnClickListener();

    void refresh();

    boolean hasMoved();
}
