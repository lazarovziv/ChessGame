package com.zivlazarov.chessengine.model.board;

import com.zivlazarov.chessengine.model.pieces.Piece;
import javafx.scene.image.ImageView;

import javax.persistence.*;
import javax.swing.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "tile")
public class Tile implements Serializable {

	@Serial
	private static final long serialVersionUID = 4L;

	@Id @GeneratedValue
	private int id;

	@Column(name = "tile_row")
	private int row;
	@Column(name = "tile_col")
	private int col;

	private TileColor tileColor;

	private boolean isEmpty;

	private boolean isThreatenedByBlack;

	private boolean isThreatenedByWhite;

	@Transient
	private Piece piece;

	private int tileNumber;

	@Transient
	private static final char[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};

	public Tile() {}

	public Tile(int row, int col, TileColor tc) {
		this.row = row;
		this.col = col;
		tileColor = tc;
		isEmpty = true; // if piece is not initialized tile is empty
		isThreatenedByWhite = false;
		isThreatenedByBlack = false;
		tileNumber = row * 8 + col;
	}

	public Tile(int row, int col, TileColor tc, Piece p) {
		this.row = row;
		this.col = col;
		tileColor = tc;
		piece = p;
		isEmpty = piece == null; // if piece is initialized (as NOT null) tile is not empty
		isThreatenedByWhite = false;
		isThreatenedByBlack = false;
		tileNumber = row * 8 + col;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public boolean isThreatenedByColor(PieceColor color) {
		if (color == PieceColor.WHITE) {
			return isThreatenedByWhite;
		} else {
			return isThreatenedByBlack;
		}
	}

	public void setPiece(Piece p) {
		piece = p;
		isEmpty = piece == null; /*|| piece.isAlive();*/
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

	public int getTileNumber() {
		return tileNumber;
	}

	public void setTileNumber(int tileNumber) {
		this.tileNumber = tileNumber;
	}

	public static Tile fromNumber(Board board, int number) {
		int r = number / 8;
		int c = number - (r * 8);

		return board.getBoard()[r][c];
	}

	public boolean equals(Tile tile) {
		return row == tile.getRow() && col == tile.getCol();
	}

	@Override
	public String toString() {
		return "[" + letters[row] /*+ ", " */+ (col+1) + "]";
	}
}