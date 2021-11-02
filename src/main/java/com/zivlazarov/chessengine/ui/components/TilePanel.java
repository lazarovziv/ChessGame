package com.zivlazarov.chessengine.ui.components;

import com.zivlazarov.chessengine.model.board.TileColor;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class TilePanel extends JPanel {

    // row and column for each tile panel (positions in the board)
    private int row;
    private int col;
    private char piece;

    private static final Color whiteTileColor = Color.decode("#FFFACD");
    private static final Color blackTileColor = Color.decode("#593E1A");

    private static final Map<TileColor, Color> tileColorMap = Map.of(TileColor.WHITE, whiteTileColor,
            TileColor.BLACK, blackTileColor);
    private static final TileColor[] colors = {TileColor.WHITE, TileColor.BLACK};

    public TilePanel(BoardPanel boardPanel, int row, int col) {
        super(new GridBagLayout());
        this.row = row;
        this.col = col;

        setPreferredSize(new Dimension(10, 10));

        drawTile();
    }

    public void drawTile() {
        removeAll();

        TileColor currentTileColor = colors[(row + col) % colors.length];
        setBackground(tileColorMap.get(currentTileColor));

        validate();
        repaint();

        removeAll();

        // TODO: find a way to draw a piece from the GameController
//        add(new JLabel(createImageIcon(tile.getPiece())));

        validate();
        repaint();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
