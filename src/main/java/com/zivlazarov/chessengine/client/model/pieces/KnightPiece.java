package com.zivlazarov.chessengine.client.model.pieces;
import com.zivlazarov.chessengine.client.model.board.Board;
import com.zivlazarov.chessengine.client.model.board.PieceColor;
import com.zivlazarov.chessengine.client.model.board.Tile;
import com.zivlazarov.chessengine.client.model.move.Move;
import com.zivlazarov.chessengine.client.model.player.Player;

//import javafx.scene.image.ImageView;

//import static com.zivlazarov.chessengine.client.ui.Game.createImageView;

public class KnightPiece extends Piece implements Cloneable {

    public KnightPiece(Player player, Board board, Tile initTile, int pieceCounter) {
        super();

        this.player = player;
        this.board = board;
        this.pieceColor = player.getPlayerColor();
        this.currentTile = initTile;
        this.lastTile = currentTile;
        this.pieceCounter = pieceCounter;

        this.value = 3;

        if (this.pieceColor == PieceColor.BLACK) {
            this.name = "bN";
            this.imageName = "blackKnight.png";
        }
        if (this.pieceColor == PieceColor.WHITE) {
            this.name = "wN";
            this.imageName = "whiteKnight.png";
        }

        this.player.addPieceToAlive(this);
        this.currentTile.setPiece(this);
        this.pieceType = PieceType.KNIGHT;
    }

    @Override
    public void refresh() {
        reset();
        generateMoves();
    }

    @Override
    public void generateMoves() {
        if (!isAlive) return;
        int[][] directions ={
                {1, 2},
                {1, -2},
                {-1, 2},
                {-1, -2},
                {2, 1},
                {2, -1},
                {-2, 1},
                {-2 ,-1}
        };

        int x = currentTile.getRow();
        int y = currentTile.getCol();

        for (int[] direction : directions) {
            int r = direction[0];
            int c = direction[1];

            if (x+r > board.getBoard().length - 1  || x+r < 0 || y+c > board.getBoard().length - 1 || y+c < 0) continue;
            Tile targetTile = board.getBoard()[x+r][y+c];
            if (targetTile.isEmpty() || targetTile.getPiece().getPieceColor() != pieceColor) {
                Move move = new Move.Builder()
                        .board(board)
                        .player(player)
                        .movingPiece(this)
                        .targetTile(targetTile)
                        .build();
                moves.add(move);
                possibleMoves.add(targetTile);
                if (!targetTile.isEmpty()) {
                    if (targetTile.getPiece().getPieceColor() != pieceColor) piecesUnderThreat.add(targetTile.getPiece());
                }
            }
        }
        possibleMoves.forEach(tile -> tile.setThreatenedByColor(pieceColor, true));
        player.getLegalMoves().addAll(possibleMoves);
        player.getMoves().addAll(moves);
    }

