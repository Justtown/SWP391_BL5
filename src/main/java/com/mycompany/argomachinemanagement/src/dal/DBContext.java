package com.mycompany.argomachinemanagement.src.dal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBContext {

    protected Connection connection;
    protected ResultSet resultSet;
    protected PreparedStatement statement;
    
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/argo_managerment_system";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "123456";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    public DBContext() {
        // Constructor không tự động tạo connection
        // Connection sẽ được tạo khi cần thiết qua getConnection()
    }
    
    /**
     * Get database connection
     * Creates a new connection if not exists or if connection is closed
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName(DB_DRIVER);
                connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, "Error getting connection", ex);
        }
        return connection;
    }
    
    /**
     * Close all resources (ResultSet, PreparedStatement, Connection)
     */
    public void closeResources() {
        try {
            if (resultSet != null && !resultSet.isClosed()) {
                resultSet.close();
            }
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, "Error closing resources", ex);
        } finally {
            resultSet = null;
            statement = null;
            connection = null;
        }
    }
    
    /**
     * Test database connection
     */
    public static void main(String[] args) {
        DBContext db = new DBContext();
        Connection conn = db.getConnection();
        if (conn != null) {
            System.out.println("Database connection successful!");
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Database connection failed!");
        }
    }
}

