package com.zivlazarov.chessengine.model.board;

import com.zivlazarov.chessengine.model.player.Player;

import java.util.ArrayList;
import java.util.List;

public class BoardNode {

    private int value;
    private Player currentPlayer;

    List<BoardNode> children;

    public BoardNode(Board board, Player player) {
        this.currentPlayer = player;
        this.value = board.getHeuristicScore(player);
        children = new ArrayList<>();
    }

    public void addChildNode(BoardNode node) {
        children.add(node);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public List<BoardNode> getChildren() {
        return children;
    }

    public void setChildren(List<BoardNode> children) {
        this.children = children;
    }

    public boolean isLeafNode() {
        return children.size() == 0;
    }
}
