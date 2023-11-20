import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Model.JDBC;

public class Main {
  public static void main(String[] args) {
    JDBC jdbc = JDBC.getInstance();

    List<String> gymNames = new ArrayList<>();

    try {
      String query1 = "SELECT gym_name FROM gyms";
      try (PreparedStatement statement2 = jdbc.getConnection().prepareStatement(query1);
           ResultSet resultSet1 = statement2.executeQuery()) {

        while (resultSet1.next()) {
          gymNames.add(resultSet1.getString("gym_name"));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    System.out.println(gymNames);
  }
}