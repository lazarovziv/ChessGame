package com.zivlazarov.chessengine.client.db;

import com.zivlazarov.chessengine.client.model.board.Tile;
import com.zivlazarov.chessengine.client.model.move.Move;
import com.zivlazarov.chessengine.client.model.pieces.*;
import com.zivlazarov.chessengine.client.model.player.Player;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DatabaseUtils {

    public static volatile SessionFactory sessionFactory = null;

    public static final String DB_URL = "jdbc:mysql://localhost/ChessGame";
    public static final String USER = "root";
    public static final String PASS = "ZivLazarov12";

    public static final Map<PieceType, Integer> pieceTypeMap = Map.of(
         PieceType.PAWN, 0,
         PieceType.BISHOP, 1,
         PieceType.KNIGHT, 2,
         PieceType.ROOK, 3,
         PieceType.QUEEN, 4,
         PieceType.KING, 5
    );

    public static SessionFactory createSessionFactory() {
        try {
            if (sessionFactory == null) {
                synchronized (SessionFactory.class) {
                    if (sessionFactory == null) {
//                        AnnotationConfiguration configuration = new AnnotationConfiguration();
//                        configuration.addAnnotatedClass(Player.class)
//                                .addAnnotatedClass(Tile.class)
//                                .addAnnotatedClass(Piece.class)
//                                .addAnnotatedClass(Move.class)
//                                .addAnnotatedClass(BishopPiece.class)
//                                .addAnnotatedClass(KingPiece.class)
//                                .addAnnotatedClass(KnightPiece.class)
//                                .addAnnotatedClass(PawnPiece.class)
//                                .addAnnotatedClass(QueenPiece.class)
//                                .addAnnotatedClass(RookPiece.class);
//                        configuration.configure(new File("hibernate.cfg.xml"));
                        sessionFactory = new Configuration()
                                .configure(new File("hibernate.cfg.xml"))
                                .addAnnotatedClass(Player.class)
                                .addAnnotatedClass(Tile.class)
                                .addAnnotatedClass(Piece.class)
                                .addAnnotatedClass(Move.class)
                                .addAnnotatedClass(BishopPiece.class)
                                .addAnnotatedClass(KingPiece.class)
                                .addAnnotatedClass(KnightPiece.class)
                                .addAnnotatedClass(PawnPiece.class)
                                .addAnnotatedClass(QueenPiece.class)
                                .addAnnotatedClass(RookPiece.class)
                                .buildSessionFactory();
                        return sessionFactory;
                    }
                }
            }
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
        return sessionFactory;
    }

    public static void closeSessionFactory() {
        sessionFactory.close();
    }
}
