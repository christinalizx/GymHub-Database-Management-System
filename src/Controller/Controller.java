package Controller;

import Model.GymUsers;
import Model.JDBC;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Controller {
  private final JDBC jdbc;
  private String loggedInUsername;

  public Controller() {
    this.jdbc = JDBC.getInstance();
  }

  /**
   * Method to verify user credentials using the MySQL function
   *
   * @param username The username to be verified
   * @param password The password to be verified
   * @return True if the credentials are valid, false otherwise
   */
  public boolean verifyCredentials(String username, String password) {
    Connection connection = null;
    CallableStatement callableStatement = null;

    try {
      connection = JDBC.getInstance().getConnection();

      // Prepare the SQL call to the MySQL function
      String callStatement = "{? = CALL VerifyUserPassword(?, ?)}";
      callableStatement = connection.prepareCall(callStatement);

      // Register the output parameter as BOOLEAN
      callableStatement.registerOutParameter(1, java.sql.Types.BOOLEAN);

      // Set the input parameters
      callableStatement.setString(2, username);
      callableStatement.setString(3, password);

      // Execute the function call
      callableStatement.execute();

      // Get the result from the output parameter
      boolean isVerified = callableStatement.getBoolean(1);

      return isVerified;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      // ... (close resources)
    }

    // Return false if an exception occurred
    return false;
  }
  public void addUser(String username, String password, String address, String gymName) {
    try {
      // Get the gymId based on the selected gymName
      int gymId = getGymIdByName(gymName);

      // Create a GymUsers object with the provided data
      GymUsers user = new GymUsers(username, password, address, gymId);

      // Add the user to the database
      addUserToDatabase(user);
      System.out.println("User added successfully!");
    } catch (SQLException e) {
      // Handle database-related errors
      e.printStackTrace();
    }
  }
  public void setLoggedInUsername(String username) {
    this.loggedInUsername = username;
  }

  public String getLoggedInUsername() {
    return loggedInUsername;
  }
  public void handleForumPost(String selectedForum) {
    // Add the logic to handle forum post button click
    // For now, just print a message
    System.out.println("Posting in forum: " + selectedForum);
  }

  private int getGymIdByName(String gymName) throws SQLException {
    // Implement the logic to retrieve the gymId based on the gymName
    Connection connection = jdbc.getConnection();
    String query = "SELECT gym_id FROM gyms WHERE gym_name = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, gymName);
      var resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getInt("gym_id");
      } else {
        throw new SQLException("Gym not found: " + gymName);
      }
    }
  }

  private void addUserToDatabase(GymUsers user) throws SQLException {
    // Implement the logic to add a user to the database
    Connection connection = jdbc.getConnection();
    String query = "INSERT INTO gym_users (username, password, address, gym_id) VALUES (?, ?, ?, ?)";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, user.getUsername());
      preparedStatement.setString(2, user.getPassword());
      preparedStatement.setString(3, user.getAddress());
      preparedStatement.setInt(4, user.getGymId());
      preparedStatement.executeUpdate();
    }
  }

  public List<String> getForumNames() {
    // Use JDBC to fetch forum names from the database
    List<String> forumNames = new ArrayList<>();

    try {
      Connection connection = jdbc.getConnection();
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT forum_name FROM forums");

      while (resultSet.next()) {
        String forumName = resultSet.getString("forum_name");
        forumNames.add(forumName);
      }

      resultSet.close();
      statement.close();
    } catch (Exception e) {
      e.printStackTrace();
      // Handle exceptions as needed
    }

    return forumNames;
  }

  public GymUsers getUserInformation(String username) {
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    GymUsers gymUser = null;

    try {
      connection = jdbc.getConnection();

      // Prepare the SQL statement to retrieve user information
      String query = "SELECT gu.username, gu.password, gu.address, gu.gym_id, gyms.gym_name " +
              "FROM gym_users gu " +
              "JOIN gyms ON gu.gym_id = gyms.gym_id " +
              "WHERE gu.username = ?";
      preparedStatement = connection.prepareStatement(query);
      preparedStatement.setString(1, username);

      // Execute the query
      resultSet = preparedStatement.executeQuery();

      // Check if the result set is not empty
      if (resultSet.next()) {
        // Extract user information from the result set
        String fetchedUsername = resultSet.getString("username");
        String password = resultSet.getString("password");
        String address = resultSet.getString("address");
        String gymName = resultSet.getString("gym_name");

        // Create a GymUsers object with the retrieved data
        gymUser = new GymUsers(fetchedUsername, password, address, getGymIdByName(gymName));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return gymUser;
  }
  public String getGymName(String username) {
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    String gymName = null;

    try {
      connection = jdbc.getConnection();

      // Prepare the SQL statement to retrieve the gym name
      String query = "SELECT gyms.gym_name FROM gyms " +
              "JOIN gym_users gu ON gu.gym_id = gyms.gym_id " +
              "WHERE gu.username = ?";
      preparedStatement = connection.prepareStatement(query);
      preparedStatement.setString(1, username);

      // Execute the query
      resultSet = preparedStatement.executeQuery();

      // Check if the result set is not empty
      if (resultSet.next()) {
        // Extract gym name from the result set
        gymName = resultSet.getString("gym_name");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      // Close resources
      try {
        if (resultSet != null) resultSet.close();
        if (preparedStatement != null) preparedStatement.close();
        if (connection != null) connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return gymName;

  }
}