    //
//    private Player player;
//
//    private ObjectProperty<Tile> currentTileProperty;
//
//    private PieceType pieceType;
//
//    private int id;
//
//    private final Set<Move> moves;
//    private final List<Tile> possibleMoves;
//    private final List<Piece> piecesUnderThreat;
//    private final Stack<Tile> historyMoves;
//    private Tile lastTile;
//    private Stack<Piece> capturedPieces;
//    private final Board board;
//
//    private String name;
//
//    private int pieceCounter;
//
//    private boolean isAlive = true;
//    private boolean isInDanger = false;
//    private Tile currentTile;
//    private PieceColor pieceColor;
//    private String imageName;
//    private Icon imageIcon;
//
//    private int value = 3;
//
//    private final Object[] allFields;
//
//    public KnightPiece(Player player, Board board, PieceColor pc, Tile initTile, int pieceCounter) {
//        this.player = player;
//        this.board = board;
//
////        name = 'N';
//        pieceColor = pc;
//        possibleMoves = new ArrayList<Tile>();
//        piecesUnderThreat = new ArrayList<>();
//        historyMoves = new Stack<>();
//        capturedPieces = new Stack<>();
//        moves = new HashSet<>();
//
//        currentTile = initTile;
//        lastTile = currentTile;
//
//        this.pieceCounter = pieceCounter;
//        if (pieceColor == PieceColor.BLACK) {
//            name = "bN";
//            imageName = "blackKnight.png";
//        }
//        if (pieceColor == PieceColor.WHITE) {
//            name = "wN";
//            imageName = "whiteKnight.png";
//        }
//        player.addPieceToAlive(this);
//
//        currentTile.setPiece(this);
//
//        currentTileProperty = new SimpleObjectProperty<>(this, "currentTile", currentTile);
////        generateTilesToMoveTo();
//        allFields = new Object[] {player, pieceType, possibleMoves, piecesUnderThreat,
//                historyMoves, lastTile, capturedPieces,
//                name, pieceCounter, isAlive, isInDanger, currentTile,
//                pieceColor, imageName, imageIcon};
//
//        pieceType = PieceType.KNIGHT;
//
//        id = 100 * value * player.getPlayerDirection() + player.getId() + pieceCounter;
//    }
//
//    @Override
//    public int getId() {
//        return id;
//    }
//
//    @Override
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    @Override
//    public String getName() {
//        return name;
//    }
//
//    @Override
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    @Override
//    public boolean isAlive() {
//        return !isAlive;
//    }
//
////    @Override
////    public ImageView getImageIcon() {
////        return imageIcon;
////    }
//
//    @Override
//    public void setIsAlive(boolean isAlive) {
//        this.isAlive = isAlive;
//    }
//
//    @Override
//    public boolean getIsInDanger() {
//        return isInDanger;
//    }
//
//    @Override
//    public void setIsInDanger(boolean isInDanger) {
//        this.isInDanger = isInDanger;
//    }
//
//    @Override
//    public List<Tile> getPossibleMoves() {
//        return possibleMoves;
//    }
//
//    @Override
//    public PieceColor getPieceColor() {
//        return pieceColor;
//    }
//
//    @Override
//    public void setPieceColor(PieceColor pieceColor) {
//        this.pieceColor = pieceColor;
//    }
//
////    @Override
////    public void setImageIcon(ImageView imageIcon) {
////        this.imageIcon = imageIcon;
////    }
//
//    @Override
//    public Tile getCurrentTile() {
//        return currentTile;
//    }
//
//    public int getPieceCounter() {
//        return pieceCounter;
//    }
//
//    @Override
//    public Stack<Tile> getHistoryMoves() {
//        return historyMoves;
//    }
//
//    @Override
//    public Tile getLastMove() {
//        if (historyMoves.size() == 0) return null;
//        return historyMoves.peek();
//    }
//
//    @Override
//    public List<Piece> getPiecesUnderThreat() {
//        return piecesUnderThreat;
//    }
//
//    public Player getPlayer() {
//        return player;
//    }
//
//    @Override
//    public void setCurrentTile(Tile currentTile) {
//        this.currentTile = currentTile;
//        if (currentTile == null) return;
//        currentTile.setPiece(this);
//    }
//
//    public String getImageName() {
//        return imageName;
//    }
//
//    @Override
//    public int getValue() {
//        return value;
//    }
//
//    @Override
//    public boolean isThreatenedAtTile(Tile tile) {
//        if (pieceColor == PieceColor.WHITE) {
//            if (tile.isThreatenedByBlack()) return true;
//            else return false;
//        }
//        if (pieceColor == PieceColor.BLACK) {
//            if (tile.isThreatenedByWhite()) return true;
//            else return false;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean isTileAvailable(Tile tile) {
//        if (tile.isEmpty()) {
//            return true;
//        } else return tile.getPiece().getPieceColor() != pieceColor;
//    }
//
//    @Override
//    public boolean canMove() {
//        return possibleMoves.size() != 0;
//    }
//
//    @Override
//    public boolean hasMoved() {
//        return false;
//    }
//
//    @Override
//    public Piece getLastPieceEaten() {
//        if (capturedPieces.size() == 0) return null;
//        return capturedPieces.peek();
//    }
//
//    @Override
//    public Object clone() throws CloneNotSupportedException {
//        return super.clone();
//    }
//
//    @Override
//    public boolean equals(Piece piece) {
//        return currentTile.getRow() == piece.getCurrentTile().getRow() &&
//                currentTile.getCol() == piece.getCurrentTile().getCol() &&
//                (name + pieceCounter).equals(piece.getName() + pieceCounter);
//    }
//
//    @Override
//    public Stack<Piece> getCapturedPieces() {
//        return capturedPieces;
//    }
//
//    @Override
//    public Tile getLastTile() {
//        return lastTile;
//    }
//
//    @Override
//    public void setLastTile(Tile lastTile) {
//        this.lastTile = lastTile;
//    }
//
//    @Override
//    public PieceType getPieceType() {
//        return pieceType;
//    }
//
//    @Override
//    public void setPieceType(PieceType pieceType) {
//        this.pieceType = pieceType;
//    }
//
//    @Override
//    public void setPlayer(Player player)  {
//        this.player = player;
//    }
//
//    @Override
//    public Set<Move> getMoves() {
//        return moves;
//    }
//
//    @Override
//    public void reset() {
//        if (possibleMoves.size() != 0) {
//            possibleMoves.clear();
//        }
//        if (piecesUnderThreat.size() != 0) {
//            piecesUnderThreat.clear();
//        }
//        if (moves.size() != 0) {
//            moves.clear();
//        }
//    }
}