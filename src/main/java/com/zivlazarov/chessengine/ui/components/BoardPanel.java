package com.zivlazarov.chessengine.ui.components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardPanel extends JPanel {

    private final List<TilePanel> tilePanels;
    private final Map<int[], TilePanel> tilePanelMap;

    public BoardPanel() {
        super(new GridLayout(8, 8));
        tilePanels = new ArrayList<>();
        tilePanelMap = new HashMap<>();
        // call drawBoard() after adding panels!!
    }

    public void addPanel(TilePanel panel) {
        tilePanelMap.put(new int[] {panel.getRow(), panel.getCol()}, panel);
        tilePanels.add(panel);
    }

    public void drawBoard(boolean showDialog) {
        removeAll();

        tilePanels.forEach(panel -> {
            panel.drawTile();
            add(panel);
        });
        // redraw side panels
        validate();
        repaint();

//        if (showDialog) {
//           showDialogInSituation();
//        }
    }

    private void showDialogInSituation() {

    }
}
