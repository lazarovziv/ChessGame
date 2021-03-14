package com.zivlazarov.chessengine;

import com.zivlazarov.chessengine.pieces.BishopPiece;
import com.zivlazarov.chessengine.pieces.PawnPiece;
import com.zivlazarov.chessengine.pieces.QueenPiece;
import com.zivlazarov.chessengine.pieces.RookPiece;

class Main {

	public static void main(String[] args) {

		Board board = new Board();

		RookPiece whiteRook = new RookPiece(board, PieceColor.WHITE, board.getBoard()[0][7]);
		BishopPiece blackBishop = new BishopPiece(board, PieceColor.BLACK, board.getBoard()[0][0]);
		QueenPiece blackQueen = new QueenPiece(board, PieceColor.BLACK, board.getBoard()[7][3]);
		PawnPiece whitePawn = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[6][3]);
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
		for (Tile tile : whitePawn.getTilesToMoveTo()) {
			System.out.println("(" + tile.getX() + ", " + tile.getY() + ")");
		}
		whitePawn.moveToTile(board.getBoard()[4][3]);
		board.printBoard();
		System.out.println("Pawn: ");
		for (Tile tile : whitePawn.getTilesToMoveTo()) {
			System.out.println("(" + tile.getX() + ", " + tile.getY() + ")");
		}
	}
}