package com.zivlazarov.chessengine.ui.utils;

import com.zivlazarov.chessengine.model.pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utilities {

    public static final Path path = Paths.get("");
    public static final String currentPath = path.toAbsolutePath().toString();

    public static ImageIcon createImageIcon(Piece piece) {
        if (piece != null) {
            try {
                BufferedImage image = ImageIO.read(new File(currentPath + "/src/main/java/" + piece.getImageName()));
                return new ImageIcon(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
