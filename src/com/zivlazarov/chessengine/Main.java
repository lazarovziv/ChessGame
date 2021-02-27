package com.zivlazarov.chessengine;

import com.zivlazarov.chessengine.pieces.KingPiece;

class Main {

	public static void main(String[] args) {

		Board board = new Board();

		KingPiece whiteKing = new KingPiece(board, PieceColor.WHITE, board.getBoard()[0][3]);
		KingPiece blackKing = new KingPiece(board, PieceColor.BLACK, board.getBoard()[7][4]);

		board.printBoard();

		System.out.println();
		System.out.println("White: ");
		for (Tile tile : whiteKing.getTilesToMoveTo()) {
			System.out.println("(" + tile.getX() + ", " + tile.getY() + ")");
		}
		System.out.println("Black: ");
		for (Tile tile : blackKing.getTilesToMoveTo()) {
			System.out.println("(" + tile.getX() + ", " + tile.getY() + ")");
		}

		blackKing.moveToTile(board.getBoard()[6][4]);

		board.printBoard();

		for (Tile tile : blackKing.getTilesToMoveTo()) {
			System.out.println("(" + tile.getX() + ", " + tile.getY() + ")");
		}
	}
}