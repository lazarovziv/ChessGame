package com.zivlazarov.test.client.db;

import com.zivlazarov.chessengine.db.DatabaseUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;

public class TestEntityTest {

    @Test
    public void testInsert() {
        Session session = DatabaseUtils.createSessionFactory().openSession();
        Transaction transaction = null;

        int testID = 0;

        try {
            transaction = session.beginTransaction();

            TestEntity entity = new TestEntity();
            entity.setName("Ziv");
            entity.setEmail("zivlazarov@gmail.com");
            testID = (int) session.save(entity);

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
