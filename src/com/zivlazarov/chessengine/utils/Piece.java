package com.zivlazarov.chessengine.utils;

import javafx.scene.image.ImageView;

import java.util.ArrayList;

public interface Piece {

    char name = 'a';
    boolean isAlive = true;
    boolean isInDanger = false;
    final ArrayList<Tile> tilesToMoveTo = new ArrayList<>();
    PieceColor pieceColor = PieceColor.WHITE;
    ImageView imageIcon = null;

    char getName();
    boolean getIsAlive();
    boolean getIsInDanger();
    ArrayList<Tile> getTilesToMoveTo();
    PieceColor getPieceColor();
    ImageView getImageIcon();
    Tile getCurrentTile();

    void setName(char name);
    void setIsAlive(boolean isAlive);
    void setIsInDanger(boolean isInDanger);
    void setPieceColor(PieceColor pieceColor);
    void setImageIcon(ImageView imageView);

    void moveToTile(Tile tile);

    void generateTilesToMoveTo();

    boolean isThreatenedAtTile(Tile tile);

    boolean isTileAvailable(Tile tile);
}
