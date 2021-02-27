package com.zivlazarov.chessengine;

import com.zivlazarov.chessengine.pieces.RookPiece;

class Main {

	public static void main(String[] args) {

		Board board = new Board();

		RookPiece whiteRook = new RookPiece(board, PieceColor.WHITE, board.getBoard()[0][7]);
		/*
		board.printBoard();
		for (Tile tile : whiteRook.getTilesToMoveTo()) {
			System.out.println("(" + tile.getX() + ", " + tile.getY() + ")");
		}*/
		whiteRook.moveToTile(board.getBoard()[1][7]);
		board.printBoard();
		for (Tile tile : whiteRook.getTilesToMoveTo()) {
			System.out.println("(" + tile.getX() + ", " + tile.getY() + ")");
		}
	}
}