package com.zivlazarov.chessengine.model.pieces;

//import javafx.scene.image.ImageView;

import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.Pair;
import javafx.beans.property.ObjectProperty;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

public interface Piece extends Cloneable, Serializable {

    String name = "";
    boolean isAlive = true;
    boolean isInDanger = false;
    final ArrayList<Tile> tilesToMoveTo = new ArrayList<>();
    PieceColor pieceColor = PieceColor.WHITE;
    final ArrayList<Piece> piecesUnderThreat = new ArrayList<>();
    final Stack<Pair<Tile, Tile>> historyMoves = new Stack<>();
//    ImageView imageIcon = null;

    String getName();
    boolean isAlive();
    boolean getIsInDanger();
    ArrayList<Tile> getPossibleMoves();
    PieceColor getPieceColor();
    Tile getCurrentTile();
    Stack<Tile> getHistoryMoves();
    Tile getLastMove();
    ArrayList<Piece> getPiecesUnderThreat();
    Player getPlayer();
    String getImageName();

    Tile getLastTile();

    boolean canMove();

    void setName(String name);
    void setIsAlive(boolean isAlive);
    void setIsInDanger(boolean isInDanger);
    void setPieceColor(PieceColor pieceColor);
    void setCurrentTile(Tile tile);

    void setPieceType(PieceType pieceType);
    PieceType getPieceType();

    Tile getCurrentTileProperty();
    ObjectProperty<Tile> currentTilePropertyProperty();
    void setCurrentTileProperty(Tile currentTileProperty);

    void setLastTile(Tile tile);

    Icon getImageIcon();
    void setImageIcon(Icon imageView);

    void moveToTile(Tile tile);

    void unmakeLastMove();

    Piece getLastPieceEaten();

    Stack<Piece> getCapturedPieces();

    void generateMoves();

    boolean isThreatenedAtTile(Tile tile);

    boolean isTileAvailable(Tile tile);

//    void setOnClickListener();

    void refresh();

    int getValue();

    boolean hasMoved();

    boolean equals(Piece piece);
}
