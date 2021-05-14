package com.zivlazarov.chessengine.utils;

import javafx.scene.image.ImageView;

import java.util.ArrayList;

public interface Piece {

    String name = "";
    boolean isAlive = true;
    boolean isInDanger = false;
    final ArrayList<Tile> tilesToMoveTo = new ArrayList<>();
    PieceColor pieceColor = PieceColor.WHITE;
    ImageView imageIcon = null;

    String getName();
    boolean getIsAlive();
    boolean getIsInDanger();
    ArrayList<Tile> getTilesToMoveTo();
    PieceColor getPieceColor();
    ImageView getImageIcon();
    Tile getCurrentTile();
    boolean canMove();

    void setName(String name);
    void setIsAlive(boolean isAlive);
    void setIsInDanger(boolean isInDanger);
    void setPieceColor(PieceColor pieceColor);
    void setImageIcon(ImageView imageView);

    void moveToTile(Tile tile);

    void generateTilesToMoveTo();

    boolean isThreatenedAtTile(Tile tile);

    boolean isTileAvailable(Tile tile);

    void setOnClickListener();

    void refresh();
}
