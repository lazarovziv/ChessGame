class Player {

    private Player opponentPlayer;
    private Board board;
    
    private PieceColor playerColor;
    private String name;
    private List<Piece> alivePieces;
    private List<Piece> deadPieces;
    private boolean hasWonGame;
    private boolean startsGame;
    
    public Player(Board b, PieceColor pc, String n, boolean sg) {
        board = b;
        playerColor = pc;
        name = n;
        alivePieces = new ArrayList();
        deadPieces = new ArrayList();
        hasWonGame = false;
        startsGame = sg;
    }
    
    public boolean movePiece(Piece piece, Tile targetTile) {
        if (alivePieces.contains(piece)) {
            if (piece.getTilesToMoveTo().contains(targetTile)) {
                piece.moveToTile(targtTile));
                return true;
            }
        }
        return false;
    }
    
    public void addPieceToAlive(Piece piece) {
        if (piece.getPieceColor() == playerColor) {
            alivePieces.add(piece);
        }
    }
    
    public void addPieceToDead(Piece piece) {
        if (piece.getPieceColor() == playerColor) {
            deadPieces.add(piece);
        }
    }
    
    public PieceColor getPlayerColor() {
        return playerColor;
    }
    
    public List<Piece> getAlivePieces() {
        return alivePieces;
    }
    
    public List<Piece> getDeadPieces() {
        return deadPieces;
    }
}