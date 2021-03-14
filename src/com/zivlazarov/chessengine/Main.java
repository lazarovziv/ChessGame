package com.zivlazarov.chessengine;

import com.zivlazarov.chessengine.pieces.*;

class Main {

	public static void main(String[] args) {

		Board board = new Board();

		//RookPiece whiteRook = new RookPiece(board, PieceColor.WHITE, board.getBoard()[0][7]);
		BishopPiece blackBishop = new BishopPiece(board, PieceColor.BLACK, board.getBoard()[0][0]);
		QueenPiece blackQueen = new QueenPiece(board, PieceColor.BLACK, board.getBoard()[7][3]);
		//PawnPiece whitePawn = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[6][3]);
		KnightPiece whiteKnight = new KnightPiece(board, PieceColor.WHITE, board.getBoard()[5][5]);
		/*
		board.printBoard();
		for (Tile tile : whiteRook.getTilesToMoveTo()) {
			System.out.println("(" + tile.getX() + ", " + tile.getY() + ")");
		}*/
		// whiteRook.moveToTile(board.getBoard()[1][7]);
		board.printBoard();
		/*
		System.out.println("Rook: ");
		for (Tile tile : whiteRook.getTilesToMoveTo()) {
			System.out.println("(" + tile.getX() + ", " + tile.getY() + ")");
		} */
		System.out.println("Pawn: ");
		for (Tile tile : whiteKnight.getTilesToMoveTo()) {
			System.out.println("(" + tile.getX() + ", " + tile.getY() + ")");

		}
	}
}