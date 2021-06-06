package com.zivlazarov.chessengine.model.utils;

import javafx.scene.image.ImageView;

public class Tile {

	private int row;
	private int col;
	private Piece piece;
	private boolean isEmpty;
	private final TileColor tileColor;
	private boolean isThreatenedByWhite;
	private boolean isThreatenedByBlack;
	private ImageView tileImageView;
	private ImageView pieceImageView;

	public Tile(int row, int col, TileColor tc) {
		this.row = row;
		this.col = col;
		tileColor = tc;
		isEmpty = true; // if piece is not initialized tile is empty
		isThreatenedByWhite = false;
		isThreatenedByBlack = false;
	}

	public Tile(int row, int col, TileColor tc, ImageView iv) {
		this.row = row;
		this.col = col;
		tileColor = tc;
		isEmpty = true; // if piece is not initialized tile is empty
		isThreatenedByWhite = false;
		isThreatenedByBlack = false;
		tileImageView = iv;
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

	public ImageView getTileImageView() {
		return tileImageView;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public void setPiece(Piece p) {
		piece = p;
		isEmpty = piece == null;
	}

	public void setThreatenedByWhite(boolean threatenedByWhite) {
		isThreatenedByWhite = threatenedByWhite;
	}

	public void setThreatenedByBlack(boolean threatenedByBlack) {
		isThreatenedByBlack = threatenedByBlack;
	}

	public boolean equals(Tile tile) {
		return row == tile.row && col == tile.col;
	}

	public void setTileImageView(ImageView tileImageView) {
		this.tileImageView = tileImageView;
	}

	public void clearPieceImageView() {
		this.setTileImageView(tileImageView);
	}

	public ImageView getPieceImageView() { return pieceImageView; }

	public void setPieceImageView(ImageView pieceImageView) { this.pieceImageView = pieceImageView; }

	@Override
	public String toString() {
		return "[" + (row+1) + ", " + (col+1) + "]";
	}
}