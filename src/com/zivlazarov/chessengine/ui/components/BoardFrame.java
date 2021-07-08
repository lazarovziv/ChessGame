package com.zivlazarov.chesengine.ui.components;

public class BoardFrame {

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final Board board;
    
    public BoardFrame() {
        board = Board.getInstance();
        gameFrame = new JFrame("Chess");
        gameFrame.setLayout(new BorderLayout());
        gameFrame.setSize(600, 600);
        boardPanel = new BoardPanel(board);
        gameFrame.add(boardPanel, BorderLayout.CENTER);
        gameFrame.setVisible(true);
    
    }
    
    private static class BoardPanel extends JPanel {
        
        final List<TilePanel> tilePanels;
        
        BoardPanel(Board board) {
            super(new GridLayout(8,8);
            tilePanels = new ArrayList();
            
            for (int r=0; r<8; r++) {
                for (int c=0; c<8; c++) {
                    TilePanel tilePanel = new TilePanel(this, board.getBoard[r][c]);
                    tilePanels.add(tilePanel);
                    add(tilePanel);
                }
            }
            // setPreferedSize();
            // validate();
        }
    }
    
    private static class TilePanel extends JPanel {
        private final Tile tile;
        
        private static final Color whiteTileColor = Color.decode("FFFACD");
        private static final Color blackTileColor = Color.decode("593E1A");
        
        TilePanel(BoardPanel boardPanel, Tile tile) {
            super(new GridBagLayout());
            this.tile = tile;
            if (tile.getTileColor() == TileColor.WHITE) setBackground(whiteTileColor);
            else setBackground(blackTileColor);
            
            if (!tile.isEmpty()) {
                this.removeAll();
                try {
                    BufferedImage image = ImageIO.read(new File(tile.getPiece().getImageName() + ".png"));
                    add(new JLabel(new ImageIcon(image)));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // setpreferredsize();
            // validate();
        }
    }
}