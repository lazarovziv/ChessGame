package com.zivlazarov.chessengine.model.board;

//import javafx.scene.image.ImageView;

import com.zivlazarov.chessengine.model.pieces.Piece;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.io.Serializable;

public class Tile implements Serializable {

	private int row;
	private int col;
	private Piece piece;
	private boolean isEmpty;
	private final TileColor tileColor;
	private boolean isThreatenedByWhite;
	private boolean isThreatenedByBlack;
	private ImageView tileImageView;
	private ImageView pieceImageView;
	private JLabel label;
	private JButton button;

	public Tile(int row, int col, TileColor tc) {
		this.row = row;
		this.col = col;
		tileColor = tc;
		isEmpty = true; // if piece is not initialized tile is empty
		isThreatenedByWhite = false;
		isThreatenedByBlack = false;
	}

	public Tile(int row, int col, TileColor tc, Piece p) {
		this.row = row;
		this.col = col;
		tileColor = tc;
		piece = p;
		isEmpty = piece == null; // if piece is initialized (as NOT null) tile is not empty
		isThreatenedByWhite = false;
		isThreatenedByBlack = false;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public Piece getPiece() {
		return piece;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public TileColor getTileColor() {
		return tileColor;
	}

	public boolean isThreatenedByWhite() {
		return isThreatenedByWhite;
	}

	public boolean isThreatenedByBlack() {
		return isThreatenedByBlack;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public void setPiece(Piece p) {
		piece = p;
		isEmpty = piece == null || piece.isAlive();
	}

	public void setThreatenedByWhite(boolean threatenedByWhite) {
		isThreatenedByWhite = threatenedByWhite;
	}

	public void setThreatenedByBlack(boolean threatenedByBlack) {
		isThreatenedByBlack = threatenedByBlack;
	}

	public void setThreatenedByColor(PieceColor pieceColor, boolean isThreatened) {
		if (pieceColor == PieceColor.WHITE) isThreatenedByWhite = isThreatened;
		if (pieceColor == PieceColor.BLACK) isThreatenedByBlack = isThreatened;

		if (!isThreatened) return;
		if (piece != null) {
			if (piece.getPieceColor() != pieceColor) {
				piece.setIsInDanger(true);
			}
		}
	}

	public ImageView getTileImageView() {
		return tileImageView;
	}

	public void setTileImageView(ImageView tileImageView) {
		this.tileImageView = tileImageView;
	}

	public ImageView getPieceImageView() {
		return pieceImageView;
	}

	public void setPieceImageView(ImageView pieceImageView) {
		this.pieceImageView = pieceImageView;
	}

	public boolean equals(Tile tile) {
		return row == tile.getRow() && col == tile.getCol();
	}

	@Override
	public String toString() {
		return "[" + (row+1) + ", " + (col+1) + "]";
	}

	public JLabel getLabel() {
		return label;
	}

	public void setLabel(JLabel label) {
		this.label = label;
	}


	public JButton getButton() {
		return button;
	}

	public void setButton(JButton button) {
		this.button = button;
	}
}