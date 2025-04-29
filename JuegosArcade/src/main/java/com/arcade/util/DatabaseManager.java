package com.arcade.util;

import com.arcade.entity.GameResult;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class DatabaseManager {
    
    private static DatabaseManager instance;
    private SessionFactory sessionFactory;
    
    private DatabaseManager() {
    }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    public void initializeDatabase() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();
            configuration.addAnnotatedClass(GameResult.class);
            
            sessionFactory = configuration.buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void saveGameResult(GameResult result) {
        if (sessionFactory == null) {
            initializeDatabase();
        }
        
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(result);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error saving game result: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void closeConnection() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
