package com.zivlazarov.chessengine.model.board;

//import javafx.scene.image.ImageView;

import com.zivlazarov.chessengine.model.pieces.Piece;

import java.io.Serializable;

public class Tile implements Serializable {

	private int row;
	private int col;
	private Piece piece;
	private boolean isEmpty;
	private final TileColor tileColor;
	private boolean isThreatenedByWhite;
	private boolean isThreatenedByBlack;
//	private ImageView tileImageView;
//	private ImageView pieceImageView;

	public Tile(int row, int col, TileColor tc) {
		this.row = row;
		this.col = col;
		tileColor = tc;
		isEmpty = true; // if piece is not initialized tile is empty
		isThreatenedByWhite = false;
		isThreatenedByBlack = false;
	}

//	public Tile(int row, int col, TileColor tc, ImageView iv) {
//		this.row = row;
//		this.col = col;
//		tileColor = tc;
//		isEmpty = true; // if piece is not initialized tile is empty
//		isThreatenedByWhite = false;
//		isThreatenedByBlack = false;
//		tileImageView = iv;
//	}

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

//	public ImageView getTileImageView() {
//		return tileImageView;
//	}

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

	public boolean equals(Tile tile) {
		return row == tile.getRow() && col == tile.getCol();
	}

//	public void setTileImageView(ImageView tileImageView) {
//		this.tileImageView = tileImageView;
//	}

//	public void clearPieceImageView() {
//		this.setTileImageView(tileImageView);
//	}

//	public ImageView getPieceImageView() { return pieceImageView; }

//	public void setPieceImageView(ImageView pieceImageView) { this.pieceImageView = pieceImageView; }

	@Override
	public String toString() {
		return "[" + (row+1) + ", " + (col+1) + "]";
	}
}