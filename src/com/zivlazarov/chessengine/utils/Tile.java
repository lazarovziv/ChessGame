package com.zivlazarov.chessengine.utils;

public class Tile {

	private int x, y;
	private Piece piece;
	private boolean isEmpty;
	private TileColor tileColor;
	private boolean isThreatenedByWhite;
	private boolean isThreatenedByBlack;

	public Tile(int x, int y, TileColor tc) {
		this.x = x;
		this.y = y;
		tileColor = tc;
		isEmpty = true; // if piece is not initialized tile is empty
		isThreatenedByWhite = false;
		isThreatenedByBlack = false;
	}

	public Tile(int x, int y, TileColor tc, Piece p) {
		this.x = x;
		this.y = y;
		tileColor = tc;
		piece = p;
		isEmpty = false; // if piece is initialized tile is not empty
		isThreatenedByWhite = false;
		isThreatenedByBlack = false;
	}

	public int getX() { 
		return x; 
	}

	public int getY() { 
		return y; 
	}

	public Piece getPiece() {
		return piece;
	}

	public boolean isEmpty() {
		if (piece != null) {
			isEmpty = false;
		} else {
			isEmpty = true;
		}
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

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setPiece(Piece p) {
		piece = p;
	}

	public void setThreatenedByWhite(boolean threatenedByWhite) {
		isThreatenedByWhite = threatenedByWhite;
	}

	public void setThreatenedByBlack(boolean threatenedByBlack) {
		isThreatenedByBlack = threatenedByBlack;
	}

	public boolean equals(Tile tile) {
		return x == tile.x && y == tile.y;
	}
}