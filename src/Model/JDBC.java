package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class JDBC {

  private static final String URL = "jdbc:mysql://database-1.cpqkz8uyycse.us-east-1.rds.amazonaws.com:3306/gymhubdb";
  private static final String USERNAME = "admin";
  private static final String PASSWORD = "WKkn3q3YaPWNW8NthFWU";
  private static JDBC instance;
  private Connection connection;

  /**
   * Private constructor to prevent instantiation outside the class
   **/
  private JDBC() {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  /**
   * Static method to get the singleton instance
   **/
  public static JDBC getInstance() {
    if (instance == null) {
      instance = new JDBC();
    }
    return instance;
  }

  public Connection getConnection() {
    return connection;
  }


}

