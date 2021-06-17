package com.zivlazarov.chessengine.model.utils;

//import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Stack;

public interface Piece {

    String name = "";
    boolean isAlive = true;
    boolean isInDanger = false;
    final ArrayList<Tile> tilesToMoveTo = new ArrayList<>();
    PieceColor pieceColor = PieceColor.WHITE;
    final ArrayList<Piece> piecesUnderThreat = new ArrayList<>();
    final Stack<Tile> historyMoves = new Stack<>();
//    ImageView imageIcon = null;

    String getName();
    boolean getIsAlive();
    boolean getIsInDanger();
    ArrayList<Tile> getTilesToMoveTo();
    PieceColor getPieceColor();
//    ImageView getImageIcon();
    Tile getCurrentTile();
    boolean canMove();

    void setName(String name);
    void setIsAlive(boolean isAlive);
    void setIsInDanger(boolean isInDanger);
    void setPieceColor(PieceColor pieceColor);
//    void setImageIcon(ImageView imageView);

    void moveToTile(Tile tile);

    void generateTilesToMoveTo();

    boolean isThreatenedAtTile(Tile tile);

    boolean isTileAvailable(Tile tile);

//    void setOnClickListener();

    void refresh();

    boolean hasMoved();
}
